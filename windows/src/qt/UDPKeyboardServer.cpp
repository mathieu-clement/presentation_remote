#include "KeyboardServer.hpp"
#include "UDPKeyboardServer.hpp"
#include "KeyboardEmulator.hpp"

#include <iostream>
#include <stdio.h>

namespace keyboard_server {
namespace qt {

UDPKeyboardServer::UDPKeyboardServer(int port) : KeyboardServer(port), mMustStop(false)
{
    emu = new KeyboardEmulator();
    socket = new QUdpSocket();
    socket->bind(QHostAddress::LocalHost, port);
}

UDPKeyboardServer::~UDPKeyboardServer()
{
    if (mCanStart) {
        delete emu;
        delete socket;
    }
}

// Not designed / tested to be run more than once, even though it may work.
void UDPKeyboardServer::run()
{
    while(!mMustStop) {
        if(socket->waitForReadyRead(-1)) {
            receiveDatagrams();
        }
    }
}

void UDPKeyboardServer::receiveDatagrams() const
{
    while (socket->hasPendingDatagrams()) {
        QByteArray datagram;
        datagram.resize(socket->pendingDatagramSize());
        QHostAddress sender;
        quint16 senderPort;

        socket->readDatagram(datagram.data(), datagram.size(),
                             &sender, &senderPort);
        processDatagram(datagram);
    } // not must stop
}

void UDPKeyboardServer::processDatagram(QByteArray datagram) const
{
    std::cout << "Datagram" << std::endl;
    if (datagram.size() > 0) {
        std::cout << "Received " << datagram.size() << " bytes:" 
                  /* << "'" << buffer << "'" */
                  << std::endl;  
        emu->pressKey((KeyCode) datagram.data()[0], (Modifier) datagram.data()[1]);
    }
} // end run()

void UDPKeyboardServer::stop()
{
    // TODO Disconnect signal from slot
}

} // end of namespace qt
} // end of namespace keyboard_server
