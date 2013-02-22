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
			if (container.getPosition() == BoardPositions.BLACK_BUNKER ||
					container.getPosition() == BoardPositions.POKEY) {
				//don't count nothing these aren't important
			} else if (container.getBlackCheckerCount() > 1) {
				boardValue += container.getBlackCheckerCount() * (25 - container.getPosition().getIndex());
			} else if (container.getBlackCheckerCount() == 1) {
				boardValue += (25 - container.getPosition().getIndex());
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
			if (container.getPosition() == BoardPositions.WHITE_BUNKER ||
					container.getPosition() == BoardPositions.POKEY) {
				//don't count nothing these aren't important
			} else if (container.getWhiteCheckerCount() > 1) {
				boardValue += container.getWhiteCheckerCount() * container.getPosition().getIndex();
			} else if (container.getWhiteCheckerCount() == 1) {
				boardValue += container.getPosition().getIndex();
			}
		}
		
		return boardValue;
	}
}
