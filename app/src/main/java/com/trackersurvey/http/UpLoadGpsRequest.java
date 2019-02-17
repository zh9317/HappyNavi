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

    private String token = "";
    private String locations = ""; // 位置信息

    public UpLoadGpsRequest(String token, String locations) {
        this.token = token;
        this.locations = locations;
    }

    @Override
    public String getUrl() {
        return UrlHeader.UPLOAD_LOCATION_URL;
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("locations", locations)
                .build();
        return requestBody;
    }
}
