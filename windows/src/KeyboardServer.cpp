#include "KeyboardServer.hpp"

#include <QSemaphore>

namespace keyboard_server {

KeyboardServer::KeyboardServer(int port, KeyboardEmulator* emu) 
: mPort(port), emu(emu), mCanStart(true), mIsProcessingSema(new QSemaphore(1))
{
}

KeyboardServer::~KeyboardServer()
{
    delete mIsProcessingSema;
}

bool KeyboardServer::canStart()
{
    return mCanStart;
}

bool KeyboardServer::isProcessing() const
{
    return mIsProcessing;
}

QSemaphore* KeyboardServer::isProcessingSema() const
{
    return mIsProcessingSema;
}

int KeyboardServer::port() const
{
    return mPort;
}

} // end of namespace
