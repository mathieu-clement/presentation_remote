package com.mathieuclement.android.teclado.app.actions;

import com.mathieuclement.api.presentation_remote.KeyCode;
import com.mathieuclement.api.presentation_remote.PresentationClient;

import java.io.IOException;

public class UpAction extends Action {
    public UpAction(PresentationClient receiver) {
        super(receiver);
    }

    @Override
    public void execute() throws ActionException {
        try {
            receiver.sendKey(KeyCode.UP);
        } catch (IOException e) {
            throw new ActionException("Could not press up arrow key", e);
        }
    }
}
