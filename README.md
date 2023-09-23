# 项目练习 - Android播放MP3文件（使用FFmpeg解码为PCM，使用AudioTrack/OpenSL ES/Oboe三种不同方式来播放）

《音视频开发进展指南-基于Android与iOS平台的实践》书中的[Android-AudioPlayer](https://github.com/zhanxiaokai/Android-AudioPlayer)项目，使用Android Studio Dolphin和MSYS2环境。

有一些修改，具体如下，

- 增加了fdk-aac/ffmpeg/lame/opus/x264/几个库的[构造脚本](./app/src/main/jni/3rdparty/buildscripts/)，支持armeabi-v7a和arm64-v8a两种ABI
  - ffmpeg不要指定--ld之类的，只要指定--cc和--cxx
  - ffmpeg要指定--disable-asm，否则arm64-v8a链接时会有问题

- gradle中配置使用cmake自动编译jni

- 修改了过时的FFmpeg API使用，avcodec_decode_audio4暂时没有修改为avcodec_send_packet和avcodec_receive_frame样式

- 131.mp3文件打包在APK中，只截取了原文件部分，使用时拷贝到应用专属存储空间，暂时没有让jni直接读资源文件

- 添加了[Oboe](https://github.com/google/oboe)（双簧管，封装了AAudio和OpenSL ES两种实现）的播放
  - [Getting Started with Oboe](https://github.com/google/oboe/blob/main/docs/GettingStarted.md)
  - [Full Guide to Oboe](https://github.com/google/oboe/blob/main/docs/FullGuide.md)
  - [Releases](https://github.com/google/oboe/releases)

![Main][main-url]
![Oboe][oboe-url]

- 添加了播放完成的处理，修复了AudioTrack Player无法启动的逻辑问题

- clang比较严格，jni一些函数有返回值但是返回的时候没有给出值，会SIGILL出错退出
  - 报错模块是libc，内容为"Fatal signal 4 (SIGILL), code 1, fault addr ..."

[main-url]: ./main.jpg
[oboe-url]: ./oboe.jpg
