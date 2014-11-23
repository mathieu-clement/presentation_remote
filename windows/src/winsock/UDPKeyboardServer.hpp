#pragma once
#ifndef winsock_UDP_KEYBOARD_SERVER_HPP
#define winsock_UDP_KEYBOARD_SERVER_HPP

#include "../KeyboardServer.hpp"

// Historical Microsoft BS...
// http://msdn.microsoft.com/en-us/library/windows/desktop/ms737629%28v=vs.85%29.aspx
#ifndef WIN32_LEAN_AND_MEAN
#define WIN32_LEAN_AND_MEAN
#endif

// #include <windows.h>
#define _WIN32_WINNT 0x501
#include <winsock2.h>
#include <ws2tcpip.h>

// For Visual-C++ compiler
//#pragma comment(lib, "Ws2_32.lib")
// For MinGW, link with "-ws2_32"

namespace keyboard_server {
namespace winsock {

class UDPKeyboardServer : public KeyboardServer {
    public:
        UDPKeyboardServer(int port, KeyboardEmulator* emu);
        ~UDPKeyboardServer();

        void run(); // blocking
        void stop();
    private:
        SOCKET sd;
        bool mMustStop;
}; // end of class

} // end of namespace winsock
} // end of namespace keyboard_server

#endif

