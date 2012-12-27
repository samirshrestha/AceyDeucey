package com.RotN.aceydeucey.model;

import com.RotN.aceydeucey.logic.CheckerContainer.GameColor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class Bunker {
	
	private static final String TAG = Bunker.class.getSimpleName();

	private Bitmap bitmap;		// the full bunker
	private Rect bunkerRect;
	private int bunkerCount;	// the number of pieces in the bunker
	
	private boolean isSelected; 
	private boolean isPossibleMove;
	
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
		} else {
			bunkerRect = new Rect((int)(boardRect.width() * 0.0292), 
					(int)(boardRect.height() * 0.5833), 
					(int)(boardRect.width() * 0.0878), 
					(int)(boardRect.height() * 0.9609));
		}
		
	}	
	
	public boolean wasTouched(float eventX, float eventY) {
		boolean wasTouched = false;
		int xLeftBound, xRightBound, yTopBound, yLowerBound;
		xLeftBound = bunkerRect.left - bitmap.getWidth() / 4;
		xRightBound = bunkerRect.right + bitmap.getWidth() /4;
		yLowerBound = bunkerRect.top - bitmap.getHeight() / 4;
		yTopBound = bunkerRect.bottom + bitmap.getHeight() / 4;
		if ((eventX >= xLeftBound) && (eventX <= xRightBound)) {
			if (eventY >= yLowerBound && (eventY <= yTopBound)) {
				Log.d(TAG, "Bunker clicked");
				wasTouched = true;
			}
		}
		
		return wasTouched;
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
	
	// the draw method which draws the corresponding frame
	public void draw(Canvas canvas) {
		// where to draw the bunker
		int startPos = bunkerRect.top;
		int count = bunkerCount;
		if (isSelected) {
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
}
