#!/bin/bash
set -e

YELLOW='\033[1;33m'
GREEN='\033[0;32m'
NC='\033[0m'

export ANDROID_NDK_HOME=$HOME/Android/Sdk/ndk/26.1.10909125
export ANDROID_NDK_ROOT=$ANDROID_NDK_HOME

# Ścieżka do lokalnie pobranego Apktoola
APKTOOL="./Apktool/apktool"

clear

# Download dependencies first.
./install-dependencies.sh

mkdir -p dist

echo -e "${YELLOW}Compiling java files...${NC}"
./shell/compile-java.sh

echo -e "${YELLOW}Transforming bytecode into .jar file...${NC}"
jar cf ./dist/FishWithAttitude.jar -C ./dist/classes .

echo -e "${YELLOW}Compiling .jar file into -> classes.dex${NC}"
./shell/dx-build.sh

echo -e "${YELLOW}Coping dist/classes.dex into -> /Fish with Attitude_1.0.39/classes.dex${NC}"
cp ./dist/classes.dex "./Fish with Attitude_1.0.39/classes.dex"

echo -e "${YELLOW}Compiling rustyfish (Rust library)...${NC}"

# build native lib
cd ./native-rusty-fish
./build.sh
cd ..

cp ./native-rusty-fish/target/armv7-linux-androideabi/release/librusty_fish.so "./Fish with Attitude_1.0.39/lib/armeabi/libgame.so"
mkdir -p ./dist/lib/arm
cp ./native-rusty-fish/target/armv7-linux-androideabi/release/librusty_fish.so ./dist/lib/arm/libgame.so
cp ./lib/libgame_real.so "./Fish with Attitude_1.0.39/lib/armeabi/"

echo -e "${YELLOW}Building an apk..${NC}"
"$APKTOOL" b "./Fish with Attitude_1.0.39" -o "FishWithAttitude_1.0.39_unsigned.apk"

echo -e "${YELLOW}Signing the apk...${NC}"
java -jar ./Tools/uber-apk-signer-1.3.0.jar --apks ./FishWithAttitude_1.0.39_unsigned.apk

echo -e "${YELLOW}Cleaning up...${NC}"
rm -rf ./FishWithAttitude_1.0.39_unsigned.apk
rm -rf ./FishWithAttitude_1.0.39_unsigned-aligned-debugSigned.apk.idsig

mkdir -p ./dist/outputs/apk
mv -f FishWithAttitude_1.0.39_unsigned-aligned-debugSigned.apk ./dist/outputs/apk/FishWithAttitude.apk

echo -e "\n${GREEN}DONE! Build successful.${NC}"
