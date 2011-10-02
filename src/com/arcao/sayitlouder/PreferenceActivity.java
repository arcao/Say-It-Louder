package com.arcao.sayitlouder;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.method.DigitsKeyListener;

public class PreferenceActivity extends android.preference.PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		
		((EditTextPreference) findPreference("shakeThresholdForce")).getEditText().setKeyListener(DigitsKeyListener.getInstance(false,false));
	}
}
