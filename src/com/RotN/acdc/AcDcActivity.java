package com.RotN.acdc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import com.RotN.acdc.bluetooth.BtService;
import com.RotN.acdc.bluetooth.BtServiceConnector;
import com.RotN.acdc.bluetooth.Constants;
import com.RotN.acdc.bluetooth.ReceivedDataParser;
import com.RotN.acdc.logic.AcDcAI;
import com.RotN.acdc.logic.CheckerContainer.GameColor;

import com.RotN.acdc.logic.Move;
import com.RotN.acdc.logic.TheGame;
import com.RotN.acdc.logic.TheGameImpl;
import com.RotN.acdc.logic.TheGame.ButtonState;
import com.RotN.acdc.logic.TheGame.PlayerType;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

public class AcDcActivity extends Activity implements TheGameImpl.GammonEventHandler {

	/** Called when the activity is first created. */
	
	private static final String TAG = AcDcActivity.class.getSimpleName();
	private TheGameImpl beerGammon;
	private Button actionButton;
	private TextView gameMessage;
	//static TextView tvTurn;	
	private Button undoButton;
	GammonBoard board;
    //private AdView mAdView;
	
	private SharedPreferences storage;
    private boolean firstAdReceived = false;
    private final Handler refreshHandler = new Handler();
	private final Runnable refreshRunnable = new RefreshRunnable();	
		
    // Message types sent from the BluetoothChatService Handler
    private int playMode;
    private ResponseReceiver receiver;
    private BtServiceConnector connecter = new BtServiceConnector();
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
   

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	requestWindowFeature(Window.FEATURE_NO_TITLE);    	
    	super.onCreate(savedInstanceState);
    	Log.e(TAG, "CREATING ACDC ACTIVITY");

    	storage = getSharedPreferences("GameStorage", Context.MODE_PRIVATE);   	
    	playMode = storage.getInt(Constants.PLAY_MODE, Constants.EXISTING_GAME);
   	
