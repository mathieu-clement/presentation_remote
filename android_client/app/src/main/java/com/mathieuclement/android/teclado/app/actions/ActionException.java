package com.mathieuclement.android.teclado.app.actions;

public class ActionException extends Exception {
    public ActionException() {
    }

    public ActionException(String detailMessage) {
        super(detailMessage);
    }

    public ActionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ActionException(Throwable throwable) {
        super(throwable);
    }
}
