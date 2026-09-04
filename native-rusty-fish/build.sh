#!/bin/bash
set -e

export ANDROID_NDK_HOME=../Ndk/android-ndk-r25c

cargo ndk -t armv7-linux-androideabi --platform 19 build --release -q

cp ./target/armv7-linux-androideabi/release/librusty_fish.so ../lib/libgame.so

echo "Patch library built!"
