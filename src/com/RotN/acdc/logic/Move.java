package com.RotN.acdc.logic;

import com.RotN.acdc.logic.CheckerContainer.BoardPositions;
import com.RotN.acdc.logic.CheckerContainer.GameColor;

public class Move {
	GameColor color;
	BoardPositions origSpot;
	BoardPositions newSpot;
	Integer moveLength;
	boolean pokeyHappened;
	
	public Move(BoardPositions origSpot, BoardPositions newSpot, GameColor color, Integer moveLength, boolean pokey) {
		this.color = color;
		this.origSpot = origSpot;
		this.newSpot = newSpot;
		this.moveLength = moveLength;
		this.pokeyHappened = pokey;
	}
	
	public Integer getMoveLength() {
		return moveLength;
	}

	public Move(Move rhs) {
		this.color = rhs.color;
		this.origSpot = rhs.origSpot;
		this.newSpot = rhs.newSpot;
		this.moveLength = rhs.moveLength;
		this.pokeyHappened = rhs.pokeyHappened;
	}
	
	public boolean pokeyHappened() {
		return this.pokeyHappened;
	}

	public GameColor getColor() {
		return color;
	}

	public void setColor(GameColor color) {
		this.color = color;
	}

	public BoardPositions getOrigSpot() {
		return origSpot;
	}

	public void setOrigSpot(BoardPositions origSpot) {
		this.origSpot = origSpot;
	}

	public BoardPositions getNewSpot() {
		return newSpot;
	}

	public void setNewSpot(BoardPositions newSpot) {
		this.newSpot = newSpot;
	}
}
