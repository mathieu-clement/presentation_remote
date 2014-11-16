#pragma once
#ifndef KEYBOARD_SERVER_HPP
#define KEYBOARD_SERVER_HPP

namespace keyboard_server {

class KeyboardServer {
    public:
        KeyboardServer(int port);
        ~KeyboardServer();

        virtual void run() = 0; // blocking
        virtual void stop() = 0; // must be invoked from another thread.
        virtual bool canStart(); // Returns true if constructor ran without failure
    protected:
        int port;
        bool mCanStart;
}; // end of class

} // end of namespace

#endif

