package com.mathieuclement.android.teclado.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.*;
import android.preference.PreferenceManager;
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

    private boolean mustKeepDisplayOn;
    private String host;
    private int port;
    private boolean volPlusIsNextSlide;
    private SharedPreferences appPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_remote_control);
        //init();
    }

    private void init() {
        // Set custom font for reset watch
        stopWatchTextView = (TextView) findViewById(R.id.txtView_stopwatch);
        if(stopWatchTextView == null) {
            setContentView(R.layout.activity_remote_control);
            stopWatchTextView = (TextView) findViewById(R.id.txtView_stopwatch);
        }
        Typeface font = Typeface.createFromAsset(getAssets(),
                "digital-7 (mono).ttf");
        stopWatchTextView.setTypeface(font);

        getActionBar().setLogo(R.drawable.ic_launcher);
        getActionBar().setDisplayUseLogoEnabled(true);

        stopWatchStartPauseImageButton = (ImageButton) findViewById(R.id.stopwatch_btn_startpause);

        appPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        setSettings();
        if ("".equals(host)) {
            replaceViewWithText("Thank you for installing Teclado!\n" +
                    "Click the wrench icon to set the server details.");
            return;
        }

        refreshSettings();
    }

    private void refreshSettings() {
        getActionBar().setSubtitle(host);

        new AsyncTask<Void, Exception, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mReceiver = new PresentationClient(host, port);
                } catch (SocketException | UnknownHostException e) {
                    publishProgress(e);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Exception... values) {
                for (Exception e : values) {
                    Toast.makeText(RemoteControlActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    replaceViewWithText("Server configuration is invalid or network is unreachable.");
                    e.printStackTrace();
                }
            }
        }.execute();

        // Keep screen on
        if (mustKeepDisplayOn && this.mWakeLock == null) {
            final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
            this.mWakeLock.acquire();
        } else if (!mustKeepDisplayOn && this.mWakeLock != null) {
            this.mWakeLock.release();
        }
    }

    private void setSettings() {
        host = appPrefs.getString(TecladoPreferenceActivity.PREF_HOST, "");
        port = Integer.parseInt(appPrefs.getString(TecladoPreferenceActivity.PREF_PORT, "-1"));
        mustKeepDisplayOn = appPrefs.getBoolean(TecladoPreferenceActivity.PREF_KEEP_DISPLAY_ON, false);
        volPlusIsNextSlide = appPrefs.getBoolean(TecladoPreferenceActivity.PREF_VOL_PLUS_IS_NEXT_SLIDE, true);
    }

    private void replaceViewWithText(String text) {
        RelativeLayout topLayout = (RelativeLayout) findViewById(R.id.topLayout_remote_control);
        topLayout.removeAllViews();
        TextView errTxtView = new TextView(this);
        errTxtView.setText(text);
        topLayout.addView(errTxtView);
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

        switch (item.getItemId()) {
            case R.id.action_help:
                startActivity(new Intent(RemoteControlActivity.this,
                        HelpActivity.class));
                break;

            case R.id.action_settings:
                startActivity(new Intent(RemoteControlActivity.this,
                        TecladoPreferenceActivity.class));
                break;

            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // React on Volume UP and Volume DOWN
        // This can be tried in the emulator by pressing + and - on the numpad
        // As found on: http://stackoverflow.com/a/2875006/753136
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (volPlusIsNextSlide)
                        nextSlide();
                    else
                        previousSlide();
                }
                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (volPlusIsNextSlide)
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
                int minutes = duration.toStandardMinutes().getMinutes() % 100;
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
            return super.onKeyUp(rawKeyCode, event);

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

        init();
    }

    @Override
    public void onDestroy() {
        if (mustKeepDisplayOn && this.mWakeLock != null) {
            this.mWakeLock.release();
        }
        super.onDestroy();
    }
}
