package com.trackersurvey.http;

import android.util.Log;

import com.trackersurvey.util.Common;
import com.trackersurvey.util.HMAC_SHA1_Util;
import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/5/25.
 */

public class UploadTraceRequest extends HttpUtil {

    private String _appkey = "app";
    private String _sign = "";
    private String _timestamp = "";
    private String Token = "";
    private String DeviceID;
    private String TraceInfo = "";

    public UploadTraceRequest(String _timestamp, String token, String deviceID, String traceInfo) {
        this._timestamp = _timestamp;
        Token = token;
        DeviceID = deviceID;
        TraceInfo = traceInfo;
    }

    @Override
    public String getUrl() {
        return UrlHeader.UPLOAD_TRACE_URL;
    }

    @Override
    public RequestBody parameter() {
        _sign = HMAC_SHA1_Util.genHMAC(Common.secretKey+"_appkey"+Common._appkey+"_timestamp"+_timestamp+"Token"+Token
                +"DeviceID"+DeviceID+"TraceInfo"+TraceInfo+Common.secretKey, Common.secretKey);
        Log.i("LOGDEMO", "开始记录footprint2018"+"_appkey"+_appkey+"_timestamp"+_timestamp+"Token"+Token
                +"DeviceID"+DeviceID+"TraceInfo"+TraceInfo+"footprint2018");
        RequestBody requestBody = new FormBody.Builder()
                .add("_appkey", _appkey)
                .add("_sign", _sign)
                .add("_timestamp", _timestamp)
                .add("Token", Token)
                .add("DeviceID", DeviceID)
                .add("TraceInfo", TraceInfo)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        HttpUtil response = new HttpUtil();
        response.responseObject = obj;
        Log.i("LogDemo", "Data::::::::::::::"+obj);
        return response;
    }
}
