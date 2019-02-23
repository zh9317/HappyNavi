package com.trackersurvey.http;

import com.trackersurvey.bean.GroupInfoData;
import com.trackersurvey.util.UrlHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class DownloadUserGroupList extends HttpUtil {

    private String token;
    private String pageIndex;
    private String pageSize;

    public DownloadUserGroupList(String token, String pageIndex, String pageSize) {
        this.token = token;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    @Override
    public String getUrl() {
        return UrlHeader.DOWNLOAD_USER_GROUP_LIST;
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
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(obj);
            List<GroupInfoData> groupInfoDataList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                GroupInfoData groupInfoData = new GroupInfoData(jsonObject);
                groupInfoDataList.add(groupInfoData);
            }
            response.responseObject = groupInfoDataList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }
}
