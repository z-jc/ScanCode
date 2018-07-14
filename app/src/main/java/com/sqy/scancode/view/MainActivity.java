package com.sqy.scancode.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.sqy.scancode.MyApplication;
import com.sqy.scancode.R;
import com.sqy.scancode.http.OkHttp;
import com.sqy.scancode.tcp.SocketManager;
import com.sqy.scancode.util.ByteUtil;
import com.sqy.scancode.util.JsonUtil;
import com.sqy.scancode.util.LogUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import android_serialport_api.SerialPortUtil;
import cn.bertsir.zbar.QrConfig;
import cn.bertsir.zbar.QrManager;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = "MainActivity";

    private Button btn_camera;
    private Button btn_zxing;
    private Button btn_bluetooth;
    private Button btn_send;
    private Button btn_base64;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        /*new Thread(new Runnable() {
            public void run() {
                String PATH_IMAGES = Environment.getExternalStorageDirectory()
                        + File.separator + "easy_check/";
                File file = new File(PATH_IMAGES + "IMG_1.jpeg");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("file", file.getAbsolutePath());
                    OkHttp.postFile(file);
                    LogUtils.e("TAG", "上传照片：" + jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();*/
    }

    /**
     * 初始化UI
     */
    private void initView() {
        btn_zxing = (Button) findViewById(R.id.bt_1);
        btn_camera = (Button) findViewById(R.id.bt_2);
        btn_bluetooth = (Button) findViewById(R.id.bt_3);
        btn_send = (Button) findViewById(R.id.bt_5);
        btn_base64 = (Button) findViewById(R.id.bt_6);
        tv = (TextView) findViewById(R.id.text);
        btn_zxing.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_bluetooth.setOnClickListener(this);
        btn_base64.setOnClickListener(this);
    }

    /**
     * 拍完照片收到回调路径
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 101:
                Log.e("TAG", "拍完照片了...");
                try {
                    List<String> list = data.getStringArrayListExtra("list");
                    for (int i = 0; i < list.size(); i++) {
                        Log.e("TAG", "照片" + i + ":" + list.get(i));
                    }
                    tv.setText(list.get(0) + "\n" + list.get(1) + "\n" + list.get(2));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_1:
                start();
                break;
            case R.id.bt_2:
                startActivityForResult(new Intent(MainActivity.this, TakePhotoActivity.class), 222);
                break;
            case R.id.bt_3:
                startActivity(new Intent(MainActivity.this, BluetoothServerActivity.class));
                break;
            case R.id.bt_5:
                try {
                    SocketManager.getInstance().sendHeartBeat(JsonUtil.getHeartbeat());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_6:
                String base64 = ByteUtil.getBase64(Environment.getExternalStorageDirectory().getAbsolutePath() + "/easy_check/IMG_1.jpeg");
                LogUtils.e("TAG", "Base64:" + base64);
                LogUtils.e("TAG", "length:" + base64.length());
                try {
                    SocketManager.getInstance().sendHeartBeat(JsonUtil.getPhoto(base64));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 扫码界面
     */
    private void start() {
        int scan_type = 0;
        int scan_view_type = 0;
        QrConfig qrConfig = new QrConfig.Builder()
                .setDesText("开始扫码")//扫描框下文字
                .setShowDes(false)//是否显示扫描框下面文字
                .setShowLight(true)//显示手电筒按钮
                .setShowTitle(true)//显示Title
                .setShowAlbum(true)//显示从相册选择按钮
                .setCornerColor(Color.parseColor("#E42E30"))//设置扫描框颜色
                .setLineColor(Color.parseColor("#E42E30"))//设置扫描线颜色
                .setLineSpeed(QrConfig.LINE_FAST)//设置扫描线速度
                .setScanType(scan_type)//设置扫码类型（二维码，条形码，全部，自定义，默认为二维码）
                .setScanViewType(scan_view_type)//设置扫描框类型（二维码还是条形码，默认为二维码）
                .setCustombarcodeformat(QrConfig.BARCODE_EAN13)//此项只有在扫码类型为TYPE_CUSTOM时才有效
                .setPlaySound(false)//是否扫描成功后bi~的声音
                .setDingPath(R.raw.beep)//设置提示音(不设置为默认的Ding~)
                .setIsOnlyCenter(false)//是否只识别框中内容(默认为全屏识别)
                .setTitleText("扫描二维码")//设置Tilte文字
                .setTitleBackgroudColor(Color.parseColor("#262020"))//设置状态栏颜色
                .setTitleTextColor(Color.WHITE)//设置Title文字颜色
                .create();
        QrManager.getInstance().init(qrConfig).startScan(MainActivity.this, new QrManager.OnScanResultCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onScanSuccess(String result) {
                LogUtils.e("TAG", "result:" + result);
                showToast("扫描到的结果:" + result);
            }
        });
    }

    /**
     * @Params:value 弹出显示的内容
     */
    private void showToast(String value) {
        Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MyApplication.isFlagSerial) {
            SerialPortUtil.close();
        }
    }
}