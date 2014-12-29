package com.mathieuclement.android.teclado.app.actions;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class ActionAsyncTask extends AsyncTask<Action, Exception, Void> {

    private Context context;

    public ActionAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Action... params) {
        for (Action action : params) {
            try {
                action.execute();
            } catch (ActionException e) {
                publishProgress(e);
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Exception... values) {
        for (Exception e : values) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
