package com.mathieuclement.android.teclado.app.actions;

import com.mathieuclement.api.presentation_remote.PresentationClient;

import java.io.IOException;

public class FirstSlideAction extends Action {
    public FirstSlideAction(PresentationClient receiver) {
        super(receiver);
    }

    @Override
    public void execute() throws ActionException {
        try {
            receiver.goToFirstSlide();
        } catch (IOException e) {
            throw new ActionException("Could not go to first slide", e);
        }
    }
}
