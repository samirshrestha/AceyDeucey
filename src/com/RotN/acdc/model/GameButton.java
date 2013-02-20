package com.RotN.acdc.model;

import com.RotN.acdc.logic.TheGameImpl;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class GameButton {
	private static final String TAG = GameButton.class.getSimpleName();

	private boolean touched; 
	private Rect buttonRect;
	
	public GameButton(int height, int width) {
		this.touched = false;
				
		setButtonRect(height, width);
	}	
	
	private void setButtonRect(int height, int width) {
		buttonRect = new Rect();
		buttonRect.left = (int)(width * 0.8);
		buttonRect.right = (int)(width * 0.95);
		buttonRect.top = (int)(height * 0.05);
		buttonRect.bottom = (int)(height * 0.125);
	}
	
	public boolean wasTouched(float eventX, float eventY) {
		boolean wasTouched = false;
		
		if ((eventX >= buttonRect.left) && (eventX <= buttonRect.right)) {
			if (eventY >= buttonRect.top && (eventY <= buttonRect.bottom)) {
				Log.d(TAG, "Button clicked");
				wasTouched = true;
			}
		}
		
		return wasTouched;
	}
	public void handleActionDown(float eventX, float eventY) {		
		if (wasTouched(eventX, eventY)) {			
			touched = true;
		}
		else
		{
			touched = false;
		}
	}
	
	public boolean handleActionUp(float eventX, float eventY) {
		boolean buttonPressed = false;
		if (touched) {
			//do something for the game...
			touched = false;
			if (wasTouched(eventX, eventY)) {
				buttonPressed = true;
			}
		}
		return buttonPressed;
	}
	
	public void draw(Canvas canvas, TheGameImpl game) {
		
		Paint buttonColor = new Paint();	
		buttonColor.setARGB(255, 70, 70, 70);
		canvas.drawRect(buttonRect, buttonColor);
		
		Paint font = new Paint();	
		font.setARGB(255, 200, 200, 200);		
		font.setTextAlign(Paint.Align.CENTER);
		font.setTextSize(25);
		canvas.drawText(game.getButtonText(), buttonRect.exactCenterX(), buttonRect.exactCenterY() + (float)(buttonRect.height()*.15), font);
				
		if (touched)
		{
			Paint paint = new Paint();
			paint.setARGB(100, 0, 0, 255);
			canvas.drawRect(buttonRect.left, buttonRect.top, buttonRect.right, buttonRect.bottom,  paint);
		}
	}
}

