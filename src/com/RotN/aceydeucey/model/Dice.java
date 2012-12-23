package com.RotN.aceydeucey.model;

import com.RotN.aceydeucey.R;
import com.RotN.aceydeucey.logic.TheGameImpl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.SparseArray;

public class Dice {
	
	private SparseArray<Bitmap> diceBitmaps;
	//private static final String TAG = Dice.class.getSimpleName();
		
	public Dice(SparseArray<Bitmap> diceBitmaps) {
		this.diceBitmaps = diceBitmaps;
	}	
	
	public void draw(Canvas canvas, TheGameImpl game) {
		
		Bitmap die = diceBitmaps.get(getBlackResourceId(game.getB1DieValue()));
		if (die != null) {
			canvas.drawBitmap(die, (float)(canvas.getWidth() * 0.2), (float)((canvas.getHeight() * 0.45) ), null);
		} else {
			die = diceBitmaps.get(getBlackResourceId(2));
			canvas.drawBitmap(die, (float)(canvas.getWidth() * 0.035), (float)(canvas.getHeight() * 0.43), null);
		}
		
		die = diceBitmaps.get(getBlackResourceId(game.getB2DieValue()));
		if (die != null) {
			canvas.drawBitmap(die, (float)(canvas.getWidth() * 0.3), (float)((canvas.getHeight() * 0.45) ), null);
			
		} else {
			die = diceBitmaps.get(getBlackResourceId(1));
			canvas.drawBitmap(die, (float)(canvas.getWidth() * 0.035), (float)(canvas.getHeight() * 0.51), null);
		}
		
		die = diceBitmaps.get(getWhiteResourceId(game.getW1DieValue()));
		if (die != null) {
			canvas.drawBitmap(die, (float)(canvas.getWidth() * .65), (float)(canvas.getHeight() * 0.45), null);
		} else {
			die = diceBitmaps.get(getWhiteResourceId(2));
			canvas.drawBitmap(die, (float)(canvas.getWidth() * 0.915), (float)(canvas.getHeight() * 0.43), null);
		}
		
		die = diceBitmaps.get(getWhiteResourceId(game.getW2DieValue()));
		if (die != null) {
			canvas.drawBitmap(die, (float)(canvas.getWidth() * .75), (float)(canvas.getHeight() * 0.45), null);
		} else {
			die = diceBitmaps.get(getWhiteResourceId(1));
			canvas.drawBitmap(die, (float)(canvas.getWidth() * 0.915), (float)(canvas.getHeight() * 0.51), null);
		}
	}
	
	private int getBlackResourceId(int dieValue) {
		int bitmapID = 0;
		
		switch (dieValue) {
		case 1:
			bitmapID = R.drawable.red_dice1;
			break;
		case 2:
			bitmapID = R.drawable.red_dice2;
			break;
		case 3:
			bitmapID = R.drawable.red_dice3;
			break;
		case 4:
			bitmapID = R.drawable.red_dice4;
			break;
		case 5:
			bitmapID = R.drawable.red_dice5;
			break;
		case 6:
			bitmapID = R.drawable.red_dice6;
			break;
		}
		
		return bitmapID;
	}
	
	private int getWhiteResourceId(int dieValue) {
		int bitmapID = 0;
		
		switch (dieValue) {
		case 1:
			bitmapID = R.drawable.white_dice1;
			break;
		case 2:
			bitmapID = R.drawable.white_dice2;
			break;
		case 3:
			bitmapID = R.drawable.white_dice3;
			break;
		case 4:
			bitmapID = R.drawable.white_dice4;
			break;
		case 5:
			bitmapID = R.drawable.white_dice5;
			break;
		case 6:
			bitmapID = R.drawable.white_dice6;
			break;
		}
		
		return bitmapID;
	}
}
