package com.RotN.acdc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class SettingsActivity extends Activity {

	private int playMode;
    private RadioButton red;
    private RadioButton white;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_settings);
		
		red = (RadioButton) findViewById(R.id.radio_red);
		white = (RadioButton) findViewById(R.id.radio_white);
		playMode = 0;
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
//		    RadioButton network = (RadioButton) findViewById(R.id.radio_network);
//		    network.setEnabled(false);
		}
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
            		playMode = 0;
	            }
	            break;
	        case R.id.radio_multi:
	            if (checked){
	            	red.setEnabled(false);
	            	white.setEnabled(false);
	            	playMode = 1;
	            }          
	            break;
//	        case R.id.radio_network:
//	            if (checked){
//	            	red.setEnabled(true);
//	            	white.setEnabled(true);
//	            	playMode = 2;
//	            }	            
//	            break;	    
	         }
	}
	
	public void startGame(View view) {
		Intent intent = new Intent(this, AcDcActivity.class);

		intent.putExtra("newGame", true);
		intent.putExtra("playMode", playMode);
		if(playMode == 1){
			intent.putExtra("whitePlayerIsHuman", true);
			intent.putExtra("redPlayerIsHuman", true);
		} else {
			intent.putExtra("whitePlayerIsHuman", white.isChecked());
			intent.putExtra("redPlayerIsHuman", red.isChecked());			
		}
		
    	startActivity(intent);
	}
}
