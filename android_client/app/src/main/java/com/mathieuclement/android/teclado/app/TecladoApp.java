package com.mathieuclement.android.teclado.app;

import android.app.Application;
import android.content.Context;

public class TecladoApp extends Application {

    private static TecladoApp instance;

    public TecladoApp() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }
}
