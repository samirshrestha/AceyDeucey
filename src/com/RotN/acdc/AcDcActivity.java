package com.RotN.acdc;

import com.RotN.acdc.logic.TheGame.ButtonState;
import com.RotN.acdc.logic.TheGameImpl;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class AcDcActivity extends Activity implements TheGameImpl.GammonEventHandler {

	/** Called when the activity is first created. */
	
	private static final String TAG = AcDcActivity.class.getSimpleName();
	private TheGameImpl beerGammon;
	GammonBoard board;
	static final int NEW_GAME_REQUEST = 0;
	private SharedPreferences storage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_ACTION_BAR); 
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	storage = getSharedPreferences("GameStorage", Context.MODE_PRIVATE);
    	super.onCreate(savedInstanceState);
	
    }
    
   	@Override
	protected void onStop() {
		Log.d(TAG, "Stopping...");
		closeItDown();
		super.onStop();
	}
   	
   	@Override
   	protected void onPause() {
   		Log.d(TAG, "Pausing...");
		closeItDown();
   		super.onPause();
   	}
	
	@Override
	protected void onStart() {
		Log.d(TAG, "Starting AcDc...");
		startItUp();
		super.onStart();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.gammon_menu, menu);
	    
	    Log.d(TAG, "Menu inflated");
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		board = (GammonBoard)this.findViewById(R.id.gammonBoard);
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.new_game:
	        	Intent settingsIntent = new Intent(this, SettingsActivity.class);
	        	settingsIntent.putExtra("TheGame", beerGammon.getGammonData());
	        	startActivityForResult(settingsIntent, NEW_GAME_REQUEST);
	            return true;
	        case R.id.undo_button:
	        	beerGammon.undoMove();
	        	board.render();
	        	return true;
	        case R.id.directions:
	        	Intent intent = new Intent(this, DirectionsActivity.class);
	        	startActivity(intent);
	        	return true;	
	        case R.id.stats:
	        	Intent statsIntent = new Intent(this, StatsActivity.class);
	        	statsIntent.putExtra("title", (int)0);
	        	statsIntent.putExtra("redStats", beerGammon.getGammonData().redStats);
	        	statsIntent.putExtra("whiteStats", beerGammon.getGammonData().whiteStats);
	        	startActivity(statsIntent);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private void closeItDown() {
		board.saveGame();
		beerGammon.removeListener(this);
	}
	
	private void startItUp() {
		// set our GammonBoard as the View		

        setContentView(R.layout.activity_ac_dc);
        
        board = (GammonBoard)this.findViewById(R.id.gammonBoard);
		Bundle extras = getIntent().getExtras();
    	if (extras != null) {	  
    		if (extras.getBoolean("newGame") == false) {
        		board.loadGame();
        		beerGammon = board.getTheGame();    			
    		} else {
	    		//startNewGame();
	    		board.newGame();
	    		beerGammon = board.getTheGame();
	    		beerGammon.getGammonData().blackHumanPlayer = extras.getBoolean("redPlayerIsHuman");
	    		beerGammon.getGammonData().whiteHumanPlayer = extras.getBoolean("whitePlayerIsHuman");
	    		if(extras.getInt("playMode") == 2){
	    			extras = null;
	    			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
					discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
					startActivity(discoverableIntent);				
	    		}
	    		extras = null;
    		}
    	} else {
    		board.loadGame();
    		beerGammon = board.getTheGame();
    	}
        
        
        beerGammon.addListener(this);
        Log.d(TAG, "View added");
        
        /*actionButton = (Button) findViewById(R.id.action_button);
        actionButton.setText(getButtonText());
        actionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                beerGammon.buttonPushed();
                board.render();
                if (beerGammon.getButtonState() == ButtonState.CLEAR_RED ||
                		beerGammon.getButtonState() == ButtonState.CLEAR_WHITE) {
                	actionButton.setEnabled(beerGammon.canMove() == false);
                	if ( (beerGammon.getTurn() == GameColor.BLACK && false == beerGammon.getGammonData().blackHumanPlayer) ||
                    		(beerGammon.getTurn() == GameColor.WHITE && false == beerGammon.getGammonData().whiteHumanPlayer) ) {
                    	AcDcAI ai = new AcDcAI();
                    	ArrayList<Move> moves = ai.GetNextMove(beerGammon.getGammonData());
                    	for (Move move : moves) {
                    		ArrayList<Move> moveToDraw = new ArrayList<Move>();
                    		moveToDraw.add(move);
                    		board.animateMoves(moveToDraw);
                    		if (move.getColor() == beerGammon.getTurn()) {
                    			beerGammon.movePiece(move.getOrigSpot(), move.getNewSpot());
                    		}
                			board.clearAnimatedPieces();
                			board.clearFloaters();
                			board.render();
                			
                			if (beerGammon.canMove() == false) {
                				break;
                			}
                    	}
                    	
                    	if (beerGammon.getButtonState() == ButtonState.CLEAR_RED ||
                        		beerGammon.getButtonState() == ButtonState.CLEAR_WHITE) {
                    		beerGammon.buttonPushed();
                    		board.render();
                    	}
                	}
                } else if ( (beerGammon.getButtonState() == ButtonState.RED_ROLL &&
                		beerGammon.getGammonData().blackHumanPlayer == false) || 
                		( beerGammon.getButtonState() == ButtonState.WHITE_ROLL &&
                		false == beerGammon.getGammonData().whiteHumanPlayer)) {
                	beerGammon.buttonPushed();
                	board.render();                	

                	AcDcAI ai = new AcDcAI();
                	ArrayList<Move> moves = ai.GetNextMove(beerGammon.getGammonData());
                	for (Move move : moves) {
                		ArrayList<Move> moveToDraw = new ArrayList<Move>();
                		moveToDraw.add(move);
                		board.animateMoves(moveToDraw);
                		if (move.getColor() == beerGammon.getTurn()) {
                			beerGammon.movePiece(move.getOrigSpot(), move.getNewSpot());
                		}
            			board.clearAnimatedPieces();
            			board.clearFloaters();
            			board.render();
            			
            			if (beerGammon.canMove() == false) {
            				break;
            			}
                	}
                	
                	if (beerGammon.getButtonState() == ButtonState.CLEAR_RED ||
                    		beerGammon.getButtonState() == ButtonState.CLEAR_WHITE) {
                		beerGammon.buttonPushed();
                		board.render();
                	}
                } 
            }
        });*/ 	    

        onBoardUpdate();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//Ask the user if they want to quit
			new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Exit")
				.setMessage("Are you sure you want to leave?")
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which){
						// Exit the activity
						AcDcActivity.this.finish();
					}
				})
				.show();
				// Say that we've consumed the event
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
    public void onDestroy() {
		Log.d(TAG, "Destroying...");
		//mAdView.destroy();
        super.onDestroy();
    }
	
	@Override
	public void onBoardUpdate() {
		if (beerGammon.getButtonState() == ButtonState.RED_WON) {
			Intent statsIntent = new Intent(this, StatsActivity.class);
			statsIntent.putExtra("title", (int)1);
        	statsIntent.putExtra("redStats", beerGammon.getGammonData().redStats);
        	statsIntent.putExtra("whiteStats", beerGammon.getGammonData().whiteStats);
        	startActivity(statsIntent);
        	beerGammon.buttonPushed();
		} else if (beerGammon.getButtonState() == ButtonState.WHITE_WON) {
			Intent statsIntent = new Intent(this, StatsActivity.class);
			statsIntent.putExtra("title", (int)2);
        	statsIntent.putExtra("redStats", beerGammon.getGammonData().redStats);
        	statsIntent.putExtra("whiteStats", beerGammon.getGammonData().whiteStats);
        	startActivity(statsIntent);
        	beerGammon.buttonPushed();
		}
	}

	public void startNewGame(){
		Log.d(TAG, "Starting New Game...Danny");
		//board = (GammonBoard)findViewById(R.id.gammonBoard);
		board.newGame();

        board.render();		
	}
	
	@Override
	public void onDiceRoll(String event) {
	}
	
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		board = (GammonBoard)this.findViewById(R.id.gammonBoard);
		if (requestCode == NEW_GAME_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				
				boolean redHumanPlayer = data.getBooleanExtra("redPlayerIsHuman", beerGammon.getGammonData().blackHumanPlayer);
				boolean whiteHumanPlayer = data.getBooleanExtra("whitePlayerIsHuman", beerGammon.getGammonData().whiteHumanPlayer);
		        
		        beerGammon = board.getTheGame();
				
				beerGammon.getGammonData().blackHumanPlayer = redHumanPlayer;
				beerGammon.getGammonData().whiteHumanPlayer = whiteHumanPlayer;
				board.newGame();
				board.saveGame();
			}
		}
	}
}
