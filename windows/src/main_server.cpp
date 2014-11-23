/**

    Single-threaded server. Less problems to worry about.

*/

//#include "winsock/UDPKeyboardServer.hpp"
#include "qt/UDPKeyboardServer.hpp"
#include "KeyboardEmulator.hpp"
#include <iostream>
#include <inttypes.h>
#include <signal.h>
#include <cstdlib>

keyboard_server::KeyboardServer* server = NULL;

void siginthandler(int param)
{
    if (server) {
        server->stop();
        std::cout << "Exiting gracefully" << std::endl;
    }
    //exit(EXIT_SUCCESS);
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

    signal(SIGINT, siginthandler);

    keyboard_server::KeyboardEmulator* emu = new keyboard_server::KeyboardEmulator();

    server = new keyboard_server::qt::UDPKeyboardServer(port, emu);
    if (server->canStart()) {
        std::cout << "Server could start." << std::endl;
        server->run();
    } else {
        std::cout << "Server could not start." << std::endl;
    }

    std::cout << "App exited normally." << std::endl;
}
