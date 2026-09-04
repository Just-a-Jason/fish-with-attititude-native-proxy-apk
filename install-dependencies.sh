#!/bin/bash
set -e

echo "Downloading dependencies..."

./shell/install-dx.sh
./shell/install-jdk.sh
./shell/install-ndk.sh
./shell/install-apktool.sh

echo "All dependencies installed! :)"
