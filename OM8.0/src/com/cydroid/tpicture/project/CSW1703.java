package com.cydroid.tpicture.project;

/**
 * Created by qiang on 4/9/18.
 */
public class CSW1703 {
    //Default
    // 前置
    public static final int default_fMetricLabels[] = {
            8, 14, 8, 14, 8, 14, 8, 14,
            8, 14, 14, 8, 8, 14, 14, 8,
            14, 8, 8, 14, 14, 8, 8, 14,
            14, 8, 14, 8, 14, 8, 14, 8,
            10, 16, 16, 10, 16, 10, 10, 16};
    // 后置
    public static final int default_rMetricLabels[] = {
            20, 16, 20, 16, 20, 16, 20, 16,
            20, 16,	16, 20, 20, 16, 16, 20,
            16, 20, 20, 16, 16, 20, 20, 16,
            16, 20, 16, 20, 16, 20, 16, 20,
            22, 26, 26, 22, 26, 22, 22, 26 };

    // 前置
    public static final int fMetricLabels[] = {
            8, 14, 8, 14, 8, 14, 8, 14,
            8, 14, 14, 8, 8, 14, 14, 8,
            14, 8, 8, 14, 14, 8, 8, 14,
            14, 8, 14, 8, 14, 8, 14, 8,
            10, 16, 16, 10, 16, 10, 10, 16};
    // 后置
    public static final int rMetricLabels[] = {
            20, 16, 20, 16, 20, 16, 20, 16,
            20, 16, 16, 20, 20, 16, 16, 20,
            16, 20, 20, 16, 16, 20, 20, 16,
            16, 20, 16, 20, 16, 20, 16, 20,
            22, 26, 26, 22, 26, 22, 22, 26 };

    // 后置 v2
    public static final int rMetricLabels_v2[] = {
            20, 16, 20, 14, 20, 16, 16, 20,
            20, 14, 16, 20, 20, 16, 14, 20,
            20, 16, 16, 20, 14, 20, 16, 20,
            22, 26, 22, 26, 26, 22, 22, 26 };

    public static final int mCameraIDs[] = { 1, 3};
    public static final float threshold = 0.5f;
    public static final int backRelexLine_15 = 1750;
    public static final int backRelexLine_19 = 2200;
    public static final int frontRelexLine_15 = 1100;
    public static final int frontRelexLine_19 = 1400;
//前置 0 900，1 1000，2 1100，3 1100，4 900，5 1000，6 1100，7 1100，8 900，9 1000，10 1100，11 1100，12 900，13 1000，14 1100，15 1100，16 1300，17 1350，18 1350 ，19 1350

    public static final int frontRelexLines[] = {
        900,1000,1100,1100,900,
        1000,1100,1100,900,1000,
        1100,1100,900,1000,1100,
        1100,1300,1350,1350,1350
    };

//后置 0 1600，1 1600，2 1750，3 1750，4 1600，5 1600，6 1750，7 1750，8 1600，9 1600，10 1750，11 1750，12 1600，13 1600，14 1750，15 1750，16 2200，17 2200，18 2200，19 2200
    public static final int backRelexLines[] = {
        1600,1600,1750,1750,1600,
        1600,1750,1750,1600,1600,
        1750,1750,1600,1600,1750,
        1750,2200,2200,2200,2200
    };

    //后摄只有前16个结果
    public static final int backRelexLines_v2[] = {
            1600,1600,1750,1750,1600,
            1600,1750,1750,1600,1600,
            1750,1750,1600,1600,1750,
            1750,  0,  0,  0,  0
    };
}
