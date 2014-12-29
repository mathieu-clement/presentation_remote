#include <QThread>
#include "KeyboardEmulator.hpp"
#include <iostream>

#if _WIN32
#define __WIN32_WINNT 0x410
//#define WINVER 0x0500
#include <windows.h>
#include <winuser.h>

// Uses API described in 
// http://msdn.microsoft.com/en-us/library/ms646310.aspx
#elif __unix__
#include <X11/Xlib.h>
#include <X11/keysym.h>
#include <cstdlib>
#endif

namespace keyboard_server {

KeyboardEmulator::KeyboardEmulator()
{
#if __unix__
    display = XOpenDisplay(0);
    if (display == NULL) {
        std::cerr << "Cannot open X11 display." << std::endl;
        exit(EXIT_FAILURE);
    }
#endif
}

KeyboardEmulator::~KeyboardEmulator()
{
#if __unix__
    XCloseDisplay(display);
#endif
}

void KeyboardEmulator::pressKey(char key, Modifier modifier) const
{
    pressKey((KeyCode) key, modifier);
}

void KeyboardEmulator::pressKey(KeyCode keyCode, Modifier modifier) const
{ 
    std::cout << "Simulating keypress " 
              << modifier << "-" << keyCode 
              << std::endl;
    // TODO Modifiers don't work like that in X11
    if(modifier) { doPressKey(modifier); KeyboardEmulator::sleep(); }
    doPressKey(keyCode); KeyboardEmulator::sleep();
    doReleaseKey(keyCode);
    if(modifier) { KeyboardEmulator::sleep(); doReleaseKey(modifier); }
}

#if _WIN32
void KeyboardEmulator::doPressKey(WORD virtualKeyCode) const
{
    INPUT  pressedInput = makeINPUT(virtualKeyCode, true);
    SendInput(1, &pressedInput, sizeof(INPUT));
}

void KeyboardEmulator::doReleaseKey(WORD virtualKeyCode) const
{
    INPUT releasedInput = makeINPUT(virtualKeyCode, false);
    SendInput(1, &releasedInput, sizeof(INPUT));
}

INPUT KeyboardEmulator::makeINPUT(WORD wVk, bool isPressed) const
{
    INPUT st;
    st.type = INPUT_KEYBOARD;
    st.ki.wVk = wVk;
    st.ki.dwFlags = isPressed ? 0 : KEYEVENTF_KEYUP;
    st.ki.wScan = 0;
    st.ki.time = 0;
    st.ki.dwExtraInfo = GetMessageExtraInfo();
    return st;
}
#elif __unix__
void KeyboardEmulator::doPressKey(int virtualKeyCode) const
{
    doPressOrReleaseKey(virtualKeyCode, 0, true);
}

void KeyboardEmulator::doReleaseKey(int virtualKeyCode) const
{
    doPressOrReleaseKey(virtualKeyCode, 0, false);
}

void KeyboardEmulator::doPressOrReleaseKey(int keycode, int modifiers, bool isPressed) const
{
    Window winRoot = XDefaultRootWindow(display);
    Window winFocus;
    int revert;
    XGetInputFocus(display, &winFocus, &revert);
    XKeyEvent event = createKeyEvent(display, winFocus, winRoot,
                                     isPressed, keycode, modifiers);
    XSendEvent(event.display, event.window, True, KeyPressMask,
               (XEvent *)&event);
}

XKeyEvent KeyboardEmulator::createKeyEvent(Display* display, Window& win,
                                                Window& winRoot, bool isPressed,
                                                int keycode, int modifiers) const
{
    XKeyEvent event;
    event.display = display;
    event.window = win;
    event.root = winRoot;
    event.subwindow = None;
    event.time = CurrentTime;
    event.x = 1;
    event.y = 1;
    event.x_root = 1;
    event.y_root = 1;
    event.same_screen = true;
    event.keycode = XKeysymToKeycode(display, keycode);
    event.state = modifiers;
    event.type = isPressed ? KeyPress : KeyRelease;

    return event;
}
#endif

void KeyboardEmulator::sleep(int ms) const
{
    //Sleep(ms);
    // QThread::msleep() might be useful as it is platform-independent
    QThread::msleep(ms);
}

} // end of namespace

