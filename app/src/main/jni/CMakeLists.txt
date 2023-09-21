cmake_minimum_required(VERSION 3.6) # 指定CMake的最低要求版本号

set(target songstudio) # 设置环境变量的名称（target）及其取值（common）
project(${target}) # 指定项目的名称

include_directories(3rdparty/include)
file(GLOB srcs ./*.cpp libcommon/*.cpp libmedia/*.cpp libmusicdecoder/*.cpp) # 把所有cpp文件和c文件都放入名称为srcs的集合中
file(GLOB hdrs ./*.h libcommon/*.h libmedia/*.h libmusicdecoder/*.h) # 把所有hpp文件和h文件都放入名称为hdrs的集合中

#add_library(${target} STATIC ${srcs} ${hdrs}) # 生成静态库（库文件的扩展名为a）
add_library(${target} SHARED ${srcs} ${hdrs}) # 生成动态库（共享库）（库文件的扩展名为so）
target_link_libraries(${target} log z OpenSLES
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libx264.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libswscale.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libswresample.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libpostproc.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libopus.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libmp3lame.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libfdk-aac.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libavutil.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libavformat.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libavfilter.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libavdevice.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libavcodec.a
        ) # 指定要链接哪些库。log表示日志库，z表示libz，librtmp需要。