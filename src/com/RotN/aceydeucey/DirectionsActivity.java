package com.RotN.aceydeucey;

import android.os.Bundle;
import android.app.Activity;
import android.text.SpannableString;
import android.text.style.BulletSpan;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DirectionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_directions);
		LinearLayout ll = (LinearLayout) findViewById(R.id.directionsLayout);
		
		TextView objectLabel = new TextView(this);
		objectLabel.setText("Object");
		objectLabel.setGravity(Gravity.CENTER);
		objectLabel.setTextAppearance(this, R.style.headerText);
		objectLabel.setPadding(0, 15, 0, 0);
		
		TextView gameObject = new TextView(this);
		gameObject.setText("The object of the game is to move all of your checkers around the board to your own home table and then bear them off. The first player to bear off all of his checkers wins the game.");
		
		TextView rfnLabel = new TextView(this);
		rfnLabel.setText("Roll For Number");
		rfnLabel.setGravity(Gravity.CENTER);
		rfnLabel.setTextAppearance(this, R.style.headerText);
		rfnLabel.setPadding(0, 15, 0, 0);
		
		TextView rollForNumber = new TextView(this);
		rollForNumber.setText("Each player has a number that can deal them a drink if rolled. The players start by rolling for their number."
				+ " Anytime your oppenent rolls their number it is your job to call them on it and they drink. If you fail to do so you are awarded the drink.");
		
		TextView startLabel = new TextView(this);
		startLabel.setText("To Start");
		startLabel.setGravity(Gravity.CENTER);
		startLabel.setTextAppearance(this, R.style.headerText);
		startLabel.setPadding(0, 15, 0, 0);
		
		TextView rollForTurn = new TextView(this);
		rollForTurn.setText("Each player rolls one die and the higher number goes first. That player then moves according to the values on the dice.");
		
		TextView enteringLabel = new TextView(this);
		enteringLabel.setText("Entering Checkers");
		enteringLabel.setGravity(Gravity.CENTER);
		enteringLabel.setTextAppearance(this, R.style.headerText);
		enteringLabel.setPadding(0, 15, 0, 0);
		
		TextView entering = new TextView(this);
		entering.setText("You enter a checker by placing it on a point in the  board corresponding to a number rolled. For example, if you roll 6-3, then you enter one checker on the six-point and one checker on the three-point. Once you have entered one or more checkers, you may use subsequent rolls to move those checkers forward, to enter more checkers, or both.");
		
		TextView moveLabel = new TextView(this);
		moveLabel.setText("Movement");
		moveLabel.setGravity(Gravity.CENTER);
		moveLabel.setTextAppearance(this, R.style.headerText);
		moveLabel.setPadding(0, 15, 0, 0);
		
		TextView movement = new TextView(this);
		movement.setText("The roll of the dice indicates how many points, or pips, the player is to move his checkers. The following rules apply:");
		
		TextView bullet1 = new TextView(this);
		CharSequence t1 = "A checker may be moved only to an open point, one that is not occupied by two or more opposing checkers.";
		SpannableString s1 = new SpannableString(t1);
		s1.setSpan(new BulletSpan(15), 0, t1.length(), 0);
		bullet1.setText(s1);
		TextView bullet2 = new TextView(this);
		CharSequence t2 = "The numbers on the two dice constitute separate moves. For example, if you roll 5 and 3, you may move one checker five spaces to an open point and another checker three spaces to an open point, or you may move the one checker a total of eight spaces to an open point, but only if the intermediate point (either three or five spaces from the starting point) is also open.";
		SpannableString s2 = new SpannableString(t2);
		s2.setSpan(new BulletSpan(15), 0, t2.length(), 0);
		bullet2.setText(s2);
		TextView bullet3 = new TextView(this);
		CharSequence t3 = "Doubles are played twice. For example, a roll of 6-6 means you have four sixes to use.";
		SpannableString s3 = new SpannableString(t3);
		s3.setSpan(new BulletSpan(15), 0, t3.length(), 0);
		bullet3.setText(s3);
		TextView bullet4 = new TextView(this);
		CharSequence t4 = "You must use both numbers of a roll if possible, or all four numbers in the case of doubles.";
		SpannableString s4 = new SpannableString(t4);
		s4.setSpan(new BulletSpan(15), 0, t4.length(), 0);
		bullet4.setText(s4);
		
		TextView acdcLabel = new TextView(this);
		acdcLabel.setText(getText(R.string.acdc_label));
		acdcLabel.setGravity(Gravity.CENTER);
		acdcLabel.setTextAppearance(this, R.style.headerText);
		acdcLabel.setPadding(0, 15, 0, 0);
		
		TextView acdc1 = new TextView(this);
		acdc1.setText(getText(R.string.acdc1));
		
		TextView doublesLabel = new TextView(this);
		doublesLabel.setText(getText(R.string.doubles_label));
		doublesLabel.setGravity(Gravity.CENTER);
		doublesLabel.setTextAppearance(this, R.style.headerText);
		doublesLabel.setPadding(0, 15, 0, 0);
		
		TextView doubles = new TextView(this);
		doubles.setText(getText(R.string.doubles_move));
		
		TextView pokeyingLabel = new TextView(this);
		pokeyingLabel.setText(getText(R.string.pokeying_label));
		pokeyingLabel.setGravity(Gravity.CENTER);
		pokeyingLabel.setTextAppearance(this, R.style.headerText);
		pokeyingLabel.setPadding(0, 15, 0, 0);
		
		TextView pokeyMove = new TextView(this);
		pokeyMove.setText(getText(R.string.pokeying_move));
		
		TextView gettingOffLabel = new TextView(this);
		gettingOffLabel.setText(getText(R.string.getting_off_label));
		gettingOffLabel.setGravity(Gravity.CENTER);
		gettingOffLabel.setTextAppearance(this, R.style.headerText);
		gettingOffLabel.setPadding(0, 15, 0, 0);
		
		TextView gettingOff = new TextView(this);
		gettingOff.setText(getText(R.string.getting_off_move));
		
		TextView bearingOffLabel = new TextView(this);
		bearingOffLabel.setText(getText(R.string.bearing_off_label));
		bearingOffLabel.setGravity(Gravity.CENTER);
		bearingOffLabel.setTextAppearance(this, R.style.headerText);
		bearingOffLabel.setPadding(0, 15, 0, 0);
		
		TextView bearingOff = new TextView(this);
		bearingOff.setText(getText(R.string.bearing_off_move));
		
		TextView scoringLabel = new TextView(this);
		scoringLabel.setText(getText(R.string.scoring_label));
		scoringLabel.setGravity(Gravity.CENTER);
		scoringLabel.setTextAppearance(this, R.style.headerText);
		scoringLabel.setPadding(0, 15, 0, 0);
		
		TextView scoring = new TextView(this);
		scoring.setText(getText(R.string.scoring_description));
		
		ll.addView(objectLabel);
		ll.addView(gameObject);
		ll.addView(rfnLabel);
		ll.addView(rollForNumber);
		ll.addView(startLabel);
		ll.addView(rollForTurn);
		ll.addView(enteringLabel);
		ll.addView(entering);
		ll.addView(moveLabel);
		ll.addView(movement);
		ll.addView(bullet1);
		ll.addView(bullet2);
		ll.addView(bullet3);
		ll.addView(bullet4);
		ll.addView(acdcLabel);
		ll.addView(acdc1);
		ll.addView(doublesLabel);
		ll.addView(doubles);
		ll.addView(pokeyingLabel);
		ll.addView(pokeyMove);
		ll.addView(gettingOffLabel);
		ll.addView(gettingOff);
		ll.addView(bearingOffLabel);
		ll.addView(bearingOff);
		ll.addView(scoringLabel);
		ll.addView(scoring);
	}
}
