# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

set(C_DIR ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp) #一定要加${CMAKE_CURRENT_SOURCE_DIR} 否则跑不通

#
#add_library(native-lib SHARED ${C_DIR}/native-lib.cpp)
#add_library(android-utils SHARED ${C_DIR}/android-utils.cpp)

add_library(core SHARED
        ${C_DIR}/main.cpp
        ${C_DIR}/core/c/model.cpp
        ${C_DIR}/core/c/readwav.cpp
        ${C_DIR}/core/c/reshape.cpp
        ${C_DIR}/core/c/android-utils.cpp
        ${C_DIR}/core/c/constant.cpp
        ${C_DIR}/core/headers/android-utils.h
        ${C_DIR}/core/headers/constant.h
        ${C_DIR}/core/headers/model.h
        ${C_DIR}/core/headers/readwav.h
        ${C_DIR}/core/headers/reshape.h)

#add_library(model SHARED ${C_DIR}/core/c/model.cpp)
#add_library(readwav SHARED ${C_DIR}/core/c/readwav.cpp)
#add_library(reshape SHARED ${C_DIR}/core/c/reshape.cpp)

find_library(log-lib log)
find_library(android android)


#target_include_directories(native-lib PRIVATE ${C_DIR}/core/headers) # required constant.h
target_include_directories(core PRIVATE ${C_DIR}/core/headers ${C_DIR}/core/c) # required all file in it
#target_include_directories(core PRIVATE ${C_DIR}/core/c) # required all file in it
#target_include_directories(model PRIVATE ${C_DIR}/core/headers) # required model.h and constant.h
#target_include_directories(readwav PRIVATE ${C_DIR}/core/headers) # required readwav.h


#target_link_libraries(
#        android-utils
#        ${log-lib}
#        ${android}
#)
#
#target_link_libraries(
#        native-lib
#        ${log-lib}
#        ${android})

target_link_libraries(
        core
        ${log-lib}
        ${android})

