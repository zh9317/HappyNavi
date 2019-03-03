package com.trackersurvey.http;

import com.trackersurvey.bean.InterestMarkerData;
import com.trackersurvey.bean.ThumbMediaFiles;
import com.trackersurvey.util.UrlHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class DownloadThumbMediaFiles extends HttpUtil {

    private String token;
    private int poiID;

    public DownloadThumbMediaFiles(String token, int poiID) {
        this.token = token;
        this.poiID = poiID;
    }

    @Override
    public String getUrl() {
        return UrlHeader.DOWNLOAD_THUMB_FILE;
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("poiID", String.valueOf(poiID))
                .add("token", token)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        HttpUtil response = new HttpUtil();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(obj);
            LinkedList<ThumbMediaFiles> list = new LinkedList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ThumbMediaFiles thumbMediaFiles = new ThumbMediaFiles(jsonObject);
                list.add(thumbMediaFiles);
            }
            response.responseObject = list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }
}
