package com.RotN.acdc.model;

import java.util.Map;

import com.RotN.acdc.logic.CheckerContainer.BoardPositions;
import com.RotN.acdc.logic.CheckerContainer.GameColor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

public class Bunker {
	
	private static final String TAG = Bunker.class.getSimpleName();

	private Bitmap bitmap;		// the full bunker
	private Rect bunkerRect;
	private int bunkerCount;	// the number of pieces in the bunker
	
	private boolean isSelected; 
	private boolean isPossibleMove;
	private BoardPositions boardPos;
	private boolean floatingPiece;
	
	public void setPossibleMove(boolean isPossibleMove) {
		this.isPossibleMove = isPossibleMove;
	}

	public Bunker(GameColor bunkerColor, Bitmap bitmap, Rect boardRect) {
		this.isSelected = false;
		this.isPossibleMove = false;
		this.bitmap = bitmap;
		bunkerCount = 15;
		if (bunkerColor == GameColor.WHITE) {
			bunkerRect = new Rect((int)(boardRect.width() * 0.0292), 
				(int)(boardRect.height() * 0.0390), 
				(int)(boardRect.width() * 0.0878), 
				(int)(boardRect.height() * 0.4166));
			boardPos = BoardPositions.WHITE_BUNKER;
		} else {
			bunkerRect = new Rect((int)(boardRect.width() * 0.0292), 
					(int)(boardRect.height() * 0.5833), 
					(int)(boardRect.width() * 0.0878), 
					(int)(boardRect.height() * 0.9609));
			boardPos = BoardPositions.BLACK_BUNKER;
		}
		
	}	
	
	public boolean wasTouched(float eventX, float eventY) {
		boolean wasTouched = false;
		int xLeftBound, xRightBound, yTopBound, yLowerBound;
		xLeftBound = bunkerRect.left - bitmap.getWidth() / 5;
		xRightBound = bunkerRect.right + bitmap.getWidth() /5;
		yLowerBound = bunkerRect.top - bitmap.getHeight() / 5;
		yTopBound = bunkerRect.bottom + bitmap.getHeight() / 5;
		if ((eventX >= xLeftBound) && (eventX <= xRightBound)) {
			if (eventY >= yLowerBound && (eventY <= yTopBound)) {
				Log.d(TAG, "Bunker clicked");
				wasTouched = true;
			}
		}
		
		return wasTouched;
	}
	
	public void wasTouched(Map<BoardPositions, Double> pointDistances, float eventX, float eventY) {
		int leftCheck = bunkerRect.left;
		int rightCheck = bunkerRect.right;
		int topCheck = bunkerRect.top;
		int bottomCheck = bunkerRect.bottom;
		
		if (isPossibleMove) {
			leftCheck = bunkerRect.left - bunkerRect.width() / 2;
			rightCheck = bunkerRect.right + bunkerRect.width() /2;
			topCheck = bunkerRect.top - bunkerRect.height() / 2;
			bottomCheck = bunkerRect.bottom + bunkerRect.height() / 2;
		}
		
	
		if ((eventX >= leftCheck) && (eventX <= rightCheck)) {
			if (eventY >= topCheck && (eventY <= bottomCheck)) {	
				double xDistance = eventX - bunkerRect.exactCenterX();
				double yDistance = eventY - bunkerRect.exactCenterY();
				double distance = Math.sqrt( (xDistance * xDistance) + (yDistance * yDistance) );
				pointDistances.put(boardPos, distance);
			}
		}
	}
	
	public void setSelected(boolean selected) {
		isSelected = selected;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public int getBunkerCount() {
		return bunkerCount;
	}
	public void setBunkerCount(int bunkerCount) {
		this.bunkerCount = bunkerCount;
	}
	
	public void setFloatingPiece(boolean floatingPiece) {
		this.floatingPiece = floatingPiece;
	}
	
	// the draw method which draws the corresponding frame
	public void draw(Canvas canvas) {
		// where to draw the bunker
		int startPos = bunkerRect.top;
		int count = bunkerCount;
		if (floatingPiece) {
			count--;
		}
		
		int bunkerWidth = bunkerRect.right - bunkerRect.left;
		int bitmapWidth = bitmap.getWidth();
		
		int left = bunkerRect.left + (bunkerWidth - bitmapWidth) / 2;
		
		for (int i = 0; i < count; i++){
			canvas.drawBitmap(bitmap, left, startPos, null);
			startPos = startPos + bitmap.getHeight();
		}
		
		if (isSelected)
		{
			Paint paint = new Paint();
			paint.setARGB(200, 255, 255, 255);
			canvas.drawRect(bunkerRect.left, bunkerRect.top, bunkerRect.right, bunkerRect.bottom,  paint);
		}
		
		if (isPossibleMove) {
			Paint paint = new Paint();
			paint.setARGB(200, 0, 0, 255);
			canvas.drawRect(bunkerRect.left, bunkerRect.top, bunkerRect.right, bunkerRect.bottom,  paint);
		}
	}
	
	public Point getAnimateStart() {
		return new Point(bunkerRect.centerX(), bunkerRect.centerY());
	}

	public Point getAnimateStop() {
		return new Point(bunkerRect.centerX(), bunkerRect.centerY());
	}
}
