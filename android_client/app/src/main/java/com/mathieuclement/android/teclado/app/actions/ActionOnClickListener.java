package com.mathieuclement.android.teclado.app.actions;

import android.view.View;
import android.widget.Toast;

public class ActionOnClickListener implements View.OnClickListener {

    private Action action;

    public ActionOnClickListener(Action action) {
        this.action = action;
    }

    @Override
    public void onClick(View v) {
        try {
            action.execute();
        } catch (ActionException e) {
            Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
