
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_PACKAGE_NAME := Cyee_SharpTest
LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform

LOCAL_PREBUILT_JNI_LIBS := \
			libs/armeabi-v7a/libsharpness-jni.so

LOCAL_SRC_FILES := $(call all-java-files-under, src)\
    src/cy/com/android/mmitest/service/INvRamService.aidl

LOCAL_PROGUARD_ENABLED := disabled
LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_MULTILIB := both
#LOCAL_STATIC_JAVA_LIBRARIES := v7jar
include $(BUILD_PACKAGE)

#include $(CLEAR_VARS)
#LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := v7jar:libs/appcompat-v7-25.3.0-sources.jar

#LOCAL_MODULE_TAGS := optional

#include $(BUILD_MULTI_PREBUILT)



