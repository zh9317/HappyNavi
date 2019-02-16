package com.trackersurvey.http;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/6/17.
 */

public class TestForWeb extends HttpUtil {

    private String Token;

    public TestForWeb(String token) {
        Token = token;
    }

    @Override
    public String baseUrl() {
        return "http://211.87.235.147:8089/footPrint/";
    }

    @Override
    public String getUrl() {
        return "questionnaire/test";
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("_appkey", "wx")
                .add("Token", Token)
                .build();
        return requestBody;
    }
}
