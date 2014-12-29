#pragma once
#ifndef command_PONG_COMMAND_HPP
#define command_PONG_COMMAND_HPP

#include <QByteArray>
#include "AbstractCommand.hpp"

namespace keyboard_server {
namespace commands {

class PongCommand : public AbstractCommand {
    public:
        bool execute (QByteArray& inputData, 
                      std::function<void(QByteArray&)>& callback);

};

} // end namespace commands
} // end namespace keyboard_server

#endif


