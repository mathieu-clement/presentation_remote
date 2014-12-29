package com.mathieuclement.android.teclado.app.activities;

import android.app.Activity;
import android.os.Bundle;
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
        row1.addView(createRemoteControlButton("Start", StartPresentationAction.class));

        // Row 2
        TableRow row2 = new TableRow(this);
        mTableLayout.addView(row2);
        row2.addView(createRemoteControlButton("U", UpAction.class), 1); // 1 = 2nd column = middle column

        // Row 3
        TableRow row3 = new TableRow(this);
        mTableLayout.addView(row3);
        row3.addView(createRemoteControlButton("L", LeftAction.class), 0);
        row3.addView(createRemoteControlButton("R", RightAction.class), 2); // 2 = 3rd column

        // Row 4
        TableRow row4 = new TableRow(this);
        mTableLayout.addView(row4);
        row4.addView(createRemoteControlButton("D", DownAction.class), 1);
    }

    public Button createRemoteControlButton(String text,
                                            Class<? extends Action> actionClass)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        RemoteControlButton button = new RemoteControlButton(this);
        button.setText(text);
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
