cmake_minimum_required (VERSION 2.8.11)
project (WindowsKeyboardServer)

# Qt5
set(CMAKE_INCLUDE_CURRENT_DIR ON)
set(CMAKE_AUTOMOC ON)
find_package(Qt5Widgets)
#set(CMAKE_PREFIX_PATH "D:/Qt/5.3/mingw482_32/lib/cmake")
set(Qt5Widgets_DIR, D:/Qt/5.3/mingw482_32/lib/cmake/Qt5Widgets)

# Windows native libraries
find_library(ws2_32_LIBRARY_PATH ws2_32)

add_executable (single_threaded_server 
                KeyboardEmulator.cpp KeyboardServer.cpp
                winsock/UDPKeyboardServer.cpp
                main_server.cpp)
target_link_libraries(single_threaded_server ${ws2_32_LIBRARY_PATH} -lstdc++)
qt5_use_modules(single_threaded_server Widgets)
                
set (WindowsKeyboardServer_VERSION_MAJOR 1)
set (WindowsKeyboardServer_VERSION_MINOR 0)

#list( APPEND CMAKE_CXX_FLAGS "-std=c++0x ${CMAKE_CXX_FLAGS} -g -Wall -Wextra")
add_definitions (
    -std=c++0x
    #-std=gnu++0x
    -g
    -Wall -Wextra
    -m32 # 32-bit executables
)

