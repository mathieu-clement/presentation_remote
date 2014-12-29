#include "KeyboardEmulator.hpp"
#include <iostream>

int main (int argc, char** argv)
{
    using namespace keyboard_server;

    KeyCode codes[] = { KEYCODE_L,
                        KEYCODE_RIGHT, KEYCODE_RIGHT, KEYCODE_RIGHT,
                        KEYCODE_RIGHT, KEYCODE_RIGHT, KEYCODE_RIGHT,
                        KEYCODE_ESCAPE,
                        (KeyCode) NULL
                      };
    Modifier modifiers[] = { MODIFIER_CTRL,
                             MODIFIER_NONE, MODIFIER_NONE, MODIFIER_NONE, 
                             MODIFIER_NONE, MODIFIER_NONE, MODIFIER_NONE, 
                             MODIFIER_NONE, 
                             (Modifier) NULL };


    KeyboardEmulator emu;
    std::cout << "Open the appropriate app in the next 5 seconds..." << std::endl;
    emu.sleep(5000);
    std::cout << "Robot started." << std::endl;
    
    KeyCode* p_crtKeyCode = &codes[0];
    Modifier* p_crtModifier = &modifiers[0];
    while ( *p_crtKeyCode != NULL || *p_crtModifier != NULL ) {
        emu.pressKey(*p_crtKeyCode, *p_crtModifier);
        emu.sleep(2000);

        p_crtKeyCode++;
        p_crtModifier++;
    } // end while

    std::cout << "Robot stopped." << std::endl;
}


