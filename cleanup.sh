#!/bin/sh
rm -rf .idea
./gradlew clean
rm -rf .gradle
rm -rf build
rm -rf */build
rm -rf iosApp/iosApp.xcworkspace
rm -rf iosApp/Pods
rm -rf iosApp/iosApp.xcodeproj/project.xcworkspace
rm -rf iosApp/iosApp.xcodeproj/xcuserdata
rm -rf iosApp/iosApp/GoogleService-Info.plist
rm -rf iosApp/iosApp/Info.plist
rm -rf composeApp/google-services.json
rm -rf composeApp/google-service-debug.json
rm -rf composeApp/google-service-release.json
