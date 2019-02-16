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

    private String Token = "";
    private int PageIndex;
    private int PageSize;
    private String keyWord = "";
    private String _appkey = "app";
    private String _sign = "";

    public DownLoadTraceList(String token, int pageIndex, int pageSize, String keyWord) {
        Token = token;
        PageIndex = pageIndex;
        PageSize = pageSize;
        this.keyWord = keyWord;
    }

    @Override
    public String getUrl() {
        return UrlHeader.DOWNLOAD_TRACE_LIST;
    }

    @Override
    public String parameterGet() {
        _sign = HMAC_SHA1_Util.genHMAC("footprint2018"+"Token"+Token+"PageIndex"+PageIndex+"PageSize"+PageSize+
                "keyWord"+keyWord+"_appkey"+_appkey+"footprint2018","footprint2018");
        String parameter = "?Token="+Token+"&PageIndex="+PageIndex+"&PageSize="+PageSize+"&keyWord="+keyWord+
                "&_appkey="+_appkey+"&_sign="+_sign;
        return parameter;
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
