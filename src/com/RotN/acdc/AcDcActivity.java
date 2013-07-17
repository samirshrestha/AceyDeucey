package com.RotN.acdc;

import java.util.ArrayList;
import com.RotN.acdc.logic.AcDcAI;
import com.RotN.acdc.logic.CheckerContainer.GameColor;
import com.RotN.acdc.logic.Move;
import com.RotN.acdc.logic.TheGameImpl;
import com.RotN.acdc.logic.TheGame.ButtonState;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class AcDcActivity extends Activity implements TheGameImpl.GammonEventHandler {

	/** Called when the activity is first created. */
	
	private static final String TAG = AcDcActivity.class.getSimpleName();
	private TheGameImpl beerGammon;
	GammonBoard board;
    //private AdView mAdView;
    
    private boolean firstAdReceived = false;private 
	final Handler refreshHandler = new Handler();
	private final Runnable refreshRunnable = new RefreshRunnable();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_ACTION_BAR); 
    	super.onCreate(savedInstanceState);
	
    }
    
   	@Override
	protected void onStop() {
		Log.d(TAG, "Stopping...");
		closeItDown();
		super.onStop();
		
		// Remove any pending ad refreshes.
		refreshHandler.removeCallbacks(refreshRunnable);
	}
   	
   	@Override
   	protected void onPause() {
   		Log.d(TAG, "Pausing...");
		closeItDown();
   		super.onPause();
   	}
	
	@Override
	protected void onStart() {
		Log.d(TAG, "Starting...");
		startItUp();
		super.onStart();
		if (!firstAdReceived) {
			// Request a new ad immediately.
		    refreshHandler.post(refreshRunnable);
		}
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
		
		GammonBoard board = (GammonBoard)this.findViewById(R.id.gammonBoard);
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.new_game:
	        	/*AlertDialog.Builder alert_newGame = new AlertDialog.Builder(this);
	        	alert_newGame.setMessage("Are you sure you want to start a new game?")
	            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	            	GammonBoard board = (GammonBoard)findViewById(R.id.gammonBoard);
	                public void onClick(DialogInterface dialog, int id) {
	                	dialog.dismiss();	                	
	    	            board.newGame();
	    	            actionButton.setText(getButtonText());
	    	            board.render();	                	
	                }
	                })
	            .setNegativeButton("No", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                //  Action for 'NO' Button
	                dialog.cancel();
	                }
	            });
	            AlertDialog alert = alert_newGame.create();
	            // Title for AlertDialog
	            alert.setTitle("Warning!");
	            // Icon for AlertDialog
	            alert.show();*/
	        	Intent settingsIntent = new Intent(this, SettingsActivity.class);
	        	startActivity(settingsIntent);
	            return true;
	        case R.id.undo_button:
	        	beerGammon.undoMove();
	        	board.render();
	        	return true;
	        case R.id.directions:
	        	Intent intent = new Intent(this, DirectionsActivity.class);
	        	startActivity(intent);
	        	return true;	        	
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private void closeItDown() {
		board.saveGame();
		beerGammon.removeListener(this);
	}
	
	private String getButtonText() {
		String buttonText = "";
		
		switch (beerGammon.getButtonState()) {
		case ROLL_FOR_TURN:
			buttonText = getText(R.string.roll_for_turn).toString();
			break;
		case RED_ROLL:
			buttonText = getText(R.string.red_roll).toString();
			break;
		case WHITE_ROLL:
			buttonText = getText(R.string.white_roll).toString();
			break;
		case CLEAR_WHITE:
		case CLEAR_RED:
			buttonText = getText(R.string.clear_dice).toString();
			break;
		case WHITE_WON:
			buttonText = getText(R.string.white_won).toString();
			break;
		case RED_WON:
			buttonText = getText(R.string.red_won).toString();
			break;
		}
		
		return buttonText;
	}
	
	private void startItUp() {
		// set our GammonBoard as the View		

        setContentView(R.layout.activity_ac_dc);
        
        /*mAdView = (AdView) this.findViewById(R.id.ad);
        mAdView.setAdListener(this);
        AdRequest adRequest = new AdRequest();
        mAdView.loadAd(adRequest);*/
        
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
	
	private class RefreshRunnable implements Runnable {
		  @Override
		  public void run() {
		    // Load an ad with an ad request.
			  //AdRequest adRequest = new AdRequest();
		      //mAdView.loadAd(adRequest);
		  }
		}

	@Override
	public void onBoardUpdate() {

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
}
