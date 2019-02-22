package com.trackersurvey.http;

import android.util.Log;

import com.trackersurvey.model.LoginModel;
import com.trackersurvey.model.UserInfoData;
import com.trackersurvey.model.UserInfoModel;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.DESUtil;
import com.trackersurvey.util.HMAC_SHA1_Util;
import com.trackersurvey.util.UrlHeader;

import org.json.JSONObject;

import java.io.File;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/6/24.
 */

public class UploadUserInfoRequest extends HttpUtil {

    private String token;
    private String filePath;
    private String userInfo;

    public UploadUserInfoRequest(String token, String filePath, String userInfo) {
        this.token = token;
        this.filePath = filePath;
        this.userInfo = userInfo;
    }

    @Override
    public String getUrl() {
        return UrlHeader.UPLOAD_USER_INFO_URL;
    }

    @Override
    public RequestBody parameter() {
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File file = new File(filePath);
        Log.i("UpdInfo", "file: " + file);
        builder.addFormDataPart("token",token);
        builder.addFormDataPart("userInfo", userInfo);
        if (!filePath.equals("")) {
            builder.addFormDataPart("file", filePath, RequestBody.create(MEDIA_TYPE_PNG, file));
        }
        RequestBody requestBody = builder.build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        HttpUtil response = new HttpUtil();
        try {
            String result = DESUtil.decrypt(obj);
            Log.i("UserInfoChangeRequest", "decrypt result : " + result);
            JSONObject jsonObject = new JSONObject(result);
            JSONObject userObj = jsonObject.getJSONObject("userInfo");
            UserInfoModel model = new UserInfoModel(userObj);
            response.responseObject = model;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
