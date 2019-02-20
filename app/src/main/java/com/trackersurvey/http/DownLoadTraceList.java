package com.trackersurvey.http;

import com.trackersurvey.model.TraceData;
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
 * Created by zh931 on 2018/5/28.
 */

public class DownLoadTraceList extends HttpUtil {

    private String token;
    private String pageIndex;
    private String pageSize;

    public DownLoadTraceList(String token, String pageIndex, String pageSize) {
        this.token = token;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    @Override
    public String getUrl() {
        return UrlHeader.DOWNLOAD_TRACE_LIST;
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("pageIndex", pageIndex)
                .add("pageSize", pageSize)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        HttpUtil response = new HttpUtil();
        try {
            JSONArray jsonArray = new JSONArray(obj);
            List<TraceData> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                TraceData traceData = new TraceData(jsonObject);
                list.add(traceData);
            }
            response.responseObject = list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }
}
