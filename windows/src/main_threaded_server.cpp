#include "winform/UDPKeyboardServer.hpp"
#include <iostream>
#include <inttypes.h>

#define _WIN32_WINNT 0x410
//#define WINVER 0x0500
#include <windows.h>

DWORD WINAPI MyThreadFunction (LPVOID lpParam)
{
    keyboard_server::KeyboardServer* server = static_cast<keyboard_server::UDPKeyboardServer*>(lpParam);
    server->run();
    return 0;
}

int main (int argc, char** argv)
{
    if (argc < 2) {
        std::cerr << "Missing argument PORT" << std::endl;
        return 1;
    }

    uintmax_t port = strtoumax(argv[1], NULL, 10);
    if (port == UINTMAX_MAX && errno == ERANGE) {
        std::cerr << "Could not convert " << argv[1] << " to integer." << std::endl;
        return 1;
    }

    using namespace keyboard_server;
    
    KeyboardServer* server = new UDPKeyboardServer(port);
    if (server->canStart()) {
        std::cout << "Server could start." << std::endl;

        DWORD tID = 0;
        HANDLE hThread = CreateThread (NULL, 0, MyThreadFunction, server, 0, &tID);
        if (hThread == NULL) {
            std::cerr << "Could not create thread." << std::endl;
            return 1;
        }

        Sleep(10000);
        server->stop();
        WaitForMultipleObjects(1, (const HANDLE *) &hThread, TRUE, INFINITE);
        CloseHandle(hThread);
    } else {
        std::cerr << "Server could not start." << std::endl;
    }

    std::cout << "App exited normally." << std::endl;
}
