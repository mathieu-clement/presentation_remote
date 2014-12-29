/**

    Multi-threaded server.
    The networking and threading code is platform-INdependent.
    The KeyboardEmulator is platform-dependent, but by 
    subclassing, it would not be very difficult to make it platform-independent.

*/
//#include "winsock/UDPKeyboardServer.hpp"
#include <QObject>
#include <iostream>
#include <inttypes.h>
#include <signal.h>
#include <cstdlib>
#include "multi_threaded_server.hpp"
#include "KeyboardServer.hpp"
#include "qt/UDPKeyboardServer.hpp"

#if __unix__
#include <X11/Xlib.h>
#include <X11/keysym.h>
#include <cstdlib>
#endif

keyboard_server::KeyboardServer* server = NULL;
WorkerThread* workerThread = NULL;

void siginthandler(int __attribute__((unused)) param)
{    if (server) {
        server->isProcessingSema()->acquire();
        if(server->isProcessing()) {
        //std::cout << "Exiting gracefully (pending request)" << std::endl;
            server->isProcessingSema()->release();
            server->stop();
            delete server;
        } else {
            //std::cout << "Exiting gracefully (kind of, NO pending request)" << std::endl;
            server->isProcessingSema()->release();
            delete server;
            if (workerThread) {
                workerThread->terminate();
                workerThread->wait();
                delete workerThread;
            }
            exit(EXIT_SUCCESS);
        }
    }
}

void WorkerThread::run() {
    // WorkerThread::setTerminationEnabled(true);
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

