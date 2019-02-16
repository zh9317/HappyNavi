package com.trackersurvey.http;

import com.trackersurvey.util.Common;
import com.trackersurvey.util.HMAC_SHA1_Util;
import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/7/1.
 */

public class GetMsgCodeRequest extends HttpUtil {

    private String mobilePhone;

    public GetMsgCodeRequest(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    @Override
    public String getUrl() {
        return UrlHeader.GET_MSG_CODE_URL;
    }

    @Override
    public RequestBody parameter() {
//        String _sign = HMAC_SHA1_Util.genHMAC(Common.secretKey+"_appkey"+Common._appkey
//                +"_timestamp"+_timestamp+"MobilePhone"+MobilePhone+Common.secretKey, Common.secretKey);
        RequestBody requestBody = new FormBody.Builder()
                .add("mobilePhone", mobilePhone)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        return super.handleData(obj);
    }
}
