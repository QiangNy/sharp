#include <jni.h>
#include <ctype.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <android/log.h>
#include <android/bitmap.h>

#include "CameraTest.h"
#include "Def.h"

#define  SDK_FAILED (-1)
#define  LOG_TAG    "SharpnessJNI"
#define  USER_REGIONS_NUMBERS 50
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

int g_nDefinedNumbers = 0;
int CreateWorkImg(unsigned char* pbSrcImg, int nWidth, int nHeight, unsigned char** ppDstGrey);

JNIEXPORT jint Java_com_cydroid_tpicture_SharpnessEngine_setMetricLabels(JNIEnv * env, jobject obj, jintArray nLabelArray, jint nUserRegions)
{
    int ret;

    jint *pLabels = (*env)->GetIntArrayElements(env, nLabelArray, 0);
    ret = setMetricLabels(pLabels, nUserRegions);
    g_nDefinedNumbers = nUserRegions;

    (*env)->ReleaseIntArrayElements(env, nLabelArray, pLabels,0);
    return ret;
}

JNIEXPORT jint Java_com_cydroid_tpicture_SharpnessEngine_getSharpnessValues(JNIEnv * env,
    jobject obj, jobject srcImg, jfloat threshold, jintArray xResultArray, jintArray xPosArray, jintArray yPosArray )
{
    AndroidBitmapInfo   info;
    void*               pixels;
    int                 ret;

    if (xResultArray == NULL || xPosArray == NULL || yPosArray == NULL)
        return SDK_FAILED;

    jint *prResult = (*env)->GetIntArrayElements(env, xResultArray, 0);
    jint *prXPos = (*env)->GetIntArrayElements(env, xPosArray, 0);
    jint *prYPos = (*env)->GetIntArrayElements(env, yPosArray, 0);

    if ((ret = AndroidBitmap_getInfo(env, srcImg, &info)) < 0)
        return SDK_FAILED;

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888)
        return SDK_FAILED;

    if ((ret = AndroidBitmap_lockPixels(env, srcImg, &pixels)) < 0)
        return SDK_FAILED;

    unsigned char* pbGreyImg = NULL;

    CreateWorkImg((unsigned char*)pixels, info.width, info.height, &pbGreyImg);
    if(pbGreyImg == NULL)
    {
        AndroidBitmap_unlockPixels(env, srcImg);
        return SDK_FAILED;
    }

    AndroidBitmap_unlockPixels(env, srcImg);
    int nRegions = g_nDefinedNumbers;

    int degree[USER_REGIONS_NUMBERS];
    int xPos[USER_REGIONS_NUMBERS];
    int yPos[USER_REGIONS_NUMBERS];

    memset(degree, 0, sizeof(degree));
    memset(xPos, 0, sizeof(xPos));
    memset(yPos, 0, sizeof(yPos));

    ret = getSharpness(pbGreyImg, info.width, info.height, threshold, degree, xPos, yPos);

    int i;
    for (i = 0; i < g_nDefinedNumbers; i++) prResult[i] = degree[i];

    int j;
    for (j = 0; j < g_nDefinedNumbers; j++) prXPos[j] = xPos[j];

    int k;
    for (k = 0; k < g_nDefinedNumbers; k++) prYPos[k] = yPos[k];

    (*env)->ReleaseIntArrayElements(env, xResultArray, prResult, 0);
    (*env)->ReleaseIntArrayElements(env, xPosArray, prXPos, 0);
    (*env)->ReleaseIntArrayElements(env, yPosArray, prYPos, 0);

    free(pbGreyImg);
    return ret;
}

int CreateWorkImg(unsigned char* pbSrcImg, int nWidth, int nHeight, unsigned char** ppDstGrey)
{
    int x, y;
    if(ppDstGrey == NULL)
        return 0;

    unsigned char* pDstGrey = (unsigned char*)malloc(nWidth * nHeight);

    for(y = 0; y < nHeight; y ++)
    {
        for(x = 0; x < nWidth; x ++)
        {
            int r = pbSrcImg[(y * nWidth + x) * 4 + 0];
            int g = pbSrcImg[(y * nWidth + x) * 4 + 1];
            int b = pbSrcImg[(y * nWidth + x) * 4 + 2];

            pDstGrey[y * nWidth + x] = (unsigned char)((r * 306 + g * 601 + b * 117) / 1024);
        }
    }

    *ppDstGrey = pDstGrey;

    return 1;
}