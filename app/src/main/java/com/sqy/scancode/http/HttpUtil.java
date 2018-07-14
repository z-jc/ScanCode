package com.sqy.scancode.http;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sqy.scancode.MyApplication;
import com.sqy.scancode.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class HttpUtil {

    public static RequestQueue requestQueue;

    public static void init(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    private static final HttpUtil instance = new HttpUtil();

    private HttpUtil() {
    }

    //这里提供了一个供外部访问本class的静态方法，可以直接访问
    public static HttpUtil getInstance() {
        return instance;
    }

    public void get(String orderUrl, final Handler handler, final int what) {
        StringRequest request = new StringRequest(Request.Method.GET, orderUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Message msg = handler.obtainMessage();
                msg.what = what;
                msg.obj = response;
                handler.sendMessage(msg);
                Log.e("TAG", "response:" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Message msg = handler.obtainMessage();
                msg.what = what;
                msg.obj = error.getMessage();
                handler.sendMessage(msg);
                Log.e("TAG", "error:" + error.getMessage());
            }
        });
        requestQueue.add(request);
    }

    public static void post(String url, final JSONObject jsonObject, final Handler handler) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        LogUtils.e("TAG", "请求成功:" + jsonObject.toString());
                        Message msg = handler.obtainMessage();
                        msg.what = 200;
                        msg.obj = jsonObject.toString();
                        handler.sendMessage(msg);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("TAG", "网络加载异常");
                Message msg = handler.obtainMessage();
                msg.what = 400;
                msg.obj = "网络加载异常...";
                handler.sendMessage(msg);
                LogUtils.e("TAG", volleyError.getMessage());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}