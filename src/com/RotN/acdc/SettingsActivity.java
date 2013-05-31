package com.RotN.acdc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	private static final String TAG = AcDcActivity.class.getSimpleName();
	private int playMode;
    private RadioButton red;
    private RadioButton white;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int SINGLE_PLAYER = 1;
    private static final int MULTI_PLAYER = 2;
    private static final int MULTI_PLAYER_BT = 3;
    public static final String NEW_GAME_KEY = "NewGame";
    private SharedPreferences storage;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_settings);
		storage = getSharedPreferences("GameStorage", Context.MODE_PRIVATE);	
		
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();		
		if (mBluetoothAdapter == null) {
		    RadioButton network = (RadioButton) findViewById(R.id.radio_network);
		    network.setEnabled(false);
		}
	}
	
	@Override
	protected void onStart() {
		Log.d(TAG, "Starting Settings...");		
		super.onStart();
		red = (RadioButton) findViewById(R.id.radio_red);
		white = (RadioButton) findViewById(R.id.radio_white);
	}
	
	public void onPlayModeClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();

	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_single:
	            if (checked){
	            	red.setEnabled(true);
            		white.setEnabled(true);
            		playMode = SINGLE_PLAYER;
	            }
	            break;
	        case R.id.radio_multi:
	            if (checked){
	            	red.setEnabled(false);
	            	white.setEnabled(false);
	            	playMode = MULTI_PLAYER;
	            }          
	            break;
	        case R.id.radio_network:
	            if (checked){
	            	red.setEnabled(true);
	            	white.setEnabled(true);
	            	playMode = MULTI_PLAYER_BT;
	            }	            
	            break;	    
	         }
	}
	
	public void startGame(View view) {
		Intent intent = new Intent(this, AcDcActivity.class);	
		intent.putExtra("playMode", playMode);
		intent.putExtra("whitePlayerIsHuman", white.isChecked());
		intent.putExtra("redPlayerIsHuman", red.isChecked());
		
		
		storage.edit().putBoolean(NEW_GAME_KEY, true).commit();
		
		if(playMode == 1){
			intent.putExtra("whitePlayerIsHuman", true);
			intent.putExtra("redPlayerIsHuman", true);
			startActivity(intent);
		} else if(playMode == 2){
			if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
	            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);	        
	        } else {
	        	startActivity(intent);
	        }
		} else {
			startActivity(intent);
		}
		
    	
	}
	
	
	public void disconnect(View view) {
		//find code for this		
    	
	}
	
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        Log.d(TAG, "onActivityResult " + resultCode);
	        switch (requestCode) {
	        case REQUEST_ENABLE_BT:
	            // When the request to enable Bluetooth returns
	            if (resultCode == Activity.RESULT_OK) {
	            	Intent intent = new Intent(this, AcDcActivity.class);
	        		intent.putExtra("newGame", true);
	        		intent.putExtra("playMode", playMode);
	        		intent.putExtra("whitePlayerIsHuman", white.isChecked());
	        		intent.putExtra("redPlayerIsHuman", red.isChecked());;
	        		startActivity(intent);
	            } else {
	                // User did not enable Bluetooth or an error occured
	                Log.d(TAG, "BT not enabled");
	                Toast.makeText(this, R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
	                
	            }
	        }
	    }

}
