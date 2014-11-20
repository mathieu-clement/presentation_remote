package com.mathieuclement.api.presentation_remote;

public enum Modifier {
    SHIFT(0x10),
    CTRL(0x11),
    ALT(0x12),
    LEFT_WINDOWS(KeyCode.LEFT_WINDOWS.getVkCode()),
    RIGHT_WINDOWS(KeyCode.RIGHT_WINDOWS.getVkCode());

    private int vkCode; // Microsoft VK Code
    // http://msdn.microsoft.com/en-us/library/windows/desktop/dd375731%28v=vs.85%29.aspx

    // Uppercase letters can be used as key code
    // or digits (matching the keys below the function keys)


    /**
     * Returns the Microsoft VK code for the Windows API (windows.h).
     * @return Microsoft VK code
     */
    int getVkCode() {
        return vkCode;
    }

    // Associate Microsoft VK code to KeyCode instance.
    private Modifier(int vkCode) {
        this.vkCode = vkCode;
    }

}
