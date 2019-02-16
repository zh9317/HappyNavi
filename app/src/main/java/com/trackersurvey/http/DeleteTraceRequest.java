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

    private String _timestamp;
    private String Token;
    private String TraceID;
    private String IsDelPoi;

    public DeleteTraceRequest(String _timestamp, String token, String traceID, String isDelPoi) {
        this._timestamp = _timestamp;
        Token = token;
        TraceID = traceID;
        IsDelPoi = isDelPoi;
    }

    @Override
    public String getUrl() {
        return UrlHeader.DELETE_TRACE;
    }

    @Override
    public RequestBody parameter() {
        String _sign = HMAC_SHA1_Util.genHMAC(Common.secretKey+"_appkey"+Common._appkey
                +"_timestamp"+_timestamp+"Token"+Token +"TraceID"+TraceID+"IsDelPoi"+IsDelPoi
                +Common.secretKey, Common.secretKey);
        RequestBody requestBody = new FormBody.Builder()
                .add("_appkey", Common._appkey)
                .add("_sign", _sign)
                .add("_timestamp", _timestamp)
                .add("Token", Token)
                .add("TraceID", TraceID)
                .add("IsDelPoi", String.valueOf(IsDelPoi))
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        return super.handleData(obj);
    }
}
