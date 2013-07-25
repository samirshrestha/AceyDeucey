package com.RotN.acdc.model;

import com.RotN.acdc.R;
import com.RotN.acdc.logic.TheGameImpl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.SparseArray;

public class Dice {
	
	private SparseArray<Bitmap> diceBitmaps;
	//private static final String TAG = Dice.class.getSimpleName();
		
	public Dice(SparseArray<Bitmap> diceBitmaps) {
		this.diceBitmaps = diceBitmaps;
	}	
	
	public void draw(Canvas canvas, TheGameImpl game) {
		
		Bitmap die = diceBitmaps.get(getBlackResourceId(game.getB2DieValue()));
		if (die != null) {
			canvas.drawBitmap(die, (float)(canvas.getWidth() * 0.34375), (float)((canvas.getHeight() * 0.43) ), null);
		} else {
			die = diceBitmaps.get(getBlackResourceId(2));
			//canvas.drawBitmap(die, (float)(canvas.getWidth() * 0.035), (float)(canvas.getHeight() * 0.43), null);
			double scale = .75;
			int left = (int)(canvas.getWidth() * 0.035);
			int top = (int)(canvas.getHeight() * 0.425);
			int right = left + (int)(die.getWidth() * scale);
			int bottom = top + (int)(die.getHeight() * scale);
			Rect dest = new Rect(left, top, right, bottom);
			canvas.drawBitmap(die, null, dest, null);
		}
		
		die = diceBitmaps.get(getBlackResourceId(game.getB1DieValue()));
		if (die != null) {
			canvas.drawBitmap(die, (float)(canvas.getWidth() * 0.2578125), (float)((canvas.getHeight() * 0.43) ), null);
			
		} else {
			die = diceBitmaps.get(getBlackResourceId(1));
			//canvas.drawBitmap(die, (float)(canvas.getWidth() * 0.035), (float)(canvas.getHeight() * 0.51), null);
			double scale = .75;
			int left = (int)(canvas.getWidth() * 0.035);
			int top = (int)(canvas.getHeight() * 0.505);
			int right = left + (int)(die.getWidth() * scale);
			int bottom = top + (int)(die.getHeight() * scale);
			Rect dest = new Rect(left, top, right, bottom);
			canvas.drawBitmap(die, null, dest, null);
		}
		
		die = diceBitmaps.get(getWhiteResourceId(game.getW1DieValue()));
		if (die != null) {
			canvas.drawBitmap(die, (float)(canvas.getWidth() * 0.703125), (float)(canvas.getHeight() * 0.43), null);
		} else {
			die = diceBitmaps.get(getWhiteResourceId(2));
			//canvas.drawBitmap(die, (float)(canvas.getWidth() * 0.915), (float)(canvas.getHeight() * 0.43), null);
			double scale = .75;
			int left = (int)(canvas.getWidth() * 0.915);
			int top = (int)(canvas.getHeight() * 0.425);
			int right = left + (int)(die.getWidth() * scale);
			int bottom = top + (int)(die.getHeight() * scale);
			Rect dest = new Rect(left, top, right, bottom);
			canvas.drawBitmap(die, null, dest, null);
		}
		
		die = diceBitmaps.get(getWhiteResourceId(game.getW2DieValue()));
		if (die != null) {
			canvas.drawBitmap(die, (float)(canvas.getWidth() * 0.79296875), (float)(canvas.getHeight() * 0.43), null);
		} else {
			die = diceBitmaps.get(getWhiteResourceId(1));
			//canvas.drawBitmap(die, (float)(canvas.getWidth() * 0.915), (float)(canvas.getHeight() * 0.51), null);
			double scale = .75;
			int left = (int)(canvas.getWidth() * 0.915);
			int top = (int)(canvas.getHeight() * 0.505);
			int right = left + (int)(die.getWidth() * scale);
			int bottom = top + (int)(die.getHeight() * scale);
			Rect dest = new Rect(left, top, right, bottom);
			canvas.drawBitmap(die, null, dest, null);
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
