#include "KeyboardServer.hpp"

namespace keyboard_server {

KeyboardServer::KeyboardServer(int port) : port(port), mCanStart(true)
{
}

KeyboardServer::~KeyboardServer()
{

}

bool KeyboardServer::canStart()
{
    return mCanStart;
}

} // end of namespace
