package com.RotN.acdc.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.RotN.acdc.logic.CheckerContainer.BoardPositions;
import com.RotN.acdc.logic.CheckerContainer.GameColor;

public class AcDcAI {
	public class Move {
		GameColor color;
		public BoardPositions origSpot;
		public BoardPositions newSpot;
	}
	public class AIMoves {
		ArrayList<Move> moves = new ArrayList<Move>();
	}
	
	public AIMoves GetNextMove(TheGame acdc) {
		AIMoves aiMove = new AIMoves();
		Move placeHolder = new Move();
		placeHolder.origSpot = BoardPositions.POINT_1;
		placeHolder.newSpot = BoardPositions.POINT_3;
		aiMove.moves.add(placeHolder);
		return aiMove;
	}
	
	private Integer evaluateBoard(TheGame acdc) {
		Integer boardValue = 0;
		
		if (acdc.turn == GameColor.BLACK) {
			boardValue = evaluateBoardBlackPerspective(acdc);
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
