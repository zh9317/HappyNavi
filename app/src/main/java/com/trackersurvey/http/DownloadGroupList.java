package com.trackersurvey.http;

import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class DownloadGroupList extends HttpUtil {

    private String token;
    private String pageIndex;
    private String pageSize;
    private String keyWord; // 可以按群组名或者描述模糊查询，如果传的是空字符串则返回所有群组

    public DownloadGroupList(String token, String pageIndex, String pageSize, String keyWord) {
        this.token = token;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.keyWord = keyWord;
    }

    @Override
    public String getUrl() {
        return UrlHeader.DOWNLOAD_GROUP_LIST;
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("pageIndex", pageIndex)
                .add("pageSize", pageSize)
                .add("keyWord", keyWord)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        HttpUtil response = new HttpUtil();
        return response;
    }
}
