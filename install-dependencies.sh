#!/bin/bash

export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools:$PATH"

DEPS="$ANDROID_HOME/installed-dependencies"

if [ ! -e ${DEPS} ]; then
    cp -r /usr/local/android-sdk-linux ${ANDROID_HOME} &&
    echo y | android update sdk --no-ui --all --filter tool &&
    echo y | android update sdk --no-ui --all --filter tools &&
    echo y | android update sdk --no-ui --all --filter platform-tools &&
    (while :
    do
        echo y
        sleep 10
    done) | android update sdk --no-ui --all --filter build-tools-23.0.3 &&
    echo y | android update sdk --no-ui --all --filter extra-android-support,extra-google-google_play_services,extra-google-m2repository,extra-android-m2repository
    touch ${DEPS}
fi

# echo y | android update sdk --no-ui --all --filter "build-tools-23.0.1,extra-android-support,extra-google-google_play_services,extra-google-m2repository,extra-android-m2repository"
# echo y | android update sdk --no-ui --all --filter "build-tools-23.0.3,extra-android-support,extra-google-google_play_services,extra-google-m2repository,extra-android-m2repository"
