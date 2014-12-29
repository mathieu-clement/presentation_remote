package com.mathieuclement.android.teclado.app.actions;

import com.mathieuclement.api.presentation_remote.PresentationClient;

import java.io.IOException;

public class BlackScreenAction extends Action {
    public BlackScreenAction(PresentationClient receiver) {
        super(receiver);
    }

    @Override
    public void execute() throws ActionException {
        try {
            receiver.toggleScreenBlack();
        } catch (IOException e) {
            throw new ActionException("Could not toggle screen black", e);
        }
    }
}
