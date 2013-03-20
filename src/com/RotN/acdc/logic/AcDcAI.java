package com.RotN.acdc.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import android.util.Log;

import com.RotN.acdc.logic.CheckerContainer.BoardPositions;
import com.RotN.acdc.logic.CheckerContainer.GameColor;

public class AcDcAI {
	
	public ArrayList<Move> GetNextMove(TheGame acdc) {
		AIMoves aiMoves = new AIMoves();
		aiMoves = GetNextMove(acdc, aiMoves);
		
		logAIMove("The Move", aiMoves);
		
		return aiMoves.moves;
	}

	private AIMoves GetNextMove(TheGame acdc, AIMoves movesUsed) {
		AIMoves aiMove = new AIMoves();
		TheGame acdcCopy = new TheGame(acdc);
				
		// next 3 lines iterate through all of the containers
		Set<Entry<Integer, CheckerContainer>> set = acdcCopy.containers.entrySet();
		Iterator<Entry<Integer, CheckerContainer>> it = set.iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, CheckerContainer> m = (Map.Entry<Integer, CheckerContainer>)it.next();
			CheckerContainer orig = m.getValue();
			// this gives me the possible moves for a given container
			TheGameImpl tempImpl = new TheGameImpl();
			tempImpl.setGammonData(acdcCopy);
			Vector<BoardPositions> options = tempImpl.getPossibleMoves(orig.getPosition());
			
			//loops through the move options
			for (BoardPositions move : options) {
				//create a copy to move pieces on
				TheGame acdcToPlayOn = new TheGame(acdcCopy);
				TheGameImpl acdcImplToPlayOn = new TheGameImpl();
				acdcImplToPlayOn.setGammonData(acdcToPlayOn);
				boolean doubles = false;
				if (acdcImplToPlayOn.getGammonData().movesRemaining.size() == 4) {
					doubles = true;
				}
				//moves the piece on our game clone and returns everything done (important in case something went to pokey)
				ArrayList<Move> moves = acdcImplToPlayOn.movePiece(orig.getPosition(), move);
				//make a copy of moves used to this point
				AIMoves possible = new AIMoves(movesUsed);
				//add the moves that we just did
				possible.moves.addAll(moves);
				// if there are no more moves remaining time to check our score
				if (acdcImplToPlayOn.getGammonData().movesRemaining.size() > 0) {
					possible = GetNextMove(acdcImplToPlayOn.getGammonData(), possible);
				} else {
					// returns a board value based on piece position
					possible.value = evaluateBoard(acdcImplToPlayOn.getGammonData());
				}
				
				if (possible.value > aiMove.value) {
					aiMove = possible;
					this.logAIMove("Possible ", aiMove);
				}
				
				if (doubles) {
					break;
				}
			}
		}
		
