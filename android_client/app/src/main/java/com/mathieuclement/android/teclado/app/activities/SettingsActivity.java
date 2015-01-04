package com.mathieuclement.android.teclado.app.activities;

import com.mathieuclement.android.teclado.app.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	final String EXTRA_host = "exhost";
	final String EXTRA_title = "extitle";
	final String EXTRA_port = "extport";
	final String EXTRA_isUPNext = "exisUPNext";
	final String EXTRA_isKeepDisplayOn = "exisKeepDisplayOn";

	EditText editTextHost;
	EditText editTextPort;
	EditText editTextTitle;
	CheckBox checkBoxKeepScreenOn;
	CheckBox checkBoxInvert;
	Button save;
	Button cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		editTextHost = (EditText) findViewById(R.id.host_settings);
		editTextPort = (EditText) findViewById(R.id.port_settings);
		editTextTitle = (EditText) findViewById(R.id.title_settings);
		checkBoxKeepScreenOn = (CheckBox) findViewById(R.id.checkBox_keepDisplay);
		checkBoxInvert = (CheckBox) findViewById(R.id.checkBox_invertNextPrev);
		save = (Button) findViewById(R.id.button_save_settings);
		cancel = (Button) findViewById(R.id.button_cancel_settings);

		Intent intent = getIntent();
		if (intent != null) {
			editTextHost.setText(intent.getStringExtra(EXTRA_host));
			editTextTitle.setText(intent.getStringExtra(EXTRA_title));
			editTextPort.setText("" + intent.getIntExtra(EXTRA_port, 1200));
			checkBoxKeepScreenOn.setChecked(intent.getBooleanExtra(
					EXTRA_isKeepDisplayOn, false));
			checkBoxInvert.setChecked(intent.getBooleanExtra(EXTRA_isUPNext,
					false));

		}

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveSettings();
				finish();
			}
		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private void saveSettings() {

		String host = editTextHost.getText().toString();
		String title = editTextTitle.getText().toString();
		String port = editTextPort.getText().toString();
		int num_port = Integer.parseInt(port);
		Boolean isInvert = checkBoxInvert.isChecked();
		Boolean isKeepDisplayOn = checkBoxKeepScreenOn.isChecked();

		SharedPreferences myPrefs = this.getSharedPreferences("myPrefs",
				MODE_WORLD_READABLE);
		SharedPreferences.Editor prefsEditor = myPrefs.edit();
		prefsEditor.putString(EXTRA_host, host);
		prefsEditor.putString(EXTRA_title, title);
		prefsEditor.putInt(EXTRA_port, num_port);
		prefsEditor.putBoolean(EXTRA_isUPNext, isInvert);
		prefsEditor.putBoolean(EXTRA_isKeepDisplayOn, isKeepDisplayOn);

		prefsEditor.commit();

	}
}
