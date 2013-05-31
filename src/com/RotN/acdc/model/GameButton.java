package com.RotN.acdc.model;

import com.RotN.acdc.logic.TheGame.ButtonState;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class GameButton {
	private static final String TAG = GameButton.class.getSimpleName();

	private boolean touched; 
	private Rect buttonRect;
	private Rect boardRect;
	private Bitmap redRoll;
	private Bitmap whiteRoll;
	private Bitmap redRollPush;
	private Bitmap whiteRollPush;
	private Bitmap redClearDice;
	private Bitmap whiteClearDice;
	private Bitmap clearDice;
	
	public void setRedRoll(Bitmap redRoll) {
		this.redRoll = redRoll;
	}

	public void setWhiteRoll(Bitmap whiteRoll) {
		this.whiteRoll = whiteRoll;
	}

	public void setRedRollPush(Bitmap redRollPush) {
		this.redRollPush = redRollPush;
	}

	public void setWhiteRollPush(Bitmap whiteRollPush) {
		this.whiteRollPush = whiteRollPush;
	}

	public void setRedClearDice(Bitmap redClearDice) {
		this.redClearDice = redClearDice;
	}

	public void setWhiteClearDice(Bitmap whiteClearDice) {
		this.whiteClearDice = whiteClearDice;
	}

	public void setClearDice(Bitmap clearDice) {
		this.clearDice = clearDice;
	}

	public GameButton(Rect boardRect) {
		this.touched = false;
		this.boardRect = boardRect;
		this.buttonRect = new Rect();
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
	public boolean handleActionDown(float eventX, float eventY) {		
		if (wasTouched(eventX, eventY)) {			
			touched = true;
		}
		else
		{
			touched = false;
		}
		
		return touched;
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
	
	public void draw(Canvas canvas, ButtonState button, boolean canMove) {
		float right = 0;
		float top = 0;
		
		switch (button) {
		case RED_ROLL:
			right = (float)(boardRect.right * 0.64843);
			top = (float)(boardRect.bottom * 0.44);
			if (touched == false) {
				canvas.drawBitmap(redRoll, right, top, null);
			} else {
				canvas.drawBitmap(redRollPush, right, top, null);
			}
			break;
		case RED_WON:
			break;
		case ROLL_FOR_TURN:
			break;
		case CLEAR_RED:
			right = (float)(boardRect.right * 0.64843);
			top = (float)(boardRect.bottom * 0.44);
			if (false == canMove) {
				if (touched == false) {
					canvas.drawBitmap(redClearDice, right, top, null);
				} else {
					canvas.drawBitmap(clearDice, right, top, null);
				}
			}
			break;
		case CLEAR_WHITE:
			right = (float)(boardRect.right * 0.18828);
			top = (float)(boardRect.bottom * 0.44);
			if (false == canMove) {
				if (touched == false) {
					canvas.drawBitmap(whiteClearDice, right, top, null);
				} else {
					canvas.drawBitmap(clearDice, right, top, null);
				}
			}
			break;
		case WHITE_ROLL:
			right = (float)(boardRect.right * 0.18828);
			top = (float)(boardRect.bottom * 0.44);
			if (touched == false) {
				canvas.drawBitmap(whiteRoll, right, top, null);
			} else {
				canvas.drawBitmap(whiteRollPush, right, top, null);
			}
			break;
		case WHITE_WON:
			break;
		default:
			break;
			
		}
		buttonRect.top = (int)(top);
		buttonRect.left = (int)(right);
		buttonRect.right = buttonRect.left + whiteRoll.getWidth();
		buttonRect.bottom = buttonRect.top + whiteRoll.getHeight();		
	}
}

