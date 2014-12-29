package com.mathieuclement.android.teclado.app.actions;

import android.view.View;

public class ActionOnClickListener implements View.OnClickListener {

    private Action action;

    public ActionOnClickListener(Action action) {
        this.action = action;
    }

    @Override
    public void onClick(View v) {
        new ActionAsyncTask(v.getContext()).execute(action);
    }
}
