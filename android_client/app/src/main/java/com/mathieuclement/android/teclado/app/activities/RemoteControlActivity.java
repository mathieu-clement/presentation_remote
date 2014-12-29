package com.mathieuclement.android.teclado.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import com.mathieuclement.android.teclado.app.R;
import com.mathieuclement.android.teclado.app.actions.*;
import com.mathieuclement.android.teclado.app.views.RemoteControlButton;
import com.mathieuclement.api.presentation_remote.PresentationClient;

import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.net.UnknownHostException;


public class RemoteControlActivity extends Activity {

    private TableLayout mTableLayout;
    private PresentationClient mReceiver;

    private final static int LEFT_COLUMN = 0;
    private final static int CENTER_COLUMN = 1;
    private final static int RIGHT_COLUMN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setSubtitle("MyComputer"); // TODO
        getActionBar().setDisplayUseLogoEnabled(true);

        try {
            mReceiver = new PresentationClient(null); // TODO
            mTableLayout = (TableLayout) findViewById(R.id.tableLayout_remote_control);

            // Make columns take all the "wideness" available
            mTableLayout.setColumnStretchable(LEFT_COLUMN, true);
            mTableLayout.setColumnStretchable(CENTER_COLUMN, true);
            mTableLayout.setColumnStretchable(RIGHT_COLUMN, true);

            createButtons();
        } catch (SocketException | UnknownHostException e) {
            //Toast.makeText(this, "Server configuration is invalid", Toast.LENGTH_LONG).show();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            RelativeLayout topLayout = (RelativeLayout) findViewById(R.id.topLayout_remote_control);
            TextView errTxtView = new TextView(this);
            errTxtView.setText("Server configuration is invalid or network is unreachable.");
            topLayout.addView(errTxtView);
            e.printStackTrace();
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            Toast.makeText(this, "Could not load the remote control buttons.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void createButtons() throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {

        // Row 1
        TableRow row1 = new TableRow(this);
        mTableLayout.addView(row1);
        row1.addView(createRemoteControlButton("Start", LEFT_COLUMN, StartPresentationAction.class));

        // Row 2
        TableRow row2 = new TableRow(this);
        mTableLayout.addView(row2);
        row2.addView(createRemoteControlButton("\u25b2", CENTER_COLUMN, UpAction.class));

        // Row 3
        TableRow row3 = new TableRow(this);
        mTableLayout.addView(row3);
        row3.addView(createRemoteControlButton("\u25c0", LEFT_COLUMN, LeftAction.class));
        row3.addView(createRemoteControlButton("\u25b6", RIGHT_COLUMN, RightAction.class));

        // Row 4
        TableRow row4 = new TableRow(this);
        mTableLayout.addView(row4);
        row4.addView(createRemoteControlButton("\u25bc", CENTER_COLUMN, DownAction.class));

        // Row 5
        TableRow row5 = new TableRow(this);
        mTableLayout.addView(row5);
        row5.addView(createRemoteControlButton("\u2b1b", LEFT_COLUMN, BlackScreenAction.class));
        row5.addView(createRemoteControlButton("\u2b1c", CENTER_COLUMN, WhiteScreenAction.class));
        row5.addView(createRemoteControlButton("\u2328", RIGHT_COLUMN, UnimplementedAction.class)); // Keyboard
    }

    public Button createRemoteControlButton(String text, int column,
                                            Class<? extends Action> actionClass)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        RemoteControlButton button = new RemoteControlButton(this);
        button.setLayoutParams(new TableRow.LayoutParams(column));
        button.setText(text);
        if (text.length() < 4) {
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        }
        Action action = actionClass.getConstructor(PresentationClient.class).newInstance(mReceiver);
        button.setOnClickListener(new ActionOnClickListener(action));
        return button;
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Home button clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
