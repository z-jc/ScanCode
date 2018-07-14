package com.sqy.scancode.http;

import com.sqy.scancode.util.LogUtils;

import java.io.File;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttp {
    /**
     * 上传文件
     * @param
     * @throws IOException
     */
    public static void postFile(File file) throws IOException {
//      MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("video/*; charset=utf-8");
//      RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, file);
//      Request request = new Request.Builder().url(Constant.VIDEO_URL).post(body).build();
        OkHttpClient client = new OkHttpClient();//获取OkHttpClient实例
        RequestBody fileBody = null;
        MultipartBody.Builder builder = null;
        Request request = null;

            fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            builder.addPart(Headers.of(
                    "Content-Disposition","form-data;name=\"file1\";filename=\"IMG_1.jpeg\""), fileBody)
                    /*.addFormDataPart("postData",postData)*/
                    .build();
            request = new Request.Builder()
                    .url("http://3cqd.com/goodsSys/api/upfile.asp")
                    .post(builder.build())
                    .build();
            LogUtils.e("TAG","request");

        /**
         * 请求回调结果
         * */
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.e("TAG","onFailure: "+e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.e("TAG","onResponse: "+response.body().string());
            }
        });
    }
}