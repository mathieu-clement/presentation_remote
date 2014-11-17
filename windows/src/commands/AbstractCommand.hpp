#pragma once
#ifndef command_ABSTRACT_COMMAND_HPP
#define command_ABSTRACT_COMMAND_HPP

namespace keyboard_server {
namespace commands {

class AbstractCommand {
    public:
        //    virtual bool do(QByteArray& data, 

        /**
         * Execute command.
         *
         * @param inputData Input / Parameters for the command, without the command name.
         * @param callback  Callback function pointer to send ata back to the sender
         * @return true on success
         */
        virtual bool execute (QByteArray& inputData, 
                              const std::function<void(QByteArray&)> &callback) = 0;

};

} // end namespace command
} // end namespace keyboard_server

#endif

