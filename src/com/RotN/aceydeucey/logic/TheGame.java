package com.RotN.aceydeucey.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import com.RotN.aceydeucey.logic.CheckerContainer.BoardPositions;
import com.RotN.aceydeucey.logic.CheckerContainer.GameColor;

public class TheGame implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
}

