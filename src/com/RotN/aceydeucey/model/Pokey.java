package com.RotN.aceydeucey.model;

import java.util.Map;

import com.RotN.aceydeucey.logic.CheckerContainer;
import com.RotN.aceydeucey.logic.CheckerContainer.BoardPositions;
import com.RotN.aceydeucey.logic.CheckerContainer.GameColor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;

public class Pokey {
	private static final String TAG = GammonPoint.class.getSimpleName();

	private Rect pokeyRect;
	private CheckerContainer.BoardPositions pointPos;
	private boolean isSelected; 
	SparseArray<Bitmap> pieceBlackBitmaps;
	SparseArray<Bitmap> pieceWhiteBitmaps;
	private GameColor turn;
	
	public Pokey(Rect boardRect, SparseArray<Bitmap> pieceBlackBitmaps, SparseArray<Bitmap> pieceWhiteBitmaps) {
		this.pointPos = BoardPositions.POKEY;
		this.turn = GameColor.NEITHER;
		
		this.pieceBlackBitmaps = pieceBlackBitmaps;
		this.pieceWhiteBitmaps = pieceWhiteBitmaps;
		
		pokeyRect = new Rect(
				(int)(boardRect.centerX() - boardRect.width() * .04), //left
				(int)(boardRect.centerY() - boardRect.height() * .225), //top
				(int)(boardRect.centerX() + boardRect.width() * .04), //right
				(int)(boardRect.centerY() + boardRect.height() * .225) //bottom
				);
		isSelected = false;
	}	
	
	public void setTurn(GameColor turn) {
		this.turn = turn;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}
	
	public CheckerContainer.BoardPositions getPointPos() {
		return pointPos;
	}
	
	public boolean wasTouched(float eventX, float eventY) {
		boolean wasTouched = false;
		
		if ((eventX >= pokeyRect.left) && (eventX <= pokeyRect.right)) {
			if (eventY >= pokeyRect.top && (eventY <= pokeyRect.bottom)) {
				Log.d(TAG, pointPos + " clicked");
				wasTouched = true;
			}
		}
		
		return wasTouched;
	}
	
	public void wasTouched(Map<BoardPositions, Double> pointDistances, float eventX, float eventY, boolean onPokey) {
		int leftCheck = pokeyRect.left - pokeyRect.width();
		int rightCheck = pokeyRect.right + pokeyRect.width();
		int topCheck = pokeyRect.top - pokeyRect.width();
		int bottomCheck = pokeyRect.bottom + pokeyRect.width();		
	
		if (onPokey) {
			if ((eventX >= leftCheck) && (eventX <= rightCheck)) {
				if (eventY >= topCheck && (eventY <= bottomCheck)) {
					double xDistance = eventX - pokeyRect.exactCenterX();
					double yDistance = eventY - pokeyRect.exactCenterY();
					double distance = Math.sqrt( (xDistance * xDistance) + (yDistance * yDistance) );
					pointDistances.put(pointPos, distance);
				}
			}
		}
	}
	
