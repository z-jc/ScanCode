package com.sqy.scancode;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.sqy.scancode.http.HttpUtil;
import com.sqy.scancode.service.ScanCodeService;
import com.sqy.scancode.util.CrashHandler;
import com.sqy.scancode.util.LogUtils;
import com.sqy.scancode.util.SharedPreferencesUtil;

public class MyApplication extends Application {
    /**
     * 标记当前串口状态(true:打开,false:关闭)
     **/
    public static boolean isFlagSerial = false;

    @Override
    public void onCreate() {
        super.onCreate();
        HttpUtil.init(getContext());
        //SharedPreferencesUtil.getInstance(getContext());
        LogUtils.init();
        CrashHandler.getInstance().init(getContext());
        startService(new Intent(getContext(), ScanCodeService.class));
    }

    /**
     * 获取上下文
     * */
    public Context getContext() {
        return this.getApplicationContext();
    }
}