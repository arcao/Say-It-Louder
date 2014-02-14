package com.arcao.sayitlouder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import java.util.ArrayList;

public class MainActivity extends Activity implements OnItemClickListener, OnItemLongClickListener {

	private static final String PREFERENCES_MESSAGE_PREFIX = "message_";
	private static final String APP_NAME = "SayItLoud";

	private TextView messageText;
	private ListView list;
	private ImageButton buttonShow;
	private ArrayAdapter<String> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		messageText = (TextView) findViewById(R.id.message);
		list = (ListView) findViewById(R.id.list);
		buttonShow = (ImageButton) findViewById(R.id.show);

		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);

		messageText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void afterTextChanged(Editable s) {
				buttonShow.setEnabled(s.toString().trim().length() > 0);
			}
		});

		if (getIntent() != null && Intent.ACTION_SEND.equals(getIntent().getAction()) && getIntent().getStringExtra(Intent.EXTRA_TEXT) != null) {
			messageText.setText(getIntent().getStringExtra(Intent.EXTRA_TEXT));
		} else {
			messageText.setText("");
		}
		
		list.addFooterView(getLayoutInflater().inflate(R.layout.main_footer, null));

		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
		list.setAdapter(adapter);

		loadMessages();
	}

	public void buttonShow_onClick(View view) {
		String message = messageText.getText().toString().trim();
		
		Intent intent = new Intent(this, DisplayActivity.class);
		intent.putExtra(DisplayActivity.INTENT_EXTRA_MESSAGE, message);

		addMessage(message);

		startActivity(intent);
	}

	private void addMessage(String message) {
		adapter.remove(message);
		adapter.insert(message, 0);

		saveMessages();
	}

	private void loadMessages() {
		SharedPreferences settings = getSharedPreferences(APP_NAME, MODE_PRIVATE);

		adapter.clear();
		int i = 0;
		String message = null;
		while ((message = settings.getString(PREFERENCES_MESSAGE_PREFIX + i, null)) != null) {
			if (message.trim().length() > 0)
				adapter.add(message);
			i++;
		}

		if (messageText.getText().length() == 0 && adapter.getCount() > 0) {
			messageText.setText(adapter.getItem(0));
		}
	}

	private void saveMessages() {
		SharedPreferences settings = getSharedPreferences(APP_NAME, MODE_PRIVATE);
		Editor editor = settings.edit();

		for (int i = 0; i < adapter.getCount(); i++) {
			editor.putString(PREFERENCES_MESSAGE_PREFIX + i, adapter.getItem(i));
		}

		// remove next items if exist
		int i = adapter.getCount();
		while (settings.contains(PREFERENCES_MESSAGE_PREFIX + i)) {
			editor.remove(PREFERENCES_MESSAGE_PREFIX + i);
			i++;
		}

		editor.commit();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (!(view instanceof TextView)) {
			// footer selected
			return false;
		}
		
		String message = adapter.getItem(position);
		adapter.remove(message);

		Toast.makeText(this, getResources().getString(R.string.item_deleted, message), Toast.LENGTH_LONG).show();

		saveMessages();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (!(view instanceof TextView)) {
			// footer selected
			return;
		}
		
		String message = adapter.getItem(position);
		messageText.setText(message);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.main_activity_option_menu_preferences:
				startActivity(new Intent(this, PreferenceActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}