	// the draw method which draws the corresponding frame
	public void draw(Canvas canvas, CheckerContainer pokeyData) {				
		SparseArray<Bitmap> pieceBitmaps;
		int count = 0;
		SparseArray<Bitmap> pieceMinorBitmaps;
		int minorCount;
		
		int blackFloatingDecrement = 0, whiteFloatingDecrement = 0;
		
		if (isSelected) {
			if (turn == GameColor.WHITE) {
				whiteFloatingDecrement = 1;
			} else if (turn == GameColor.BLACK) {
				blackFloatingDecrement = 1;
			}
		}
		
		if (pokeyData.getBlackCheckerCount() > pokeyData.getWhiteCheckerCount()) {
			pieceBitmaps = pieceBlackBitmaps;
			count = pokeyData.getBlackCheckerCount() - blackFloatingDecrement;
			pieceMinorBitmaps = pieceWhiteBitmaps;
			minorCount = pokeyData.getWhiteCheckerCount() - whiteFloatingDecrement;
		} else {
			pieceBitmaps = pieceWhiteBitmaps;
			count = pokeyData.getWhiteCheckerCount() - whiteFloatingDecrement;
			pieceMinorBitmaps = pieceBlackBitmaps;
			minorCount = pokeyData.getBlackCheckerCount() - blackFloatingDecrement;
		}
		
		int startPos = pokeyRect.top;
		int left = pokeyRect.centerX() - pieceBitmaps.get(1).getWidth() / 2;
		
		//first piece
		if (count >= 11) { //requires 3 initial pieces
			canvas.drawBitmap(pieceBitmaps.get(3), left, startPos, null);
			startPos = startPos + pieceBitmaps.get(3).getHeight();
		} else if (count >= 6){
			canvas.drawBitmap(pieceBitmaps.get(2), left, startPos, null);
			startPos = startPos + pieceBitmaps.get(2).getHeight();
		}
		else if (count > 0){
			canvas.drawBitmap(pieceBitmaps.get(1), left, startPos, null);
			startPos = startPos + pieceBitmaps.get(1).getHeight();
		}
		
		//second piece
		if (count >= 12) { //requires 3 second pieces
			canvas.drawBitmap(pieceBitmaps.get(3), left, startPos, null);
			startPos = startPos + pieceBitmaps.get(3).getHeight();
		} else if (count >= 7){
			canvas.drawBitmap(pieceBitmaps.get(2), left, startPos, null);
			startPos = startPos + pieceBitmaps.get(2).getHeight();
		}
		else if (count > 1){
			canvas.drawBitmap(pieceBitmaps.get(1), left, startPos, null);
			startPos = startPos + pieceBitmaps.get(1).getHeight();
		}
		
		//third piece
		if (count >= 13) { //requires 3 third pieces
			canvas.drawBitmap(pieceBitmaps.get(3), left, startPos, null);
			startPos = startPos + pieceBitmaps.get(3).getHeight();
		} else if (count >= 8){
			canvas.drawBitmap(pieceBitmaps.get(2), left, startPos, null);
			startPos = startPos + pieceBitmaps.get(2).getHeight();
		}
		else if (count > 2){
			canvas.drawBitmap(pieceBitmaps.get(1), left, startPos, null);
			startPos = startPos + pieceBitmaps.get(1).getHeight();
		}
		
		//fourth piece
		if (count >= 14) { //requires 3 fourth pieces
			canvas.drawBitmap(pieceBitmaps.get(3), left, startPos, null);
			startPos = startPos + pieceBitmaps.get(3).getHeight();
		} else if (count >= 9){
			canvas.drawBitmap(pieceBitmaps.get(2), left, startPos, null);
			startPos = startPos + pieceBitmaps.get(2).getHeight();
		}
		else if (count > 3){
			canvas.drawBitmap(pieceBitmaps.get(1), left, startPos, null);
			startPos = startPos + pieceBitmaps.get(1).getHeight();
		}
		
		//fifth piece
		if (count == 15) { 
			canvas.drawBitmap(pieceBitmaps.get(3), left, startPos, null);
			startPos = startPos + pieceBitmaps.get(3).getHeight();
		} else if (count >= 10){
			canvas.drawBitmap(pieceBitmaps.get(2), left, startPos, null);
			startPos = startPos + pieceBitmaps.get(2).getHeight();
		}
		else if (count > 4){
			canvas.drawBitmap(pieceBitmaps.get(1), left, startPos, null);
			startPos = startPos + pieceBitmaps.get(1).getHeight();
		}
		
		//first piece
		if (minorCount == 6){
			canvas.drawBitmap(pieceMinorBitmaps.get(2), left, startPos, null);
			startPos = startPos + pieceMinorBitmaps.get(2).getHeight();
		}
		else if (minorCount > 0){
			canvas.drawBitmap(pieceMinorBitmaps.get(1), left, startPos, null);
			startPos = startPos + pieceMinorBitmaps.get(1).getHeight();
		}
				
		//second piece
		if (minorCount > 1){
			canvas.drawBitmap(pieceMinorBitmaps.get(1), left, startPos, null);
			startPos = startPos + pieceMinorBitmaps.get(1).getHeight();
		}
				
		//third piece
		if (minorCount > 2){
			canvas.drawBitmap(pieceMinorBitmaps.get(1), left, startPos, null);
			startPos = startPos + pieceMinorBitmaps.get(1).getHeight();
		}
				
		//fourth piece
		if (minorCount > 3){
			canvas.drawBitmap(pieceMinorBitmaps.get(1), left, startPos, null);
			startPos = startPos + pieceMinorBitmaps.get(1).getHeight();
		}
				
		//fifth piece
		if (minorCount > 4){
			canvas.drawBitmap(pieceMinorBitmaps.get(1), left, startPos, null);
			startPos = startPos + pieceMinorBitmaps.get(1).getHeight();
		}
	}
	
	
}
