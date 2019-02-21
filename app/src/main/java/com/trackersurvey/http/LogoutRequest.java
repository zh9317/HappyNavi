package com.trackersurvey.http;

import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class LogoutRequest extends HttpUtil {

    private String token;

    public LogoutRequest(String token) {
        this.token = token;
    }

    @Override
    public String getUrl() {
        return UrlHeader.LOGOUT_URL;
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        return super.handleData(obj);
    }
}
