package com.trackersurvey.http;

import android.util.Log;

import com.trackersurvey.util.Common;
import com.trackersurvey.util.HMAC_SHA1_Util;
import com.trackersurvey.util.UrlHeader;

import java.io.File;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/6/24.
 */

public class UploadUserInfoRequest extends HttpUtil {

    private String _timestamp;
    private String Token;
    private String NickName;
    private String RealName;
    private String Birthdate;
    private String Sex;
    private String HobbyIDs;
    private String Registeritem2;// 籍贯
    private String Registeritem3;// 住址
    private String Registeritem4;// 教育程度
    private String Registeritem5;// 年收入
    private String Registeritem6;// 职业
    private String Registeritem7;// 是否已婚
    private String Registeritem8;// 子女数
    private String imagePath;

    public UploadUserInfoRequest(String _timestamp, String token, String nickName, String realName,
                                 String birthdate, String sex, String hobbyIDs, String registeritem2,
                                 String registeritem3, String registeritem4, String registeritem5,
                                 String registeritem6, String registeritem7, String registeritem8,
                                 String imagePath) {
        this._timestamp = _timestamp;
        Token = token;
        NickName = nickName;
        RealName = realName;
        Birthdate = birthdate;
        Sex = sex;
        HobbyIDs = hobbyIDs;
        Registeritem2 = registeritem2;
        Registeritem3 = registeritem3;
        Registeritem4 = registeritem4;
        Registeritem5 = registeritem5;
        Registeritem6 = registeritem6;
        Registeritem7 = registeritem7;
        Registeritem8 = registeritem8;
        this.imagePath = imagePath;
    }

    @Override
    public String getUrl() {
        return UrlHeader.UPLOAD_USER_INFO_URL;
    }

    @Override
    public RequestBody parameter() {
        String _sign = HMAC_SHA1_Util.genHMAC(Common.secretKey+"_appkey"+Common._appkey
                +"_timestamp"+_timestamp+"Token"+Token+"NickName"+NickName+"RealName"+RealName
                +"Birthdate"+Birthdate+"Sex"+Sex+"HobbyIDs"+HobbyIDs+"Registeritem2"+Registeritem2
                +"Registeritem3"+Registeritem3+"Registeritem4"+Registeritem4+"Registeritem5"+Registeritem5
                +"Registeritem6"+Registeritem6+"Registeritem7"+Registeritem7+"Registeritem8"+Registeritem8
                +Common.secretKey, Common.secretKey);
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File file = new File(imagePath);
        Log.i("UpdInfo", "file:"+file);
        builder.addFormDataPart("_appkey", Common._appkey);
        builder.addFormDataPart("_sign", _sign);
        builder.addFormDataPart("_timestamp", _timestamp);
        builder.addFormDataPart("Token", Token);
        builder.addFormDataPart("NickName", NickName);
        builder.addFormDataPart("RealName", RealName);
        builder.addFormDataPart("Birthdate", Birthdate);
        builder.addFormDataPart("Sex", Sex);
        builder.addFormDataPart("HobbyIDs", HobbyIDs);
        builder.addFormDataPart("Registeritem2", Registeritem2);
        builder.addFormDataPart("Registeritem3", Registeritem3);
        builder.addFormDataPart("Registeritem4", Registeritem4);
        builder.addFormDataPart("Registeritem5", Registeritem5);
        builder.addFormDataPart("Registeritem6", Registeritem6);
        builder.addFormDataPart("Registeritem7", Registeritem7);
        builder.addFormDataPart("Registeritem8", Registeritem8);
        if (!imagePath.equals("")) {
            builder.addFormDataPart("HeadImg", imagePath, RequestBody.create(MEDIA_TYPE_PNG, file));
        }else {
            builder.addFormDataPart("HeadImg", imagePath);
        }
        RequestBody requestBody = builder.build();
//        RequestBody requestBody = new FormBody.Builder()
//                .add("_appkey", Common._appkey)
//                .add("_sign", _sign)
//                .add("_timestamp", _timestamp)
//                .add("Token", Token)
//                .add("NickName", NickName)
//                .add("RealName", RealName)
//                .add("Birthdate", Birthdate)
//                .add("GenderID", GenderID)
//                .add("HobbyIDs", HobbyIDs)
//                .add("Registeritem2", Registeritem2)
//                .add("Registeritem3", Registeritem3)
//                .add("Registeritem4", Registeritem4)
//                .add("Registeritem5", Registeritem5)
//                .add("Registeritem6", Registeritem6)
//                .add("Registeritem7", Registeritem7)
//                .add("Registeritem8", Registeritem8)
//                .add("HeadImg", imagePath)
//                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        return super.handleData(obj);
    }
}
