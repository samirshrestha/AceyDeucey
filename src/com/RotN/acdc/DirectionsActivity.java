package com.RotN.acdc;

import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.BulletSpan;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DirectionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_directions);
		LinearLayout ll = (LinearLayout) findViewById(R.id.directionsLayout);
		
		TextView gameOverview = new TextView(this);
		gameOverview.setText("The American version of Acey-Deucey has been a favorite game of the U.S. Navy, Marine Corps, and Merchant Marine since the First World War.");
		
		TextView setupLabel = new TextView(this);
		setupLabel.setText("Setup");
		setupLabel.setGravity(Gravity.CENTER);
		setupLabel.setTextAppearance(this, R.style.headerText);
		setupLabel.setPadding(0, 15, 0, 0);
		
		TextView setupObject = new TextView(this);
		setupObject.setText("Each player starts with fifteen checkers off the board. The players enter their checkers in the opponent's home board, then bring them around the board as shown in the diagram below.");
		
		ImageView setupImage = new ImageView(this);
		setupImage.setBackgroundResource(R.drawable.acdc);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		setupImage.setLayoutParams(params);
		setupImage.setPadding(0, 10, 0, 0);
		
		TextView objectLabel = new TextView(this);
		objectLabel.setText("Object");
		objectLabel.setGravity(Gravity.CENTER);
		objectLabel.setTextAppearance(this, R.style.headerText);
		objectLabel.setPadding(0, 15, 0, 0);
		
		TextView gameObject = new TextView(this);
		gameObject.setText("The object of the game is to move all of your checkers around the board to your own home table and then bear them off. The first player to bear off all of his checkers wins the game.");
		
		TextView startLabel = new TextView(this);
		startLabel.setText("To Start");
		startLabel.setGravity(Gravity.CENTER);
		startLabel.setTextAppearance(this, R.style.headerText);
		startLabel.setPadding(0, 15, 0, 0);
		
		TextView rollForTurn = new TextView(this);
		rollForTurn.setText(" Each player rolls one die and the higher number goes first. That player then rolls both dice again to begin his first turn.");
		
		TextView enteringLabel = new TextView(this);
		enteringLabel.setText("Entering Checkers");
		enteringLabel.setGravity(Gravity.CENTER);
		enteringLabel.setTextAppearance(this, R.style.headerText);
		enteringLabel.setPadding(0, 15, 0, 0);
		
		TextView entering = new TextView(this);
		entering.setText("You enter a checker by placing it on a point in the opponent's home board corresponding to a number rolled. For example, if you roll 6-3, then you enter one checker on the opponent's six-point and one checker on his three-point. Once you have entered one or more checkers, you may use subsequent rolls to move those checkers forward, to enter more checkers, or both.");
		
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
		CharSequence t4 = "You must use both numbers of a roll if possible, or all four numbers in the case of doubles. If you can play one number but not both, you must play the higher one.";
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
		
		TextView acdcBullet1 = new TextView(this);
		CharSequence acdcT1 = "First you play the 1 and 2 in the normal way.";
		SpannableString acdcS1 = new SpannableString(acdcT1);
		acdcS1.setSpan(new BulletSpan(15), 0, acdcT1.length(), 0);
		acdcBullet1.setText(acdcS1);
		TextView acdcBullet2 = new TextView(this);
		CharSequence acdcT2 = "Then you name any roll of doubles you wish and play it accordingly.";
		SpannableString acdcS2 = new SpannableString(acdcT2);
		acdcS2.setSpan(new BulletSpan(15), 0, acdcT2.length(), 0);
		acdcBullet2.setText(acdcS2);
		TextView acdcBullet3 = new TextView(this);
		CharSequence acdcT3 = "Then you roll again and play the roll as usual.";
		SpannableString acdcS3 = new SpannableString(acdcT3);
		acdcS3.setSpan(new BulletSpan(15), 0, acdcT3.length(), 0);
		acdcBullet3.setText(acdcS3);		
		TextView acdcBullet4 = new TextView(this);
		CharSequence acdcT4 = "If the number rolled is another 1-2, you keep going—naming and playing a double of your choice, and then rolling again.";
		SpannableString acdcS4 = new SpannableString(acdcT4);
		acdcS4.setSpan(new BulletSpan(15), 0, acdcT4.length(), 0);
		acdcBullet4.setText(acdcS4);	
		
		TextView acdc2 = new TextView(this);
		acdc2.setText(getText(R.string.acdc2));
		acdc2.setPadding(0,  10,  0,  0);
		
		TextView hittingLabel = new TextView(this);
		hittingLabel.setText(getText(R.string.hitting_label));
		hittingLabel.setGravity(Gravity.CENTER);
		hittingLabel.setTextAppearance(this, R.style.headerText);
		hittingLabel.setPadding(0, 15, 0, 0);
		
		TextView hitting = new TextView(this);
		hitting.setText(getText(R.string.hitting));
		
		TextView barLabel = new TextView(this);
		barLabel.setText(getText(R.string.entering_label));
		barLabel.setGravity(Gravity.CENTER);
		barLabel.setTextAppearance(this, R.style.headerText);
		barLabel.setPadding(0, 15, 0, 0);
		
		TextView bar = new TextView(this);
		bar.setText(getText(R.string.entering));
		
		TextView bearingOffLabel = new TextView(this);
		bearingOffLabel.setText(getText(R.string.bearing_off_label));
		bearingOffLabel.setGravity(Gravity.CENTER);
		bearingOffLabel.setTextAppearance(this, R.style.headerText);
		bearingOffLabel.setPadding(0, 15, 0, 0);
		
		TextView bearingOff1 = new TextView(this);
		bearingOff1.setText(getText(R.string.bearing_off1));
		
		TextView scoringLabel = new TextView(this);
		scoringLabel.setText(getText(R.string.scoring_label));
		scoringLabel.setGravity(Gravity.CENTER);
		scoringLabel.setTextAppearance(this, R.style.headerText);
		scoringLabel.setPadding(0, 15, 0, 0);
		
		TextView scoring = new TextView(this);
		scoring.setText(getText(R.string.scoring));
		
		TextView strategyLabel = new TextView(this);
		strategyLabel.setText(getText(R.string.strategy_label));
		strategyLabel.setGravity(Gravity.CENTER);
		strategyLabel.setTextAppearance(this, R.style.headerText);
		strategyLabel.setPadding(0, 15, 0, 0);
		
		TextView strategy = new TextView(this);
		strategy.setText(getText(R.string.strategy));
		
		TextView infoLabel = new TextView(this);
		infoLabel.setText("More info");
		infoLabel.setGravity(Gravity.CENTER);
		infoLabel.setTextAppearance(this, R.style.headerText);
		infoLabel.setPadding(0, 15, 0, 0);
		
		TextView info = new TextView(this);
		info.setText(Html.fromHtml(
	            "For more info, please visit " +
	                    "<a href=\"http://www.bkgm.com\">Backgammon Galore</a> "));
		info.setMovementMethod(LinkMovementMethod.getInstance());
		
		ll.addView(gameOverview);
		ll.addView(setupLabel);
		ll.addView(setupObject);
		ll.addView(setupImage);
		ll.addView(objectLabel);
		ll.addView(gameObject);
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
		ll.addView(acdcBullet1);
		ll.addView(acdcBullet2);
		ll.addView(acdcBullet3);
		ll.addView(acdcBullet4);
		ll.addView(acdc2);
		ll.addView(hittingLabel);
		ll.addView(hitting);
		ll.addView(barLabel);
		ll.addView(bar);
		ll.addView(bearingOffLabel);
		ll.addView(bearingOff1);
		ll.addView(scoringLabel);
		ll.addView(scoring);
		ll.addView(strategyLabel);
		ll.addView(strategy);
		ll.addView(infoLabel);
		ll.addView(info);
	}
}
