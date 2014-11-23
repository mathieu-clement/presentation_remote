#pragma once
#ifndef KEYBOARD_SERVER_HPP
#define KEYBOARD_SERVER_HPP

#include "KeyboardEmulator.hpp"

#include <QSemaphore>
#include <QString>

namespace keyboard_server {

class KeyboardServer {
    public:
        KeyboardServer(int port, KeyboardEmulator* emu);
        virtual ~KeyboardServer();

        virtual void run() = 0; // blocking
        virtual void stop() = 0; // must be invoked from another thread.
        bool canStart() const; // Returns true if constructor ran without failure
        bool isProcessing() const; // Is performing a key press. 
                                   // Needed because pressKey() must not be interrupted,
                                   // otherwise key stays pressed forever.
        QSemaphore* isProcessingSema() const;
        virtual QString serverIpAddress() const = 0;
        virtual bool isIPv4Address() const = 0;
        virtual bool isIPv6Address() const = 0;
        virtual bool onAllInterfaces() const = 0;
        int port() const;
    protected:
        int mPort;
        KeyboardEmulator* emu;
        bool mCanStart;
        bool mIsProcessing;
        QSemaphore* mIsProcessingSema;
}; // end of class

} // end of namespace

#endif

