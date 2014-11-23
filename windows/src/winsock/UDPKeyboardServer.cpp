#include "../KeyboardServer.hpp"
#include "UDPKeyboardServer.hpp"
#include "../KeyboardEmulator.hpp"

// Historical Microsoft BS...
// http://msdn.microsoft.com/en-us/library/windows/desktop/ms737629%28v=vs.85%29.aspx
#ifndef WIN32_LEAN_AND_MEAN
#define WIN32_LEAN_AND_MEAN
#endif

//#include <windows.h>
#define _WIN32_WINNT 0x501
#include <winsock2.h>
#include <ws2tcpip.h>

// For Visual-C++ compiler
//#pragma comment(lib, "Ws2_32.lib")
// For MinGW, link with "-ws2_32"

#include <iostream>
#include <stdio.h>

namespace keyboard_server {
namespace winsock {

UDPKeyboardServer::UDPKeyboardServer(int port, KeyboardEmulator* emu) 
: KeyboardServer(port, emu), mMustStop(false)
{
    WSADATA wsaData;
    int iResult = WSAStartup(MAKEWORD(2,2), &wsaData); // WinSock version 2.2
    if (iResult != 0) {
        mCanStart = false;
        return;
    }

    sd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sd == INVALID_SOCKET) {
        mCanStart = false;
        WSACleanup();
        return;
    }

    struct sockaddr_in server;
    server.sin_family = AF_INET;
    server.sin_addr.s_addr = INADDR_ANY;
    server.sin_port = htons(port);

    //iResult = bind(sd, result->ai_addr, (int)result->ai_addrlen);
    iResult = bind(sd, (struct sockaddr *) &server, sizeof(server));
    if (iResult == SOCKET_ERROR) {
        closesocket(sd);
        mCanStart = false;
        WSACleanup();
        return;
    }
}

UDPKeyboardServer::~UDPKeyboardServer()
{
    if (mCanStart) {
        closesocket(sd);
        WSACleanup();
    }
}

// Not designed / tested to be run more than once, even though it may work.
void UDPKeyboardServer::run()
{
    // http://www.codeproject.com/Articles/11740/A-simple-UDP-time-server-and-client-for-beginners
    
    int client_length;
    int BUFFER_SIZE = 32;
    char buffer[BUFFER_SIZE];
    while (!mMustStop) {
        memset(buffer, '\0', BUFFER_SIZE);
        struct sockaddr_in client;
        int bytes_received = recvfrom(sd, buffer, BUFFER_SIZE, 0,
                                (struct sockaddr *) &client, &client_length);
/*        if (bytes_received == SOCKET_ERROR) {
            std::cerr << "recvfrom() failed with error code: " << WSAGetLastError() << std::endl;
            return;
        }
        */ 
        if (bytes_received > 0) {
            std::cout << "Received " << bytes_received << " bytes:" 
                      /* << "'" << buffer << "'" */
                      << std::endl;  
            emu->pressKey((KeyCode) buffer[0], (Modifier) buffer[1]);
        }
    } // end while not must stop
} // end run()

void UDPKeyboardServer::stop()
{
    mMustStop = true;
}

} // end of namespace winsock
} // end of namespace keyboard_server
