#!/bin/bash
cd "$(dirname "$0")"

mkdir -p ../Apktool

if [ ! -f "../Apktool/apktool" ] || [ ! -f "../Apktool/apktool.jar" ]; then
    echo "Installing Apktool..."

    wget -q --show-progress https://raw.githubusercontent.com/iBotPeaches/Apktool/master/scripts/linux/apktool -O ../Apktool/apktool
    chmod +x ../Apktool/apktool

    JAR_URL=$(curl -s https://api.github.com/repos/iBotPeaches/Apktool/releases/latest | grep "browser_download_url" | grep -o 'https://[^"]*' | head -n 1)

    if [ -z "$JAR_URL" ]; then
        echo "Nie udało się pobrać linku automatycznie. Pobieram wersję zapasową..."
        JAR_URL="https://github.com/iBotPeaches/Apktool/releases/download/v3.0.3/apktool_3.0.3.jar"
    fi

    wget -q --show-progress "$JAR_URL" -O ../Apktool/apktool.jar
    chmod +x ../Apktool/apktool

    echo "Apktool successfully installed!"
else
    echo "Apktool is already installed. Skipping."
fi
