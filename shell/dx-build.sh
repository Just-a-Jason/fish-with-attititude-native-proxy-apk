#!/bin/bash
cd "$(dirname "$0")/.."

DX="./Sdk/build-tools/25.0.3/dx"

"$DX" --dex --output=./dist/classes.dex ./dist/FishWithAttitude.jar
