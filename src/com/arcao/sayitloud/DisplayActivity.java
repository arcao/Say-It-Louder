package com.arcao.sayitloud;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.arcao.sayitloud.shake.ShakeDetector;
import com.arcao.sayitloud.shake.ShakeListener;
import com.arcao.sayitloud.view.MessageView;

public class DisplayActivity extends Activity implements ShakeListener {
	public static final String INTENT_EXTRA_MESSAGE = "MESSAGE";
	
	private ShakeDetector detector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.display);
		LinearLayout v = (LinearLayout) findViewById(R.id.displayHolder);
		v.addView(new MessageView(this, getIntent().getStringExtra(INTENT_EXTRA_MESSAGE)));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		detector = new ShakeDetector(this);
		detector.addListener(this);
		detector.start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		detector.stop();
		detector.removeListener(this);
	}

	@Override
	public void onShake() {
		
	}
}
