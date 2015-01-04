#include <QThread>
#include "KeyboardEmulator.hpp"
#include <iostream>

#define __WIN32_WINNT 0x410
//#define WINVER 0x0500
#include <windows.h>
//#include <winuser.h>

// Uses API described in 
// http://msdn.microsoft.com/en-us/library/ms646310.aspx

namespace keyboard_server {

KeyboardEmulator::KeyboardEmulator()
{
}

KeyboardEmulator::~KeyboardEmulator()
{
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

void KeyboardEmulator::sleep(int ms) const
{
    //Sleep(ms);
    // QThread::msleep() might be useful as it is platform-independent
    QThread::msleep(ms);
}

} // end of namespace

