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
    LEFT_WINDOWS(0x5B),
    RIGHT_WINDOWS(0x5C),
    ADD(0x6B),
    COMMA(0xBC),
    SUBTRACT(0xBD),
    MULTIPLY(0x6A),
    DIVIDE(0x6F),
    PERIOD(0xBE), DOT(0xBE),
    FUNCTION_1(0x70),
    FUNCTION_2(0x71),
    FUNCTION_3(0x72),
    FUNCTION_4(0x73),
    FUNCTION_5(0x74),
    FUNCTION_6(0x75),
    FUNCTION_7(0x76),
    FUNCTION_8(0x77),
    FUNCTION_9(0x78),
    FUNCTION_10(0x79),
    FUNCTION_11(0x7A),
    FUNCTION_12(0x7B),
    SLEEP(0x5F),
    DIGIT_0(0x30),
    DIGIT_1(0x31),
    DIGIT_2(0x32),
    DIGIT_3(0x33),
    DIGIT_4(0x34),
    DIGIT_5(0x35),
    DIGIT_6(0x36),
    DIGIT_7(0x37),
    DIGIT_8(0x38),
    DIGIT_9(0x39),
    A(0x41),
    B(0x42),
    C(0x43),
    D(0x44),
    E(0x45),
    F(0x46),
    G(0x47),
    H(0x48),
    I(0x49),
    J(0x4A),
    K(0x4B),
    L(0x4C),
    M(0x4D),
    N(0x4E),
    O(0x4F),
    P(0x50),
    Q(0x51),
    R(0x52),
    S(0x53),
    T(0x54),
    U(0x55),
    V(0x56),
    W(0x57),
    X(0x58),
    Y(0x59),
    Z(0x5A),
    NUMPAD_0(0x60),
    NUMPAD_1(0x61),
    NUMPAD_2(0x62),
    NUMPAD_3(0x63),
    NUMPAD_4(0x64),
    NUMPAD_5(0x65),
    NUMPAD_6(0x66),
    NUMPAD_7(0x67),
    NUMPAD_8(0x68),
    NUMPAD_9(0x69);


    private int vkCode; // Microsoft VK Code
                        // http://msdn.microsoft.com/en-us/library/windows/desktop/dd375731%28v=vs.85%29.aspx

    // Uppercase letters can be used as key code
    // or digits (matching the keys below the function keys)

    /**
     * Create keycode from letter
     * @param letter lowercase or uppercase letter
     * @return keycode for the letter key
     */
    public KeyCode createFromLetter(char letter) {
        char upper = Character.toUpperCase(letter);
        return KeyCode.valueOf(String.format("%c", upper));
    }

    /**
     * Create keycode from operation
     * @param operation '+', '-', '*', '/'
     * @return keycode
     */
    public KeyCode createFromOperation(char operation) {
        switch (operation) {
            case '+': return KeyCode.ADD;
            case '-': return KeyCode.SUBTRACT;
            case '*': return KeyCode.MULTIPLY;
            case '/': return KeyCode.DIVIDE;

            default:
                throw new IllegalArgumentException("" + operation + " is not an operation.");
        }
    }

    /**
     * Create keycode from punctuation
     * @param punctuation '.', ','
     * @return keycode
     */
    public KeyCode createFromPunctuation(char punctuation) {
        switch (punctuation) {
            case '.': return KeyCode.PERIOD;
            case ',': return KeyCode.COMMA;

            default:
                throw new IllegalArgumentException("" + punctuation + " is not a known punctuation character.");
        }
    }


    /**
     * Create keycode from digit (matching the keys on the row
     * below the function keys)
     * @param digit digit
     * @return keycode matching the key below the function keys
     */
    public KeyCode createRowKeyCodeFromDigit(int digit) {
        return KeyCode.valueOf(String.format("DIGIT_%d", digit));
    }

    // Uppercase letters can be used as key code
    // or digits (matching the keys below the function keys)
    public KeyCode createKeypadKeyCodeFromDigit(int digit) {
        return KeyCode.valueOf(String.format("NUMPAD_%d", digit));
    }

    // Associate Microsoft VK code to KeyCode instance.
    private KeyCode(int vkCode) {
        this.vkCode = vkCode;
    }

    /**
     * Returns the Microsoft VK code for the Windows API (windows.h).
     * @return Microsoft VK code
     */
    int getVkCode() {
        return vkCode;
    }
}
