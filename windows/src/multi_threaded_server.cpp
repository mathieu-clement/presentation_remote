//#include "winsock/UDPKeyboardServer.hpp"
#include "multi_threaded_server.hpp"
#include "qt/UDPKeyboardServer.hpp"
#include <iostream>
#include <inttypes.h>
#include <signal.h>
#include <cstdlib>

#include <QObject>

keyboard_server::KeyboardServer* server = NULL;

void siginthandler(int param)
{
    if (server) {
        server->stop();
        std::cout << "Exiting gracefully" << std::endl;
    }
    exit(EXIT_SUCCESS);
}

void WorkerThread::run() {
    std::cout << "Before server->run()" << std::endl;
    server->run();
    std::cout << "After server->run()" << std::endl;
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

    server = new keyboard_server::qt::UDPKeyboardServer(port);
    if (server->canStart()) {
        std::cout << "Server could start." << std::endl;

        WorkerThread* workerThread = new WorkerThread();
        std::cout << "Before start" << std::endl;
        workerThread->start();
        std::cout << "After start" << std::endl;
        workerThread->wait();
        std::cout << "After wait" << std::endl;
    } else {
        std::cout << "Server could not start." << std::endl;
    }

    std::cout << "App exited normally." << std::endl;
}

