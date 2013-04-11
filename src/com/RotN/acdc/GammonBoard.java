package com.RotN.acdc;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import com.RotN.acdc.logic.AcDcAI;
import com.RotN.acdc.logic.CheckerContainer;
import com.RotN.acdc.logic.CheckerContainer.BoardPositions;
import com.RotN.acdc.logic.CheckerContainer.GameColor;
import com.RotN.acdc.logic.Move;
import com.RotN.acdc.logic.TheGame;
import com.RotN.acdc.logic.TheGameImpl;
import com.RotN.acdc.model.Bunker;
import com.RotN.acdc.model.Dice;
import com.RotN.acdc.model.GammonPoint;
import com.RotN.acdc.model.Piece;
import com.RotN.acdc.model.Pokey;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GammonBoard extends SurfaceView implements
SurfaceHolder.Callback {
	
	//private Piece piece;
	private Bitmap board;	
	private Bunker blackBunker;
	private Bunker whiteBunker;
	private Pokey pokey;
	private Dice dice;
	private TheGameImpl beerGammon;
	private Map<BoardPositions, GammonPoint> boardPoints;
	private SparseArray<Bitmap> pieceWhiteBitmaps;
	private SparseArray<Bitmap> pieceBlackBitmaps;
	private BoardPositions selectedPosition;
	private Piece floatingPiece;
	private Piece animateWhite;
	private Piece animateBlack;
	private boolean clickToMove = false;
	Context fileContext;

	public String getWhiteValue() {
		AcDcAI ai = new AcDcAI();
		return ai.evaluateBoardWhitePerspective(beerGammon.getGammonData()).toString();
	}

	public String getBlackValue() {
		AcDcAI ai = new AcDcAI();
		return ai.evaluateBoardBlackPerspective(beerGammon.getGammonData()).toString();
	}

	public GammonBoard(Context context) {
		super(context);
		
		fileContext = context;
		
		initGammonBoard();
	}
	
	public GammonBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		fileContext = context;
		
		initGammonBoard();
	}
	
	public GammonBoard(Context context, AttributeSet ats, int defaultStyle) {
		super(context, ats, defaultStyle);
		
		fileContext = context;
		
		initGammonBoard();
	}
	
	protected void initGammonBoard() {
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
		
		beerGammon = new TheGameImpl();
		beerGammon.setFileContext(fileContext);
	}
	
	private static final String TAG = GammonBoard.class.getSimpleName();
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
		int height) {
		buildOurSurface();
	}
	
	private void buildOurSurface() {
						
		selectedPosition = BoardPositions.NONE;
		
		//determine the scale we're working with
		BitmapFactory.Options options = getImageOutSize(getResources(), R.drawable.background);
		float scale = (float) options.outHeight/ (float) getHeight();
		createPieceBitmapArray(scale);
		createBoardPoints(getWidth(), getHeight());
		float diceScale = (float) (scale / 1.5);
		dice = new Dice(createDiceBitmapArray(diceScale));
		Rect boardRect = new Rect(0,0, getWidth(), getHeight());
		
		//create the bunkers
		options = getImageOutSize(getResources(), R.drawable.red_thin_checker);
		int newWidth = Math.round(options.outWidth/scale);
		int newHeight = Math.round(options.outHeight/scale);
		Bitmap bitmapBlackBunker = getImageExactSize(getResources(), R.drawable.red_thin_checker, newWidth, newHeight);
		blackBunker = new Bunker(GameColor.BLACK, bitmapBlackBunker, boardRect);
		Bitmap bitmapWhiteBunker = getImageExactSize(getResources(), R.drawable.white_thin_checker, newWidth, newHeight);
		whiteBunker = new Bunker(GameColor.WHITE, bitmapWhiteBunker, boardRect);
		
		pokey = new Pokey(new Rect(0, 0, getWidth(), getHeight()), pieceBlackBitmaps, pieceWhiteBitmaps);
		
		render();		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredHeight = measureHeight(heightMeasureSpec);
		
		double ratio = 1280.0 / 768.0;		
		
		setMeasuredDimension((int)(measuredHeight * ratio), measuredHeight);
	}
	
	private int measureHeight(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		
		//Default size if no limits are specified
		int result = 500;
		
		if (specMode == MeasureSpec.AT_MOST) {
			//Calculate the ideal size of your
			// control within this maximum size.
			// If your control fills the available
			// space return the outer bound.
			result = specSize;
		} else if (specMode == MeasureSpec.EXACTLY ) {
			// if your control can fit within these bounds return that value
			result = specSize;
		}
		
		return result;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		board = decodeSampledBitmapFromResource(getResources(), R.drawable.background, getWidth(), getHeight());
		buildOurSurface();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			boardTouchDown(event);
		} if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (floatingPiece.isTouched()) {
				// the piece was picked up and is being dragged
				floatingPiece.setX((int)event.getX());
				floatingPiece.setY((int)event.getY());
			}			
			
		} if (event.getAction() == MotionEvent.ACTION_UP) {	
			floatingPiece.setTouched(false);
			
			if (selectedPosition != BoardPositions.NONE) {

				Map<BoardPositions, Double> containerDistances = new HashMap<BoardPositions, Double>();
				Log.d(TAG, "Up Event, selected position: " + selectedPosition);
				
				whiteBunker.wasTouched(containerDistances, event.getX(), event.getY());
				
				blackBunker.wasTouched(containerDistances, event.getX(), event.getY());	
				
				Log.d(TAG, "We are checking points");
				Set<Entry<BoardPositions, GammonPoint>> set = boardPoints.entrySet();
				Iterator<Entry<BoardPositions, GammonPoint>> it = set.iterator();
				while (it.hasNext()) {
					Map.Entry<BoardPositions, GammonPoint> m = (Map.Entry<BoardPositions, GammonPoint>)it.next();
					m.getValue().wasTouched(containerDistances, event.getX(), event.getY(), MotionEvent.ACTION_UP, beerGammon);
				}
				
				BoardPositions upContainer = findTheClosestContainer(containerDistances);
				if (upContainer == selectedPosition) {
					this.clickToMove = true;
				} else if (upContainer != BoardPositions.NONE) {
					ArrayList<Move> moves = beerGammon.movePiece(selectedPosition, upContainer);
					
					if (this.clickToMove){
						animateMoves(moves);
					}
					
					selectedPosition = BoardPositions.NONE;
					clearSelectedSpot();
					clearPossibleMoves();
					this.clickToMove = false;
				}
			}
		}
		render();
		
		return true;
	}
	
	private BoardPositions findTheClosestContainer(Map<BoardPositions, Double> pointDistances) {
		BoardPositions closestBoardPosition = BoardPositions.NONE;
		double xDistance = 0;
		Set<Entry<BoardPositions, Double>> set = pointDistances.entrySet();
		Iterator<Entry<BoardPositions, Double>> it = set.iterator();
		while (it.hasNext()) {
			Map.Entry<BoardPositions, Double> m = (Map.Entry<BoardPositions, Double>)it.next();
			if (closestBoardPosition == BoardPositions.NONE) {
				closestBoardPosition = m.getKey();
				xDistance = m.getValue();
			} else if (m.getValue() < xDistance) {
				closestBoardPosition = m.getKey();
				xDistance = m.getValue();
			}
		}
		
		return closestBoardPosition;
	}
	
	private void boardTouchDown(MotionEvent event) {
		floatingPiece.setX((int)event.getX());
		floatingPiece.setY((int)event.getY());
		
		Map<BoardPositions, Double> containerDistances = new HashMap<BoardPositions, Double>();
		
		whiteBunker.wasTouched(containerDistances, event.getX(), event.getY());
		
		blackBunker.wasTouched(containerDistances, event.getX(), event.getY());	
		
		pokey.wasTouched(containerDistances, event.getX(), event.getY(), beerGammon.onPokey());
		
		Log.d(TAG, "We are checking points");
		Set<Entry<BoardPositions, GammonPoint>> set = boardPoints.entrySet();
		Iterator<Entry<BoardPositions, GammonPoint>> it = set.iterator();
		while (it.hasNext()) {
			Map.Entry<BoardPositions, GammonPoint> m = (Map.Entry<BoardPositions, GammonPoint>)it.next();
			m.getValue().wasTouched(containerDistances, event.getX(), event.getY(), MotionEvent.ACTION_DOWN, beerGammon);
		}
		
		BoardPositions downContainer = findTheClosestContainer(containerDistances);
		
		if (downContainer == selectedPosition) {
			selectedPosition = BoardPositions.NONE;
			clearSelectedSpot();
			clearPossibleMoves();
		} else if (selectedPosition != BoardPositions.NONE) {
			//don't set anything, we already have a selected position
		} else {
		
			if (downContainer == BoardPositions.WHITE_BUNKER) {
				whiteBunker.setSelected(true);
				selectedPosition = BoardPositions.WHITE_BUNKER;
				if (whiteBunker.getBunkerCount() > 0) {
					floatingPiece.setColor(GameColor.WHITE);
					floatingPiece.setTouched(true);
				}
			} else if (BoardPositions.BLACK_BUNKER == downContainer){
				blackBunker.setSelected(true);
				selectedPosition = BoardPositions.BLACK_BUNKER;
				if (blackBunker.getBunkerCount() > 0) {
					floatingPiece.setColor(GameColor.BLACK);
					floatingPiece.setTouched(true);
				}
			} else if (BoardPositions.POKEY == downContainer) {
				selectedPosition = BoardPositions.POKEY;
				pokey.setSelected(true);
				CheckerContainer pokeyData = beerGammon.getContainer(BoardPositions.POKEY);
				if (beerGammon.getTurn() == GameColor.BLACK && pokeyData.getBlackCheckerCount() > 0) {
					floatingPiece.setColor(GameColor.BLACK);
					floatingPiece.setTouched(true);
				} else if (beerGammon.getTurn() == GameColor.WHITE && pokeyData.getWhiteCheckerCount() > 0) {
					floatingPiece.setColor(GameColor.WHITE);
					floatingPiece.setTouched(true);
				}
			} else if (BoardPositions.NONE != downContainer){
				boardPoints.get(downContainer).setSelected(true);
				selectedPosition = downContainer;
				CheckerContainer pointData = beerGammon.getContainer(selectedPosition);
				if (beerGammon.getTurn() == GameColor.BLACK && pointData.getBlackCheckerCount() > 0) {
					floatingPiece.setColor(GameColor.BLACK);
					floatingPiece.setTouched(true);
				} else if (beerGammon.getTurn() == GameColor.WHITE && pointData.getWhiteCheckerCount() > 0) {
					floatingPiece.setColor(GameColor.WHITE);
					floatingPiece.setTouched(true);
				}
			}
		}
		
		updatePossibleMoves();
	}
	
	public void render() {
		blackBunker.setBunkerCount(beerGammon.getBlackBunkerCount());
		whiteBunker.setBunkerCount(beerGammon.getWhiteBunkerCount());
		
		pokey.setTurn(beerGammon.getTurn());
		
		Canvas canvas = getHolder().lockCanvas();
		if (null == board) {
			board = decodeSampledBitmapFromResource(getResources(), R.drawable.background, getWidth(), getHeight());
		}
		canvas.drawBitmap(board, null, new Rect(0,0,getWidth(), getHeight()), null);
		blackBunker.draw(canvas, floatingPiece.isTouched());
		whiteBunker.draw(canvas, floatingPiece.isTouched());
		CheckerContainer pokeyData = beerGammon.getContainer(BoardPositions.POKEY);
		pokey.draw(canvas, pokeyData, floatingPiece.isTouched());
						
		renderHighBoardPoints(canvas);
		dice.draw(canvas, beerGammon);
		renderLowBoardPoints(canvas);
		
		floatingPiece.draw(canvas);
		animateWhite.setTouched(animateBlack.isTouched());
		animateBlack.setTouched(!animateWhite.isTouched());
		animateWhite.draw(canvas);
		animateBlack.draw(canvas);
		
		if (canvas != null) {
			getHolder().unlockCanvasAndPost(canvas);
		}		
	}
	
	private void updatePossibleMoves() {
		clearPossibleMoves();
		if (selectedPosition != BoardPositions.NONE) {
			Vector<BoardPositions> possibleMoves = beerGammon.getPossibleMoves(selectedPosition, true);
			
			for (int index = 0; index < possibleMoves.size(); index++) {
				if (possibleMoves.get(index) == BoardPositions.BLACK_BUNKER)
				{
					blackBunker.setPossibleMove(true);
				} else if (possibleMoves.get(index) == BoardPositions.WHITE_BUNKER) {
					whiteBunker.setPossibleMove(true);
				} else if (possibleMoves.get(index) == BoardPositions.POKEY) {
					//do nothing how the fuck did we get here?
				} else {
					GammonPoint temp = boardPoints.get(possibleMoves.get(index));
					if (temp != null) {
						temp.setPossibleMove(true);
					}
				}
			}
		}
	}
	
	private void createBoardPoints(int boardWidth, int boardHeight)
	{
		boardPoints = new HashMap<BoardPositions, GammonPoint>();
		boardPoints.put(CheckerContainer.BoardPositions.POINT_1, new GammonPoint(CheckerContainer.BoardPositions.POINT_1, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_2, new GammonPoint(CheckerContainer.BoardPositions.POINT_2, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_3, new GammonPoint(CheckerContainer.BoardPositions.POINT_3, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_4, new GammonPoint(CheckerContainer.BoardPositions.POINT_4, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_5, new GammonPoint(CheckerContainer.BoardPositions.POINT_5, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_6, new GammonPoint(CheckerContainer.BoardPositions.POINT_6, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_7, new GammonPoint(CheckerContainer.BoardPositions.POINT_7, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_8, new GammonPoint(CheckerContainer.BoardPositions.POINT_8, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_9, new GammonPoint(CheckerContainer.BoardPositions.POINT_9, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_10, new GammonPoint(CheckerContainer.BoardPositions.POINT_10, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_11, new GammonPoint(CheckerContainer.BoardPositions.POINT_11, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_12, new GammonPoint(CheckerContainer.BoardPositions.POINT_12, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_13, new GammonPoint(CheckerContainer.BoardPositions.POINT_13, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_14, new GammonPoint(CheckerContainer.BoardPositions.POINT_14, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_15, new GammonPoint(CheckerContainer.BoardPositions.POINT_15, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_16, new GammonPoint(CheckerContainer.BoardPositions.POINT_16, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_17, new GammonPoint(CheckerContainer.BoardPositions.POINT_17, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_18, new GammonPoint(CheckerContainer.BoardPositions.POINT_18, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_19, new GammonPoint(CheckerContainer.BoardPositions.POINT_19, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_20, new GammonPoint(CheckerContainer.BoardPositions.POINT_20, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_21, new GammonPoint(CheckerContainer.BoardPositions.POINT_21, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_22, new GammonPoint(CheckerContainer.BoardPositions.POINT_22, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_23, new GammonPoint(CheckerContainer.BoardPositions.POINT_23, new Rect(0,0, boardWidth, boardHeight)));
		boardPoints.put(CheckerContainer.BoardPositions.POINT_24, new GammonPoint(CheckerContainer.BoardPositions.POINT_24, new Rect(0,0, boardWidth, boardHeight)));
		
	}
	
	private SparseArray<Bitmap> createDiceBitmapArray(float scale) {
		SparseArray<Bitmap> diceBitmaps = new SparseArray<Bitmap>();
		
		BitmapFactory.Options options = getImageOutSize(getResources(), R.drawable.red_dice1);
		int newWidth = Math.round(options.outWidth/scale);
		int newHeight = Math.round(options.outHeight/scale);
		
		diceBitmaps.put(R.drawable.red_dice1, getImageExactSize(getResources(), R.drawable.red_dice1, newWidth, newHeight));
		diceBitmaps.put(R.drawable.red_dice2, getImageExactSize(getResources(), R.drawable.red_dice2, newWidth, newHeight));
		diceBitmaps.put(R.drawable.red_dice3, getImageExactSize(getResources(), R.drawable.red_dice3, newWidth, newHeight));
		diceBitmaps.put(R.drawable.red_dice4, getImageExactSize(getResources(), R.drawable.red_dice4, newWidth, newHeight));
		diceBitmaps.put(R.drawable.red_dice5, getImageExactSize(getResources(), R.drawable.red_dice5, newWidth, newHeight));
		diceBitmaps.put(R.drawable.red_dice6, getImageExactSize(getResources(), R.drawable.red_dice6, newWidth, newHeight));
		
		diceBitmaps.put(R.drawable.white_dice1, getImageExactSize(getResources(), R.drawable.white_dice1, newWidth, newHeight));
		diceBitmaps.put(R.drawable.white_dice2, getImageExactSize(getResources(), R.drawable.white_dice2, newWidth, newHeight));
		diceBitmaps.put(R.drawable.white_dice3, getImageExactSize(getResources(), R.drawable.white_dice3, newWidth, newHeight));
		diceBitmaps.put(R.drawable.white_dice4, getImageExactSize(getResources(), R.drawable.white_dice4, newWidth, newHeight));
		diceBitmaps.put(R.drawable.white_dice5, getImageExactSize(getResources(), R.drawable.white_dice5, newWidth, newHeight));
		diceBitmaps.put(R.drawable.white_dice6, getImageExactSize(getResources(), R.drawable.white_dice6, newWidth, newHeight));
		
		return diceBitmaps;
	}
	
	private void createPieceBitmapArray(float scale) {
		pieceWhiteBitmaps = new SparseArray<Bitmap>();
		pieceBlackBitmaps = new SparseArray<Bitmap>();
		
		BitmapFactory.Options options = getImageOutSize(getResources(), R.drawable.red_checker);
		int newWidth = Math.round(options.outWidth/scale);
		int newHeight = Math.round(options.outHeight/scale);
		pieceBlackBitmaps.put(1, getImageExactSize(getResources(), R.drawable.red_checker, newWidth, newHeight));
		pieceWhiteBitmaps.put(1, getImageExactSize(getResources(), R.drawable.white_checker, newWidth, newHeight));
		
		options = getImageOutSize(getResources(), R.drawable.red_checker2);
		newWidth = Math.round(options.outWidth/scale);
		newHeight = Math.round(options.outHeight/scale);
		pieceBlackBitmaps.put(2, getImageExactSize(getResources(), R.drawable.red_checker2, newWidth, newHeight));
		pieceWhiteBitmaps.put(2, getImageExactSize(getResources(), R.drawable.white_checker2, newWidth, newHeight));
		
		options = getImageOutSize(getResources(), R.drawable.red_checker3);
		newWidth = Math.round(options.outWidth/scale);
		newHeight = Math.round(options.outHeight/scale);
		pieceBlackBitmaps.put(3, getImageExactSize(getResources(), R.drawable.red_checker3, newWidth, newHeight));
		pieceWhiteBitmaps.put(3, getImageExactSize(getResources(), R.drawable.white_checker3, newWidth, newHeight));
		
		floatingPiece = new Piece(pieceBlackBitmaps.get(1), pieceWhiteBitmaps.get(1), 0, 0);
		animateWhite = new Piece(pieceBlackBitmaps.get(1), pieceWhiteBitmaps.get(1), 0, 0);
		animateWhite.setColor(GameColor.WHITE);
		animateBlack = new Piece(pieceBlackBitmaps.get(1), pieceWhiteBitmaps.get(1), 0, 0);
		animateBlack.setColor(GameColor.BLACK);		
		animateBlack.setTouched(true);
	}
	
	private void clearSelectedSpot(){
		blackBunker.setSelected(false);
		whiteBunker.setSelected(false);
		pokey.setSelected(false);
		
		Set<Entry<BoardPositions, GammonPoint>> set = boardPoints.entrySet();
		Iterator<Entry<BoardPositions, GammonPoint>> it = set.iterator();
		while (it.hasNext()) {
			Map.Entry<BoardPositions, GammonPoint> m = (Map.Entry<BoardPositions, GammonPoint>)it.next();
			
			m.getValue().setSelected(false);
		}
	}
	
	private void clearPossibleMoves(){
		
		whiteBunker.setPossibleMove(false);
		blackBunker.setPossibleMove(false);
		Set<Entry<BoardPositions, GammonPoint>> set = boardPoints.entrySet();
		Iterator<Entry<BoardPositions, GammonPoint>> it = set.iterator();
		while (it.hasNext()) {
			Map.Entry<BoardPositions, GammonPoint> m = (Map.Entry<BoardPositions, GammonPoint>)it.next();
			
			m.getValue().setPossibleMove(false);
		}
	}
	
	public void saveGame() {
		try {
			TheGame gammonData = beerGammon.getGammonData();
			FileOutputStream stream = fileContext.openFileOutput("gamedata", Context.MODE_PRIVATE);
			ObjectOutputStream objOut = new ObjectOutputStream(stream);
			objOut.writeObject(gammonData);
			objOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadGame() {
		try {
			Log.d(TAG, "Deserial starting");
			FileInputStream fis = fileContext.openFileInput("gamedata");
			ObjectInputStream objIn = new ObjectInputStream(fis);			
			TheGame gammonData = (TheGame) objIn.readObject();
			Log.d(TAG, "Deserial succeeded");
			Log.d(TAG, "Dice B1B2W1W2: " + gammonData.blackDie1 + gammonData.blackDie2 + gammonData.whiteDie1 + gammonData.whiteDie2);
			Log.d(TAG, "Moves remaining: " + gammonData.movesRemaining);
			Log.d(TAG, "Button Text: " + gammonData.buttonState);
			beerGammon.setGammonData(gammonData);
			objIn.close();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void newGame() {
		beerGammon.initializeGame();
	}

	public void setBeerGammon(TheGameImpl beerGammon) {
		this.beerGammon = beerGammon;
		beerGammon.setFileContext(fileContext);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	        if (width > height) {
	            inSampleSize = Math.round((float)height / (float)reqHeight);
	        } else {
	            inSampleSize = Math.round((float)width / (float)reqWidth);
	        }
	    }
	    return inSampleSize;
	}
	
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    options.inScaled = false;
	    options.inPreferredConfig = Bitmap.Config.RGB_565;

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}
	
	public static BitmapFactory.Options getImageOutSize(Resources res, int resId) {
		// decode with inJustDecodeBounds=true to check dimensions
	    BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);
	    
	    return options;
	}
	
	public static Bitmap getImageExactSize(Resources res, int resId,
	        int reqWidth, int reqHeight) {
		Bitmap bitmap = decodeSampledBitmapFromResource(res, resId, reqWidth, reqHeight);
		
		if (bitmap.getWidth() != reqWidth ||
				bitmap.getHeight() != reqHeight) {
			return Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false);
		} else {
			return bitmap;
		}
	}
	
	public TheGameImpl getTheGame() {
		return beerGammon;
	}
	
	private void renderHighBoardPoints(Canvas canvas) {
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_24));
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_23));
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_22));
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_21));
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_20));
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_19));
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_18));
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_17));
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_16));
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_15));
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_14));
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_13));		
	}
	
	private void renderLowBoardPoints(Canvas canvas) {
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_12));
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_11));	
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_10));	
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_9));	
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_8));	
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_7));	
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_6));	
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_5));	
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_4));	
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_3));	
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_2));	
		renderBoardPoint(canvas, boardPoints.get(BoardPositions.POINT_1));		}
	
	private void renderBoardPoint(Canvas canvas, GammonPoint point) {
		CheckerContainer container = beerGammon.getContainer(point.getPointPos());
		
		if (container.getWhiteCheckerCount() > 0) {
			point.draw(canvas, pieceWhiteBitmaps, container, floatingPiece.isTouched());				
		}
		else {
			point.draw(canvas, pieceBlackBitmaps, container, floatingPiece.isTouched());
		}
	}
	
	private void animateMoves(ArrayList<Move> moves) {
		AnimationThread draw = new AnimationThread(this, moves);
		draw.start();
	}
	
	public boolean updateAnimatedPieces() {
		boolean animationComplete = false;
		
		return animationComplete;
	}
}