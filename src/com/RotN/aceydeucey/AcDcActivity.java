package com.RotN.aceydeucey;

import com.RotN.aceydeucey.logic.TheGameImpl;
import com.RotN.aceydeucey.logic.TheGame.ButtonState;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AcDcActivity extends Activity implements TheGameImpl.GammonEventHandler, AdListener{

	/** Called when the activity is first created. */
	
	private static final String TAG = AcDcActivity.class.getSimpleName();
	private TheGameImpl beerGammon;
	private Button actionButton;
	//static TextView tvTurn;	
	private Button undoButton;
	GammonBoard board;
    private AdView mAdView;
    
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
	        case R.id.advanced_mode:
	        	item.setChecked(!item.isChecked());
	            return true;
	        case R.id.undo_button:
	        	beerGammon.undoMove();
	        	board.render();
	        	return true;
	        case R.id.directions:
	        	Intent intent = new Intent(this, DirectionsActivity.class);
	        	startActivity(intent);
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onDataChange(String key, String value) {
		if (key == "turn") {
			//tvTurn.setText(value);	
		}	
		else if (key == "button") {
			actionButton.setText(value);
			if (beerGammon.getButtonState() != ButtonState.TURN_FINISHED) {
				actionButton.setEnabled(true);
			}
		}
	}

	@Override
	public void onUndoChange(boolean undoEnabled) {		
		undoButton.setEnabled(undoEnabled);

        if (beerGammon.getButtonState() == ButtonState.TURN_FINISHED) {
        	actionButton.setEnabled(beerGammon.canMove() == false);
        }
	}

	@Override
	public void onMoreDrinks(String player, String toastMessage) {
		// get your custom_toast.xml layout
		LayoutInflater inflater = getLayoutInflater();

		View layout = inflater.inflate(R.layout.custom_toast,
		  (ViewGroup) findViewById(R.id.custom_toast_layout_id));
		
		TextView toastPlayer = (TextView) layout.findViewById(R.id.toast_player);
		toastPlayer.setText(player);

		// set a message
		TextView text = (TextView) layout.findViewById(R.id.toast_message);
		text.setText(toastMessage);
		
		if (player == "Red Player") {
			layout.setBackgroundColor(0x96FF0000);
			toastPlayer.setTextColor(0x96FFFFFF);
			text.setTextColor(0x96FFFFFF);
		}
		else {
			layout.setBackgroundColor(0x96FFFFFF);
			toastPlayer.setTextColor(0x96000000);
			text.setTextColor(0x96000000);
		}
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// Toast...
		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, -60);
		toast.setView(layout);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();	
	}
	
	private void closeItDown() {
		board.saveGame();
		beerGammon.removeListener(this);
	}
	
	private void startItUp() {
		// set our GammonBoard as the View		
		
		beerGammon = new TheGameImpl();

        setContentView(R.layout.activity_ac_dc);
        

        mAdView = (AdView) this.findViewById(R.id.ad);
        mAdView.setAdListener(this);
        AdRequest adRequest = new AdRequest();
        adRequest.addKeyword("beer");
        mAdView.loadAd(adRequest);
        
        board = (GammonBoard)this.findViewById(R.id.gammonBoard);
        board.setBeerGammon(beerGammon);
        board.loadGame();
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
	}
	
	@Override
	public void onReceiveAd(Ad ad) {
	  firstAdReceived = true;
	  // Hide the custom image and show the AdView.
	  mAdView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onFailedToReceiveAd(Ad ad, ErrorCode code) {
		if (!firstAdReceived) {
			// Hide the AdView and show the custom image.
			Log.d(TAG, "We Got Here");
			mAdView.setVisibility(View.GONE);

			refreshHandler.removeCallbacks(refreshRunnable);
			refreshHandler.postDelayed(refreshRunnable, 60 * 1000);
	  }
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
		mAdView.destroy();
        super.onDestroy();
    }

	@Override
	public void onDismissScreen(Ad arg0) {
		
	}

	@Override
	public void onLeaveApplication(Ad arg0) {
		
	}

	@Override
	public void onPresentScreen(Ad arg0) {
		
	}
	
	private class RefreshRunnable implements Runnable {
		  @Override
		  public void run() {
		    // Load an ad with an ad request.
			  AdRequest adRequest = new AdRequest();
		      adRequest.addKeyword("beer");
		      mAdView.loadAd(adRequest);
		  }
		}
}
