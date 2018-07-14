package com.sqy.scancode.view;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sqy.scancode.R;
import com.sqy.scancode.adapter.ServerAdapter;
import com.sqy.scancode.bluetoogh.BluetoothMsg;
import com.sqy.scancode.bluetoogh.ChatMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothServerActivity extends Activity {

    // 服务器名称
    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";

    private ListView mListView;
    private ArrayList<ChatMessage> list;
    private Button sendButton;
    private EditText editMsgView;
    private ServerAdapter mAdapter;   // 服务端ListView适配器
    private Context mContext;

    private BluetoothAdapter mBluetoothAdapter;  // Bluetooth适配器
    //private BluetoothDevice mBluetoothDevice;
    private BluetoothServerSocket mServerSocket; // 服务端socket
    private BluetoothSocket socket;              // socket
    private ServerThread mServerThread;          // 服务端线程
    private ReadThread mReadThread;              // 读取流线程
    public static boolean isConnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothserver);
        init();
    }

    /**
     * 初始化变量
     */
    private void init() {
        // TODO Auto-generated method stub
        mContext = this;
        list = new ArrayList<ChatMessage>();
        mAdapter = new ServerAdapter(mContext, list);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setFastScrollEnabled(true);
        mListView.setAdapter(mAdapter);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        editMsgView = (EditText) findViewById(R.id.edit);
        editMsgView.clearFocus();
        editMsgView.setEnabled(false);

        sendButton = (Button) findViewById(R.id.send);
        sendButton.setEnabled(false);
        sendButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String msg = editMsgView.getText().toString();
                if (msg.length() > 0) {
                    sendMessageHandler(msg);
                    editMsgView.setText("");
                    //editMsgView.clearFocus();
                    /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editMsgView.getWindowToken(), 0);*/
                } else {
                    Toast.makeText(mContext, "发送内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        registerBoradcastReceiver();
    }

    /**
     * 监听设备的连接与断开
     */
    private void registerBoradcastReceiver() {
        IntentFilter stateChangeFilter = new IntentFilter(
                BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter connectedFilter = new IntentFilter(
                BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter disConnectedFilter = new IntentFilter(
                BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(stateChangeReceiver, stateChangeFilter);
        registerReceiver(stateChangeReceiver, connectedFilter);
        registerReceiver(stateChangeReceiver, disConnectedFilter);
    }

    /**
     * 广播监听蓝牙连接状态
     */
    private BroadcastReceiver stateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                //连接上了
                isConnect = true;
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String mac = device.getAddress();
                showToast(name + "已连接");
                Log.e("TAG", name + "已连接" + mac);
                Message msg2 = LinkDetectedHandler.obtainMessage();
                msg2.obj = name + "已经连接";
                msg2.what = 0;
                LinkDetectedHandler.sendMessage(msg2);
            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                //蓝牙连接被切
                isConnect = false;
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String mac = device.getAddress();
                Log.e("TAG", name + "连接已断开...");
                Message msg2 = LinkDetectedHandler.obtainMessage();
                msg2.obj = name + "连接已断开...";
                msg2.what = 0;
                LinkDetectedHandler.sendMessage(msg2);
                showToast(name + "的连接被断开");
                return;
            }
        }
    };

    private void showToast(String msg) {
        Toast.makeText(BluetoothServerActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    // Handler刷新UI
    private Handler LinkDetectedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Toast.makeText(mContext, (String)msg.obj, Toast.LENGTH_SHORT).show();
            if (msg.what == 1) {
                list.add(new ChatMessage((String) msg.obj, true));
            } else {
                list.add(new ChatMessage((String) msg.obj, false));
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(list.size() - 1);
        }
    };

    // 当服务器连接上客户端的时候才可以选择发送数据和断开连接
    private Handler refreshUI = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                //disconnect.setEnabled(true);
                sendButton.setEnabled(true);
                editMsgView.setEnabled(true);
            }
        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (BluetoothMsg.isOpen) {
            Toast.makeText(mContext, "连接已打开，可以通信", Toast.LENGTH_SHORT).show();
        }
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                // 发送打开蓝牙的意图
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, RESULT_FIRST_USER);

                // 设置蓝牙的可见性，最大值3600秒，默认120秒，0表示永远可见(作为客户端，可见性可以不设置，服务端必须要设置)
                Intent displayIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                displayIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                startActivity(displayIntent);
                // 直接打开
                mBluetoothAdapter.enable();
            }
        }
        mServerThread = new ServerThread();
        mServerThread.start();
        BluetoothMsg.isOpen = true;
    }

    //开启服务器
    private class ServerThread extends Thread {
        public void run() {
            try {
                /* 创建一个蓝牙服务器
                 * 参数分别：服务器名称、UUID	 */
                mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM,
                        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

                Message msg = new Message();
                msg.obj = "请稍候，正在等待客户端的连接...";
                msg.what = 0;
                LinkDetectedHandler.sendMessage(msg);
                while (true) {
                    /* 接受客户端的连接请求 */
                    // 这是一个阻塞过程，直到建立一个连接或者连接失效
                    // 通过BluetoothServerSocket得到一个BluetoothSocket对象，管理这个连接
                    if (!isConnect) {
                        socket = mServerSocket.accept();
                        Message uiMessage = new Message();
                        uiMessage.what = 0;
                        refreshUI.sendMessage(uiMessage);
                        Log.e("TAG", "有新的连接加入进来了...");

                        //启动接受数据
                        mReadThread = new ReadThread();
                        mReadThread.start();
                    }
                    Thread.sleep(1000);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //读取数据
    private class ReadThread extends Thread {
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream mmInStream = null;
            try {
                mmInStream = socket.getInputStream();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            Log.e("TAG", "开始读取数据....");
            while (true) {
                try {
                    // Read from the InputStream
                    if ((bytes = mmInStream.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        String s = new String(buf_data);
                        Message msg = new Message();
                        msg.obj = "Clint:" + s;
                        Log.e("TAG", "来自Clint的消息:" + s);
                        msg.what = 1;
                        LinkDetectedHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    try {
                        mmInStream.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    // 发送数据
    private void sendMessageHandler(String msg) {
        if (socket == null) {
            Toast.makeText(mContext, "没有可用的连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream os = socket.getOutputStream();
            os.write(msg.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        list.add(new ChatMessage("Server:" + msg, false));
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(list.size() - 1);
    }

    // 停止服务
    private void closeServer() {
        isConnect = false;
        new Thread() {
            public void run() {
                if (mServerThread != null) {
                    mServerThread.interrupt();
                    mServerThread = null;
                }
                if (mReadThread != null) {
                    mReadThread.interrupt();
                    mReadThread = null;
                }
                try {
                    if (socket != null) {
                        socket.close();
                        socket = null;
                    }
                    if (mServerSocket != null) {
                        mServerSocket.close();
                        mServerSocket = null;
                    }
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        closeServer();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothAdapter.disable();
        }
        BluetoothMsg.serviceOrCilent = BluetoothMsg.ServerOrCilent.NONE;
        BluetoothMsg.isOpen = false;
    }
}