    	if(playMode == Constants.MULTI_PLAYER_BT){
    		if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
            }
    	}
    	
        bindService(new Intent(this, BtService.class), connecter.btServiceConnection,
                Context.BIND_AUTO_CREATE);
    }
    
      
   	@Override
	protected void onStop() {
   		super.onStop();
   		doUnbindService();
		Log.d(TAG, "Stopping...");
		
		//closeItDown();
		// Remove any pending ad refreshes.
		//refreshHandler.removeCallbacks(refreshRunnable);
	}
   	
   	@Override
   	protected void onPause() {
   		Log.d(TAG, "Pausing...");
   		if (receiver != null) unregisterReceiver(receiver);
		closeItDown();
   		super.onPause();
   	}

   	@Override
    public synchronized void onResume() {
        super.onResume();
        Log.e(TAG, "ON RESUME...");
        RegisterReciever();
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
	        	Intent settingsIntent = new Intent(this, SettingsActivity.class);
	        	startActivity(settingsIntent);
	            return true;
	        case R.id.undo_button:
	        	beerGammon.undoMove();
	        	board.render();
	        	return true;
	        case R.id.bluetoothMode:
	        	storage.edit().putInt(Constants.PLAY_MODE, Constants.MULTI_PLAYER_BT).commit();
        		if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, Constants.ENABLE_BT_FOR_FIND_GAME);
                } else {
                	Intent serverIntent = new Intent(this, DeviceListActivity.class);
    	            startActivityForResult(serverIntent, Constants.REQUEST_CONNECT_DEVICE);
                }	        	 
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
		case MAKE_DISCOVERABLE:
			buttonText = getText(R.string.discoverable).toString();
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
        boolean isNewGame = storage.getBoolean(Constants.NEW_GAME_KEY, false);   
        
        if(isNewGame){
        	storage.edit().putBoolean(Constants.NEW_GAME_KEY, false).commit();
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
                actionButtonClicked();                
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
	
	private void actionButtonClicked() {
		//tell the logic we clicked
		beerGammon.buttonPushed();
        board.render();              
        if(beerGammon.getButtonState() == ButtonState.MAKE_DISCOVERABLE){
        	ensureDiscoverable();
        } else if (beerGammon.getButtonState() == ButtonState.TURN_FINISHED) {
        	//a player has rolled determine if they can clear the dice
        	actionButton.setEnabled(beerGammon.canMove() == false);
        	//determine if a computer is moving
        	if ( (beerGammon.getTurn() == GameColor.BLACK && PlayerType.COMPUTER == beerGammon.getGammonData().blackPlayer) ||
            		(beerGammon.getTurn() == GameColor.WHITE && PlayerType.COMPUTER == beerGammon.getGammonData().whitePlayer) ) {
            	performComputerMove();
        	}
        } else if ( (connecter.btService.playMode == Constants.MULTI_PLAYER_BT) && 
        	(beerGammon.getButtonState() == ButtonState.RED_ROLL || beerGammon.getButtonState() == ButtonState.WHITE_ROLL)) {  
        	//FINISHED TURN!!
        	sendGameData();
        }
	}
	
	private void performComputerMove() {
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
	
	private void sendGameData() {
		TheGame acdc = beerGammon.getGammonData();
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {    			
			ObjectOutputStream stream = new ObjectOutputStream(byteStream);
			stream.writeObject(acdc); //Causes NotSerializableException on clear dice
			byte[] gameData = byteStream.toByteArray();
			connecter.btService.sendGameData(gameData);
			EndTurn();
		} catch (IOException e) {
			Log.d(TAG, "Unable to send game data");
			e.printStackTrace();
		}
	}

	public void startNewGame(){
		Log.d(TAG, "Starting New Game...");
		Bundle extras = getIntent().getExtras();
		
		board.newGame();
		beerGammon = board.getTheGame();
		if (extras.getBoolean("redPlayerIsHuman")) {
			beerGammon.getGammonData().blackPlayer = PlayerType.HUMAN;
			if (playMode == Constants.MULTI_PLAYER_BT) {
				beerGammon.getGammonData().whitePlayer = PlayerType.BT_HUMAN;
			} else {
				beerGammon.getGammonData().whitePlayer = PlayerType.COMPUTER;
			}
		}
		
		if (extras.getBoolean("whitePlayerIsHuman")) {
			beerGammon.getGammonData().whitePlayer = PlayerType.HUMAN;
			if (playMode == Constants.MULTI_PLAYER_BT) {
				beerGammon.getGammonData().blackPlayer = PlayerType.BT_HUMAN;
			} else {
				beerGammon.getGammonData().blackPlayer = PlayerType.COMPUTER;
			}
		}
		
		if(playMode == Constants.MULTI_PLAYER_BT){	
            //Intent serverIntent = new Intent(this, DeviceListActivity.class);
            //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			if(BluetoothAdapter.getDefaultAdapter().isEnabled()){
				startBTGame(R.string.waiting_for_join);
			}
		} 
	}
	
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
	        case Constants.REQUEST_ENABLE_BT:
	            if (resultCode == Activity.RESULT_OK) {      	
	            	startBTGame(R.string.waiting_for_join);
	            } else {
	                // User did not enable Bluetooth or an error occured
	                Log.d(TAG, "BT not enabled");
	                Toast.makeText(this, R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
	                finish();
	            }
	            break;
	        case Constants.ENABLE_BT_FOR_FIND_GAME:
	        	if (resultCode == Activity.RESULT_OK) {
		        	Intent serverIntent = new Intent(this, DeviceListActivity.class);
		            startActivityForResult(serverIntent, Constants.REQUEST_CONNECT_DEVICE);
	        	} else {
	        		//ADD NEW MESSAGING
	                Toast.makeText(this, R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
	                finish();
	        	}
	        	break;
        }
    }

    private void startBTGame(int resourceIdForMessageText){
    	Intent BtService = new Intent(this, BtService.class);        
        startService(BtService);
        Log.d(TAG, "Show Game Message");
        gameMessage = (TextView) findViewById(R.id.game_message);
        gameMessage.setText(resourceIdForMessageText);
        gameMessage.setVisibility(View.VISIBLE);
        actionButton = (Button) findViewById(R.id.action_button);
        actionButton.setText(R.string.discoverable);
        beerGammon.getGammonData().buttonState = ButtonState.MAKE_DISCOVERABLE;
    }
	
	@Override
	public void onDiceRoll(String event) {
	}
	
	private void RegisterReciever(){
		receiver = new ResponseReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ResponseReceiver.MSG_CONNECTED);
		filter.addAction(ResponseReceiver.MSG_RECEIVED);
		filter.addAction(ResponseReceiver.MSG_GAME_REQUEST);
		registerReceiver(receiver, filter);   	
    }

	public class ResponseReceiver extends BroadcastReceiver {
		public static final String MSG_CONNECTED = "com.RotN.acdc.intent.action.MESSAGE_CONNECTED";
		public static final String MSG_RECEIVED = "com.RotN.acdc.intent.action.MESSAGE_RECEIVED";
		public static final String MSG_GAME_REQUEST = "com.RotN.acdc.intent.action.MESSAGE_GAME_REQUEST";
	
		@Override
	    public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Recieved Msg From Service");

			if (intent.getAction().equals(MSG_CONNECTED)) {
				String message = intent.getExtras().getString("ConnectedTo");
				Toast.makeText(getApplicationContext(), "Connected To: " + message, Toast.LENGTH_SHORT).show();
				if(connecter.btService.isClient){
					EndTurn();
				} else {
					gameMessage = (TextView) findViewById(R.id.game_message);		       
			        gameMessage.setVisibility(View.GONE);
			        actionButton = (Button) findViewById(R.id.action_button);
			        actionButton.setText(R.string.roll_for_turn);
			        actionButton.setVisibility(View.VISIBLE);
				}
		    } else if (intent.getAction().equals(MSG_RECEIVED)) {
		    	byte[] rawData = intent.getExtras().getByteArray(BtService.BT_MESSAGE);	    	
		    	ReceivedDataParser parser = new ReceivedDataParser(rawData);
					
				switch (parser.MsgType){
					case Constants.DATA_GAME_DATA:
						receivedGammonData(parser.Data);
						break;
					case Constants.DATA_PLAYER_COLOR:
						break;
					case Constants.DATA_STRING:
						String writeMessage = new String(parser.Data);
						Log.d(TAG, writeMessage);
						Toast.makeText(getApplicationContext(), "Wooo gotta move", Toast.LENGTH_SHORT).show();
						break;
				}
		    } else if (intent.getAction().equals(MSG_GAME_REQUEST)) {
		    	Log.d(TAG, "Recieved GAME REQUEST");
		    	beerGammon.getGammonData().buttonState = ButtonState.ROLL_FOR_TURN;
		    	sendGameData();
		    }
	    }
	}

	private void StartTurn(){
		gameMessage = (TextView) findViewById(R.id.game_message);		       
        gameMessage.setVisibility(View.GONE);
        actionButton = (Button) findViewById(R.id.action_button);
        actionButton.setVisibility(View.VISIBLE);
	}
	
	private void EndTurn(){
        gameMessage = (TextView) findViewById(R.id.game_message);
        gameMessage.setText(R.string.opponents_turn);
        gameMessage.setVisibility(View.VISIBLE);
        actionButton = (Button) findViewById(R.id.action_button);
        actionButton = (Button) findViewById(R.id.action_button);
        actionButton.setVisibility(View.INVISIBLE);
	}

	private void receivedGammonData(byte[] data){		
		ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
		ObjectInputStream objIn;
		try {
			
			objIn = new ObjectInputStream(byteStream);
			TheGame gammonData = (TheGame) objIn.readObject();
			Log.d(TAG, "Recieved Game Data. Length: "+ data.length);
			beerGammon.getGammonData().blackDie1 = gammonData.blackDie1;
			beerGammon.getGammonData().blackDie2 = gammonData.blackDie2;
			beerGammon.getGammonData().whiteDie1 = gammonData.whiteDie1;
			beerGammon.getGammonData().whiteDie2 = gammonData.whiteDie2;
			beerGammon.calculateMovesRemaining();
			beerGammon.setGammonData(gammonData);
			StartTurn();
			
		} catch (StreamCorruptedException e) {
			Log.d(TAG, "Recieved StreamCorruptedException");
        } catch (IOException e) {
			Log.d(TAG, "Not Game Data. Recieved IOException. Ignore.");
        } catch (ClassNotFoundException e) {
			Log.d(TAG, "Recieved ClassNotFoundException");
        } 	
	}
	
	
	private void doUnbindService() {
	    if (connecter.isBound) {
	    	Log.d(TAG, "UNBIND SERVICE");
	        // Detach our existing connection.
	        unbindService(connecter.btServiceConnection);
	        connecter.isBound = false;
	    }
	}

	private void ensureDiscoverable() {
        Log.d(TAG, "ensure discoverable");
        if (BluetoothAdapter.getDefaultAdapter().getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        } else {
        	Toast.makeText(this, R.string.already_discoverable, Toast.LENGTH_SHORT).show();
        }
    }
}
