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
		gameOverview.setText(getText(R.string.overview));
		
		TextView setupLabel = new TextView(this);
		setupLabel.setText(getText(R.string.setup_label));
		setupLabel.setGravity(Gravity.CENTER);
		setupLabel.setTextAppearance(this, R.style.headerText);
		setupLabel.setPadding(0, 15, 0, 0);
		
		TextView setupObject = new TextView(this);
		setupObject.setText(getText(R.string.setup));
		
		ImageView setupImage = new ImageView(this);
		setupImage.setBackgroundResource(R.drawable.acdc);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		setupImage.setLayoutParams(params);
		setupImage.setPadding(0, 10, 0, 0);
		
		TextView objectLabel = new TextView(this);
		objectLabel.setText(getText(R.string.object_label));
		objectLabel.setGravity(Gravity.CENTER);
		objectLabel.setTextAppearance(this, R.style.headerText);
		objectLabel.setPadding(0, 15, 0, 0);
		
		TextView gameObject = new TextView(this);
		gameObject.setText(getText(R.string.object));
		
		TextView startLabel = new TextView(this);
		startLabel.setText(getText(R.string.start_label));
		startLabel.setGravity(Gravity.CENTER);
		startLabel.setTextAppearance(this, R.style.headerText);
		startLabel.setPadding(0, 15, 0, 0);
		
		TextView rollForTurn = new TextView(this);
		rollForTurn.setText(getText(R.string.start));
		
		TextView enteringLabel = new TextView(this);
		enteringLabel.setText(getText(R.string.bunker_entering_label));
		enteringLabel.setGravity(Gravity.CENTER);
		enteringLabel.setTextAppearance(this, R.style.headerText);
		enteringLabel.setPadding(0, 15, 0, 0);
		
		TextView entering = new TextView(this);
		entering.setText(getText(R.string.bunker_entering));
		
		TextView moveLabel = new TextView(this);
		moveLabel.setText(getText(R.string.movement_label));
		moveLabel.setGravity(Gravity.CENTER);
		moveLabel.setTextAppearance(this, R.style.headerText);
		moveLabel.setPadding(0, 15, 0, 0);
		
		TextView movement = new TextView(this);
		movement.setText(getText(R.string.movement));
		
		TextView bullet1 = new TextView(this);
		CharSequence t1 = getText(R.string.movement1);
		SpannableString s1 = new SpannableString(t1);
		s1.setSpan(new BulletSpan(15), 0, t1.length(), 0);
		bullet1.setText(s1);
		TextView bullet2 = new TextView(this);
		CharSequence t2 = getText(R.string.movement2);
		SpannableString s2 = new SpannableString(t2);
		s2.setSpan(new BulletSpan(15), 0, t2.length(), 0);
		bullet2.setText(s2);
		TextView bullet3 = new TextView(this);
		CharSequence t3 = getText(R.string.movement3);
		SpannableString s3 = new SpannableString(t3);
		s3.setSpan(new BulletSpan(15), 0, t3.length(), 0);
		bullet3.setText(s3);
		TextView bullet4 = new TextView(this);
		CharSequence t4 = getText(R.string.movement4);
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
		CharSequence acdcT1 = getText(R.string.acdcb1);
		SpannableString acdcS1 = new SpannableString(acdcT1);
		acdcS1.setSpan(new BulletSpan(15), 0, acdcT1.length(), 0);
		acdcBullet1.setText(acdcS1);
		TextView acdcBullet2 = new TextView(this);
		CharSequence acdcT2 = getText(R.string.acdcb2);
		SpannableString acdcS2 = new SpannableString(acdcT2);
		acdcS2.setSpan(new BulletSpan(15), 0, acdcT2.length(), 0);
		acdcBullet2.setText(acdcS2);
		TextView acdcBullet3 = new TextView(this);
		CharSequence acdcT3 = getText(R.string.acdcb3);
		SpannableString acdcS3 = new SpannableString(acdcT3);
		acdcS3.setSpan(new BulletSpan(15), 0, acdcT3.length(), 0);
		acdcBullet3.setText(acdcS3);		
		TextView acdcBullet4 = new TextView(this);
		CharSequence acdcT4 = getText(R.string.acdcb4);
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
		infoLabel.setText(getText(R.string.more_info_label));
		infoLabel.setGravity(Gravity.CENTER);
		infoLabel.setTextAppearance(this, R.style.headerText);
		infoLabel.setPadding(0, 15, 0, 0);
		
		TextView info = new TextView(this);
		info.setText(Html.fromHtml(
	            getText(R.string.more_info) + " " +
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
