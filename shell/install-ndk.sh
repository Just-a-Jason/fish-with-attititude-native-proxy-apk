mkdir -p ../Ndk

if [ ! -d "../Ndk/android-ndk-r25c" ]; then
    echo "Installing Android NDK r25c..."

    wget https://dl.google.com/android/repository/android-ndk-r25c-linux.zip
    unzip -q android-ndk-r25c-linux.zip -d ../Ndk/
    rm android-ndk-r25c-linux.zip

    echo "Android NDK r25c was successfully installed!"
else
    echo "Android NDK r25c is already installed. Skipping."
fi
