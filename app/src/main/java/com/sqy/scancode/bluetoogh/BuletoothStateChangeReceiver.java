package com.sqy.scancode.bluetoogh;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sqy.scancode.util.LogUtils;
import com.sqy.scancode.view.BluetoothServerActivity;

public class BuletoothStateChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
            /**
             * 蓝牙已连接
             * */
            BluetoothServerActivity.isConnect = true;
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String name = device.getName();
            String mac = device.getAddress();
            LogUtils.e("TAG", "连接上---name:" + name + ",mac:" + mac);
        } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
            /**
             * 已断开蓝牙
             * */
            BluetoothServerActivity.isConnect = false;
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String name = device.getName();
            String mac = device.getAddress();
            LogUtils.e("TAG", "断开连接---name:" + name + ",mac:" + mac);
            return;
        }
    }
}
