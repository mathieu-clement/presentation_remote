//#include "winsock/UDPKeyboardServer.hpp"
#include "multi_threaded_server.hpp"
#include "qt/UDPKeyboardServer.hpp"
#include <iostream>
#include <inttypes.h>
#include <signal.h>
#include <cstdlib>

#include <QObject>

keyboard_server::KeyboardServer* server = NULL;
WorkerThread* workerThread = NULL;

void siginthandler(int param)
{
    if (server) {
        server->isProcessingSema()->acquire();
        if(server->isProcessing()) {
        std::cout << "Exiting gracefully (pending request)" << std::endl;
            server->stop();
            server->isProcessingSema()->release();
            delete server;
        } else {
            std::cout << "Exiting gracefully (kind of, NO pending request)" << std::endl;
            server->isProcessingSema()->release();
            delete server;
            if (workerThread) {
                workerThread->exit();
                delete workerThread;
            }
            exit(EXIT_SUCCESS);
        }
    }
    //exit(EXIT_SUCCESS);
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

    keyboard_server::KeyboardEmulator* emu = new keyboard_server::KeyboardEmulator();
    // TODO delete after usage

    server = new keyboard_server::qt::UDPKeyboardServer(port, emu);
    if (server->canStart()) {
        std::cout << "Server could start." << std::endl;

        workerThread = new WorkerThread();
        std::cout << "Before start" << std::endl;
        workerThread->start();
        std::cout << "After start" << std::endl;
        workerThread->wait();
        std::cout << "After wait" << std::endl;
        delete workerThread;
    } else {
        std::cout << "Server could not start." << std::endl;
    }

    delete server;
    delete emu;

    std::cout << "App exited normally." << std::endl;
}

