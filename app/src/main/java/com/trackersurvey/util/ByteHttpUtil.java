package com.trackersurvey.util;

import android.util.Log;

import com.trackersurvey.happynavi.LoginActivity;
import com.trackersurvey.http.HttpUtil;
import com.trackersurvey.http.ResponseData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class ByteHttpUtil {

    private static OkHttpClient client;
    public         Object       responseObject; // 请求返回Data字段值
    public         String       message;
    public         String       code;
    //    public         String       responseString;
    public         InputStream  inputStream;
    public static  String       s;
    public static  int          isConnected;

    public void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        // 打印请求信息
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("HttpLogInfo", message);
            }
        });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (client == null) {
            client = new OkHttpClient.Builder()
                    //.readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    //.writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    //.connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    //FaceBook 网络调试器，可在Chrome调试网络请求，查看SharePreferences,数据库等
                    //.addNetworkInterceptor(new StethoInterceptor())
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .addNetworkInterceptor(httpLoggingInterceptor)
                    .cookieJar(new CookieJar() {
                        private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            cookieStore.put(url.host(), cookies);
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            List<Cookie> cookies = cookieStore.get(url.host());
                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    })
                    .build();
        }
        RequestBody requestBody = this.parameter();
        if (requestBody == null) {
            Log.i("Cookie", "requestBody == null");
        } else {
            Log.i("Cookie", "requestBody != null");
        }

        Request request;
        if (requestBody == null) {
            request = new Request.Builder()
                    .url(address + this.parameterGet())
                    .build();
        } else if (LoginActivity.token != null) {
            Log.i("info_", "Token != null, 这里执行了");
            Log.i("info_", "" + s);
            request = new Request.Builder()
                    .addHeader("Cookie", "Token:" + LoginActivity.token)
                    .url(address)
                    .post(requestBody)
                    .build();
            Log.i("Cookie", "加入Cookie了");
            Log.i("Cookie", " s!=null 加入Cookie" + "Token:" + LoginActivity.token);
        } else {
            Log.i("info_", "Token == null, 这里执行了");
            Log.i("info_", "" + s);
            request = new Request.Builder()
                    .url(address)
                    .post(requestBody)
                    .addHeader("Cookie", "123")
                    .build();
            Log.i("Cookie", "加入Cookie了");
            Log.i("Cookie", " s==null 加入Cookie" + "123");
        }
        client.newCall(request).enqueue(callback);
    }

    /**
     * 请求数据块
     *
     * @param data 接口类型参数
     */
    public void requestHttpData(final ResponseData data) {

        this.sendOkHttpRequest(this.baseUrl() + this.getUrl(), new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isConnected = 0;
                try {
                    data.onResponseData(false, null);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Log.i("HttpUtil", "无法访问");
                //Toast.makeText(this, "连接失败，请重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                isConnected = 1;
                // cookie持久化
                Headers headers = response.headers();
                Log.d("info_headers", "header " + headers);
                List<String> cookies = headers.values("Set-Cookie");
                if (cookies.size() != 0) {
                    String session = cookies.get(0);
                    Log.d("info_cookies", "onResponse-size: " + cookies);
                    s = session.substring(0, session.indexOf(";"));
                    Log.i("info_s", "session is  :" + s);
                }
                // Json字符串
                inputStream = response.body().byteStream();

                FileOutputStream fileOutputStream = null;
                try {
                    String imageName = Common.currentTimeMill();
                    File file = new File(Common.PHOTO_PATH + imageName + "_cloud.jpg");
                    fileOutputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[2048];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.flush();
                } catch (IOException e) {
                    Log.i("wangshu", "IOException");
                    e.printStackTrace();
                }

                ByteHttpUtil responseData = new ByteHttpUtil();
                //Log.i("HttpUtil", "responseString:" + responseString);
                Log.i("HttpUtil", "这儿打印了");
                LogUtil.i("HttpUtil", "LogUtil: responseString:" + inputStream);
                // 将Json字符串转为JSONObject对象
                responseData = handleData(inputStream);
                data.onResponseData(true, responseData.responseObject);
            }
        });
    }

    public String getUrl() {
        return "";
    }

    public String baseUrl() {
        return UrlHeader.BASE_URL_NEW;
    }

    public RequestBody parameter() {
        return null;
    }

    public String parameterGet() {
        return "";
    }

    public ByteHttpUtil handleData(InputStream inputStream) {
        return new ByteHttpUtil();
    }

    public interface ResponseData {
        void onResponseData(boolean isSuccess, Object responseObject) throws IOException;
    }

}
