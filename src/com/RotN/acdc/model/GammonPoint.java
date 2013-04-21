package com.RotN.acdc.model;

import java.util.Map;

import com.RotN.acdc.logic.CheckerContainer;
import com.RotN.acdc.logic.CheckerContainer.BoardPositions;
import com.RotN.acdc.logic.CheckerContainer.GameColor;
import com.RotN.acdc.logic.TheGameImpl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.MotionEvent;

public class GammonPoint {
	//private static final String TAG = GammonPoint.class.getSimpleName();

	private boolean isSelected; 
	private boolean isPossibleMove;
	private boolean isAIOrigMove;
	private boolean isAINextMove;
	private Rect pointRect;
	private CheckerContainer.BoardPositions pointPos;
	private Point animateStart = new Point();
	private Point animateStop = new Point();
	private boolean floatingPiece = false;
	
	public void setFloatingPiece(boolean floatingPiece) {
		this.floatingPiece = floatingPiece;
	}

	int pointWidth;
	public int getPointWidth() {
		return pointWidth;
	}

	int bitmapWidth;	
	int left;
	int pieceHeight;
	
	public GammonPoint(CheckerContainer.BoardPositions pointPos, Rect boardRect) {
		this.isSelected = false;
		this.isPossibleMove = false;
		this.pointPos = pointPos;
		this.isAINextMove = false;
		this.isAIOrigMove = false;
		
		SetPointRect(boardRect);
		pieceHeight = pointWidth;
	}	

	public CheckerContainer.BoardPositions getPointPos() {
		return pointPos;
	}
	
	public boolean wasTouched(float eventX, float eventY) {
		boolean wasTouched = false;
		
		int leftCheck = pointRect.left;
		int rightCheck = pointRect.right;
		int topCheck = pointRect.top;
		int bottomCheck = pointRect.bottom;
	
		if ((eventX >= leftCheck) && (eventX <= rightCheck)) {
			if (eventY >= topCheck && (eventY <= bottomCheck)) {
				wasTouched = true;
			}
		}
		
		return wasTouched;
	}
	
	public void wasTouched(Map<BoardPositions, Double> pointDistances, float eventX, float eventY, int mouseAction, TheGameImpl gammon) {
		int leftCheck = pointRect.left;
		int rightCheck = pointRect.right;
		int topCheck = pointRect.top;
		int bottomCheck = pointRect.bottom;
		
		if (MotionEvent.ACTION_UP == mouseAction) {
			if (isPossibleMove) {
				leftCheck = (int) (pointRect.left - pointRect.width());
				rightCheck = (int) (pointRect.right + pointRect.width());
				topCheck = (int) (pointRect.top - pointRect.width());
				bottomCheck = (int) (pointRect.bottom + pointRect.width());
			}
		}
		
		
		if (MotionEvent.ACTION_DOWN == mouseAction)  { //only check the mouse down if they is a movable piece
			if (false == gammon.onPokey()) {
				if ( (gammon.getTurn() == GameColor.BLACK && gammon.getContainer(pointPos).getBlackCheckerCount() > 0) ||
						(gammon.getTurn() == GameColor.WHITE && gammon.getContainer(pointPos).getWhiteCheckerCount() > 0) ) {
					leftCheck = (int) (pointRect.left - pointRect.width());
					rightCheck = (int) (pointRect.right + pointRect.width());
					topCheck = (int) (pointRect.top - pointRect.width());
					bottomCheck = (int) (pointRect.bottom + pointRect.width());	
					
				} 
			} 
		} 
		
		if ((eventX >= leftCheck) && (eventX <= rightCheck)) {
			if (eventY >= topCheck && (eventY <= bottomCheck)) {
				double xDistance = eventX - pointRect.exactCenterX();
				double yDistance = eventY - pointRect.exactCenterY();
				double distance = Math.sqrt( (xDistance * xDistance) + (yDistance * yDistance) );
				pointDistances.put(pointPos, distance);
			}
		}	
	}
	
