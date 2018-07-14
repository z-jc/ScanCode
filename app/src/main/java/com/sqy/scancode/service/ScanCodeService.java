package com.sqy.scancode.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sqy.scancode.MyApplication;
import com.sqy.scancode.tcp.SocketManager;
import com.sqy.scancode.tcp.SocketMsg;
import com.sqy.scancode.util.ByteUtil;
import com.sqy.scancode.util.Bytes;
import com.sqy.scancode.util.DateUtil;
import com.sqy.scancode.util.JsonUtil;
import com.sqy.scancode.util.LogUtils;
import com.sqy.scancode.view.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android_serialport_api.SerialPortUtil;

public class ScanCodeService extends Service {

    private String TAG = "ScanCodeService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e(TAG, "当前时间毫秒值:" + DateUtil.getTime());
        EventBus.getDefault().register(this);
        if (!MyApplication.isFlagSerial) {
            SerialPortUtil.open();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!MyApplication.isFlagSerial) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                SerialPortUtil.sendString(Bytes.getDCDY(), handler);
            }
        }).start();
        try {
            SocketManager.getInstance().sendHeartBeat(JsonUtil.getHeartbeat());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收服务端的数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketMsg(SocketMsg socketMsg) {
        String data = socketMsg.strData;
        LogUtils.e(TAG, "来自ServerSocket的数据:" + data);
    }

    /**
     * 接收串口数据
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String data = msg.obj.toString();
            LogUtils.e(TAG, "来自SerialPort的数据:" + data);
            switch (msg.what) {
                case 0x01:

                    break;
                case 0x02:

                    break;
                case 0x03:

                    break;
                case 0x04:

                    break;
                case 0x83:

                    break;
                case 0x85:

                    break;
                case 0x86:

                    break;
                case 0x41:

                    break;
                case 0x42:

                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG, "onDestroy...");
        EventBus.getDefault().unregister(this);
        if(MyApplication.isFlagSerial){
            SerialPortUtil.close();
        }
    }
}