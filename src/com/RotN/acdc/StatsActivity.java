package com.RotN.acdc;

import com.RotN.acdc.logic.Stats;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class StatsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);
		fillOutData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stats, menu);
		return true;
	}
	
	private void fillOutData() {
		Intent intent = getIntent();
		Stats redStats = (Stats) intent.getSerializableExtra("redStats");
		Stats whiteStats = (Stats) intent.getSerializableExtra("whiteStats");
		
		TextView white = (TextView) findViewById(R.id.textViewWhiteRolls);
		white.setText(String.valueOf(whiteStats.rolls));		
		TextView red = (TextView) findViewById(R.id.textViewRedRolls);
		red.setText(String.valueOf(redStats.rolls));
		
		white = (TextView) findViewById(R.id.textViewWhiteAcDc);
		white.setText(String.valueOf(whiteStats.acdcs));		
		red = (TextView) findViewById(R.id.textViewRedAcDc);
		red.setText(String.valueOf(redStats.acdcs));
		
		white = (TextView) findViewById(R.id.textViewWhite6s);
		white.setText(String.valueOf(whiteStats.sixes));		
		red = (TextView) findViewById(R.id.textViewRed6s);
		red.setText(String.valueOf(redStats.sixes));
		
		white = (TextView) findViewById(R.id.textViewWhite5s);
		white.setText(String.valueOf(whiteStats.fives));		
		red = (TextView) findViewById(R.id.textViewRed5s);
		red.setText(String.valueOf(redStats.fives));
		
		white = (TextView) findViewById(R.id.textViewWhite4s);
		white.setText(String.valueOf(whiteStats.fours));		
		red = (TextView) findViewById(R.id.textViewRed4s);
		red.setText(String.valueOf(redStats.fours));
		
		white = (TextView) findViewById(R.id.textViewWhite3s);
		white.setText(String.valueOf(whiteStats.threes));		
		red = (TextView) findViewById(R.id.textViewRed3s);
		red.setText(String.valueOf(redStats.threes));
		
		white = (TextView) findViewById(R.id.textViewWhite2s);
		white.setText(String.valueOf(whiteStats.twos));		
		red = (TextView) findViewById(R.id.textViewRed2s);
		red.setText(String.valueOf(redStats.twos));
		
		white = (TextView) findViewById(R.id.textViewWhite1s);
		white.setText(String.valueOf(whiteStats.ones));		
		red = (TextView) findViewById(R.id.textViewRed1s);
		red.setText(String.valueOf(redStats.ones));
	}
}