	public void setSelected(boolean selected) {
		isSelected = selected;
	}
	
	public void setAIOrigMove(boolean origMove) {
		isAIOrigMove = origMove;
	}
	
	public void setAINextMove(boolean nextMove) {
		isAINextMove = nextMove;
	}
	
	public void setPossibleMove(boolean possibleMove) {
		this.isPossibleMove = possibleMove;
	}
	
	// the draw method which draws the corresponding frame
	public void draw(Canvas canvas, SparseArray<Bitmap> pieceBitmaps, CheckerContainer pointData) {	
		
		pointWidth = pointRect.right - pointRect.left;
		bitmapWidth = pieceBitmaps.get(1).getWidth();
		pieceHeight = pieceBitmaps.get(1).getHeight();
		
		left = pointRect.left + (pointWidth - bitmapWidth) / 2;

		int count = 0;
		if (pointData.getBlackCheckerCount() > 0) {
			count = pointData.getBlackCheckerCount();
		} else {
			count = pointData.getWhiteCheckerCount();
		}
		
		if (floatingPiece) {
			count--;
		}
		
		drawTriangleShading(canvas);	
		
		if (pointPos == BoardPositions.POINT_13 ||
				pointPos == BoardPositions.POINT_14 ||
				pointPos == BoardPositions.POINT_15 ||
				pointPos == BoardPositions.POINT_16 ||
				pointPos == BoardPositions.POINT_17 ||
				pointPos == BoardPositions.POINT_18 ||
				pointPos == BoardPositions.POINT_19 ||
				pointPos == BoardPositions.POINT_20 ||
				pointPos == BoardPositions.POINT_21 ||
				pointPos == BoardPositions.POINT_22 ||
				pointPos == BoardPositions.POINT_23 ||
				pointPos == BoardPositions.POINT_24) {
			int startPos = pointRect.top;
			
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
			
			calculateAnimatePoints(count, true);
		}
		else {
			int startPos = pointRect.bottom;
			
			//first piece
			if (count >= 11) { //requires 3 initial pieces
				startPos = startPos - pieceBitmaps.get(3).getHeight();
				canvas.drawBitmap(pieceBitmaps.get(3), left, startPos, null);
			} else if (count >= 6){
				startPos = startPos - pieceBitmaps.get(2).getHeight();
				canvas.drawBitmap(pieceBitmaps.get(2), left, startPos, null);
			}
			else if (count > 0){
				startPos = startPos - pieceBitmaps.get(1).getHeight();
				canvas.drawBitmap(pieceBitmaps.get(1), left, startPos, null);
			}
			
			//second piece
			if (count >= 12) { //requires 3 second pieces
				startPos = startPos - pieceBitmaps.get(3).getHeight();
				canvas.drawBitmap(pieceBitmaps.get(3), left, startPos, null);
			} else if (count >= 7){
				startPos = startPos - pieceBitmaps.get(2).getHeight();
				canvas.drawBitmap(pieceBitmaps.get(2), left, startPos, null);
			}
			else if (count > 1){
				startPos = startPos - pieceBitmaps.get(1).getHeight();
				canvas.drawBitmap(pieceBitmaps.get(1), left, startPos, null);
			}
			
			//third piece
			if (count >= 13) { //requires 3 third pieces
				startPos = startPos - pieceBitmaps.get(3).getHeight();
				canvas.drawBitmap(pieceBitmaps.get(3), left, startPos, null);
			} else if (count >= 8){
				startPos = startPos - pieceBitmaps.get(2).getHeight();
				canvas.drawBitmap(pieceBitmaps.get(2), left, startPos, null);
			}
			else if (count > 2){
				startPos = startPos - pieceBitmaps.get(1).getHeight();
				canvas.drawBitmap(pieceBitmaps.get(1), left, startPos, null);
			}
			
			//fourth piece
			if (count >= 14) { //requires 3 fourth pieces
				startPos = startPos - pieceBitmaps.get(3).getHeight();
				canvas.drawBitmap(pieceBitmaps.get(3), left, startPos, null);
			} else if (count >= 9){
				startPos = startPos - pieceBitmaps.get(2).getHeight();
				canvas.drawBitmap(pieceBitmaps.get(2), left, startPos, null);
			}
			else if (count > 3){
				startPos = startPos - pieceBitmaps.get(1).getHeight();
				canvas.drawBitmap(pieceBitmaps.get(1), left, startPos, null);
			}
			
			//fifth piece
			if (count == 15) { 
				startPos = startPos - pieceBitmaps.get(3).getHeight();
				canvas.drawBitmap(pieceBitmaps.get(3), left, startPos, null);
			} else if (count >= 10){
				startPos = startPos - pieceBitmaps.get(2).getHeight();
				canvas.drawBitmap(pieceBitmaps.get(2), left, startPos, null);
			}
			else if (count > 4){
				startPos = startPos - pieceBitmaps.get(1).getHeight();
				canvas.drawBitmap(pieceBitmaps.get(1), left, startPos, null);
			}
			
			calculateAnimatePoints(count, false);
		}	
	}
	
