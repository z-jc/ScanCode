package com.sqy.scancode.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/6/7.
 */

public class DateUtil {
    /**
     * 获取当前时间(格式化)
     */
    public static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 获取当前时间毫秒值
     */
    public static String getTime() {
        return String.valueOf(System.currentTimeMillis());
    }
}