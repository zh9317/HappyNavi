package com.trackersurvey.http;

import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class DeletePOIRequest extends HttpUtil {

    private String Token;
    private int poiID;

    public DeletePOIRequest(String token, int poiID) {
        Token = token;
        this.poiID = poiID;
    }

    @Override
    public String getUrl() {
        return UrlHeader.DELETE_POI_URL;
    }

    @Override
    public RequestBody parameter() {
        RequestBody responseBody = new FormBody.Builder()
                .add("token", Token)
                .add("poiID", String.valueOf(poiID))
                .build();
        return responseBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        return super.handleData(obj);
    }
}
