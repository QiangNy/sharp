package com.cydroid.tpicture;

import android.graphics.Bitmap;

public class SharpnessEngine {
    public static final int SDK_SUCCESS = 0;
    public static final int SDK_FAILED = 1;

    public static final int SDK_Left = 0;
    public static final int SDK_Right = 1;

    public static final int SDK_Top = 0;
    public static final int SDK_Bottom = 1;

    public native static int setMetricLabels(int[] anLabels);
    public native static int getSharpnessValues(Bitmap srcImg, float threshold, int[] resultArray, int[] xPos, int[] yPos);

    static {
        System.loadLibrary("sharpness-jni");
    }
}
