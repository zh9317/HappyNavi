package com.trackersurvey.http;

import com.trackersurvey.util.Common;
import com.trackersurvey.util.HMAC_SHA1_Util;
import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/6/1.
 */

public class DeleteTraceRequest extends HttpUtil {

    private String token;
    private String traceID;

    public DeleteTraceRequest(String token, String traceID) {
        this.token = token;
        this.traceID = traceID;
    }

    @Override
    public String getUrl() {
        return UrlHeader.DELETE_TRACE;
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("traceID", traceID)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        return super.handleData(obj);
    }
}
