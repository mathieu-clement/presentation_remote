package com.mathieuclement.android.teclado.app.activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mathieuclement.android.teclado.app.R;
import com.mathieuclement.android.teclado.app.TecladoApp;
import com.mathieuclement.android.teclado.app.actions.*;
import com.mathieuclement.api.presentation_remote.PresentationClient;

import java.net.SocketException;
import java.net.UnknownHostException;


public class RemoteControlActivity extends Activity {

    private PresentationClient mReceiver;
    private static final String TAG = "RemoteControl";
    private TextView stopWatchTextView;

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

            // Set custom font for stop watch
            stopWatchTextView = (TextView) findViewById(R.id.txtView_stopwatch);
            Typeface font = Typeface.createFromAsset(getAssets(), "digital-7.ttf");
            stopWatchTextView.setTypeface(font);
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
            case R.id.rc_btn_start:
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
            default:
                Toast.makeText(TecladoApp.getContext(), "This action is not yet implemented.", Toast.LENGTH_SHORT).show();
                return;
        }

        new ActionAsyncTask(this).execute(action);
    }
}
