package com.trackersurvey.http;

import android.util.Log;

import com.trackersurvey.util.Common;
import com.trackersurvey.util.HMAC_SHA1_Util;
import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/5/28.
 */

public class EndTraceRequest extends HttpUtil {

    private String token = "";
    private String traceinfo = "";

    public EndTraceRequest(String token, String traceinfo) {
        this.token = token;
        this.traceinfo = traceinfo;
    }

    @Override
    public String getUrl() {
        return UrlHeader.UPLOAD_TRACE_UPDATE_URL;
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("traceinfo", traceinfo)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        return super.handleData(obj);
    }
}
