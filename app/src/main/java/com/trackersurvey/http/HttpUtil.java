package com.trackersurvey.http;

import android.util.Log;

import com.trackersurvey.happynavi.LoginActivity;
import com.trackersurvey.util.LogUtil;
import com.trackersurvey.util.UrlHeader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

/**
 * Created by zh931 on 2018/5/9.
 */

public class HttpUtil {
    private static OkHttpClient client;
    public Object responseObject; // 请求返回Data字段值
    public String message;
    public String code;
    public String responseString;
    public static String s;
    public static int isConnected;
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
        }else {
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
                    .addHeader("Cookie", "Token:"+LoginActivity.token)
                    .url(address)
                    .post(requestBody)
                    .build();
            Log.i("Cookie", "加入Cookie了");
            Log.i("Cookie", " s!=null 加入Cookie" + "Token:"+LoginActivity.token);
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
                    data.onResponseData(false, null, null, "无法连接");
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
                responseString = response.body().string();
                //
                HttpUtil responseData = new HttpUtil();
                //Log.i("HttpUtil", "responseString:" + responseString);
                Log.i("HttpUtil", "这儿打印了");
                LogUtil.i("HttpUtil", "LogUtil: responseString:" + responseString);
                // 将Json字符串转为JSONObject对象
                JSONObject jsonobj = null;
                try {
                    if (responseString == null || responseString.equals("")) {
                        data.onResponseData(false, null, null, "无返回值");
                        return;
                    }
                    jsonobj = new JSONObject(responseString);
                    if (jsonobj.getString("code").equals("0")) {
                        Log.i("HttpUtil", "code=0");
                        // handleData()返回HttpUtil类的对象responseData
                        if (jsonobj.has("data")) {
                            responseData = handleData(jsonobj.getString("data"));
                        } else {
                            data.onResponseData(true, jsonobj.getString("code"), responseData.responseObject,
                                    jsonobj.getString("msg"));
                            return;
                        }
                        if (responseData.responseObject != null) {
                            // Log.i("HttpUtil", responseString);
                            // responseData调成员变量responseObject
                            data.onResponseData(true, jsonobj.getString("code"), responseData.responseObject,
                                    jsonobj.getString("msg"));
                            return;
                        } else {
                            data.onResponseData(true, jsonobj.getString("code"), responseData.responseObject,
                                    jsonobj.getString("msg"));
                            return;
                        }
                    } else {
                        Log.i("HttpUtil", "code!=0");
                        data.onResponseData(true, jsonobj.getString("code"), responseData.responseObject,
                                jsonobj.getString("msg"));
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
    public HttpUtil handleData(String obj) {
        return new HttpUtil();
    }
}
