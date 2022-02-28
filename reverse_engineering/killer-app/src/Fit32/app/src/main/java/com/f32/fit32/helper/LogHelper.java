package com.f32.fit32.helper;

import android.util.Log;
import android.widget.Toast;

public class LogHelper {
    private final static String PREFIX = "AFF ";

    public static void debug(String string) {
        Log.d(PREFIX + "DEBUG", string);
    }

    public static void error(String string) {
        Log.e(PREFIX + "ERROR", string);
        toast("ERROR " + string);
    }

    public static void toast(String string) {
        Toast.makeText(SharedPreferencesHelper.instance.getContext(), string, Toast.LENGTH_LONG).show();
    }
}