	private void calculateAnimatePoints(int count, boolean upperPoints) {
		int offset = pieceHeight;
		int startY = pointRect.top;
		if (false == upperPoints) {
			offset *= -1;
			startY = pointRect.bottom - pieceHeight;
		}
		animateStop.x = pointRect.centerX();
		animateStart.x = pointRect.centerX();
		switch (count) {
		case 5:
		case 10:
		case 15:
			animateStop.y = startY;
			animateStart.y = startY + offset * 4;
		case 0:
			animateStart.y = startY;
			animateStop.y = startY;
			break;
		case 1:
		case 6:
		case 11:
			animateStart.y = startY;
			animateStop.y = startY + offset;
			break;
		case 2:
		case 7:
		case 12:
			animateStart.y = startY + offset;
			animateStop.y = startY + offset * 2;
			break;
		case 3:
		case 8:
		case 13:
			animateStart.y = startY + offset * 2;
			animateStop.y = startY + offset * 3;
			break;
		case 4:
		case 9:
		case 14:
			animateStart.y = startY + offset * 3;
			animateStop.y = startY + offset * 4;
			break;
		}
		
		animateStart.y += (pieceHeight / 2);
		animateStop.y += (pieceHeight / 2);
	}
	
	public Point getAnimateStart() {
		return animateStart;
	}

	public Point getAnimateStop() {
		return animateStop;
	}

