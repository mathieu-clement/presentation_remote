#pragma once
#ifndef qt_UDP_KEYBOARD_SERVER_HPP
#define qt_UDP_KEYBOARD_SERVER_HPP

#include "../KeyboardServer.hpp"
#include "../KeyboardEmulator.hpp"
#include "../commands/AbstractCommand.hpp"

#include <QUdpSocket>
#include <QHostAddress>

namespace keyboard_server {
namespace qt {

enum MagicCommand {
    PING_COMMAND = 0x01
};

class UDPKeyboardServer : public KeyboardServer {
    public:
        UDPKeyboardServer(int port, KeyboardEmulator* emu);
        ~UDPKeyboardServer();

        void run(); // blocking
        void stop();
        
        QString serverIpAddress() const;
        bool isIPv4Address() const;
        bool isIPv6Address() const;
        bool onAllInterfaces() const;
    private:
        void receiveDatagrams();
        void processDatagram(QByteArray, QHostAddress&, quint16); 
        void processMagicCommand(MagicCommand, QByteArray& cmdData,
                                 QHostAddress& sender, quint16 senderPort);
        // Callback that is passed as parameter to a Command
        void callbackForMagicCommand (QByteArray& outputData);
        keyboard_server::commands::AbstractCommand& findCommandInstance(MagicCommand);

        bool mMustStop;
        QUdpSocket* socket;
        KeyboardEmulator* emu;
}; // end of class

} // end of namespace qt
} // end of namespace keyboard_server

#endif

