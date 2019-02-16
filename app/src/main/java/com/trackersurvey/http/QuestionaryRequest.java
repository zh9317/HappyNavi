package com.trackersurvey.http;

import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/6/13.
 */

public class QuestionaryRequest extends HttpUtilForWebView {

    private String _appkey = "wx";
    private String Token;

    public QuestionaryRequest(String token) {
        Token = token;
    }

    @Override
    public String getUrl() {
        return UrlHeader.QUESTIONARY_URL;
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("_appkey", _appkey)
                .add("Token", Token)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtilForWebView handleData(String obj) {
        HttpUtilForWebView response = new HttpUtilForWebView();
        response.responseObject = obj;
        return response;
    }
}
