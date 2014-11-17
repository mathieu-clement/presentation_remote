#include "../KeyboardServer.hpp"
#include "UDPKeyboardServer.hpp"
#include "../KeyboardEmulator.hpp"
#include "../commands/AbstractCommand.hpp"

#include <iostream>
#include <stdexcept>
#include <string>
#include <stdio.h>

#include <QAbstractSocket>
#include <QHostAddress>
#include <QByteArray>

namespace keyboard_server {
namespace qt {

UDPKeyboardServer::UDPKeyboardServer(int port, KeyboardEmulator* emu) 
: KeyboardServer(port, emu), mMustStop(false)
{
    socket = new QUdpSocket();
    mCanStart = socket->bind(port);
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
        processDatagram(datagram, sender, senderPort);
    } // not must stop
}

void UDPKeyboardServer::processDatagram(QByteArray datagram, QHostAddress& sender, quint16 senderPort)
{
    //std::cout << "Datagram" << std::endl;
    if (datagram.size() > 0) {
    //    std::cout << "Received " << datagram.size() << " bytes:" 
    //              /* << "'" << buffer << "'" */
    //              << std::endl;  
        

        mIsProcessingSema->acquire();
        mIsProcessing = true;
        mIsProcessingSema->release();

        char c = (char) datagram.data()[0]; // 1 byte
        if (c >= 'A' && c <= 'Z') {
            emu->pressKey(c, (Modifier) datagram.data()[1]);
        } else {
            KeyCode keyCode = (KeyCode) datagram.data()[0];
            if ((keyCode & 0xFF) == KEYCODE_MAGIC) {
                // Truncate command from datagram data, so we leave only
                // with the command data/parameters
                // Can be empty
                QByteArray inputData = datagram.right(datagram.size()-2);
                processMagicCommand((MagicCommand) datagram.data()[1], 
                                    inputData,
                                    sender, senderPort);
            } else {
                emu->pressKey(keyCode,                        // 1 byte
                              (Modifier) datagram.data()[1]); // 1 byte
            }
        }

        mIsProcessingSema->acquire();
        mIsProcessing = false;
        mIsProcessingSema->release();
    }
} // end processDatagram()

void UDPKeyboardServer::processMagicCommand(MagicCommand cmd, 
                                            QByteArray& cmdData,
                                            QHostAddress& sender, quint16 senderPort)
{
        try {
            keyboard_server::commands::AbstractCommand& cmdInstance = findCommandInstance(cmd);
            std::function<void(QByteArray&)> funcPointer = 
                std::bind(&UDPKeyboardServer::callbackForMagicCommand, this,
                          std::placeholders::_1);
            cmdInstance.execute(cmdData, funcPointer);
        } catch (std::invalid_argument& ia) {
            std::cerr << "Command '" << cmd << "' does not exist." << std::endl;
        }
}

keyboard_server::commands::AbstractCommand&
UDPKeyboardServer::findCommandInstance(MagicCommand cmd)
{
    // TODO:
    // Find command in HashMap<MagicCommand, AbstractCommand> = <enum, instance>

    /*
    keyboard_server::commands::AbstractCommand& cmdInstance =
        keyboard_server::commands::PongCommand();
    return cmdInstance;
    */

    //throw std::invalid_argument(std::string("Command does not exist."));
}

void UDPKeyboardServer::callbackForMagicCommand (QByteArray& outputData)
{
    std::cout << "[Callback] Send some output data... : " << QString(outputData).toStdString()
              << std::endl;
}

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
