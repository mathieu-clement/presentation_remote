/**

    Like the multi-threaded server, but with a tray-icon interface.
    Works everywhere if "QSystemTrayIcon::isSystemTrayAavailable()".

    The networking and threading code is platform-INdependent.
    The KeyboardEmulator is platform-dependent, but by 
    subclassing, it would not be very difficult to make it platform-independent.

*/

#include "tray_icon_app.hpp"
#include "qt/UDPKeyboardServer.hpp"

#include <QApplication>
#include <QAbstractSocket>
#include <QList>
#include <QMenu>
#include <QNetworkInterface>
#include <QSystemTrayIcon>

#include <iostream>
#include <sstream>
#include <signal.h>
#include <cstdlib>
#include <inttypes.h>

// icon. Uses QRC file.
#ifndef MY_TRAY_ICON_PATH
#define MY_TRAY_ICON_PATH ":/images/tray_icon.png"
#endif

/* Duration of balloon in seconds */
#ifndef MESSAGE_DURATION
#define MESSAGE_DURATION 30
#endif

keyboard_server::KeyboardServer* server = NULL;
WorkerThread* workerThread = NULL;
QSystemTrayIcon* icon;


void quitGracefully()
{
    if (server) {
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
    //exit(EXIT_SUCCESS);
}

void siginthandler(int __attribute__((unused)) param)
{
    quitGracefully();
}

void WorkerThread::run() {
    // WorkerThread::setTerminationEnabled(true);
    //std::cout << "Before server->run()" << std::endl;
    server->run();
    //std::cout << "After server->run()" << std::endl;
}


void showMessage() {

        if(QSystemTrayIcon::supportsMessages()) {
            std::stringstream msgStream;
            msgStream << "Listening on port " 
                      << server->port() << std::endl;
            if (!server->onAllInterfaces()) {
                msgStream << server->serverIpAddress().toStdString();
            } else {
                QList<QNetworkInterface> interfaces = QNetworkInterface::allInterfaces();
                for (int i = 0; i < interfaces.size(); ++i) {
                    QNetworkInterface itf = interfaces.at(i);
                    // Match IP version
                    // Filter out inactive interfaces
                    if( (itf.flags() & QNetworkInterface::IsUp) &&
                        (itf.flags() & QNetworkInterface::IsRunning) &&
                        !(itf.flags() & QNetworkInterface::IsLoopBack) &&
                        !(itf.flags() & QNetworkInterface::IsPointToPoint) ) {

                            msgStream << itf.humanReadableName().toStdString()
                                      << ":" << std::endl;

                        QList<QNetworkAddressEntry> addressEntries = itf.addressEntries();
                        for (int j = 0; j < addressEntries.size(); ++j) {
                            QHostAddress addr = addressEntries.at(j).ip();
                            if ((addr.protocol() == QAbstractSocket::IPv4Protocol &&
                               server->isIPv4Address()) ||
                               (addr.protocol() == QAbstractSocket::IPv6Protocol &&
                               server->isIPv6Address())) {
                                     msgStream << "    " << addr.toString().toStdString() 
                                               << std::endl;
                            } // address is OK
                        } // for all addresses
                    } // interface is OK
                } // for interface
            } // on all interfaces
            icon->showMessage(QString("Presentation Remote Server"), 
                              QString(msgStream.str().c_str()),
                              QSystemTrayIcon::Information, MESSAGE_DURATION*1000); // INTL (tr?)
            // when clicked, SIGNAL(messageClicked()) is emitted.
        } // supports messages

        // TODO React to click on icon and show message again
        // TODO Detect MS PowerPoint, OpenOffice or Adobe automatically, incl. version.
} // end showMessage()



int main (int argc, char** argv)
{
    if (!QSystemTrayIcon::isSystemTrayAvailable()) {
        std::cerr << "No tray icon available on this system. " 
                 << "Try text-version instead. Exiting." 
                 << std::endl;
        exit(EXIT_FAILURE);
    }

    // See example
    // http://qt-project.org/doc/qt-4.8/desktop-systray.html
   

    keyboard_server::KeyboardEmulator* emu = new keyboard_server::KeyboardEmulator();
    
    if (argc < 2) {
        // Port missing, find next available port, starting from 12000
        for (int port = 12000; port < 65000; ++port) {
            server = new keyboard_server::qt::UDPKeyboardServer(port, emu);
            if(!server->canStart()) {
                delete server;
            } else {
                break;
            }
        }
        // POST: Server can start
    } else {
        // Read port from command-line
        // Convert from string to integer
        uintmax_t port = strtoumax(argv[1], NULL, 10);
        if (port == UINTMAX_MAX && errno == ERANGE) {
            std::cerr << "Could not convert " << argv[1] << " to integer." << std::endl;
            return 1;
        }

        server = new keyboard_server::qt::UDPKeyboardServer(port, emu);
    }

    if(!server->canStart()) {
        std::cerr << "Server can't start." << std::endl;
        exit(EXIT_FAILURE);
    }


    signal(SIGINT, siginthandler);

    bool status = 1;

    if (server->canStart()) {
        //std::cout << "Server could start." << std::endl;

        workerThread = new WorkerThread();
        //std::cout << "Before start" << std::endl;
        workerThread->start();
        //std::cout << "After start" << std::endl;

        QApplication app(argc, argv);
        app.setOrganizationName("Mathieu Clement");
        app.setApplicationName("Keyboard Server (Tray Icon)"); // INTL

        MyTrayIconAppObject* myObj = new MyTrayIconAppObject();

        icon = new QSystemTrayIcon(); // the tray icon
        myObj->setTrayIcon(icon);
        icon->setIcon(QIcon(QString(MY_TRAY_ICON_PATH)));
        QMenu* contextMenu = createContextMenu(myObj);
        icon->setContextMenu(contextMenu);
        QObject::connect(icon, SIGNAL(activated(QSystemTrayIcon::ActivationReason)),
                         myObj, SLOT(iconActivated(QSystemTrayIcon::ActivationReason)));
        icon->show();

        showMessage();

        status = app.exec();

        delete icon;
        delete contextMenu;
        delete myObj;
        workerThread->wait();
        //std::cout << "After wait" << std::endl;
        delete workerThread;
    } else {
        std::cerr << "Server could not start." << std::endl;
    }

    delete server;
    delete emu;

    return status;
}

QMenu* createContextMenu(MyTrayIconAppObject* obj)
{
    QMenu* menu = new QMenu();
    //menu->addSeparator();
    // TODO Who is responsible to free action resources?
    QAction* quitAction = menu->addAction(QString("Quit")); // INTL
    QObject::connect(quitAction, SIGNAL(triggered()), obj, SLOT(quitApplication())); 
    return menu;
}

void MyTrayIconAppObject::setTrayIcon(QSystemTrayIcon* icon)
{
    this->trayIcon = icon;
}

void MyTrayIconAppObject::quitApplication()
{
    //std::cout << "Application will exit." << std::endl;
    this->trayIcon->setVisible(false);
    quitGracefully();
    exit(EXIT_SUCCESS);
}

void MyTrayIconAppObject::iconActivated(QSystemTrayIcon::ActivationReason reason)
 {
     switch (reason) {
         case QSystemTrayIcon::Trigger:
         case QSystemTrayIcon::DoubleClick:
             showMessage();
             break;
         case QSystemTrayIcon::MiddleClick:
             showMessage();
             break;
         default:
             ;
     }
 }
