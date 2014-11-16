#include "KeyboardServer.hpp"

namespace keyboard_server {

KeyboardServer::KeyboardServer(int port) : mCanStart(true)
{
    this->port = port;
}

KeyboardServer::~KeyboardServer()
{

}

bool KeyboardServer::canStart()
{
    return mCanStart;
}

} // end of namespace
