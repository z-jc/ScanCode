package ble.redo.youten.blec_s;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ClientActivity extends Activity implements OnItemClickListener {

    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter; // Bluetooth适配器
    private BluetoothDevice device;             // 蓝牙设备
    private ListView mListView;
    private ArrayList<ChatMessage> list;
    private ClientAdapter clientAdapter;        // ListView适配器
    private Button sendButton = null;
    private EditText editText = null;
    private BluetoothSocket socket;     // 客户端socket
    private ClientThread mClientThread; // 客户端运行线程
    private ReadThread mReadThread;     // 读取流线程

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        // TODO Auto-generated method stub
        mContext = this;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//获取本地蓝牙...

        list = new ArrayList<ChatMessage>();// 初始化list,由于我们最后会将数据以列表的形式进行显示，因此使用ListView...
        clientAdapter = new ClientAdapter(mContext, list);  //适配器,用来限制如何显示ListView...
        mListView = (ListView) findViewById(R.id.list);
        mListView.setFastScrollEnabled(true);  //使用快速滑动功能..目的是能够快速滑动到指定位置...
        mListView.setAdapter(clientAdapter);
        mListView.setOnItemClickListener(this);

        /* 下面是注册receiver监听，注册广播...说一下为什么要注册广播...
         * 因为蓝牙的通信，需要进行设备的搜索，搜索到设备后我们才能够实现连接..如果没有搜索，那还谈什么连接...
         * 因此我们需要搜索，搜索的过程中系统会自动发出三个广播...这三个广播为：
         * ACTION_DISCOVERY_START:开始搜索...
         * ACTION_DISCOVERY_FINISH:搜索结束...
         * ACTION_FOUND:正在搜索...一共三个过程...因为我们需要对这三个响应过程进行接收，然后实现一些功能，因此
         * 我们需要对广播进行注册...知道广播的人应该都知道，想要对广播进行接收，必须进行注册，否则是接收不到的...
         * */
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        // 定义一个集合，来保存已经配对过的设备...
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {  //如果存在设备...
            for (BluetoothDevice device : pairedDevices) {//遍历...

                //将手机名字和物理地址放入到listview中...
                list.add(new ChatMessage(device.getName() + "\t\t\t" + device.getAddress(), true));

                clientAdapter.notifyDataSetChanged();  //重新绘制ListView

                mListView.setSelection(list.size() - 1); //设置list保存的信息的位置...说白了该条信息始终在上一条信息的下方...
            }
        } else {
            list.add(new ChatMessage("没有已经配对过的设备", true));
            clientAdapter.notifyDataSetChanged();
            mListView.setSelection(list.size() - 1);
        }

        editText = (EditText) findViewById(R.id.edit);
        editText.setEnabled(false);
        editText.clearFocus();  //设置没有焦点..也就是无法输入任何文字...

        sendButton = (Button) findViewById(R.id.btn_send);
        sendButton.setEnabled(false);

        sendButton.setOnClickListener(new OnClickListener() {//设置监听，只有获取到焦点后才能进行此过程...

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String msg = editText.getText().toString();

                if (msg.length() > 0) {
                    //调用第五部分线程，通过线程发送我们输入的文本...
                    sendMessageHandler(msg);
                    //发送完清空...
                    editText.setText("");
                    /*editText.clearFocus();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//定义一个输入法对象...通过Context.INPUT_METHOD_SERVICE获取实例...
                    //当EditText没有焦点的时候，阻止输入法的弹出...其实就是在没点击EditText获取焦点的时候，没有输入法的显示...
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);*/
                } else {
                    Toast.makeText(mContext, "发送内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /**
         * 注册广播监听监听连接状态
         * */
        registerBoradcastReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mBluetoothAdapter != null) {  //本地蓝牙存在...
            if (!mBluetoothAdapter.isEnabled()) {   //判断蓝牙是否被打开...
                // 发送打开蓝牙的意图，系统会弹出一个提示对话框,打开蓝牙是需要传递intent的...
                // intent这个重要的东西，大家应该都知道，它能够实现应用之间通信和交互....
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //打开本机的蓝牙功能...使用startActivityForResult（）方法...这里我们开启的这个Activity是需要它返回执行结果给主Activity的...
                startActivityForResult(enableIntent, RESULT_FIRST_USER);
                Intent displayIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                // 设置蓝牙的可见性，最大值3600秒，默认120秒，0表示永远可见(作为客户端，可见性可以不设置，服务端必须要设置)
                displayIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                //这里只需要开启另一个activity，让其一直显示蓝牙...没必要把信息返回..因此调用startActivity()
                startActivity(displayIntent);
                //打开蓝牙
                mBluetoothAdapter.enable();
            }
        }
    }

    /**
     * 扫描蓝牙设备
     */
    @Override
    protected void onResume() {
        super.onResume();
        scanDevice();
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
     * 广播接收连接状态
     */
    private BroadcastReceiver stateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                //连接上了
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String mac = device.getAddress();
                Log.e("TAG", name + "已连接");
                showToast(name + "已连接");
                Message msg2 = linkDetectedHandler.obtainMessage();
                msg2.obj = name + "已经连接";
                msg2.what = 0;
                linkDetectedHandler.sendMessage(msg2);
            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                //蓝牙连接被切断
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String mac = device.getAddress();
                Log.e("TAG", name + "连接已断开...");
                showToast(name + "连接已断开...");
                Message msg2 = linkDetectedHandler.obtainMessage();
                msg2.obj = name + "连接已断开...";
                msg2.what = 0;
                linkDetectedHandler.sendMessage(msg2);
                return;
            }
        }
    };

    private void showToast(String value) {
        Toast.makeText(ClientActivity.this, value, Toast.LENGTH_SHORT).show();
    }


    /**
     * 蓝牙设备扫描过程中(mBluetoothAdapter.startDiscovery())会发出的消息
     * ACTION_FOUND 扫描到远程设备
     * ACTION_DISCOVERY_FINISHED 扫描结束
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction(); //获取当前正在执行的动作...
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 通过EXTRA_DEVICE附加域来得到一个BluetoothDevice设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 如果这个设备是不曾配对过的，添加到list列表
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    list.add(new ChatMessage(device.getName() + "\t\t\t" + device.getAddress(), false));
                    clientAdapter.notifyDataSetChanged();
                    mListView.setSelection(list.size() - 1);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);  //这步是如果设备过多是否显示滚动条...
                if (mListView.getCount() == 0) {
                    list.add(new ChatMessage("没有发现蓝牙设备", false));
                    clientAdapter.notifyDataSetChanged();
                    mListView.setSelection(list.size() - 1);
                }
            }
        }
    };

    private Handler linkDetectedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //这步多了一个判断..判断的是ListView保存的数据是我们要传输给服务端的数据，还是那些我们定义好的提示数据...
            if (msg.what == 1) {
                list.add(new ChatMessage((String) msg.obj, true));
            } else {
                list.add(new ChatMessage((String) msg.obj, false));
            }
            clientAdapter.notifyDataSetChanged();
            mListView.setSelection(list.size() - 1);
        }
    };

    // Handler更新UI...
    // 当连接上服务器的时候才可以选择发送数据和断开连接，并且要对界面进行刷新操作...
    private Handler refreshUI = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                //disconnect.setEnabled(true);
                sendButton.setEnabled(true);
                editText.setEnabled(true);
            }
        }
    };

    // 开启客户端连接服务端，一个新的线程...
    private class ClientThread extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (device != null) {
                try {
                    /**
                     * 下面这步也是关键，我们如果想要连接服务器，我们需要调用方法createRfcommSocketToServiceRecord
                     * 参数00001101-0000-1000-8000-00805F9B34FB表示的是默认的蓝牙串口...通过传递参数调用方法，
                     * 会返回给我们一个套接字..这一步就是获取套接字，实现连接的过程...
                     * */
                    socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    Message msg = linkDetectedHandler.obtainMessage();
                    msg.obj = "请稍候，正在连接服务器: " + BluetoothMsg.BlueToothAddress;
                    msg.what = 0;
                    linkDetectedHandler.sendMessage(msg); //调用线程，显示msg信息...

                    // 通过socket连接服务器，正式形成连接...这是一个阻塞过程，直到连接建立或者连接失效...
                    socket.connect();
                    //如果实现了连接，那么服务端和客户端就共享一个RFFCOMM信道...
                    Message msg2 = new Message();
                    msg2.obj = "已经连接上服务端！可以发送信息";
                    msg2.what = 0;
                    linkDetectedHandler.sendMessage(msg2); //调用线程，显示msg信息...
                    // 如果连接成功了...这步就会执行...更新UI界面...否则走catch（IOException e）
                    Message uiMessage = new Message();
                    uiMessage.what = 0;
                    refreshUI.sendMessage(uiMessage);

                    // 可以开启读数据线程
                    mReadThread = new ReadThread();
                    mReadThread.start();
                } catch (IOException e) {
                    Message msg = linkDetectedHandler.obtainMessage();
                    msg.obj = "连接服务端异常！断开连接重新试一试。";
                    msg.what = 0;
                    linkDetectedHandler.sendMessage(msg);
                }
            }
        }
    }

    /**
     * 读取数据
     * */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream is = null;
            try {
                is = socket.getInputStream(); //获取服务器发过来的所有字节...
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("get");
            while (true) {
                try {//读取过程，将数据信息保存在ListView中...
                    if ((bytes = is.read(buffer)) > 0) {
                        byte[] data = new byte[bytes];
                        for (int i = 0; i < data.length; i++) {
                            data[i] = buffer[i];
                        }
                        String s = new String(data);
                        Message msg = linkDetectedHandler.obtainMessage();
                        msg.obj = "Server:" + s;
                        msg.what = 1;  //这里的meg.what=1...表示的是服务器发送过来的数据信息..
                        linkDetectedHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    try {
                        is.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    //这一步表示的是发送数据的过程...
    private void sendMessageHandler(String msg) {
        if (socket == null) {
            Toast.makeText(mContext, "没有可用的连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream os = socket.getOutputStream();
            os.write(msg.getBytes());//获取所有的自己然后往外发送...
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.add(new ChatMessage("Clint：" + msg, false));  //将数据存放到list中
        clientAdapter.notifyDataSetChanged();
        mListView.setSelection(list.size() - 1);
    }

    // 停止服务
    private void closeClient() {
        new Thread() {
            public void run() {
                if (mClientThread != null) {
                    mClientThread.interrupt();
                    mClientThread = null;
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
                } catch (IOException e) {
                }
            }
        }.start();
    }

    /**
     * 扫描设备
     * */
    private void scanDevice() {
        if (mBluetoothAdapter.isDiscovering()) {  //如果正在处于扫描过程...
            mBluetoothAdapter.cancelDiscovery();  //取消扫描...
        } else {
            list.clear();
            clientAdapter.notifyDataSetChanged();
            // 每次扫描前都先判断一下是否存在已经配对过的设备
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    list.add(new ChatMessage(device.getName() + "\t\t\t" + device.getAddress(), true));
                    clientAdapter.notifyDataSetChanged();
                    mListView.setSelection(list.size() - 1);
                }
            } else {
                list.add(new ChatMessage("No devices have been paired", true));
                clientAdapter.notifyDataSetChanged();
                mListView.setSelection(list.size() - 1);
            }
            /* 开始搜索 */
            mBluetoothAdapter.startDiscovery();
        }
    }

    /**
     * 去连接某一台设备
     * */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        ChatMessage item = list.get(arg2);  //item保存着message和一个boolean数值...
        String info = item.getMessage();    //单纯获取message的信息...
        String address = info.substring(info.length() - 17);//获取MAC地址...其实就是硬件地址...
        BluetoothMsg.BlueToothAddress = address;
        // 停止扫描
        // BluetoothAdapter.startDiscovery()很耗资源，在尝试配对前必须中止它
        mBluetoothAdapter.cancelDiscovery();
        // 通过Mac地址去尝试连接一个设备
        device = mBluetoothAdapter.getRemoteDevice(BluetoothMsg.BlueToothAddress);
        mClientThread = new ClientThread();  //开启新的线程...
        mClientThread.start();
        BluetoothMsg.isOpen = true;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
            // 关闭蓝牙
            mBluetoothAdapter.disable();
        }
        unregisterReceiver(mReceiver);
        closeClient();
        BluetoothMsg.isOpen = false;
        BluetoothMsg.serviceOrCilent = BluetoothMsg.ServerOrCilent.NONE;
    }

}