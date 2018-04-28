package com.cydroid.tpicture.utils;

import java.lang.reflect.Method;

/**
 * Created by qiang on 4/9/18.
 */
public class TestUtil {
    private static final String TAG = "TestUtil";

    public static String getProperty(String key) {
        String value = "known";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String)(get.invoke(c, key, "" ));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static void setProperty(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getCyZnVersionNum() {
        return getProperty(SystemProp.PROP_SYSTEM_NUMBER);
    }

    public static String getCyZnVersion() {
        String subversion = getCyZnVersionNum().split("_")[0].substring(0, 7);
        DswLog.d(TAG, "getCyZnVersion subversion="+subversion);
        return subversion;
    }
}
