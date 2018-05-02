#!/bin/bash

cd ~/debug/TclMMI/Android_SharpnessDemo/Cyee_SharpTest

gradle build

cd ~/gionee/project/build_apk_env_8.0/build_apk_env

pwd
#adb install -r /home/qiang/debug/TclMMI/Android_SharpnessDemo/app/build/outputs/apk/app-release.apk Cyee_SharpTest/build/outputs/apk/Cyee_SharpTest-release.apk 

./Local_sign_CyMp.sh platform /home/qiang/debug/TclMMI/Android_SharpnessDemo/Cyee_SharpTest/build/outputs/apk/Cyee_SharpTest-release.apk 
echo "signed OK"

adb install -r /home/qiang/debug/TclMMI/Android_SharpnessDemo/Cyee_SharpTest/build/outputs/apk/Cyee_SharpTest-release_platform.apk























