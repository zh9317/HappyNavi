package com.trackersurvey.http;

import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 开始一条轨迹，传入轨迹名、开始时间、运动类型、返回traceID
 */

public class StartTraceRequest extends HttpUtil {

    private String token;
    private String tracename;
    private String starttime;
    private String sporttypes;

    public StartTraceRequest(String token, String tracename, String starttime, String sporttypes) {
        this.token = token;
        this.tracename = tracename;
        this.starttime = starttime;
        this.sporttypes = sporttypes;
    }

    @Override
    public String getUrl() {
        return UrlHeader.UPLOAD_START_TRACE_URL;
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("tracename", tracename)
                .add("starttime", starttime)
                .add("sporttypes", sporttypes)
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
