package com.trackersurvey.http;

import com.trackersurvey.util.Common;
import com.trackersurvey.util.HMAC_SHA1_Util;
import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/5/24.
 */

public class UpLoadGpsRequest extends HttpUtil {

    private String _appkey = "app";
    private String _sign = "";
    private String _timestamp = "";
    private String Token = "";
    private String data = ""; // 位置信息

    public UpLoadGpsRequest(String _timestamp, String token, String data) {
        this._timestamp = _timestamp;
        Token = token;
        this.data = data;
    }

    @Override
    public String getUrl() {
        return UrlHeader.UPLOAD_LOCATION_URL;
    }

    @Override
    public RequestBody parameter() {
        _sign = HMAC_SHA1_Util.genHMAC(Common.secretKey+"_appkey"+Common._appkey+"_timestamp"+_timestamp+
                "Token"+Token+"data"+data+Common.secretKey, Common.secretKey);
        RequestBody requestBody = new FormBody.Builder()
                .add("_appkey", _appkey)
                .add("_sign", _sign)
                .add("_timestamp", _timestamp)
                .add("Token", Token)
                .add("data", data)
                .build();
        return requestBody;
    }
}
