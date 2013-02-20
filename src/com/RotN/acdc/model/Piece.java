package com.RotN.acdc.model;

import com.RotN.acdc.logic.CheckerContainer.GameColor;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Piece {
	private Bitmap bitmapBlack; // the actual bitmap
	private Bitmap bitmapWhite;
	private GameColor pieceColor;
	private int x; // the x coord
	private int y; // the y coord
	private boolean touched; //if droid is touched/picked up
	
	public Piece(Bitmap bitmapBlack, Bitmap bitmapWhite, int x, int y){
		this.bitmapBlack = bitmapBlack;
		this.bitmapWhite = bitmapWhite;
		this.x = x;
		this.y = y;
		this.pieceColor = GameColor.NEITHER;
		this.touched = false;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	public void setTouched(boolean touched) {
		this.touched = touched;
	}
	
	public boolean isTouched() {
		return this.touched;
	}
	
	public void setColor(GameColor color) {
		this.pieceColor = color;
	}

	public void draw(Canvas canvas) {
		if (touched) {
			if (pieceColor == GameColor.BLACK) {
				canvas.drawBitmap(bitmapBlack, x - (bitmapBlack.getWidth() / 2), y - (bitmapBlack.getHeight() / 2), null);
			} else {
				canvas.drawBitmap(bitmapWhite, x - (bitmapWhite.getWidth() / 2), y - (bitmapWhite.getHeight() / 2), null);
			}
		}
	}

	public GameColor getColor() {
		return pieceColor;
	}
}
