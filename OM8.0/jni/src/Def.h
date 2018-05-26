#ifndef _DEF_
#define _DEF_
#include <android/log.h>

#define  LOG_TAG    "SharpnessJNI"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#define DEBUG_EN 0

#define PI				           3.141592653589793f
#define SCALE_IMAGE_WIDTH	       (1024)
#define SCALE_IMAGE_HEIGHT	       (768)
#define ORG_STEP		           (4)
#define NORMALIZE_W_H              (40)
#define SHARPNESS_CRITERIA_POINTS  (4)
#define SHARPNESS_MAX_VALUE        (620.f)

#endif //_CAMERATEST_
