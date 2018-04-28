LOCAL_PATH := $(call my-dir)

INSTALL_PATH := inner
include $(CLEAR_VARS)
LOCAL_MODULE := sharpnessengine
LOCAL_SRC_FILES := $(INSTALL_PATH)/libsharpness.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE    := sharpness-jni
LOCAL_SRC_FILES := sharpness_jni.c
LOCAL_STATIC_LIBRARIES := sharpnessengine


LOCAL_LDLIBS    := -llog -lz -ljnigraphics $(NDK_ROOT)/sources/cxx-stl/gnu-libstdc++/4.9/libs/armeabi-v7a/libgnustl_static.a

LOCAL_CFLAGS    := -O3 -fsigned-char -mfpu=neon -fvisibility=hidden -DCPU_ONLY
LOCAL_CPPFLAGS	:= -O3 -fsigned-char -mfpu=neon -fvisibility=hidden -DCPU_ONLY -std=c++11 -Wno-narrowing

APP_OPTIM       := release

LOCAL_CFLAGS += -O2 -fomit-frame-pointer -fstrict-aliasing -ffunction-sections -fdata-sections -ffast-math
LOCAL_CPPFLAGS += -O2 -fomit-frame-pointer -fstrict-aliasing -ffunction-sections -fdata-sections -ffast-math
LOCAL_LDFLAGS += -Wl,--gc-sections
LOCAL_C_INCLUDES := $(LOCAL_PATH) openkv\include sha des des/openssl src

include $(BUILD_SHARED_LIBRARY)
