package com.mathieuclement.android.teclado.app.actions;

import android.widget.Toast;
import com.mathieuclement.android.teclado.app.TecladoApp;
import com.mathieuclement.api.presentation_remote.PresentationClient;

public class UnimplementedAction extends Action {
    public UnimplementedAction(PresentationClient receiver) {
        super(receiver);
    }

    @Override
    public void execute() throws ActionException {
        Toast.makeText(TecladoApp.getContext(), "This action is not yet implemented.", Toast.LENGTH_SHORT).show();
    }
}
