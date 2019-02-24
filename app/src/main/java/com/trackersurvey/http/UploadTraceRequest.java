package com.trackersurvey.http;

import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class UploadTraceRequest extends HttpUtil {

    private String token;
    private String traceInfo;

    public UploadTraceRequest(String token, String traceInfo) {
        this.token = token;
        this.traceInfo = traceInfo;
    }

    @Override
    public String getUrl() {
        return UrlHeader.UPLOAD_TRACE;
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("traceInfo", traceInfo)
                .build();
        return requestBody;
    }
}
