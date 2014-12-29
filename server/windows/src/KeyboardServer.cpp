#include <QSemaphore>
#include "KeyboardServer.hpp"

namespace keyboard_server {

KeyboardServer::KeyboardServer(int port, KeyboardEmulator* emu) : mPort(port), emu(emu), mCanStart(true)
{
    mIsProcessingSema = new QSemaphore(1);
}

KeyboardServer::~KeyboardServer()
{
    delete mIsProcessingSema;
}

bool KeyboardServer::canStart() const
{
    return mCanStart;
}

QSemaphore* KeyboardServer::isProcessingSema() const
{
    return mIsProcessingSema;
}

bool KeyboardServer::isProcessing() const
{
    return mIsProcessing;
}

int KeyboardServer::port() const
{
    return mPort;
}

} // end of namespace
