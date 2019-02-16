package com.trackersurvey.http;

import com.trackersurvey.util.Common;
import com.trackersurvey.util.HMAC_SHA1_Util;
import com.trackersurvey.util.UrlHeader;

/**
 * Created by zh931 on 2018/6/18.
 */

public class DownloadPoiListRequest extends HttpUtil {

    private String _timestamp;
    private String Token;
    private int pageIndex;
    private int pageSize;
    private String keyWord;

    public DownloadPoiListRequest(String _timestamp, String token, int pageIndex, int pageSize, String keyWord) {
        this._timestamp = _timestamp;
        Token = token;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.keyWord = keyWord;
    }

    @Override
    public String getUrl() {
        return UrlHeader.DOWNLOAD_POI_LIST_URL;
    }

    @Override
    public String parameterGet() {
        String _sign = HMAC_SHA1_Util.genHMAC(Common.secretKey+"_appkey"+Common._appkey
                        + "_timestamp"+_timestamp+"Token"+Token+"pageIndex"+pageIndex+"pageSize"
                        +pageSize+"keyWord"+keyWord+Common.secretKey, Common.secretKey);
        String parameter = "?_appkey="+Common._appkey+"&_timestamp="+_timestamp+"&_sign="+_sign
                +"&Token="+Token+"&pageIndex="+pageIndex+"&pageSize="+pageSize+"&keyWord="+keyWord;
        return parameter;
    }

    @Override
    public HttpUtil handleData(String obj) {
        HttpUtil response = new HttpUtil();
        return super.handleData(obj);
    }
}
