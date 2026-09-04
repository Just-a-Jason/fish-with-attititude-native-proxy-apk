#!/bin/bash
set -e

mkdir -p ../Jdk

if [ ! -f "../Jdk/bin/javac" ]; then
    echo "Installing Java 8 JDK..."
    curl -L -o jdk8.tar.gz "https://api.adoptium.net/v3/binary/latest/8/ga/linux/x64/jdk/hotspot/normal/eclipse"

    tar -xzf jdk8.tar.gz -C ../Jdk --strip-components=1
    rm jdk8.tar.gz
    echo "JDK was successfully installed!"
else
    echo "JDK is installed. Skipping."
fi
