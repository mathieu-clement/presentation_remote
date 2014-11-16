#include "KeyboardServer.hpp"
#include "UDPKeyboardServer.hpp"
#include "KeyboardEmulator.hpp"

#include <iostream>
#include <stdio.h>

#include <QAbstractSocket>

namespace keyboard_server {
namespace qt {

UDPKeyboardServer::UDPKeyboardServer(int port, KeyboardEmulator* emu) 
: KeyboardServer(port, emu), mMustStop(false)
{
    socket = new QUdpSocket();
    socket->bind(port);
}

UDPKeyboardServer::~UDPKeyboardServer()
{
    if (mCanStart) {
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

void UDPKeyboardServer::receiveDatagrams()
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

void UDPKeyboardServer::processDatagram(QByteArray datagram)
{
    std::cout << "Datagram" << std::endl;
    if (datagram.size() > 0) {
        std::cout << "Received " << datagram.size() << " bytes:" 
                  /* << "'" << buffer << "'" */
                  << std::endl;  
        mIsProcessingSema->acquire();
        mIsProcessing = true;
        mIsProcessingSema->release();

        emu->pressKey((KeyCode) datagram.data()[0], (Modifier) datagram.data()[1]);

        mIsProcessingSema->acquire();
        mIsProcessing = false;
        mIsProcessingSema->release();
    }
} // end run()

void UDPKeyboardServer::stop()
{
    mMustStop = true;
}

QString UDPKeyboardServer::serverIpAddress() const
{
    return socket->localAddress().toString();
}

bool UDPKeyboardServer::onAllInterfaces() const
{
    return socket->localAddress() == QHostAddress::AnyIPv4 ||
           socket->localAddress() == QHostAddress::AnyIPv6 ||
           socket->localAddress() == QHostAddress::Any;
}

bool UDPKeyboardServer::isIPv4Address() const
{
    return socket->localAddress().protocol() == QAbstractSocket::IPv4Protocol;
}

bool UDPKeyboardServer::isIPv6Address() const
{
    return socket->localAddress().protocol() == QAbstractSocket::IPv6Protocol;
}

} // end of namespace qt
} // end of namespace keyboard_server
