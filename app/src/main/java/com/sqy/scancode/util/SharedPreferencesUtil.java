
package com.sqy.scancode.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2018/6/7.
 */

public class SharedPreferencesUtil {

    public static String User = "ScanCode";
    public static SharedPreferencesUtil mInstance = null;
    public static SharedPreferences sharedPreferences;

    public static SharedPreferencesUtil getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new SharedPreferencesUtil();
            sharedPreferences = context.getSharedPreferences(User, Context.MODE_PRIVATE);
        }
        return mInstance;
    }

    /**
     * 写入字符串型数据
     */
    public static void setString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 获取字符串型数据
     */
    public static String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    /**
     * 写入整型数据
     */
    public static void setInteger(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 获取整型数据
     */
    public static int getInteger(String key) {
        return sharedPreferences.getInt(key, 10);
    }

    /**
     * 写入布尔型数据
     */
    public static void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 获取布尔型数据
     */
    public static boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
}