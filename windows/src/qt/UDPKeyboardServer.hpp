#pragma once
#ifndef qt_UDP_KEYBOARD_SERVER_HPP
#define qt_UDP_KEYBOARD_SERVER_HPP

#include "KeyboardServer.hpp"
#include "KeyboardEmulator.hpp"

#include <QUdpSocket>

namespace keyboard_server {
namespace qt {

class UDPKeyboardServer : public KeyboardServer {
    public:
        UDPKeyboardServer(int port);
        ~UDPKeyboardServer();

        void run(); // blocking
        void stop();
    private:
        void receiveDatagrams() const;
        void processDatagram(QByteArray) const;

        bool mMustStop;
        QUdpSocket* socket;
        KeyboardEmulator* emu;
}; // end of class

} // end of namespace qt
} // end of namespace keyboard_server

#endif

