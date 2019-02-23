package com.trackersurvey.http;

import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class JoinGroupRequest extends HttpUtil {

    private String token;
    private int groupID;

    public JoinGroupRequest(String token, int groupID) {
        this.token = token;
        this.groupID = groupID;
    }

    @Override
    public String getUrl() {
        return UrlHeader.JOIN_GROUP;
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody =  new FormBody.Builder()
                .add("token", token)
                .add("groupID", String.valueOf(groupID))
                .build();
        return requestBody;
    }

    @Override
    public void requestHttpData(ResponseData data) {
        super.requestHttpData(data);
    }
}
