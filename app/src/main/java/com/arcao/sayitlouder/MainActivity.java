package com.arcao.sayitlouder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import de.timroes.android.listview.EnhancedListView;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements OnItemClickListener, OnItemLongClickListener, EnhancedListView.OnDismissCallback, EnhancedListView.OnShouldSwipeCallback {

	private static final String PREFERENCES_MESSAGE_PREFIX = "message_";
	private static final String APP_NAME = "SayItLoud";
	private static final String STATE_MESSAGE = "message";

	private TextView messageText;
	private ImageButton buttonShow;
	private ArrayAdapter<String> adapter;
	private EnhancedListView list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		messageText = (TextView) findViewById(R.id.message);
		list = (EnhancedListView) findViewById(R.id.list);
		buttonShow = (ImageButton) findViewById(R.id.show);

		list.setDismissCallback(this);
		list.setShouldSwipeCallback(this);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);
		list.setRequireTouchBeforeDismiss(false);
		list.setUndoStyle(EnhancedListView.UndoStyle.MULTILEVEL_POPUP);
		list.enableSwipeToDismiss();

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
		} else if (savedInstanceState != null) {
			CharSequence message = savedInstanceState.getCharSequence(STATE_MESSAGE);
			messageText.setText(message != null ? message : "");
		} else {
			messageText.setText("");
		}
		
		list.addFooterView(getLayoutInflater().inflate(R.layout.main_footer, null));

		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
		list.setAdapter(adapter);

		loadMessages();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (messageText.getText() != null) {
			outState.putCharSequence(STATE_MESSAGE, messageText.getText());
		}
	}

	public void buttonShow_onClick(View view) {
		if (messageText.getText() == null)
			return;

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
		String message;
		while ((message = settings.getString(PREFERENCES_MESSAGE_PREFIX + i, null)) != null) {
			if (message.trim().length() > 0)
				adapter.add(message);
			i++;
		}

		if ((messageText.getText() == null || messageText.getText().length() == 0) && adapter.getCount() > 0) {
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
		if (position >= adapter.getCount()) {
			// footer selected
			return false;
		}

		list.delete(position);
		return true;
	}

	@Override
	public boolean onShouldSwipe(EnhancedListView enhancedListView, int position) {
		return position < adapter.getCount();
	}

	@Override
	public EnhancedListView.Undoable onDismiss(EnhancedListView enhancedListView, final int position) {
		final String message = adapter.getItem(position);
		adapter.remove(message);

		// return an Undoable
		return new EnhancedListView.Undoable() {
			// Reinsert the item to the adapter
			@Override public void undo() {
				adapter.insert(message, position);
			}

			// Return a string for your item
			@Override public String getTitle() {
				return getResources().getString(R.string.item_deleted, message);
			}

			// Delete item completely from your persistent storage
			@Override public void discard() {
				saveMessages();
			}
		};
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position >= adapter.getCount()) {
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