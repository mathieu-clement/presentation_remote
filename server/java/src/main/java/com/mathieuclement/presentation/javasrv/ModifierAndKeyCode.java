package com.mathieuclement.presentation.javasrv;

public class ModifierAndKeyCode {
    private final int modifier;
    private final int keyCode;

    public ModifierAndKeyCode(int modifier, int keyCode) {
        this.modifier = modifier;
        this.keyCode = keyCode;
    }

    public int getModifier() {
        return modifier;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
