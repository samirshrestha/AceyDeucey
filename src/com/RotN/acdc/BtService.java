package com.RotN.acdc;

import java.nio.ByteBuffer;

import com.RotN.acdc.AcDcActivity.ResponseReceiver;
import com.RotN.acdc.bluetooth.Constants;
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
    
    
    public static final String ACTION_CONNECTED = "connected";
    public static final String BT_MESSAGE = "msg";
    
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
        	
        	byte[] type = ByteBuffer.allocate(4).putInt(Constants.DATA_STRING).array();
            byte[] msg = message.getBytes();
            byte[] send = new byte[type.length + msg.length];
            System.arraycopy(type, 0, send, 0, type.length);
            System.arraycopy(msg, 0, send, type.length, msg.length);
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
		        // Get the message bytes and tell the BluetoothService to write	    	
		    	byte[] send = mergeDataTransferObjects(Constants.DATA_GAME_DATA, gameData);
	            btThread.write(send);
		    }
		}
    }
    
    private byte[] mergeDataTransferObjects(int msgType, byte[] data){
    	byte[] type = ByteBuffer.allocate(4).putInt(msgType).array();
    	byte[] output = new byte[type.length + data.length];
    	System.arraycopy(type, 0, output, 0, type.length);
        System.arraycopy(data, 0, output, type.length, data.length);
        
        return output;
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
    
    private void ReadMessage(byte[] gameData, int size){
    	byte[] data = new byte[size];
    	System.arraycopy(gameData, 0, data, 0, size);
    	Intent broadcastIntent = new Intent(ResponseReceiver.MSG_RECEIVED);
        broadcastIntent.putExtra(BT_MESSAGE, data);
        sendBroadcast(broadcastIntent);
    }
    
	private void requestGameData() {
		Intent broadcastIntent = new Intent(ResponseReceiver.MSG_GAME_REQUEST);
        broadcastIntent.putExtra(BT_MESSAGE, true);
        sendBroadcast(broadcastIntent);		
	}
    
	private final Handler BTHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case Constants.MESSAGE_STATE_CHANGE:
                Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothThread.STATE_CONNECTED:
                	SendConnectedToMessage(deviceName);
                	isConnected = true;
                	if(!isClient){
                		//TODO send game data
                		requestGameData();
                	}
                    break;
                case BluetoothThread.STATE_CONNECTING:
                	
                	break;
                case BluetoothThread.STATE_LISTEN:
                case BluetoothThread.STATE_NONE:
                    //mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case Constants.MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                //String writeMessage = new String(writeBuf);
                //mConversationArrayAdapter.add("Me:  " + writeMessage);
                
                break;
            case Constants.MESSAGE_READ:           	
                byte[] readBuf = (byte[]) msg.obj;
                Log.i(TAG, "READ BYTE ARRAY SIZE: " + msg.arg1);
                ReadMessage(readBuf, msg.arg1);                  
                break;
            case Constants.MESSAGE_DEVICE_NAME:
                // save the connected device's name
                deviceName = msg.getData().getString(DEVICE_NAME);
                break;

            case Constants.MESSAGE_FAILED:
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
