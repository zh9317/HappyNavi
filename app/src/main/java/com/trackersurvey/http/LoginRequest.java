package com.trackersurvey.http;

import android.util.Log;

import com.trackersurvey.model.LoginModel;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.DESUtil;
import com.trackersurvey.util.HMAC_SHA1_Util;
import com.trackersurvey.util.UrlHeader;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/5/9.
 */

public class LoginRequest extends HttpUtil {

//    private String userID = "";
    private String loginName = "";
    private String password = "";

//    public LoginRequest(String userID, String password, String deviceId) {
//        this.userID = userID;
//        this.password = password;
//        this.deviceId = deviceId;
//    }

    public LoginRequest(String loginName, String password) {
        this.loginName = loginName;
        this.password = password;
    }

    @Override
    public String getUrl() {
        return UrlHeader.LOGIN_URL_NEW;
    }

//    @Override
//    public RequestBody parameter() {
//        RequestBody requestBody = new FormBody.Builder()
//                .add("userID", userID)
//                .add("password", password)
//                .add("deviceId", deviceId)
//                .build();
//        return requestBody;
//    }


    @Override
    public RequestBody parameter() {
        Log.i("HttpUtil", "loginName"+loginName+"password"+password);
        RequestBody requestBody = new FormBody.Builder()
                .add("loginName", loginName)
                .add("password", password)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        HttpUtil response = new HttpUtil();
        try {
            String result = DESUtil.decrypt(obj);
            Log.i("LoginRequest", "decrypt result : " + result);
            JSONObject jsonObject = new JSONObject(result);
            LoginModel model = new LoginModel(jsonObject);
            response.responseObject = model;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
