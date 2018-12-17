#!/bin/bash

if [ -z "$TRAVIS_TAG" ]; then
    ./gradlew clean assembleDebug

    COMMIT_COUNT=$(git rev-list --count HEAD)
    export ARTIFACT="meow-r${COMMIT_COUNT}.apk"

    mv app/build/outputs/apk/debug/app-debug.apk $ARTIFACT
else
    ./gradlew clean assembleRelease

    export ARTIFACT="meow-${TRAVIS_TAG}.apk"

    mv app/build/outputs/apk/debug/app-release.apk $ARTIFACT
fi