package com.RotN.acdc.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import com.RotN.acdc.logic.CheckerContainer.BoardPositions;
import com.RotN.acdc.logic.CheckerContainer.GameColor;

public class TheGame implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	
	public enum ButtonState {
		ROLL_FOR_NUMBER("Roll For Numbers"), ROLL_FOR_TURN("Roll For Turn"), RED_ROLL("Red Roll"), WHITE_ROLL("White Roll"), TURN_FINISHED("Clear Dice"), 
		WHITE_WON("White Won!!!"), BLACK_WON("Red Won!!!");
		
		private String buttonText;
		
		private ButtonState(String buttonText) {
			this.buttonText = buttonText;
		}
		
		public String getText() {
			return buttonText;
		}
	}
	public Map<Integer, CheckerContainer> containers;
	public GameColor turn;
	public int whiteDie1;
	public int whiteDie2;
	public int blackDie1;
	public int blackDie2;
	public boolean aceyDeucey;
	public boolean acdcOrigMove = false;
	public ArrayList<Integer> movesRemaining;
	public boolean whiteMovingIn, blackMovingIn, allBlackPiecesOut, allWhitePiecesOut;
	public ButtonState buttonState;
	public int savedStatesCount;
	
	@SuppressLint("UseSparseArrays")
	public TheGame() {
		containers = new HashMap<Integer, CheckerContainer>();
		movesRemaining = new ArrayList<Integer>();
		
		containers.put(1, new CheckerContainer(BoardPositions.POINT_1));
		containers.put(2, new CheckerContainer(BoardPositions.POINT_2));
		containers.put(3, new CheckerContainer(BoardPositions.POINT_3));
		containers.put(4, new CheckerContainer(BoardPositions.POINT_4));
		containers.put(5, new CheckerContainer(BoardPositions.POINT_5));
		containers.put(6, new CheckerContainer(BoardPositions.POINT_6));
		containers.put(7, new CheckerContainer(BoardPositions.POINT_7));
		containers.put(8, new CheckerContainer(BoardPositions.POINT_8));
		containers.put(9, new CheckerContainer(BoardPositions.POINT_9));
		containers.put(10, new CheckerContainer(BoardPositions.POINT_10));
		containers.put(11, new CheckerContainer(BoardPositions.POINT_11));
		containers.put(12, new CheckerContainer(BoardPositions.POINT_12));
		containers.put(13, new CheckerContainer(BoardPositions.POINT_13));
		containers.put(14, new CheckerContainer(BoardPositions.POINT_14));
		containers.put(15, new CheckerContainer(BoardPositions.POINT_15));
		containers.put(16, new CheckerContainer(BoardPositions.POINT_16));
		containers.put(17, new CheckerContainer(BoardPositions.POINT_17));
		containers.put(18, new CheckerContainer(BoardPositions.POINT_18));
		containers.put(19, new CheckerContainer(BoardPositions.POINT_19));
		containers.put(20, new CheckerContainer(BoardPositions.POINT_20));
		containers.put(21, new CheckerContainer(BoardPositions.POINT_21));
		containers.put(22, new CheckerContainer(BoardPositions.POINT_22));
		containers.put(23, new CheckerContainer(BoardPositions.POINT_23));
		containers.put(24, new CheckerContainer(BoardPositions.POINT_24));
		containers.put(BoardPositions.BLACK_BUNKER.getIndex(), new CheckerContainer(BoardPositions.BLACK_BUNKER));
		containers.put(BoardPositions.WHITE_BUNKER.getIndex(), new CheckerContainer(BoardPositions.WHITE_BUNKER));
		containers.put(BoardPositions.POKEY.getIndex(), new CheckerContainer(BoardPositions.POKEY));
	}
	
	@SuppressLint("UseSparseArrays")
	public TheGame(TheGame rhs) {
		this.containers = new HashMap<Integer, CheckerContainer>();
		this.movesRemaining = new ArrayList<Integer>();
		
		containers.put(1, new CheckerContainer(rhs.containers.get(1)));
		containers.put(2, new CheckerContainer(rhs.containers.get(2)));
		containers.put(3, new CheckerContainer(rhs.containers.get(3)));
		containers.put(4, new CheckerContainer(rhs.containers.get(4)));
		containers.put(5, new CheckerContainer(rhs.containers.get(5)));
		containers.put(6, new CheckerContainer(rhs.containers.get(6)));
		containers.put(7, new CheckerContainer(rhs.containers.get(7)));
		containers.put(8, new CheckerContainer(rhs.containers.get(8)));
		containers.put(9, new CheckerContainer(rhs.containers.get(9)));
		containers.put(10, new CheckerContainer(rhs.containers.get(10)));
		containers.put(11, new CheckerContainer(rhs.containers.get(11)));
		containers.put(12, new CheckerContainer(rhs.containers.get(12)));
		containers.put(13, new CheckerContainer(rhs.containers.get(13)));
		containers.put(14, new CheckerContainer(rhs.containers.get(14)));
		containers.put(15, new CheckerContainer(rhs.containers.get(15)));
		containers.put(16, new CheckerContainer(rhs.containers.get(16)));
		containers.put(17, new CheckerContainer(rhs.containers.get(17)));
		containers.put(18, new CheckerContainer(rhs.containers.get(18)));
		containers.put(19, new CheckerContainer(rhs.containers.get(19)));
		containers.put(20, new CheckerContainer(rhs.containers.get(20)));
		containers.put(21, new CheckerContainer(rhs.containers.get(21)));
		containers.put(22, new CheckerContainer(rhs.containers.get(22)));
		containers.put(23, new CheckerContainer(rhs.containers.get(23)));
		containers.put(24, new CheckerContainer(rhs.containers.get(24)));
		containers.put(BoardPositions.BLACK_BUNKER.getIndex(), new CheckerContainer(rhs.containers.get(BoardPositions.BLACK_BUNKER.getIndex())));
		containers.put(BoardPositions.WHITE_BUNKER.getIndex(), new CheckerContainer(rhs.containers.get(BoardPositions.WHITE_BUNKER.getIndex())));
		containers.put(BoardPositions.POKEY.getIndex(), new CheckerContainer(rhs.containers.get(BoardPositions.POKEY.getIndex())));
		
		for (Integer moveLength : rhs.movesRemaining) {
			this.movesRemaining.add(moveLength);
		}
		
		this.turn = rhs.turn;
		this.whiteDie1 = rhs.whiteDie1;
		this.whiteDie2 = rhs.whiteDie2;
		this.blackDie1 = rhs.blackDie1;
		this.blackDie2 = rhs.blackDie2;
		this.aceyDeucey = rhs.aceyDeucey;
		this.acdcOrigMove = rhs.acdcOrigMove;
		this.whiteMovingIn = rhs.whiteMovingIn;
		this.blackMovingIn = rhs.blackMovingIn;
		this.allBlackPiecesOut = rhs.allBlackPiecesOut;
		this.allWhitePiecesOut = rhs.allWhitePiecesOut;
		this.buttonState = rhs.buttonState;
		this.savedStatesCount = rhs.savedStatesCount;
	}
}

