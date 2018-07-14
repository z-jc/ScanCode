package android_serialport_api;

import android.os.Handler;
import android.os.Message;

import com.sqy.scancode.MyApplication;
import com.sqy.scancode.util.ByteUtil;
import com.sqy.scancode.util.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.sqy.scancode.MyApplication.isFlagSerial;


/**
 * Created by Administrator on 2018/5/31.
 */

public class SerialPortUtil {

    public static String TAG = "SerialPortUtil";
    public static SerialPort serialPort = null;
    public static InputStream inputStream = null;
    public static OutputStream outputStream = null;
    public static Thread receiveThread = null;
    public static String strData = "";
    public static Handler mHandler;

    /**
     * 打开串口
     */
    public static boolean open() {
        boolean isopen = false;
        try {
            serialPort = new SerialPort(new File("/dev/ttyS3"), 115200, 0);
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            receive();
            isopen = true;
            MyApplication.isFlagSerial = true;
        } catch (IOException e) {
            e.printStackTrace();
            isopen = false;
        }
        return isopen;
    }

    /**
     * 关闭串口
     */
    public static boolean close() {
        boolean isClose = false;
        LogUtils.e(TAG, "关闭串口");
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            isClose = true;
            MyApplication.isFlagSerial = false;//关闭串口时，连接状态标记为false
        } catch (IOException e) {
            e.printStackTrace();
            isClose = false;
        }
        return isClose;
    }

    /**
     * 发送串口指令
     */
    public static void sendString(String data, Handler handler) {
        mHandler = handler;
        try {
            outputStream.write(ByteUtil.hex2byte(data));
            outputStream.flush();
            LogUtils.e(TAG, "sendSerialData:" + data);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e(TAG, "发送指令出现异常");
        }
    }

    /**
     * 接收串口数据的方法
     */
    public static void receive() {
        if (receiveThread != null) {
            return;
        }
        receiveThread = new Thread() {
            @Override
            public void run() {
                while (MyApplication.isFlagSerial) {
                    try {
                        byte[] readData = new byte[32];
                        if (inputStream == null) {
                            return;
                        }
                        int size = inputStream.read(readData);
                        if (size > 0 && isFlagSerial) {
                            strData = ByteUtil.byteToStr(readData, size);
                            Message msgs = mHandler.obtainMessage();
                            msgs.obj = strData;
                            switch (strData.substring(2, 4)) {
                                case "01":
                                    msgs.what = 0x01;
                                    break;
                                case "02":
                                    msgs.what = 0x02;
                                    break;
                                case "03":
                                    msgs.what = 0x03;
                                    break;
                                case "04":
                                    msgs.what = 0x04;
                                    break;
                                case "83":
                                    msgs.what = 0x83;
                                    break;
                                case "85":
                                    msgs.what = 0x85;
                                    break;
                                case "86":
                                    msgs.what = 0x86;
                                    break;
                                case "41":
                                    msgs.what = 0x41;
                                    break;
                                case "42":
                                    msgs.what = 0x42;
                                    break;
                            }
                            mHandler.sendMessage(msgs);
                            LogUtils.e(TAG, "readSerialData:" + strData);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        receiveThread.start();
    }
}