package com.trackersurvey.http;

import com.trackersurvey.util.Common;
import com.trackersurvey.util.HMAC_SHA1_Util;
import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/7/1.
 */

public class DownloadUserInfo extends HttpUtil {

    private String _timestamp;
    private String Token;
    private String Lang;

    public DownloadUserInfo(String _timestamp, String token, String lang) {
        this._timestamp = _timestamp;
        Token = token;
        Lang = lang;
    }

    @Override
    public String getUrl() {
        return UrlHeader.DOWNLOAD_USER_INFO_URL;
    }

    @Override
    public RequestBody parameter() {
        String _sign = HMAC_SHA1_Util.genHMAC(Common.secretKey+"_appkey"+Common._appkey
                +"_timestamp"+_timestamp+"Token"+Token+"Lang"+Lang+Common.secretKey, Common.secretKey);
        RequestBody requestBody = new FormBody.Builder()
                .add("_sign", _sign)
                .add("_appkey", Common._appkey)
                .add("_timestamp", _timestamp)
                .add("Token", Token)
                .add("Lang", Lang)
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
