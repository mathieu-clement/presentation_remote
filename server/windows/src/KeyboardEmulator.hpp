#pragma once
#ifndef KEYBOARD_EMULATOR_HPP
#define KEYBOARD_EMULATOR_HPP

#if _WIN32
#define __WIN32_WINNT 0x410
//#define WINVER 0x0500
#include <windows.h>
#elif __unix__

// http://www.doctort.org/adam/nerd-notes/x11-fake-keypress-event.html

#include <X11/Xlib.h>
#include <X11/keysym.h>

#define VK_DOWN             XK_Down
#define VK_UP               XK_Up
#define VK_RIGHT            XK_Right
#define VK_LEFT             XK_Left
#define VK_NEXT             XK_Next
#define VK_PRIOR            XK_Prior
// TODO Not so sure about the next one
#define VK_ESCAPE           XK_Cancel
#define VK_HOME             XK_Home
#define VK_F1               XK_F1
#define VK_F2               XK_F2
#define VK_F3               XK_F3
#define VK_F4               XK_F4
#define VK_F5               XK_F5
#define VK_F6               XK_F6
#define VK_F7               XK_F7
#define VK_F8               XK_F8
#define VK_F9               XK_F9
#define VK_F10              XK_F10 
#define VK_F11              XK_F11
#define VK_F12              XK_F12

#define VK_CONTROL          XK_Control_L
#define VK_SHIFT            XK_Shift_L
#define VK_MENU             XK_Alt_L

#endif

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

#if _WIN32
    protected:
        void doPressKey(WORD) const;
        void doReleaseKey(WORD) const;

    private:
        INPUT makeINPUT(WORD wVk, bool isPressed) const;
#elif __unix__
    protected:
        void doPressKey(int) const;
        void doReleaseKey(int) const;

    private:
        XKeyEvent createKeyEvent(Display* display, Window& win,
                                 Window& winRoot, bool isPressed,
                                 int keycode, int modifiers) const;
        void doPressOrReleaseKey(int keycode, int modifiers, bool isPressed) const;
        Display* display;
#endif
    
}; // end of class

} // end of namespace

#endif

