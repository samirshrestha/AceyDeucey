package com.RotN.acdc;

import com.RotN.acdc.bluetooth.BluetoothThread;
import com.RotN.acdc.bluetooth.BtService;
import com.RotN.acdc.bluetooth.Constants;
import com.RotN.acdc.bluetooth.BtService.LocalBinder;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	private static final String TAG = AcDcActivity.class.getSimpleName();
	private int playMode;
    private RadioButton red;
    private RadioButton white; 
    private RelativeLayout connection;
    private SharedPreferences storage;
    private BluetoothThread mChatService = null;
    private boolean mBound = false;
    private BtService mService;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_settings);
		storage = getSharedPreferences("GameStorage", Context.MODE_PRIVATE);
		mChatService = new BluetoothThread(this, mHandler);
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
		connection = (RelativeLayout) findViewById(R.id.conntected_text);
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
            		connection.setVisibility(View.GONE);
            		playMode = Constants.SINGLE_PLAYER;
	            }
	            break;
	        case R.id.radio_multi:
	            if (checked){
	            	red.setEnabled(false);
	            	white.setEnabled(false);
	            	playMode = Constants.MULTI_PLAYER;
	            	connection.setVisibility(View.GONE);
	            }          
	            break;
	        case R.id.radio_network:
	            if (checked){
	            	red.setEnabled(true);
	            	white.setEnabled(true);
	            	playMode = Constants.MULTI_PLAYER_BT;
	            	if (mChatService.getState() == BluetoothThread.STATE_CONNECTED) {
	            		TextView deviceText = (TextView) findViewById(R.id.connected_to);
	            		deviceText.setText(BluetoothThread.connectedDeviceName);	
	            		connection.setVisibility(View.VISIBLE);
	                }            	
	            }	            
	            break;	    
	         }
	}
	
	public void startGame(View view) {
		Intent intent = new Intent(this, AcDcActivity.class);	
		intent.putExtra("whitePlayerIsHuman", white.isChecked());
		intent.putExtra("redPlayerIsHuman", red.isChecked());
		
		
		storage.edit().putBoolean(Constants.NEW_GAME_KEY, true).commit();
		storage.edit().putInt(Constants.PLAY_MODE, playMode).commit();
		storage.edit().remove(DeviceListActivity.EXTRA_DEVICE_ADDRESS).commit();
		if(mBound)
			mService.playMode = playMode;
		if(playMode == 1){
			intent.putExtra("whitePlayerIsHuman", true);
			intent.putExtra("redPlayerIsHuman", true);
			startActivity(intent);
		} else if(playMode == 2){
			if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
	            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);	        
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
	
	 // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case Constants.MESSAGE_STATE_CHANGE:
                Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothThread.STATE_CONNECTED:
                    /*mTitle.setText(R.string.title_connected_to);
                    mTitle.append(mConnectedDeviceName);*/
                    //mConversationArrayAdapter.clear();
                	Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothThread.STATE_CONNECTING:
                    //mTitle.setText(R.string.title_connecting);
                	Toast.makeText(getApplicationContext(), "Connecting..."
                            , Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothThread.STATE_LISTEN:
                case BluetoothThread.STATE_NONE:
                    //mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case Constants.MESSAGE_DEVICE_NAME:
                // save the connected device's name
               /* mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();*/
                break;
            case Constants.MESSAGE_FAILED:
            	//if (!msg.getData().getString(TOAST).contains("Unable to connect device")) {
                    /*Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();     */       		
            	//}
                break;
            }
        }
    };

	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        Log.d(TAG, "onActivityResult " + resultCode);
	        switch (requestCode) {
	        case Constants.REQUEST_ENABLE_BT:
	            // When the request to enable Bluetooth returns
	            if (resultCode == Activity.RESULT_OK) {
	            	Intent intent = new Intent(this, AcDcActivity.class);
	        		intent.putExtra("newGame", true);
	        		intent.putExtra("whitePlayerIsHuman", white.isChecked());
	        		intent.putExtra("redPlayerIsHuman", red.isChecked());;
	        		//startActivity(intent);
	            } else {
	                // User did not enable Bluetooth or an error occured
	                Log.d(TAG, "BT not enabled");
	                Toast.makeText(this, R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
	                
	            }
	        }
	    }

	 private ServiceConnection mConnection = new ServiceConnection() {

	        @Override
	        public void onServiceDisconnected(ComponentName name) {
	            mService = null;
	            mBound = false;
	        }

	        @Override
	        public void onServiceConnected(ComponentName name, IBinder service) {
	            LocalBinder binder = (LocalBinder) service;
	            mService = binder.getService();
	            mBound = true;
	        }
		};
		
}
