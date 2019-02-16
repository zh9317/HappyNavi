package com.trackersurvey.http;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/5/21.
 */

public class TestRequest extends HttpUtil {

    @Override
    public String baseUrl() {
        return "";
    }

    @Override
    public String getUrl() {
        return "http://211.87.235.76:8080/footPrint/test";
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("_appkey", "web")
                .build();
        return requestBody;
    }

}
