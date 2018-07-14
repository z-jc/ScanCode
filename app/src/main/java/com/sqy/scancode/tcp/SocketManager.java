package com.sqy.scancode.tcp;

import com.sqy.scancode.manger.ThreadPoolManager;
import com.sqy.scancode.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketManager {

    public static SocketManager instance = new SocketManager();

    private SocketManager() {}

    public static SocketManager getInstance() {
        return instance;
    }

    /**
     * 发送心跳包
     */
    public void sendHeartBeat(String senData) {
        ThreadPoolManager.getSingleInstance().execute(new SendData(senData));
    }

    class SendData implements Runnable {

        private String TAG = "SendData";
        private PrintWriter printWriter = null;
        private int PORT = 9988;     //端口号
        private String IP = "139.217.20.92";
        private String sendData;

        public SendData(String data) {
            this.sendData = data;
        }

        public void run() {
            Socket socket = null;
            try {
                socket = new Socket(IP, PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            LogUtils.e(TAG, "发送的数据包:" + sendData);
            int ar_i = sendHeartBeatPackage(socket, sendData);//数据包
            if (ar_i != 1) {
                LogUtils.e(TAG, "发送失败");
            }
        }

        /**
         * 发送心跳包到服务端
         */
        public int sendHeartBeatPackage(Socket socket1, String msg) {
            int returnCode = 0;
            if (socket1.isClosed() || socket1 == null) {
                returnCode = 0;
            }
            try {
                printWriter = new PrintWriter(socket1.getOutputStream());
                printWriter.println(msg);
                printWriter.flush();
                socket1.getOutputStream().flush();
                InputStream mmInStream = null;
                byte[] buffer = new byte[1024];
                int bytes;
                try {
                    mmInStream = socket1.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (true) {
                    try {
                        if ((bytes = mmInStream.read(buffer)) > 0) {
                            byte[] buf_data = new byte[bytes];
                            for (int i = 0; i < bytes; i++) {
                                buf_data[i] = buffer[i];
                            }
                            String result = new String(buf_data);
                            EventBus.getDefault().post(new SocketMsg(result));
                            mmInStream.close();
                            socket1.close();
                            returnCode = 1;
                        }
                    } catch (IOException e) {
                        break;
                    }
                }
            } catch (Exception e) {
                returnCode = 0;
            }
            return returnCode;
        }
    }
}