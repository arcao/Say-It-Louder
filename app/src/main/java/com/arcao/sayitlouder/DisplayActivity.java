package com.arcao.sayitlouder;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import com.arcao.sayitlouder.configuration.BasicConfiguration;
import com.arcao.sayitlouder.view.MessageView;
import java.util.Map;

public class DisplayActivity extends AppCompatActivity {
	public static final String INTENT_EXTRA_MESSAGE = "MESSAGE";
	
	private MessageView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		BasicConfiguration configuration = new BasicConfiguration();
		configuration.setForegroundColor(getPrefsInt(prefs, "foregroundColor", getResources().getColor(R.color.white)));
		configuration.setBackgroundColor(getPrefsInt(prefs, "backgroundColor", getResources().getColor(R.color.black)));
		configuration.setTouchHighlightAllowed(prefs.getBoolean("highlightTouch", configuration.isTouchHighlightAllowed()));
		configuration.setShakeHighlightAllowed(prefs.getBoolean("highlightShake", configuration.isShakeHighlightAllowed()));
		configuration.setShakeThresholdForce(getPrefsInt(prefs, "shakeThresholdForce", configuration.getShakeThresholdForce()));
		configuration.setHighlightMode(getPrefsInt(prefs, "highlightMode", configuration.getHighlightMode()));
		configuration.setMirroredY(prefs.getBoolean("mirroredY", configuration.isMirroredY()));

		getWindow().setBackgroundDrawable(new ColorDrawable(configuration.getBackgroundColor()));

		view = new MessageView(this, configuration, getIntent().getStringExtra(INTENT_EXTRA_MESSAGE));

		// Android feature / bug - ListPreference supports string values only, see http://code.google.com/p/android/issues/detail?id=2096 
		//noinspection WrongConstant
		setRequestedOrientation(getPrefsInt(prefs, "displayOrientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED));
		setContentView(view);
	}

	@Override
	protected void onResume() {
		view.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		view.onPause();
		super.onPause();
	}
	
	protected int getPrefsInt(SharedPreferences prefs, String key, int defaultValue) {
		Map<String, ?> prefsMap = prefs.getAll();

		if (prefsMap == null)
			return defaultValue;

		Object value = prefsMap.get(key);
		
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
