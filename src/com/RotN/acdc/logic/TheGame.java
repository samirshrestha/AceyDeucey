package com.RotN.acdc.logic;

import java.io.Serializable;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import com.RotN.acdc.logic.CheckerContainer.BoardPositions;
import com.RotN.acdc.logic.CheckerContainer.GameColor;

public class TheGame implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;
	
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
	public ArrayList<CheckerContainer> containers;
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
	public boolean whiteHumanPlayer = false;
	public boolean blackHumanPlayer = true;
	
	@SuppressLint("UseSparseArrays")
	public TheGame() {
		containers = new ArrayList<CheckerContainer>();
		movesRemaining = new ArrayList<Integer>();
		
		containers.add(new CheckerContainer(BoardPositions.WHITE_BUNKER));
		containers.add(new CheckerContainer(BoardPositions.POINT_1));
		containers.add(new CheckerContainer(BoardPositions.POINT_2));
		containers.add(new CheckerContainer(BoardPositions.POINT_3));
		containers.add(new CheckerContainer(BoardPositions.POINT_4));
		containers.add(new CheckerContainer(BoardPositions.POINT_5));
		containers.add(new CheckerContainer(BoardPositions.POINT_6));
		containers.add(new CheckerContainer(BoardPositions.POINT_7));
		containers.add(new CheckerContainer(BoardPositions.POINT_8));
		containers.add(new CheckerContainer(BoardPositions.POINT_9));
		containers.add(new CheckerContainer(BoardPositions.POINT_10));
		containers.add(new CheckerContainer(BoardPositions.POINT_11));
		containers.add(new CheckerContainer(BoardPositions.POINT_12));
		containers.add(new CheckerContainer(BoardPositions.POINT_13));
		containers.add(new CheckerContainer(BoardPositions.POINT_14));
		containers.add(new CheckerContainer(BoardPositions.POINT_15));
		containers.add(new CheckerContainer(BoardPositions.POINT_16));
		containers.add(new CheckerContainer(BoardPositions.POINT_17));
		containers.add(new CheckerContainer(BoardPositions.POINT_18));
		containers.add(new CheckerContainer(BoardPositions.POINT_19));
		containers.add(new CheckerContainer(BoardPositions.POINT_20));
		containers.add(new CheckerContainer(BoardPositions.POINT_21));
		containers.add(new CheckerContainer(BoardPositions.POINT_22));
		containers.add(new CheckerContainer(BoardPositions.POINT_23));
		containers.add(new CheckerContainer(BoardPositions.POINT_24));
		containers.add(new CheckerContainer(BoardPositions.BLACK_BUNKER));
		containers.add(new CheckerContainer(BoardPositions.POKEY));
	}
	
	@SuppressLint("UseSparseArrays")
	public TheGame(TheGame rhs) {
		this.containers = new ArrayList<CheckerContainer>();
		this.movesRemaining = new ArrayList<Integer>();
		
		containers.add(new CheckerContainer(rhs.containers.get(BoardPositions.WHITE_BUNKER.getIndex())));
		containers.add(new CheckerContainer(rhs.containers.get(1)));
		containers.add(new CheckerContainer(rhs.containers.get(2)));
		containers.add(new CheckerContainer(rhs.containers.get(3)));
		containers.add(new CheckerContainer(rhs.containers.get(4)));
		containers.add(new CheckerContainer(rhs.containers.get(5)));
		containers.add(new CheckerContainer(rhs.containers.get(6)));
		containers.add(new CheckerContainer(rhs.containers.get(7)));
		containers.add(new CheckerContainer(rhs.containers.get(8)));
		containers.add(new CheckerContainer(rhs.containers.get(9)));
		containers.add(new CheckerContainer(rhs.containers.get(10)));
		containers.add(new CheckerContainer(rhs.containers.get(11)));
		containers.add(new CheckerContainer(rhs.containers.get(12)));
		containers.add(new CheckerContainer(rhs.containers.get(13)));
		containers.add(new CheckerContainer(rhs.containers.get(14)));
		containers.add(new CheckerContainer(rhs.containers.get(15)));
		containers.add(new CheckerContainer(rhs.containers.get(16)));
		containers.add(new CheckerContainer(rhs.containers.get(17)));
		containers.add(new CheckerContainer(rhs.containers.get(18)));
		containers.add(new CheckerContainer(rhs.containers.get(19)));
		containers.add(new CheckerContainer(rhs.containers.get(20)));
		containers.add(new CheckerContainer(rhs.containers.get(21)));
		containers.add(new CheckerContainer(rhs.containers.get(22)));
		containers.add(new CheckerContainer(rhs.containers.get(23)));
		containers.add(new CheckerContainer(rhs.containers.get(24)));
		containers.add(new CheckerContainer(rhs.containers.get(BoardPositions.BLACK_BUNKER.getIndex())));
		containers.add(new CheckerContainer(rhs.containers.get(BoardPositions.POKEY.getIndex())));
		
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
		this.whiteHumanPlayer = rhs.whiteHumanPlayer;
		this.blackHumanPlayer = rhs.blackHumanPlayer;
	}
}

