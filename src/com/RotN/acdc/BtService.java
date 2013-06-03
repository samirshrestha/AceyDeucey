package com.RotN.acdc;

import com.RotN.acdc.AcDcActivity.ResponseReceiver;
import com.RotN.acdc.logic.BluetoothThread;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BtService extends Service {
	
	private static final String TAG = BtService.class.getSimpleName();
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_FAILED = 5;

    public static final String ACTION_CONNECTED = "connected";
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final int MSG_ADDRESS = 0;
    public static final int GET_DEVICE_NAME = 1;
    // Local BT adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothThread btThread = null;
    private String deviceName = null;
    private final IBinder mBinder = new LocalBinder();
    public boolean isClient = false;
    public boolean isConnected = false;
    

    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (btThread.getState() != BluetoothThread.STATE_CONNECTED) {
        	Log.e(TAG, "NOT CONNECTED");
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e(TAG, "Sending Message..." + message);
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            btThread.write(send);
        }
    }
    
    public void sendGameData(byte [] gameData){
		if(btThread != null){
			Log.d(TAG, "GameData...");
		    if (btThread.getState() != BluetoothThread.STATE_CONNECTED) {
		        Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
		        return;
		    }
		
		    // Check that there's actually something to send
		    if (gameData.length > 0) {
		        // Get the message bytes and tell the BluetoothChatService to write
		    	btThread.write(gameData);
		
		        // Reset out string buffer to zero 
		        //mOutStringBuffer.setLength(0);
		    }
		}
    }
 
    public void connectToDevice(String macAddress){
    	// Get the BLuetoothDevice object
    	Log.e(TAG, "MAC Address: " + macAddress);
    	if(!isConnected){
	        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddress);
	        // Attempt to connect to the device
	        if (btThread == null) {
	        	StartBTService();	       
		    }
	        isClient = true;
	        
	        btThread.connect(device);
    	}
        
        
    }
    
    private void StartBTService(){
    	Log.d(TAG, "Setting Up Bluetooth");
    	btThread = new BluetoothThread(this, BTHandler);
    	if (btThread.getState() == BluetoothThread.STATE_NONE) {
    		btThread.start();
        }
    }
    
    private void SendConnectedToMessage(String NameOfDevice){
    	Intent broadcastIntent = new Intent(ResponseReceiver.MSG_CONNECTED);
        broadcastIntent.putExtra("ConnectedTo", NameOfDevice);
        sendBroadcast(broadcastIntent);
    }
    
    private void writeGameData(byte[] gameData){
    	Intent broadcastIntent = new Intent(ResponseReceiver.MSG_GAME_DATA);
        broadcastIntent.putExtra("GameData", gameData);
        sendBroadcast(broadcastIntent);
    }
    
	private final Handler BTHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothThread.STATE_CONNECTED:
                	SendConnectedToMessage(deviceName);
                	isConnected = true;
                    break;
                case BluetoothThread.STATE_CONNECTING:
                	
                	break;
                case BluetoothThread.STATE_LISTEN:
                case BluetoothThread.STATE_NONE:
                    //mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                //String writeMessage = new String(writeBuf);
                //mConversationArrayAdapter.add("Me:  " + writeMessage);
                
                break;
            case MESSAGE_READ:           	
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                //String message = new String(readBuf, 0, msg.arg1);
                /*if (readMessage.length() > 0) {
                    mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                }*/    
                writeGameData(readBuf);
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                deviceName = msg.getData().getString(DEVICE_NAME);
                break;

            case MESSAGE_FAILED:
            	isClient = false;
            	isConnected = false;
            	//storage.edit().putBoolean("isConnected", false).commit();
            	//if (!msg.getData().getString(TOAST).contains("Unable to connect device")) {           		
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();            		
            	//}
                break;
            }
        }
    };
    
    @Override
	public IBinder onBind(Intent workIntent) {
    	Log.e(TAG, "Binding to Service...");
    	return mBinder;
	}
    
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Unbinding Service");
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        BtService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BtService.this;
        }
    }

    @Override
    public void onCreate() {
    	Log.e(TAG, "BT SERVICE CREATED");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }
		//btService = new BluetoothService(this, mHandler);
	}
    
    @Override
    public void onStart(Intent intent, int startId) {
    	Log.e(TAG, "BT  SERVICE STARTED");
	    if (btThread == null) {
	    	StartBTService();
	    }
    }

	@Override
    public void onDestroy() {
    	Log.e(TAG, "BT SERVICE STOPPED");
    }
    
}
