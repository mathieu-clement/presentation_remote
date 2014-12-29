#include <QByteArray>
#include "PongCommand.hpp"

namespace keyboard_server {
namespace commands {

bool PongCommand::execute(QByteArray& inputData, 
                          std::function<void(QByteArray&)>& callback)
{
    // Give back to the client what he sent (as the ping utility does)
    callback(inputData);

    return true;
} // end execute()

} // end namespace commands
} // end namespace keyboard_server
