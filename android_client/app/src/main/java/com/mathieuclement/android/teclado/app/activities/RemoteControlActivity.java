package com.mathieuclement.android.teclado.app.activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mathieuclement.android.teclado.app.R;
import com.mathieuclement.android.teclado.app.TecladoApp;
import com.mathieuclement.android.teclado.app.actions.*;
import com.mathieuclement.android.teclado.app.utils.StopWatch;
import com.mathieuclement.api.presentation_remote.PresentationClient;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.net.SocketException;
import java.net.UnknownHostException;


public class RemoteControlActivity extends Activity {

    private PresentationClient mReceiver;
    private static final String TAG = "RemoteControl";

    private TextView stopWatchTextView;
    private ImageButton stopWatchStartPauseImageButton;
    private Handler stopWatchHandler = new StopWatchHandler();
    private static final PeriodFormatter stopWatchFormatter = new PeriodFormatterBuilder()
            .minimumPrintedDigits(2).printZeroAlways().appendMinutes()
            .appendSeparator(":")
            .minimumPrintedDigits(2).printZeroAlways().appendSeconds()
            .toFormatter();
    private static final int STOPWATCH_REFRESH_RATE = 1000; // ms
    private static final int STOPWATCH_UPDATE_MSG = 1;
    private static final int STOPWATCH_STOP_UPDATES_MSG = 2;
    private StopWatch stopWatch = new StopWatch();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        //getActionBar().setHomeButtonEnabled(true);
        getActionBar().setSubtitle("MyComputer"); // TODO
        getActionBar().setLogo(R.drawable.ic_launcher);
        getActionBar().setDisplayUseLogoEnabled(true);

        try {
            mReceiver = new PresentationClient(null); // TODO

            // Set custom font for reset watch
            stopWatchTextView = (TextView) findViewById(R.id.txtView_stopwatch);
            Typeface font = Typeface.createFromAsset(getAssets(), "digital-7 (mono).ttf");
            stopWatchTextView.setTypeface(font);

            stopWatchStartPauseImageButton = (ImageButton) findViewById(R.id.stopwatch_btn_startpause);
        } catch (SocketException | UnknownHostException e) {
            //Toast.makeText(this, "Server configuration is invalid", Toast.LENGTH_LONG).show();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            RelativeLayout topLayout = (RelativeLayout) findViewById(R.id.topLayout_remote_control);
            topLayout.removeAllViews();
            TextView errTxtView = new TextView(this);
            errTxtView.setText("Server configuration is invalid or network is unreachable.");
            topLayout.addView(errTxtView);
            e.printStackTrace();
        }
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
                Toast.makeText(this, "-- Help --", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                Toast.makeText(this, "-- Settings --", Toast.LENGTH_SHORT).show();
                break;
            /*
            case android.R.id.home:
                Toast.makeText(this, "-- Home --", Toast.LENGTH_SHORT).show();
                break;
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
                    Log.d(TAG, "Volume up / Next slide");
                    new ActionAsyncTask(this).execute(new RightAction(mReceiver));
                }
                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    Log.d(TAG, "Volume down / Previous slide");
                    new ActionAsyncTask(this).execute(new LeftAction(mReceiver));
                }
                return true;

            default:
                return super.dispatchKeyEvent(event);
        }
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
            case R.id.stopwatch_btn_reset:
                stopWatch.reset();
                stopWatchTextView.setText("00:00");
                stopWatchHandler.sendEmptyMessage(STOPWATCH_STOP_UPDATES_MSG);
                return;
            case R.id.stopwatch_btn_startpause:
                if (stopWatch.isRunning()) {
                    stopWatch.pause();
                    stopWatchStartPauseImageButton.setImageResource(android.R.drawable.ic_media_play);
                    stopWatchHandler.sendEmptyMessage(STOPWATCH_STOP_UPDATES_MSG);
                } else {
                    if (stopWatch.isStarted()) {
                        stopWatch.resume();
                    } else {
                        stopWatch.start();
                    }
                    stopWatchStartPauseImageButton.setImageResource(android.R.drawable.ic_media_pause);
                    stopWatchHandler.sendEmptyMessage(STOPWATCH_UPDATE_MSG);
                }
                return;
            default:
                Toast.makeText(TecladoApp.getContext(), "This action is not yet implemented.", Toast.LENGTH_SHORT).show();
                return;
        }

        new ActionAsyncTask(this).execute(action);
    }

    private class StopWatchHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == STOPWATCH_UPDATE_MSG) {
                stopWatchTextView.setText(stopWatchFormatter.print(stopWatch.getElapsedDuration().toPeriod()));
                this.sendEmptyMessageDelayed(STOPWATCH_UPDATE_MSG, STOPWATCH_REFRESH_RATE);
            } else if (msg.what == STOPWATCH_STOP_UPDATES_MSG) {
                this.removeMessages(STOPWATCH_UPDATE_MSG);
            }
        }
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
}
