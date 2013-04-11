package com.RotN.acdc.model;

import com.RotN.acdc.logic.CheckerContainer.GameColor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

public class Piece {
	private Bitmap bitmapBlack; // the actual bitmap
	private Bitmap bitmapWhite;
	private GameColor pieceColor;
	private int x; // the x coord
	private int y; // the y coord
	private boolean touched; //if piece is touched/picked up
	private Speed speed;

	private Point animateStart = new Point(0,0);
	private Point animateStop = new Point(0,0);
	
	public void setAnimateStart(Point animateStart) {
		this.animateStart = animateStart;
		calculateSpeed();
	}

	public void setAnimateStop(Point animateStop) {
		this.animateStop = animateStop;
		calculateSpeed();
	}

	public Piece(Bitmap bitmapBlack, Bitmap bitmapWhite, int x, int y){
		this.bitmapBlack = bitmapBlack;
		this.bitmapWhite = bitmapWhite;
		this.x = x;
		this.y = y;
		this.pieceColor = GameColor.NEITHER;
		this.touched = false;
		speed = new Speed();
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
	
	private void calculateSpeed() {
		//points are different, calculate speed
		if (false == this.animateStart.equals(this.animateStop)) {
			
			double xDistance = Math.abs(this.animateStop.x - this.animateStart.x);
			speed.setXv((float) (xDistance / 100));
			if (this.animateStart.x > this.animateStop.x) {
				speed.setxDirection(Speed.DIRECTION_RIGHT);
			} else {
				speed.setxDirection(Speed.DIRECTION_LEFT);
			}
			
			double yDistance = Math.abs(this.animateStop.y - this.animateStart.y);
			speed.setYv((float) (yDistance / 100));
			if (this.animateStart.y > this.animateStop.y) {
				speed.setyDirection(Speed.DIRECTION_DOWN);
			} else {
				speed.setyDirection(Speed.DIRECTION_UP);
			}
		}
	}
}
