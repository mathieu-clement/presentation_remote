package com.mathieuclement.android.teclado.app.actions;

import com.mathieuclement.api.presentation_remote.PresentationClient;

import java.io.IOException;

public class WhiteScreenAction extends Action {
    public WhiteScreenAction(PresentationClient receiver) {
        super(receiver);
    }

    @Override
    public void execute() throws ActionException {
        try {
            receiver.toggleScreenWhite();
        } catch (IOException e) {
            throw new ActionException("Could not toggle screen white", e);
        }
    }
}
