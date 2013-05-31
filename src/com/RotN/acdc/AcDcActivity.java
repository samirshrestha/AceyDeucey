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
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

public class AcDcActivity extends Activity implements TheGameImpl.GammonEventHandler {

	/** Called when the activity is first created. */
	
	private static final String TAG = AcDcActivity.class.getSimpleName();
	private TheGameImpl beerGammon;
	private Button actionButton;
	//static TextView tvTurn;	
	private Button undoButton;
	GammonBoard board;
    //private AdView mAdView;
    
	private SharedPreferences storage;
    private boolean firstAdReceived = false;
    private final Handler refreshHandler = new Handler();
	private final Runnable refreshRunnable = new RefreshRunnable();	
	
	
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    private int playMode = -1;
    
    //Play Mode
    public static final String NEW_GAME_KEY = "NewGame";
    private static final int EXISTING_GAME = 0;
    private static final int SINGLE_PLAYER = 1;
    private static final int MULTI_PLAYER = 2;
    private static final int MULTI_PLAYER_BT = 3;
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local BT adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);    	
    	super.onCreate(savedInstanceState);
    	Log.e(TAG, "CREATING ACDC ACTIVITY");
    	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	storage = getSharedPreferences("GameStorage", Context.MODE_PRIVATE);
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
    public synchronized void onResume() {
        super.onResume();
        Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null && playMode == MULTI_PLAYER_BT) {
        	Log.e(TAG, "Restart Chat Service...");
        	// Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
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
		if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
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
		case TURN_FINISHED:
			buttonText = getText(R.string.clear_dice).toString();
			break;
		case WHITE_WON:
			buttonText = getText(R.string.white_won).toString();
			break;
		case BLACK_WON:
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
        boolean isNewGame = storage.getBoolean(NEW_GAME_KEY, false);   
        
        if(isNewGame){
        	storage.edit().putBoolean(NEW_GAME_KEY, false).commit();
	    	startNewGame();			    		   		
        } else {
        	Log.d(TAG, "Loading Game");
        	board.loadGame();
    		beerGammon = board.getTheGame();
        }		       
        
        beerGammon.addListener(this);
        Log.d(TAG, "View added");
        
        actionButton = (Button) findViewById(R.id.action_button);
        actionButton.setText(getButtonText());
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
                			board.clearFloaters();
                			board.render();
                			
                			if (beerGammon.canMove() == false) {
                				break;
                			}
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
            			board.clearFloaters();
            			board.render();
            			
            			if (beerGammon.canMove() == false) {
            				break;
            			}
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
        if (mChatService != null) mChatService.stop();
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
		actionButton.setText(getButtonText());
		actionButton.setEnabled(beerGammon.canMove() == false);
		undoButton.setEnabled(beerGammon.getGammonData().savedStatesCount > 0);
	}

	public void startNewGame(){
		Log.d(TAG, "Starting New Game...");
		Bundle extras = getIntent().getExtras();
    	playMode = extras.getInt("playMode");
		board.newGame();
		beerGammon = board.getTheGame();
		beerGammon.getGammonData().blackHumanPlayer = extras.getBoolean("redPlayerIsHuman");
		beerGammon.getGammonData().whiteHumanPlayer = extras.getBoolean("whitePlayerIsHuman");
		if(playMode == MULTI_PLAYER_BT){	    			
			/*Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);*/
			
			// Initialize the BluetoothChatService to perform connections   			
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);	
            
            Log.d(TAG, "Bind Handler to chat service...");
			mChatService = new BluetoothChatService(this, mHandler);

            //Initialize the buffer for outgoing messages
            mOutStringBuffer = new StringBuffer(""); 
		}		
	}
	
	 private void sendMoves(String moves) {
	        // Check that we're actually connected before trying anything
	        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
	            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
	            return;
	        }

	        // Check that there's actually something to send
	        if (moves.length() > 0) {
	            // Get the message bytes and tell the BluetoothChatService to write
	            byte[] send = moves.getBytes();
	            mChatService.write(send);

	            // Reset out string buffer to zero 
	            mOutStringBuffer.setLength(0);
	        }
	    }

	
	 // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    /*mTitle.setText(R.string.title_connected_to);
                    mTitle.append(mConnectedDeviceName);*/
                    //mConversationArrayAdapter.clear();
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                    //mTitle.setText(R.string.title_connecting);
                	Toast.makeText(getApplicationContext(), "Connecting..."
                            , Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    //mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                if (readMessage.length() > 0) {
                    mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                }
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
            	//if (!msg.getData().getString(TOAST).contains("Unable to connect device")) {
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();            		
            	//}
                break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	
            	// Get the device MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                Log.d(TAG, "TRY CONNECTING!! " + address);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                mChatService.connect(device);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
            	startItUp();
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

	
	@Override
	public void onDiceRoll(String event) {
	}
}
