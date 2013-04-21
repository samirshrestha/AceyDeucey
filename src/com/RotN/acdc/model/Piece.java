package com.RotN.acdc.model;

import com.RotN.acdc.logic.CheckerContainer.BoardPositions;
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
	private BoardPositions wherePieceCameFrom = BoardPositions.NONE;

	public BoardPositions getWherePieceCameFrom() {
		return wherePieceCameFrom;
	}

	public void setWherePieceCameFrom(BoardPositions wherePieceCameFrom) {
		this.wherePieceCameFrom = wherePieceCameFrom;
	}

	private Point animateStart = new Point(0,0);
	private Point animateStop = new Point(0,0);
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Point getAnimateStart() {
		return animateStart;
	}

	public Point getAnimateStop() {
		return animateStop;
	}
	
	public void setAnimationPoints(Point animateStart, Point animateStop) {
		this.animateStart = animateStart;
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
			
			speed.setXv(7);
			if (this.animateStart.x < this.animateStop.x) {
				speed.setxDirection(Speed.DIRECTION_RIGHT);
			} else {
				speed.setxDirection(Speed.DIRECTION_LEFT);
			}
			
			speed.setYv(7);
			if (this.animateStart.y < this.animateStop.y) {
				speed.setyDirection(Speed.DIRECTION_DOWN);
			} else {
				speed.setyDirection(Speed.DIRECTION_UP);
			}
		}
	}
	
	public boolean updateAnimatePiece() {
		
		if (this.touched) {
			x += (speed.getXv() * speed.getxDirection()); 
			y += (speed.getYv() * speed.getyDirection());			
			
		} else {
			//Log.d("Animate", "Starting the draw");
			this.touched = true;
			x = animateStart.x;
			y = animateStart.y;			

			x += (speed.getXv() * speed.getxDirection()); 
			y += (speed.getYv() * speed.getyDirection());
		}
		
		boolean pieceFinished = reachedDestination();
		
		return pieceFinished;
	}
	
	private boolean reachedDestination() {
		boolean xFinished = false;
		boolean yFinished = false;
		
		if (speed.getxDirection() == Speed.DIRECTION_RIGHT) {
			if (x > animateStop.x) {
				x = animateStop.x;
				xFinished = true;
			}				
		} else {
			if (x < animateStop.x) {
				x = animateStop.x;
				xFinished = true;
			}
		}
		
		if (speed.getyDirection() == Speed.DIRECTION_DOWN) {
			if (y > animateStop.y) {
				y = animateStop.y;
				yFinished = true;
			}				
		} else {
			if (y < animateStop.y) {
				y = animateStop.y;
				yFinished = true;
			}
		}
		
		boolean pieceFinished = false;		
		if (xFinished && yFinished) {
			//Log.d("Animate", "Stopping the draw");
			pieceFinished = true;
		}
		
		return pieceFinished;
	}
	
	public int getDistanceFromAnimateFinish() {
		int distance = (int)Math.sqrt(Math.pow(animateStop.x - x, 2) + Math.pow(animateStop.y - y, 2));
		return distance;
	}
}
