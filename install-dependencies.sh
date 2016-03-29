#!/bin/bash

DEPS="$ANDROID_HOME/installed-dependencies"

if [ ! -e $DEPS ]; then
    echo y | android update sdk --no-ui --all --filter tool &&
    echo y | android update sdk --no-ui --all --filter tools &&
    echo y | android update sdk --no-ui --all --filter platform-tools &&
    echo y | android update sdk --no-ui --all --filter build-tools-23.0.3,extra-android-support,extra-google-google_play_services,extra-google-m2repository,extra-android-m2repository
    touch $DEPS
fi

# echo y | android update sdk --no-ui --all --filter "build-tools-23.0.1,extra-android-support,extra-google-google_play_services,extra-google-m2repository,extra-android-m2repository"
# echo y | android update sdk --no-ui --all --filter "build-tools-23.0.3,extra-android-support,extra-google-google_play_services,extra-google-m2repository,extra-android-m2repository"
