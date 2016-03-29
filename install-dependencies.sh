#!/bin/bash


function updateAndroidSDK() {
    echo y | android update sdk --no-ui --all --filter tool
    echo y | android update sdk --no-ui --all --filter tools
    echo y | android update sdk --no-ui --all --filter platform-tools
    (while :
    do
        echo y
        sleep 10
    done) | android update sdk --no-ui --all --filter build-tools-23.0.3
    echo y | android update sdk --no-ui --all --filter extra-android-support,extra-google-google_play_services,extra-google-m2repository,extra-android-m2repository
}

function installM2RepositoryRev26() {
    cd ${ANDROID_HOME}/extras/android
    rm -rf m2repository
    wget http://dl.google.com/android/repository/android_m2repository_r26.zip
    unzip android_m2repository_r26.zip
}

updateAndroidSDK
installM2RepositoryRev26