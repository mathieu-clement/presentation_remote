#pragma once
#ifndef KEYBOARD_EMULATOR_HPP
#define KEYBOARD_EMULATOR_HPP

#define _WIN32_WINNT 0x410
//#define WINVER 0x0500
#include <windows.h>

namespace keyboard_server {

enum KeyCode { // 1 byte
    KEYCODE_DOWN = VK_DOWN, 
    KEYCODE_UP = VK_UP,
    KEYCODE_RIGHT = VK_RIGHT,
    KEYCODE_LEFT = VK_LEFT,
    KEYCODE_PAGE_DOWN = VK_NEXT,
    KEYCODE_PAGE_UP = VK_PRIOR,
    KEYCODE_ESCAPE = VK_ESCAPE,
    KEYCODE_HOME = VK_HOME,

    KEYCODE_H = 'H',
    KEYCODE_L = 'L',

    KEYCODE_FUNCTION_1  = VK_F1,
    KEYCODE_FUNCTION_2  = VK_F2,
    KEYCODE_FUNCTION_3  = VK_F3,
    KEYCODE_FUNCTION_4  = VK_F4,
    KEYCODE_FUNCTION_5  = VK_F5,
    KEYCODE_FUNCTION_6  = VK_F6,
    KEYCODE_FUNCTION_7  = VK_F7,
    KEYCODE_FUNCTION_8  = VK_F8,
    KEYCODE_FUNCTION_9  = VK_F9,
    KEYCODE_FUNCTION_10 = VK_F10,
    KEYCODE_FUNCTION_11 = VK_F11,
    KEYCODE_FUNCTION_12 = VK_F12,

    KEYCODE_MAGIC = 0xFF // Special non synthetized commands
                         // For instance for server ping
};

enum Modifier { // 1 byte
    // [Our Name] = [Microsoft Virtual Key code]
    MODIFIER_NONE = 0,
    MODIFIER_CTRL = VK_CONTROL,
    MODIFIER_SHIFT = VK_SHIFT,
    MODIFIER_ALT = VK_MENU
};

class KeyboardEmulator {
    public:
        KeyboardEmulator();
        virtual ~KeyboardEmulator();
        
        void pressKey(char,    Modifier = MODIFIER_NONE) const;
        void pressKey(KeyCode, Modifier = MODIFIER_NONE) const;
        void sleep(int ms = 100) const; // OS independent sleep

    protected:
        void doPressKey(WORD) const;
        void doReleaseKey(WORD) const;

    private:
        INPUT makeINPUT(WORD wVk, bool isPressed) const;
    
}; // end of class

} // end of namespace

#endif

