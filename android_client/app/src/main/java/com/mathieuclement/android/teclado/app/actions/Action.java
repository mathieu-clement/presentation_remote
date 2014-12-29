package com.mathieuclement.android.teclado.app.actions;

import com.mathieuclement.api.presentation_remote.PresentationClient;

public abstract class Action {
    protected PresentationClient receiver;

    public Action(PresentationClient receiver) {
        this.receiver = receiver;
    }

    public abstract void execute() throws ActionException;
}
