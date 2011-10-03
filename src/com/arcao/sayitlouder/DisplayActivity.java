package com.arcao.sayitlouder;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;

import com.arcao.sayitlouder.view.MessageView;

public class DisplayActivity extends Activity {
	public static final String INTENT_EXTRA_MESSAGE = "MESSAGE";
	
	private MessageView view;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.display);
		LinearLayout v = (LinearLayout) findViewById(R.id.displayHolder);
		view = new MessageView(this, getIntent().getStringExtra(INTENT_EXTRA_MESSAGE));
		v.addView(view);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		view.setForegroundColor(getPrefsInt(prefs, "foregroundColor", getResources().getColor(R.color.white)));
		view.setBackgroundColor(getPrefsInt(prefs, "backgroundColor", getResources().getColor(R.color.black)));
		view.setTouchHighlightAllowed(prefs.getBoolean("highlightTouch", view.isTouchHighlightAllowed()));
		view.setShakeHighlightAllowed(prefs.getBoolean("highlightShake", view.isShakeHighlightAllowed()));
		view.setShakeThresholdForce(getPrefsInt(prefs, "shakeThresholdForce", view.getShakeThresholdForce()));
		view.setHighlightMode(getPrefsInt(prefs, "highlightMode", view.getHighlightMode()));
		
		// Android feature / bug - ListPreference supports string values only, see http://code.google.com/p/android/issues/detail?id=2096 
		setRequestedOrientation(getPrefsInt(prefs, "displayOrientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED));
	}

	@Override
	protected void onResume() {
		super.onResume();
		view.onResume();
	}

	@Override
	protected void onPause() {
		view.onPause();
		super.onPause();
	}
	
	protected int getPrefsInt(SharedPreferences prefs, String key, int defaultValue) {
		Object value = prefs.getAll().get(key);
		
		if (value == null)
			return defaultValue;
		
		if (value instanceof Integer)
			return (Integer) value;
		
		try {
			return Integer.parseInt(value.toString());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