	private void SetPointRect(Rect boardRect) //pass in the board rect
	{
		int left = 0, top = 0, right = 0, bottom = 0;
		switch (pointPos) {
		case POINT_1:
			left = (int)(boardRect.width() * .1044);
			top = (int)(boardRect.height() * .58);
			right = (int)(boardRect.width() * .1649);
			bottom = (int)(boardRect.height() * .96);
			break;
		case POINT_10:
			left = (int)(boardRect.width() * .7128);
			top = (int)(boardRect.height() * .58);
			right = (int)(boardRect.width() * .7714);
			bottom = (int)(boardRect.height() * .96);
			break;
		case POINT_11:
			left = (int)(boardRect.width() * .7714);
			top = (int)(boardRect.height() * .58);
			right = (int)(boardRect.width() * .8339);
			bottom = (int)(boardRect.height() * .96);
			break;
		case POINT_12:
			left = (int)(boardRect.width() * .8339);
			top = (int)(boardRect.height() * .58);
			right = (int)(boardRect.width() * .8955);
			bottom = (int)(boardRect.height() * .96);
			break;
		case POINT_13:
			left = (int)(boardRect.width() * .8339);
			top = (int)(boardRect.height() * .04);
			right = (int)(boardRect.width() * .8955);
			bottom = (int)(boardRect.height() * .42);
			break;
		case POINT_14:
			left = (int)(boardRect.width() * .7714);
			top = (int)(boardRect.height() * .04);
			right = (int)(boardRect.width() * .8339);
			bottom = (int)(boardRect.height() * .42);
			break;
		case POINT_15:
			left = (int)(boardRect.width() * .7128);
			top = (int)(boardRect.height() * .04);
			right = (int)(boardRect.width() * .7714);
			bottom = (int)(boardRect.height() * .42);
			break;
		case POINT_16:
			left = (int)(boardRect.width() * .6523);
			top = (int)(boardRect.height() * .04);
			right = (int)(boardRect.width() * .7128);
			bottom = (int)(boardRect.height() * .42);
			break;
		case POINT_17:
			left = (int)(boardRect.width() * .5907);
			top = (int)(boardRect.height() * .04);
			right = (int)(boardRect.width() * .6523);
			bottom = (int)(boardRect.height() * .42);
			break;
		case POINT_18:
			left = (int)(boardRect.width() * .5302);
			top = (int)(boardRect.height() * .04);
			right = (int)(boardRect.width() * .5908);
			bottom = (int)(boardRect.height() * .42);
			break;
		case POINT_19:
			left = (int)(boardRect.width() * .4072);
			top = (int)(boardRect.height() * .04);
			right = (int)(boardRect.width() * .4677);
			bottom = (int)(boardRect.height() * .42);
			break;
		case POINT_2:
			left = (int)(boardRect.width() * .1650);
			top = (int)(boardRect.height() * .58);
			right = (int)(boardRect.width() * .2265);
			bottom = (int)(boardRect.height() * .96);
			break;
		case POINT_20:
			left = (int)(boardRect.width() * .3466);
			top = (int)(boardRect.height() * .04);
			right = (int)(boardRect.width() * .4072);
			bottom = (int)(boardRect.height() * .42);
			break;
		case POINT_21:
			left = (int)(boardRect.width() * .2861);
			top = (int)(boardRect.height() * .04);
			right = (int)(boardRect.width() * .3466);
			bottom = (int)(boardRect.height() * .42);
			break;
		case POINT_22:
			left = (int)(boardRect.width() * .2265);
			top = (int)(boardRect.height() * .04);
			right = (int)(boardRect.width() * .2861);
			bottom = (int)(boardRect.height() * .42);
			break;
		case POINT_23:
			left = (int)(boardRect.width() * .165);
			top = (int)(boardRect.height() * .04);
			right = (int)(boardRect.width() * .223);
			bottom = (int)(boardRect.height() * .42);
			break;
		case POINT_24:
			left = (int)(boardRect.width() * .107);
			top = (int)(boardRect.height() * .04);
			right = (int)(boardRect.width() * .165);
			bottom = (int)(boardRect.height() * .42);
			break;
		case POINT_3:
			left = (int)(boardRect.width() * .2265);
			top = (int)(boardRect.height() * .58);
			right = (int)(boardRect.width() * .2861);
			bottom = (int)(boardRect.height() * .96);
			break;
		case POINT_4:
			left = (int)(boardRect.width() * .2861);
			top = (int)(boardRect.height() * .58);
			right = (int)(boardRect.width() * .3466);
			bottom = (int)(boardRect.height() * .96);
			break;
		case POINT_5:
			left = (int)(boardRect.width() * .3466);
			top = (int)(boardRect.height() * .58);
			right = (int)(boardRect.width() * .4072);
			bottom = (int)(boardRect.height() * .96);
			break;
		case POINT_6:
			left = (int)(boardRect.width() * .4072);
			top = (int)(boardRect.height() * .58);
			right = (int)(boardRect.width() * .4677);
			bottom = (int)(boardRect.height() * .96);
			break;
		case POINT_7:
			left = (int)(boardRect.width() * .5302);
			top = (int)(boardRect.height() * .58);
			right = (int)(boardRect.width() * .5908);
			bottom = (int)(boardRect.height() * .96);
			break;
		case POINT_8:
			left = (int)(boardRect.width() * .5907);
			top = (int)(boardRect.height() * .58);
			right = (int)(boardRect.width() * .6523);
			bottom = (int)(boardRect.height() * .96);
			break;
		case POINT_9:
			left = (int)(boardRect.width() * .6523);
			top = (int)(boardRect.height() * .58);
			right = (int)(boardRect.width() * .7128);
			bottom = (int)(boardRect.height() * .96);
			break;
		default:
			break; 
		}		

		pointRect = new Rect(left, top, right, bottom);			
	}
	
