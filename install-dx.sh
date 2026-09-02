wget https://dl.google.com/android/repository/build-tools_r25.0.3-linux.zip

mkdir -p Sdk/build-tools/25.0.3

unzip build-tools_r25.0.3-linux.zip -d Sdk/build-tools/temp_extract

mv Sdk/build-tools/temp_extract/android-7.1.1/* Sdk/build-tools/25.0.3/

rm -rf Sdk/build-tools/temp_extract
rm build-tools_r25.0.3-linux.zip
chmod +x Sdk/build-tools/25.0.3/dx
