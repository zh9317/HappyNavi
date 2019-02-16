package com.trackersurvey.http;

import com.trackersurvey.util.Common;
import com.trackersurvey.util.HMAC_SHA1_Util;
import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/5/10.
 */

public class RegisterRequest extends HttpUtil {
    private String loginName = "";
    private String password = "";
    private String code = "";

    public RegisterRequest(String loginName, String password, String code) {
        this.loginName = loginName;
        this.password = password;
        this.code = code;
    }

    @Override
    public String getUrl() {
        return UrlHeader.REGISTER_URL;
    }

    @Override
    public RequestBody parameter() {
//        String signature = HMAC_SHA1_Util.genHMAC(Common.secretKey+"_appkey"+Common._appkey
//                +"_timestamp"+_timestamp +"LoginName"+LoginName+"Password"+Password +Common.secretKey,
//                Common.secretKey);
        RequestBody requestBody = new FormBody.Builder()
                .add("loginName", loginName)
                .add("password", password)
                .add("code", code)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        HttpUtil response = new HttpUtil();
        response.responseObject = obj;
        return response;
    }
}
