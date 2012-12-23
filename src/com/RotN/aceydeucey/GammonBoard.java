package com.RotN.aceydeucey;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import com.RotN.aceydeucey.logic.CheckerContainer;
import com.RotN.aceydeucey.logic.CheckerContainer.BoardPositions;
import com.RotN.aceydeucey.logic.CheckerContainer.GameColor;
import com.RotN.aceydeucey.logic.TheGame;
import com.RotN.aceydeucey.logic.TheGameImpl;
import com.RotN.aceydeucey.model.Bunker;
import com.RotN.aceydeucey.model.Dice;
import com.RotN.aceydeucey.model.GammonPoint;
import com.RotN.aceydeucey.model.Piece;
import com.RotN.aceydeucey.model.Pokey;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
	private Bitmap scaledBoard;
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
	Context fileContext;

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
	}
	
	private static final String TAG = GammonBoard.class.getSimpleName();
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
		int height) {
		buildOurSurface();
	}
	
	private void buildOurSurface() {
		Bitmap background = decodeSampledBitmapFromResource(getResources(), R.drawable.background, getWidth(), getHeight());
				
		selectedPosition = BoardPositions.NONE;
		
		//determine the scale we're working with
		BitmapFactory.Options options = getImageOutSize(getResources(), R.drawable.background);
		float scale = (float)background.getHeight()/(float)getHeight();
		//scale our board
		int newWidth = Math.round(background.getWidth()/scale);
		int newHeight = Math.round(background.getHeight()/scale);
		scaledBoard = Bitmap.createScaledBitmap(background, newWidth, newHeight, true);
		scale = (float) options.outHeight/ (float) getHeight();
		createPieceBitmapArray(scale);
		createBoardPoints(newWidth, newHeight);
		dice = new Dice(createDiceBitmapArray(scale));
		Rect boardRect = new Rect(0,0, newWidth, newHeight);
		
		//create the bunkers
		options = getImageOutSize(getResources(), R.drawable.red_thin_checker);
		newWidth = Math.round(options.outWidth/scale);
		newHeight = Math.round(options.outHeight/scale);
		Bitmap bitmapBlackBunker = getImageExactSize(getResources(), R.drawable.red_thin_checker, newWidth, newHeight);
		blackBunker = new Bunker(GameColor.BLACK, bitmapBlackBunker, boardRect);
		Bitmap bitmapWhiteBunker = getImageExactSize(getResources(), R.drawable.white_thin_checker, newWidth, newHeight);
		whiteBunker = new Bunker(GameColor.WHITE, bitmapWhiteBunker, boardRect);
		
		pokey = new Pokey(new Rect(0, 0, scaledBoard.getWidth(), scaledBoard.getHeight()), pieceBlackBitmaps, pieceWhiteBitmaps);
		
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
			
			Set<Entry<BoardPositions, GammonPoint>> set = boardPoints.entrySet();
			Iterator<Entry<BoardPositions, GammonPoint>> it = set.iterator();
			while (it.hasNext()) {
				Map.Entry<BoardPositions, GammonPoint> m = (Map.Entry<BoardPositions, GammonPoint>)it.next();
				if (m.getValue().wasTouched(event.getX(), event.getY())) {
					m.getValue().setHovering(true);
			    }
				else {
					m.getValue().setHovering(false);
				}
			}
		} if (event.getAction() == MotionEvent.ACTION_UP) {	
			floatingPiece.setTouched(false);
			
			if (selectedPosition != BoardPositions.NONE) {
			Log.d(TAG, "Up Event, selected position: " + selectedPosition);
				if (whiteBunker.wasTouched(event.getX(), event.getY())) {
					beerGammon.movePiece(selectedPosition, BoardPositions.WHITE_BUNKER);
				} else if (blackBunker.wasTouched(event.getX(), event.getY())) {
					beerGammon.movePiece(selectedPosition, BoardPositions.BLACK_BUNKER);
				} else {					
					Log.d(TAG, "We are checking points");
					Set<Entry<BoardPositions, GammonPoint>> set = boardPoints.entrySet();
					Iterator<Entry<BoardPositions, GammonPoint>> it = set.iterator();
					while (it.hasNext()) {
						Map.Entry<BoardPositions, GammonPoint> m = (Map.Entry<BoardPositions, GammonPoint>)it.next();
						if (m.getValue().wasTouched(event.getX(), event.getY())) {
						   	beerGammon.movePiece(selectedPosition, m.getValue().getPointPos());
						   	break;
						}
					}
					
				}
			}
			selectedPosition = BoardPositions.NONE;
			clearSelectedSpot();
			clearPossibleMoves();
		}
		render();
		
		return true;
	}
	
	private void boardTouchDown(MotionEvent event) {
		floatingPiece.setX((int)event.getX());
		floatingPiece.setY((int)event.getY());
		if (whiteBunker.wasTouched(event.getX(), event.getY())) {
			whiteBunker.setSelected(true);
			selectedPosition = BoardPositions.WHITE_BUNKER;
			if (whiteBunker.getBunkerCount() > 0) {
				floatingPiece.setColor(GameColor.WHITE);
				floatingPiece.setTouched(true);
			}
		} else if (blackBunker.wasTouched(event.getX(), event.getY())){
			blackBunker.setSelected(true);
			selectedPosition = BoardPositions.BLACK_BUNKER;
			if (blackBunker.getBunkerCount() > 0) {
				floatingPiece.setColor(GameColor.BLACK);
				floatingPiece.setTouched(true);
			}
		} else if (pokey.wasTouched(event.getX(), event.getY())) {
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
		} else {
			Set<Entry<BoardPositions, GammonPoint>> set = boardPoints.entrySet();
			Iterator<Entry<BoardPositions, GammonPoint>> it = set.iterator();
			while (it.hasNext()) {
				Map.Entry<BoardPositions, GammonPoint> m = (Map.Entry<BoardPositions, GammonPoint>)it.next();
				if (m.getValue().wasTouched(event.getX(), event.getY())) {
					m.getValue().setSelected(true);
					selectedPosition = m.getValue().getPointPos();
					CheckerContainer pointData = beerGammon.getContainer(selectedPosition);
					if (beerGammon.getTurn() == GameColor.BLACK && pointData.getBlackCheckerCount() > 0) {
						floatingPiece.setColor(GameColor.BLACK);
						floatingPiece.setTouched(true);
					} else if (beerGammon.getTurn() == GameColor.WHITE && pointData.getWhiteCheckerCount() > 0) {
						floatingPiece.setColor(GameColor.WHITE);
						floatingPiece.setTouched(true);
					}
				   break;
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
		canvas.drawColor(Color.GRAY);
		canvas.drawBitmap(scaledBoard, 0, 0, null); // draw the background
		blackBunker.draw(canvas);
		whiteBunker.draw(canvas);
		dice.draw(canvas, beerGammon);
		floatingPiece.draw(canvas);
		CheckerContainer pokeyData = beerGammon.getContainer(BoardPositions.POKEY);
		pokey.draw(canvas, pokeyData);
						
		Set<Entry<BoardPositions, GammonPoint>> set = boardPoints.entrySet();
		Iterator<Entry<BoardPositions, GammonPoint>> it = set.iterator();

		while (it.hasNext()) {
			Map.Entry<BoardPositions, GammonPoint> m = (Map.Entry<BoardPositions, GammonPoint>)it.next();
			CheckerContainer container = beerGammon.getContainer(m.getKey());

			if (container.getWhiteCheckerCount() > 0) {
				m.getValue().draw(canvas, pieceWhiteBitmaps, container);				
			}
			else {
				m.getValue().draw(canvas, pieceBlackBitmaps, container);
			}
		}
		
		if (canvas != null) {
			getHolder().unlockCanvasAndPost(canvas);
		}		
	}
	
	private void updatePossibleMoves() {
		clearPossibleMoves();
		if (selectedPosition != BoardPositions.NONE) {
			Vector<BoardPositions> possibleMoves = beerGammon.getPossibleMoves(selectedPosition);
			
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
			m.getValue().setHovering(false);
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
}