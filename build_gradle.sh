#!/bin/bash

DEBUG="-d"
RELEASE="-r"
buildType="build"
appName=app-debug-unaligned.apk
if [ "$1" == "$DEBUG" ]; then
	buildType="assembleDebug"
elif [ "$1" == "$RELEASE" ]; then
	buildType="assembleRelease"
	appName=app-release.apk
else
	echo -e "Executing with default 'build' command.\nUse '-r' for release or '-d' for debug"
fi
echo "Executing build."
#./gradlew $buildType
deviceId='cat build_gradle.properties 2> /dev/null'
if [ ${deviceId:+1} ]
then
echo -e "\nSelect device to install on:\nType in only device id:\n"
adb devices
echo "Type device ID and press [ENTER]"
read deviceId
echo $deviceId > build_gradle.properties
fi
echo "adb -s $deviceId install -r app/build/apk/$appName"
adb -s $deviceId install -r app/build/apk/$appName

