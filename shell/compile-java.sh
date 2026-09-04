#!/bin/bash
set -e

cd "$(dirname "$0")"

mkdir -p ../dist/classes/

ANDROID_JAR="../Sdk/android-6.jar"

SOURCE_FILES=$(find ../src-java -name "*.java" -print)

/usr/lib/jvm/java-8-openjdk-amd64/bin/javac \
      -source 1.6 -target 1.6 \
      -cp "$ANDROID_JAR" \
      -bootclasspath "$ANDROID_JAR" \
      -d ../dist/classes/ \
      $SOURCE_FILES