	private void drawTriangleShading(Canvas canvas) {
		if (this.isPossibleMove || this.isSelected || 
				this.isAINextMove || this.isAIOrigMove) {
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			if (this.isAINextMove) {
				paint.setARGB(200, 0, 0, 0);
			} else if (this.isAIOrigMove) {
				paint.setARGB(200, 0, 255, 0);
			}
			else if (this.isSelected) {				
				paint.setARGB(200, 255, 255, 255);
			}
			else if (this.isPossibleMove){
				int transparent = 200;
				if (pointPos.getIndex() % 2 == 0) {
					paint.setARGB(transparent, 255, 0, 0);
				} else {
					paint.setARGB(transparent, 255, 215, 0);
				}
			}		
			else {
				paint.setARGB(100, 255, 255, 255);
			}
	
		    paint.setStrokeWidth(2);   
		    paint.setStyle(Paint.Style.FILL_AND_STROKE);
		    paint.setAntiAlias(true);
	
		    Point point1_draw = new Point();        
		    Point point2_draw = new Point();    
		    Point point3_draw = new Point();
		    
		    //determine the direction the triangle goes
		    if (pointPos == BoardPositions.POINT_13 ||
					pointPos == BoardPositions.POINT_14 ||
					pointPos == BoardPositions.POINT_15 ||
					pointPos == BoardPositions.POINT_16 ||
					pointPos == BoardPositions.POINT_17 ||
					pointPos == BoardPositions.POINT_18 ||
					pointPos == BoardPositions.POINT_19 ||
					pointPos == BoardPositions.POINT_20 ||
					pointPos == BoardPositions.POINT_21 ||
					pointPos == BoardPositions.POINT_22 ||
					pointPos == BoardPositions.POINT_23 ||
					pointPos == BoardPositions.POINT_24)
		    {
		    	point1_draw.x = pointRect.left;
		    	point1_draw.y = pointRect.top;
		    	point2_draw.x = pointRect.right;
		    	point2_draw.y = pointRect.top;
		    	point3_draw.x = (pointRect.left + pointRect.right) / 2;
		    	point3_draw.y = pointRect.bottom;
		    }
		    else
		    {
		    	point1_draw.x = pointRect.left;
		    	point1_draw.y = pointRect.bottom;
		    	point2_draw.x = pointRect.right;
		    	point2_draw.y = pointRect.bottom;
		    	point3_draw.x = (pointRect.left + pointRect.right) / 2;
		    	point3_draw.y = pointRect.top;
		    }
	
		    Path path = new Path();
		    path.setFillType(Path.FillType.EVEN_ODD);
		    path.moveTo(point1_draw.x,point1_draw.y);
		    path.lineTo(point2_draw.x,point2_draw.y);
		    path.lineTo(point3_draw.x,point3_draw.y);
		    path.lineTo(point1_draw.x,point1_draw.y);
		    path.close();
	
		    canvas.drawPath(path, paint);
		    
		    Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		    borderPaint.setARGB(255, 255, 255, 255);
		    borderPaint.setStrokeWidth(4);
		    borderPaint.setStyle(Paint.Style.STROKE);
		    canvas.drawLine(point1_draw.x, point1_draw.y, point2_draw.x, point2_draw.y, borderPaint);
		    canvas.drawLine(point2_draw.x, point2_draw.y, point3_draw.x, point3_draw.y, borderPaint);
		    canvas.drawLine(point1_draw.x, point1_draw.y, point3_draw.x, point3_draw.y, borderPaint);
		}
	}
}
