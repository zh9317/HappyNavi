package com.trackersurvey.http;


import com.trackersurvey.util.UrlHeader;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/6/2.
 */

public class UploadPoiRequest extends HttpUtil {

    private String token;
    private String tracePoi;

    public UploadPoiRequest(String token, String tracePoi) {
        this.token = token;
        this.tracePoi = tracePoi;
    }

    @Override
    public String getUrl() {
        return UrlHeader.UPLOAD_POI_URL;
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("tracePoi", tracePoi)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        HttpUtil response = new HttpUtil();
        try {
            JSONObject jsonObject = new JSONObject(obj);
            response.responseObject = jsonObject.getInt("poiID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }
}
