package com.RotN.acdc.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import com.RotN.acdc.logic.CheckerContainer.BoardPositions;
import com.RotN.acdc.logic.CheckerContainer.GameColor;

public class AcDcAI {

	public class AIMoves implements Cloneable {
		ArrayList<Move> moves = new ArrayList<Move>();
		Integer value = -9000;
		
		@Override
		public AIMoves clone() {
			try {
				return (AIMoves)super.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	//TODO a lot of work here...
	public AIMoves GetNextMove(TheGameImpl acdc, AIMoves movesUsed) {
		AIMoves aiMove = new AIMoves();
				
		// next 3 lines iterate through all of the containers
		Set<Entry<Integer, CheckerContainer>> set = acdc.getGammonData().containers.entrySet();
		Iterator<Entry<Integer, CheckerContainer>> it = set.iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, CheckerContainer> m = (Map.Entry<Integer, CheckerContainer>)it.next();
			CheckerContainer orig = m.getValue();
			// this gives me the possible moves for a given container
			Vector<BoardPositions> options = acdc.getPossibleMoves(orig.getPosition());
			
			//loops through the move options
			for (BoardPositions move : options) {
				TheGameImpl acdcTemp = acdc.clone();
				//moves the piece on our game clone and returns everything done (important in case something went to pokey)
				ArrayList<Move> moves = acdcTemp.movePiece(orig.getPosition(), move);
				//make a copy of moves used to this point
				AIMoves possible = movesUsed.clone();
				//add the moves that we just did
				possible.moves.addAll(moves);
				// if there are no more moves remaining time to check our score
				if (acdcTemp.getGammonData().movesRemaining.size() > 0) {
					possible = GetNextMove(acdcTemp, possible);
				} else {
					// returns a board value based on piece position
					possible.value = evaluateBoard(acdcTemp.getGammonData());
					if (possible.value > aiMove.value) {
						aiMove = possible;
					}
				}
			}
		}
		
		return aiMove;
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
