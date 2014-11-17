#pragma once
#ifndef command_PONG_COMMAND_HPP
#define command_PONG_COMMAND_HPP

#include <QByteArray>

namespace keyboard_server {
namespace commands {

class PongCommand {
    public:
        bool execute (QByteArray& inputData, 
                      const std::function<void(QByteArray&)>& callback);

};

} // end namespace commands
} // end namespace keyboard_server

#endif


