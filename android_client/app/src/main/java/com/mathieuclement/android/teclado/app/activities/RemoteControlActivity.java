package com.mathieuclement.android.teclado.app.activities;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mathieuclement.android.teclado.app.R;
import com.mathieuclement.android.teclado.app.TecladoApp;
import com.mathieuclement.android.teclado.app.actions.*;
import com.mathieuclement.android.teclado.app.utils.StopWatch;
import com.mathieuclement.api.presentation_remote.KeyCode;
import com.mathieuclement.api.presentation_remote.PresentationClient;

import org.joda.time.Duration;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class RemoteControlActivity extends Activity {

	private PresentationClient mReceiver;
	private static final String TAG = "RemoteControl";

	private TextView stopWatchTextView;
	private ImageButton stopWatchStartPauseImageButton;
	private Handler stopWatchHandler = new StopWatchHandler();
	/*
	 * private static final PeriodFormatter stopWatchFormatter = new
	 * PeriodFormatterBuilder() .printZeroAlways()
	 * .minimumPrintedDigits(2).appendMinutes() .appendSeparator(":")
	 * .minimumPrintedDigits(2).appendSeconds() .toFormatter();
	 */
	private static final int STOPWATCH_REFRESH_RATE = 1000; // ms
	private static final int STOPWATCH_UPDATE_MSG = 1;
	private static final int STOPWATCH_STOP_UPDATES_MSG = 2;
	private StopWatch stopWatch = new StopWatch();

	protected PowerManager.WakeLock mWakeLock; // keep screen on

	private boolean isKeepDisplayOn;
	private String host;
	private String title;
	private int port;
	private boolean isUPNext;

	private static final String DEFAULT_HOST = "127.0.0.0";
	private static final String DEFAULT_TITLE = "MyComputer";
	private static final int DEFAULT_PORT = 1200;
	private static final boolean DEFAULT_ISKEEPDISPLAYON = false;
	private static final boolean DEFAULT_ISUPNEXT = false;

	final String EXTRA_host = "exhost";
	final String EXTRA_title = "extitle";
	final String EXTRA_port = "extport";
	final String EXTRA_isUPNext = "exisUPNext";
	final String EXTRA_isKeepDisplayOn = "exisKeepDisplayOn";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_control);
		init();
	}

	private void init() {
		SharedPreferences appPrefs = this.getSharedPreferences("myPrefs",
				MODE_WORLD_READABLE);
		host = appPrefs.getString(EXTRA_host, DEFAULT_HOST);
		title = appPrefs.getString(EXTRA_title, DEFAULT_TITLE);
		port = appPrefs.getInt(EXTRA_port, DEFAULT_PORT);
		isKeepDisplayOn = appPrefs.getBoolean(EXTRA_isKeepDisplayOn,
				DEFAULT_ISKEEPDISPLAYON);
		isUPNext = appPrefs.getBoolean(EXTRA_isUPNext, DEFAULT_ISUPNEXT);

		// load settings
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setSubtitle(title); // TODO
		getActionBar().setLogo(R.drawable.ic_launcher);
		getActionBar().setDisplayUseLogoEnabled(true);

		try {
			mReceiver = new PresentationClient(host); // TODO

			// Set custom font for reset watch
			stopWatchTextView = (TextView) findViewById(R.id.txtView_stopwatch);
			Typeface font = Typeface.createFromAsset(getAssets(),
					"digital-7 (mono).ttf");
			stopWatchTextView.setTypeface(font);

			stopWatchStartPauseImageButton = (ImageButton) findViewById(R.id.stopwatch_btn_startpause);

			// Keep screen on
			if (isKeepDisplayOn) {
				final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				this.mWakeLock = pm.newWakeLock(
						PowerManager.SCREEN_DIM_WAKE_LOCK, "Teclado");
				this.mWakeLock.acquire();
			}

			// get preferences

		} catch (SocketException | UnknownHostException e) {
			// Toast.makeText(this, "Server configuration is invalid",
			// Toast.LENGTH_LONG).show();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			RelativeLayout topLayout = (RelativeLayout) findViewById(R.id.topLayout_remote_control);
			topLayout.removeAllViews();
			TextView errTxtView = new TextView(this);
			errTxtView
					.setText("Server configuration is invalid or network is unreachable.");
			topLayout.addView(errTxtView);
			e.printStackTrace();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_remote_control, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		// TODO

		switch (item.getItemId()) {
		case R.id.action_help:
			Intent intent2 = new Intent(RemoteControlActivity.this,
					HelpActivity.class);
			startActivity(intent2);
			break;
		case R.id.action_settings:
			Intent intent = new Intent(RemoteControlActivity.this,
					SettingsActivity.class);
			intent.putExtra(EXTRA_host, host);
			intent.putExtra(EXTRA_isKeepDisplayOn, isKeepDisplayOn);
			intent.putExtra(EXTRA_isUPNext, isUPNext);
			intent.putExtra(EXTRA_title, title);
			intent.putExtra(EXTRA_port, port);
			startActivityForResult(intent, 1);
			break;
		/*
		 * case android.R.id.home: Toast.makeText(this, "-- Home --",
		 * Toast.LENGTH_SHORT).show(); break;
		 */
		default:
			return false;
		}

		return true;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// React on Volume UP and Volume DOWN
		// This can be tried in the emulator by pressing + and - on the numpad
		// TODO Add setting to use opposite behavior
		// As found on: http://stackoverflow.com/a/2875006/753136
		int action = event.getAction();
		int keyCode = event.getKeyCode();

		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (action == KeyEvent.ACTION_DOWN) {
				if (isUPNext)
					nextSlide();
				else
					previousSlide();
			}
			return true;

		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (action == KeyEvent.ACTION_DOWN) {
				if (isUPNext)
					previousSlide();
				else
					nextSlide();
			}
			return true;

		default:
			return super.dispatchKeyEvent(event);
		}
	}

	// Call a next slide action
	private void nextSlide() {
		Toast.makeText(this, "Next slide", Toast.LENGTH_SHORT).show();
		new ActionAsyncTask(this).execute(new RightAction(mReceiver));
	}

	private void previousSlide() {
		Toast.makeText(this, "Previous slide", Toast.LENGTH_SHORT).show();
		new ActionAsyncTask(this).execute(new LeftAction(mReceiver));
	}

	// Method called by buttons from the view
	public void performAction(View source) {
		Action action;

		switch (source.getId()) {
		case R.id.rc_btn_start_presentation:
			action = new StartPresentationAction(mReceiver);
			break;
		case R.id.rc_btn_up:
			action = new UpAction(mReceiver);
			break;
		case R.id.rc_btn_left:
			action = new LeftAction(mReceiver);
			break;
		case R.id.rc_btn_right:
			action = new RightAction(mReceiver);
			break;
		case R.id.rc_btn_down:
			action = new DownAction(mReceiver);
			break;
		case R.id.rc_btn_black_screen:
			action = new BlackScreenAction(mReceiver);
			break;
		case R.id.rc_btn_white_screen:
			action = new WhiteScreenAction(mReceiver);
			break;
		case R.id.rc_btn_first_slide:
			action = new FirstSlideAction(mReceiver);
			break;
		case R.id.rc_img_btn_keyboard:
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
					InputMethodManager.HIDE_IMPLICIT_ONLY);
			return;
		case R.id.stopwatch_btn_reset:
			stopWatch.reset();
			stopWatchTextView.setText("00:00");
			updateStopWatchPlayPauseButtonIcon();
			stopWatchHandler.sendEmptyMessage(STOPWATCH_STOP_UPDATES_MSG);
			return;
		case R.id.stopwatch_btn_startpause:
			if (stopWatch.isRunning()) {
				stopWatch.pause();
				stopWatchHandler.sendEmptyMessage(STOPWATCH_STOP_UPDATES_MSG);
			} else {
				if (stopWatch.isStarted()) {
					stopWatch.resume();
				} else {
					stopWatch.start();
				}
				stopWatchHandler.sendEmptyMessage(STOPWATCH_UPDATE_MSG);
			}
			updateStopWatchPlayPauseButtonIcon();
			return;
		default:
			Toast.makeText(TecladoApp.getContext(),
					"This action is not yet implemented.", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		new ActionAsyncTask(this).execute(action);
	}

	// Update stopwatch play / pause icon based on stopwatch current state
	private void updateStopWatchPlayPauseButtonIcon() {
		if (stopWatch.isRunning()) {
			stopWatchStartPauseImageButton
					.setImageResource(android.R.drawable.ic_media_pause);
		} else {
			stopWatchStartPauseImageButton
					.setImageResource(android.R.drawable.ic_media_play);
		}
	}

	private class StopWatchHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == STOPWATCH_UPDATE_MSG) {
				Duration duration = stopWatch.getElapsedDuration();
				int minutes = duration.toStandardMinutes().getMinutes();
				int seconds = duration.toStandardSeconds().getSeconds() % 60;
				stopWatchTextView.setText(String.format("%02d:%02d", minutes,
						seconds));
				this.sendEmptyMessageDelayed(STOPWATCH_UPDATE_MSG,
						STOPWATCH_REFRESH_RATE);
			} else if (msg.what == STOPWATCH_STOP_UPDATES_MSG) {
				this.removeMessages(STOPWATCH_UPDATE_MSG);
			}
		}
	}

	@Override
	public boolean onKeyUp(final int rawKeyCode, final KeyEvent event) {
		// Ignore power button, back button, equal sign, etc.
		if (rawKeyCode == KeyEvent.KEYCODE_DEL) {
			// Accept back space
		} else if (rawKeyCode < KeyEvent.KEYCODE_0
				|| rawKeyCode > KeyEvent.KEYCODE_Z)
			return false;

		new ActionAsyncTask(this).execute(new Action(mReceiver) {
			@Override
			public void execute() throws ActionException {
				try {
					KeyCode libKeyCode;

					switch (rawKeyCode) {
					case KeyEvent.KEYCODE_ENTER:
						libKeyCode = KeyCode.ENTER;
						break;

					case KeyEvent.KEYCODE_DEL: // Backspace
						libKeyCode = KeyCode.BACKSPACE;
						break;

					default:
						char c = (char) event.getUnicodeChar();
						if ((event.isShiftPressed() || event.isCapsLockOn() || event
								.isMetaPressed()) && Character.isAlphabetic(c)) {
							c = Character.toUpperCase(c);
						}
						libKeyCode = KeyCode.createFromChar(c);
					}
					this.receiver.sendKey(libKeyCode);
				} catch (IOException e) {
					throw new ActionException(
							"Could not send key to the server");
				} catch (Exception e) {
					throw new ActionException(
							"Server doesn't know what to do with key "
									+ rawKeyCode);
				}
			}
		});
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (stopWatch.isRunning())
			stopWatchHandler.sendEmptyMessage(STOPWATCH_STOP_UPDATES_MSG);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (stopWatch.isRunning())
			stopWatchHandler.sendEmptyMessage(STOPWATCH_UPDATE_MSG);
	}

	@Override
	public void onDestroy() {
		this.mWakeLock.release();
		super.onDestroy();
	}
}
