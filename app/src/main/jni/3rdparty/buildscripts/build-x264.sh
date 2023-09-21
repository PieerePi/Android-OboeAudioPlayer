#!/bin/bash
#NDK目录
NDK=/d/Android/Sdk/ndk/21.4.7075529

#工具路径
HOST_TAG=windows-x86_64
TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/$HOST_TAG

#安装目录
ANDROID_LIB_PATH="$(pwd)/../install.dir"

ANDROID_API_VERSION=21

#https://developer.android.google.cn/ndk/guides/other_build_systems
TARGET_ARCH_ABI="armeabi-v7a arm64-v8a"

function build_android
{
echo "build for android $CPU"
./configure \
--host=$TARGET \
--disable-cli \
--enable-static \
--enable-pic \
--prefix="$ANDROID_LIB_PATH/$ABI"
make clean
make -j8
make install
echo "building for android $CPU completed"
}

for ABI in $TARGET_ARCH_ABI
do
	if [ $ABI = "armeabi-v7a" ]; then
		CPU=armv7-a
		TARGET=arm-linux-androideabi
		TARGET2=armv7a-linux-androideabi
	elif [ $ABI = "arm64-v8a" ]; then
		CPU=armv8-a
		TARGET=aarch64-linux-android
		TARGET2=aarch64-linux-android
	fi
	export AR=$TOOLCHAIN/bin/$TARGET-ar
	export CC=$TOOLCHAIN/bin/$TARGET2$ANDROID_API_VERSION-clang
	export AS=$CC
	export CXX=$TOOLCHAIN/bin/$TARGET2$ANDROID_API_VERSION-clang++
	export LD=$TOOLCHAIN/bin/$TARGET-ld
	export RANLIB=$TOOLCHAIN/bin/$TARGET-ranlib
	export STRIP=$TOOLCHAIN/bin/$TARGET-strip
	OPTIMIZE_CFLAGS="-march=$CPU"
	export CFLAGS="-Os -fPIC -fdeclspec $OPTIMIZE_CFLAGS -D__ANDROID_API__=$ANDROID_API_VERSION"
	export CPPFLAGS="-Os -fPIC -fdeclspec $OPTIMIZE_CFLAGS -D__ANDROID_API__=$ANDROID_API_VERSION"
	make distclean
	build_android
done
