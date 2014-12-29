package com.mathieuclement.android.teclado.app.actions;

import com.mathieuclement.api.presentation_remote.KeyCode;
import com.mathieuclement.api.presentation_remote.PresentationClient;

import java.io.IOException;

public class LeftAction extends Action {
    public LeftAction(PresentationClient receiver) {
        super(receiver);
    }

    @Override
    public void execute() throws ActionException {
        try {
            receiver.sendKey(KeyCode.LEFT);
        } catch (IOException e) {
            throw new ActionException("Could not press left arrow key", e);
        }
    }
}
