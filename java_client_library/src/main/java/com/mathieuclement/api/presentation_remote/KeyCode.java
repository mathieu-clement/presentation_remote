package com.mathieuclement.api.presentation_remote;

public enum KeyCode {
    BACKSPACE(0x08),
    TAB(0x09),
    ENTER(0x0D),
    SPACE(0x20),
    PAGE_UP(0x21),
    PAGE_DOWN(0x22),
    END(0x23),
    HOME(0x24),
    LEFT(0x25),
    RIGHT(0x27),
    UP(0x26),
    DOWN(0x28),
    DELETE(0x2E),
    ;


    private int vkCode; // Microsoft VK Code
                        // http://msdn.microsoft.com/en-us/library/windows/desktop/dd375731%28v=vs.85%29.aspx

    // Uppercase letters can be used as key code
    // or digits (matching the keys below the function keys)
    KeyCode(char letterOrDigit) {
        assert Character.isUpperCase(letterOrDigit) || Character.isDigit(letterOrDigit);
        this.vkCode = letterOrDigit;
    }

    KeyCode(int vkCode) {
        this.vkCode = vkCode;
    }
}
