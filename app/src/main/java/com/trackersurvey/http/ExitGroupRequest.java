package com.trackersurvey.http;

import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ExitGroupRequest extends HttpUtil {

    private String token;
    private int groupID;

    public ExitGroupRequest(String token, int groupID) {
        this.token = token;
        this.groupID = groupID;
    }

    @Override
    public String getUrl() {
        return UrlHeader.EXIT_GROUP_URL;
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
