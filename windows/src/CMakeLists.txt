cmake_minimum_required (VERSION 2.8.11)
project (WindowsKeyboardServer)

# Compile with GCC / MinGW because of Qt

# Qt5
set(CMAKE_INCLUDE_CURRENT_DIR ON)
set(CMAKE_AUTOMOC ON)
find_package(Qt5Network REQUIRED)
find_package(Qt5Concurrent REQUIRED)
find_package(Qt5Widgets REQUIRED)

#set(CMAKE_PREFIX_PATH "D:/Qt/5.3/mingw482_32/lib/cmake")
#set(Qt5Network_DIR, D:/Qt/5.3/mingw482_32/lib/cmake/Qt5Network)

# Windows native libraries
find_library(ws2_32_LIBRARY_PATH ws2_32)

########################
# SINGLE THREADED
########################
add_executable (single_threaded_server 
                KeyboardEmulator.cpp KeyboardServer.cpp
                qt/UDPKeyboardServer.cpp
                main_server.cpp)
# If using winsock/UDPKeyboardServer.cpp:
#target_link_libraries(single_threaded_server ${ws2_32_LIBRARY_PATH} -lstdc++)
target_link_libraries(single_threaded_server -lstdc++)
qt5_use_modules(single_threaded_server Network)

########################
# MULTI THREADED
########################
add_executable (multi_threaded_server 
                KeyboardEmulator.cpp KeyboardServer.cpp
                qt/UDPKeyboardServer.cpp
                multi_threaded_server.cpp)
target_link_libraries(multi_threaded_server -lstdc++)
qt5_use_modules(multi_threaded_server Network Concurrent)
                
########################
# TRAY-ICON APPLICATION
########################
file (GLOB Resources *.qrc)
qt5_add_resources(ResourceSources ${Resources})
include_directories(${CMAKE_CURRENT_BINARY_DIR})
add_executable (tray_icon_app 
                KeyboardEmulator.cpp KeyboardServer.cpp
                qt/UDPKeyboardServer.cpp
                #ConfigurationWindow.cpp
                tray_icon_app.cpp
                ${ResourceSources})
target_link_libraries(tray_icon_app -lstdc++
                      -Wl,-subsystem,windows) # this line avoids the CLI to show up on Windows
qt5_use_modules(tray_icon_app Network Concurrent Widgets)
                
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

#file(COPY ${CMAKE_SOURCE_DIR}/../resources DESTINATION ${CMAKE_BINARY_DIR})
