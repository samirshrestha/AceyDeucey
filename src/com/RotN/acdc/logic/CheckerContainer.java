package com.RotN.acdc.logic;

import java.io.Serializable;

public class CheckerContainer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum GameColor
	{
		BLACK,
		WHITE,
		NEITHER
	}
	
	public enum BoardPositions {
		NONE(-1), WHITE_BUNKER(0), BLACK_BUNKER(25), POKEY(26), POINT_1(1), POINT_2(2), POINT_3(3),
		POINT_4(4), POINT_5(5), POINT_6(6), POINT_7(7), POINT_8(8), POINT_9(9), POINT_10(10),
		POINT_11(11), POINT_12(12), POINT_13(13), POINT_14(14), POINT_15(15), POINT_16(16), 
		POINT_17(17), POINT_18(18), POINT_19(19), POINT_20(20), POINT_21(21), POINT_22(22),
		POINT_23(23), POINT_24(24);
		
		private int index;
		
		private BoardPositions(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
	}
	
	private BoardPositions position;
	private int whiteCheckerCount;
	private int blackCheckerCount;
	
	public CheckerContainer(BoardPositions position)
	{
		this.position = position;
		whiteCheckerCount = 0;
		blackCheckerCount = 0;
	}
	
	public CheckerContainer(CheckerContainer rhs) {
		this.position = rhs.position;
		this.whiteCheckerCount = rhs.whiteCheckerCount;
		this.blackCheckerCount = rhs.blackCheckerCount;
	}

	public int getWhiteCheckerCount() {
		return whiteCheckerCount;
	}

	public void setWhiteCheckerCount(int whiteCheckerCount) {
		this.whiteCheckerCount = whiteCheckerCount;
	}

	public int getBlackCheckerCount() {
		return blackCheckerCount;
	}

	public void setBlackCheckerCount(int blackCheckerCount) {
		this.blackCheckerCount = blackCheckerCount;
	}

	public BoardPositions getPosition() {
		return position;
	}
	
	public int addBlackPiece() {
		return blackCheckerCount++;
	}
	
	public int addWhitePiece() {
		return whiteCheckerCount++;
	}
	
	public int addPiece(GameColor pieceColor) {
		if (pieceColor == GameColor.BLACK) {
			return blackCheckerCount++;
		} else {
			return whiteCheckerCount++;
		}
	}
	
	public int removePiece(GameColor pieceColor) {
		if (pieceColor == GameColor.BLACK) {
			return blackCheckerCount--;
		} else {
			return whiteCheckerCount--;
		}
	}
	
	public int getCount(GameColor color) {
		if (color == GameColor.BLACK) {
			return blackCheckerCount;
		} else {
			return whiteCheckerCount;
		}
	}
}
