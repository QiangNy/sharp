package com.cydroid.tpicture.utils;

import com.cydroid.tpicture.project.CSW1703;

/**
 * Created by qiang on 4/9/18.
 */
public class BigUtil {
    private static final String TAG = "BigUtil";



    //jude the project

    public static int[] getfMetricLabels() {
        switch (TestUtil.getCyZnVersion()) {
            case "CSW1703":
                DswLog.d(TAG, "getfMetricLabels CSW1703");
                return CSW1703.fMetricLabels;

            default:
                DswLog.d(TAG, "getfMetricLabels default");
                return CSW1703.default_fMetricLabels;
        }

    }

    public static int[] getrMetricLabels() {
        switch (TestUtil.getCyZnVersion()) {
            case "CSW1703":
                DswLog.d(TAG, "getrMetricLabels CSW1703");

                //后摄只有16个参数
                //return CSW1703.rMetricLabels;
                return CSW1703.rMetricLabels_v2;
            default:
                DswLog.d(TAG, "getrMetricLabels default");
                return CSW1703.default_rMetricLabels;
        }
    }

    //static param
    public static final String ACTION_ASK_PICTURESERVICE = "ThisCamera";

    //broad action
    public static final String BROAD_START_TEST = "action.broadstart.picture.start";
    public static final String BROAD_STOP_TEST = "action.broadstart.picture.stop";
    public static final String BROAD_STAT_WHITETEST = "action.broadstart.picture.white";
    public static final String ACTION_START_MMIASERVICE = "cy.com.android.mmitest.NvRamService";

    //tpicture Nvram flag
    public static final int ACTION_NVRAM_FLAG_501 = 501;
    public static final int SN_LONG_LENGTH = 510;

    public static final int NVRAM_FLAG_PASS = 0x50;

    public static final int ACTION_DEFAULT_ERROR = -1;
    public static final int ACTION_WRITE_NV_FLAG = 20;
    public static final int ACTION_START_TESTREPORT = 21;

    public static final int ACTION_CAMERA1 = 1;
    public static final int ACTION_CAMERA2 = 2;
    public static final int ACTION_CAMERA3 = 3;
    public static final int ACTION_CAMERA4 = 4;

    public static final int ACTION_TAKE_PHOTO = 8;
    public static final int ACTION_KEY_BACK   = 9;

    public static final int ACTION_CAMERA_LIST = 100;

}