		return aiMove;
	}
	
	public void logAIMove(String tag, AIMoves option) {
		Log.d("AI", tag + " Value: " + option.value);
		
		for (Move move: option.moves) {
			Log.d("AI", move.origSpot.toString() + ", " + move.newSpot.toString() + ", " + move.color.toString());
		}
	}
	
	private Integer evaluateBoard(TheGame acdc) {
		int boardValue = 0;
		
		if (acdc.turn == GameColor.BLACK) {
			boardValue = evaluateBoardBlackPerspective(acdc);
		} else {
			boardValue = evaluateBoardWhitePerspective(acdc);
		}
		
		return boardValue;
	}
	
	public Integer evaluateBoardBlackPerspective(TheGame acdc) {
		Integer boardValue = 0;
		
		Set<Entry<Integer, CheckerContainer>> set = acdc.containers.entrySet();
		Iterator<Entry<Integer, CheckerContainer>> it = set.iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, CheckerContainer> m = (Map.Entry<Integer, CheckerContainer>)it.next();
			CheckerContainer container = m.getValue();
			int containerValue = 25 - container.getPosition().getIndex();
			if (container.getPosition() == BoardPositions.BLACK_BUNKER ||
					container.getPosition() == BoardPositions.POKEY) {
				//don't count nothing these aren't important
			} else if (container.getBlackCheckerCount() > 1) {
				boardValue += container.getBlackCheckerCount() * (containerValue);
			} else if (container.getBlackCheckerCount() == 1) {
				//pieces that get hit farther along hurt more so lower the value of the open ones
				if (containerValue > 18) {
					containerValue = (int)(containerValue * 0.25);
				} else if (containerValue > 12) {
					containerValue = (int)(containerValue * .5);
				} else if (containerValue > 6) {
					containerValue = (int)(containerValue * .75);
				}
				
				if (checkForWhitePieceWithinNineSpots(acdc, container) ) {
					boardValue += (containerValue - 12);
				} else {
					boardValue += (containerValue - 6);
				}	
			}
		}
		
		return boardValue;
	}
	
	public Integer evaluateBoardWhitePerspective(TheGame acdc) {
		Integer boardValue = 0;
		
		Set<Entry<Integer, CheckerContainer>> set = acdc.containers.entrySet();
		Iterator<Entry<Integer, CheckerContainer>> it = set.iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, CheckerContainer> m = (Map.Entry<Integer, CheckerContainer>)it.next();
			CheckerContainer container = m.getValue();
			int containerValue = container.getPosition().getIndex();
			if (container.getPosition() == BoardPositions.WHITE_BUNKER ||
					container.getPosition() == BoardPositions.POKEY) {
				//don't count nothing these aren't important
			} else if (container.getWhiteCheckerCount() > 1) {
				boardValue += container.getWhiteCheckerCount() * containerValue;
			} else if (container.getWhiteCheckerCount() == 1) {
				//pieces that get hit farther along hurt more so lower the value of the open ones				
				if (containerValue > 18) {
					containerValue = (int)(containerValue * 0.25);
				} else if (containerValue > 12) {
					containerValue = (int)(containerValue * .5);
				} else if (containerValue > 6) {
					containerValue = (int)(containerValue * .75);
				}
				
				if (checkForBlackPieceWithinNineSpots(acdc, container) ) {
					boardValue += containerValue - 12;
				} else {
					boardValue += containerValue - 6;
				}				
			}
		}
		
		return boardValue;
	}
	
	private boolean checkForBlackPieceWithinNineSpots(TheGame acdc, CheckerContainer container) {
		boolean blackFound = false;
		
		int containerIndex = container.getPosition().getIndex();
		
		for (int index = 1; index <= 9; index++) {
			if (index + containerIndex == 25) {
				if ( (acdc.containers.get(containerIndex + index).getBlackCheckerCount() > 0 &&
						acdc.allBlackPiecesOut == false) ||
						acdc.containers.get(BoardPositions.POKEY.getIndex()).getBlackCheckerCount() > 0) {
					blackFound = true;
					break;
				}
			} else if (index + containerIndex > 25) {
				break;
			} else if (acdc.containers.get(containerIndex + index).getBlackCheckerCount() > 0) {
				blackFound = true;
				break;
			}
		}
		
		return blackFound;
	}
	
	private boolean checkForWhitePieceWithinNineSpots(TheGame acdc, CheckerContainer container) {
		boolean whiteFound = false;
		
		int containerIndex = container.getPosition().getIndex();
		
		for (int index = 1; index <= 9; index++) {
			if (containerIndex - index == 0) {
				if ( (acdc.containers.get(containerIndex - index).getWhiteCheckerCount() > 0 &&
						acdc.allWhitePiecesOut == false) ||
						acdc.containers.get(BoardPositions.POKEY.getIndex()).getWhiteCheckerCount() > 0) {
					whiteFound = true;
					break;
				}
			} else if (containerIndex - index < 0) {
				break;
			} else if (acdc.containers.get(containerIndex - index).getWhiteCheckerCount() > 0) {
				whiteFound = true;
				break;
			}
		}
		
		return whiteFound;
	}
}
