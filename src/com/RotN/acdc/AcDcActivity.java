package com.RotN.acdc;

import java.util.ArrayList;

import com.RotN.acdc.logic.AcDcAI;
import com.RotN.acdc.logic.CheckerContainer.GameColor;
import com.RotN.acdc.logic.Move;
import com.RotN.acdc.logic.TheGameImpl;
import com.RotN.acdc.logic.TheGame.ButtonState;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Button;

public class AcDcActivity extends Activity implements TheGameImpl.GammonEventHandler {

	/** Called when the activity is first created. */
	
	private static final String TAG = AcDcActivity.class.getSimpleName();
	private TheGameImpl beerGammon;
	private Button actionButton;
	//static TextView tvTurn;	
	private Button undoButton;
	GammonBoard board;
    //private AdView mAdView;
    
    private boolean firstAdReceived = false;private 
	final Handler refreshHandler = new Handler();
	private final Runnable refreshRunnable = new RefreshRunnable();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);    	
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
	    MenuItem redPlayer = menu.findItem(R.id.human_red_player);
	    redPlayer.setChecked(beerGammon.getGammonData().blackHumanPlayer);

	    MenuItem whitePlayer = menu.findItem(R.id.human_white_player);
	    whitePlayer.setChecked(beerGammon.getGammonData().whiteHumanPlayer);
	    Log.d(TAG, "Menu inflated");
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		GammonBoard board = (GammonBoard)this.findViewById(R.id.gammonBoard);
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.new_game:
	        	AlertDialog.Builder alert_newGame = new AlertDialog.Builder(this);
	        	alert_newGame.setMessage("Are you sure you want to start a new game?")
	            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	            	GammonBoard board = (GammonBoard)findViewById(R.id.gammonBoard);
	                public void onClick(DialogInterface dialog, int id) {
	                	dialog.dismiss();	                	
	    	            board.newGame();
	    	            actionButton.setText(beerGammon.getButtonText());
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
	            alert.show();
	            return true;
	        case R.id.undo_button:
	        	beerGammon.undoMove();
	        	board.render();
	        	return true;
	        case R.id.directions:
	        	Intent intent = new Intent(this, DirectionsActivity.class);
	        	startActivity(intent);
	        	return true;
	        case R.id.human_red_player:
	        	item.setChecked(!item.isChecked());
	        	board.getTheGame().getGammonData().blackHumanPlayer = item.isChecked();
	            onBoardUpdate();
	            return true;
	        case R.id.human_white_player:
	        	item.setChecked(!item.isChecked());
	        	board.getTheGame().getGammonData().whiteHumanPlayer = item.isChecked();
	            onBoardUpdate();
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
        
        /*mAdView = (AdView) this.findViewById(R.id.ad);
        mAdView.setAdListener(this);
        AdRequest adRequest = new AdRequest();
        mAdView.loadAd(adRequest);*/
        
        board = (GammonBoard)this.findViewById(R.id.gammonBoard);
        board.loadGame();
        beerGammon = board.getTheGame();
        beerGammon.addListener(this);
        Log.d(TAG, "View added");
        
        actionButton = (Button) findViewById(R.id.action_button);
        actionButton.setText(beerGammon.getButtonText());
        actionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                beerGammon.buttonPushed();
                board.render();
                if (beerGammon.getButtonState() == ButtonState.TURN_FINISHED) {
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
                			board.clearFloater(move.getOrigSpot());
                			board.render();
                    	}
                    	
                    	if (beerGammon.getButtonState() == ButtonState.TURN_FINISHED) {
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
            			board.clearFloater(move.getOrigSpot());
            			board.render();
                	}
                	
                	if (beerGammon.getButtonState() == ButtonState.TURN_FINISHED) {
                		beerGammon.buttonPushed();
                		board.render();
                	}
                } 
            }
        });            

	    undoButton = (Button) findViewById(R.id.undo_button);
	    undoButton.setEnabled(beerGammon.getGammonData().savedStatesCount > 0);
	    undoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                beerGammon.undoMove();
                board.render();
                if (beerGammon.getButtonState() == ButtonState.TURN_FINISHED) {
                	actionButton.setEnabled(beerGammon.canMove() == false);
                }
            }
        });    
	    

        onBoardUpdate();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && undoButton.isEnabled()) {
        	beerGammon.undoMove();
        	board.render();
        	return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
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
		actionButton.setText(beerGammon.getButtonText());
		actionButton.setEnabled(beerGammon.canMove() == false);
		undoButton.setEnabled(beerGammon.getGammonData().savedStatesCount > 0);
	}
}
