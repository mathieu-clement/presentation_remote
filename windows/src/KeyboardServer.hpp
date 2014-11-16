#pragma once
#ifndef KEYBOARD_SERVER_HPP
#define KEYBOARD_SERVER_HPP

#include "KeyboardEmulator.hpp"

#include <QSemaphore>

namespace keyboard_server {

class KeyboardServer {
    public:
        KeyboardServer(int port, KeyboardEmulator* emu);
        ~KeyboardServer();

        virtual void run() = 0; // blocking
        virtual void stop() = 0; // must be invoked from another thread.
        virtual bool canStart(); // Returns true if constructor ran without failure
        bool isProcessing() const; // Is performing a key press. 
                                   // Needed because pressKey() must not be interrupted,
                                   // otherwise key stays pressed forever.
        QSemaphore* isProcessingSema() const;
    protected:
        int port;
        KeyboardEmulator* emu;
        bool mCanStart;
        bool mIsProcessing;
        QSemaphore* mIsProcessingSema;
}; // end of class

} // end of namespace

#endif

