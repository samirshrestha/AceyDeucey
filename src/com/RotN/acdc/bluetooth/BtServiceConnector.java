package com.RotN.acdc.bluetooth;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.RotN.acdc.bluetooth.BtService.LocalBinder;

public class BtServiceConnector {

	public boolean isBound = false;
	public BtService btService;
	
	public ServiceConnection btServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        	btServiceConnection = null;
            isBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            btService = binder.getService();
            isBound = true;
        }
	};
	

}
