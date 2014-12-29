package com.mathieuclement.android.teclado.app.actions;

import com.mathieuclement.api.presentation_remote.PresentationClient;

import java.io.IOException;

public class StartPresentationAction extends Action {
    public StartPresentationAction(PresentationClient receiver) {
        super(receiver);
    }

    @Override
    public void execute() throws ActionException {
        try {
            receiver.startPresentation();
        } catch (IOException e) {
            throw new ActionException("Could not start presentation", e);
        }
    }
}
