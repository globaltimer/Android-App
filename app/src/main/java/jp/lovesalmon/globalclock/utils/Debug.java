package jp.lovesalmon.globalclock.utils;

import android.util.Log;

/**
 * Created by hhonda_admin on 2017-02-07.
 */

public class Debug {
    public static final boolean DBG = false;
    public static void Log(String value) {
        Log.e("DLog", value);
    }

    public static void Log(String value, Throwable throwable) {
        Log.e("DLog", value, throwable);
    }
}
