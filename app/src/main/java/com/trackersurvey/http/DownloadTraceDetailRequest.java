package com.trackersurvey.http;

import com.trackersurvey.model.GpsData;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.HMAC_SHA1_Util;
import com.trackersurvey.util.UrlHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/6/1.
 */

public class DownloadTraceDetailRequest extends HttpUtil {

    private String token;
    private String traceID;

    public DownloadTraceDetailRequest(String token, String traceID) {
        this.token = token;
        this.traceID = traceID;
    }

    @Override
    public String getUrl() {
        return UrlHeader.DOWNLOAD_TRACE_DETAIL;
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
        HttpUtil response = new HttpUtil();
        try {
            JSONArray jsonArray = new JSONArray(obj);
            List<GpsData> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                GpsData gpsData = new GpsData(jsonObject);
                list.add(gpsData);
            }
            response.responseObject = list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }
}
