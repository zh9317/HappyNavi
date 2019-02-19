package com.trackersurvey.http;

import com.trackersurvey.model.PoiChoiceModel;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.HMAC_SHA1_Util;
import com.trackersurvey.util.UrlHeader;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/6/2.
 */

public class DownloadPoiChoices extends HttpUtil {

    private String token;

    public DownloadPoiChoices(String token) {
        this.token = token;
    }

    @Override
    public String getUrl() {
        return UrlHeader.DOWNLOAD_POI_CHOICES;
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        HttpUtil response = new HttpUtil();
        try {
            JSONObject jsonObject = new JSONObject(obj);
            PoiChoiceModel poiChoiceModel = new PoiChoiceModel(jsonObject);
            response.responseObject = poiChoiceModel;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }
}
