package com.trackersurvey.http;

import com.trackersurvey.bean.InterestMarkerData;
import com.trackersurvey.util.UrlHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/6/18.
 */

public class DownloadPoiRequest extends HttpUtil {
    private String Token;
    private int    pageIndex;
    private int    pageSize;
    private long   traceID;

    public DownloadPoiRequest(String token, int pageIndex, int pageSize, long traceID) {
        this.Token = token;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.traceID = traceID;
    }

    @Override
    public String getUrl() {
        return UrlHeader.DOWNLOAD_POI_URL;
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("pageIndex", String.valueOf(pageIndex))
                .add("pageSize", String.valueOf(pageSize))
                .add("traceID", String.valueOf(traceID))
                .add("token", Token)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        HttpUtil response = new HttpUtil();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(obj);

            List<InterestMarkerData> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                InterestMarkerData interestMarkerData = new InterestMarkerData(jsonObject);
                list.add(interestMarkerData);
            }
            response.responseObject = list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }
}
