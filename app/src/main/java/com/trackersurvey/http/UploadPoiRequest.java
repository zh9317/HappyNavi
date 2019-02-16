package com.trackersurvey.http;

import android.util.Log;

import com.trackersurvey.model.TracePoiModel;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.HMAC_SHA1_Util;
import com.trackersurvey.util.UrlHeader;

import java.io.File;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/6/2.
 */

public class UploadPoiRequest extends HttpUtil {

    private String _timestamp;
    private String Token;
    private String Share;
    private String TracePoi;
    private String File;
    private List<String> picList;
    private int fileType;

    public UploadPoiRequest(String _timestamp, String token, String share, String tracePoi, String file) {
        this._timestamp = _timestamp;
        Token = token;
        Share = share;
        TracePoi = tracePoi;
        File = file;
    }

    public UploadPoiRequest(String _timestamp, String token, String share, String tracePoi, String file,
                            List<String> picList, int fileType) {
        this._timestamp = _timestamp;
        Token = token;
        Share = share;
        TracePoi = tracePoi;
        File = file;
        this.picList = picList;
        this.fileType = fileType;
    }

    @Override
    public String getUrl() {
        return UrlHeader.UPLOAD_POI_URL;
    }

    @Override
    public RequestBody parameter() {
        String _sign = HMAC_SHA1_Util.genHMAC(Common.secretKey+"_appkey"+Common._appkey
                +"_timestamp"+_timestamp+"Token"+Token+"Share"+Share+"TracePoi"+TracePoi+Common.secretKey,
                Common.secretKey);
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File file = new File(File);
        builder.addFormDataPart("_appkey", Common._appkey);
        builder.addFormDataPart("_sign", _sign);
        builder.addFormDataPart("_timestamp", _timestamp);
        builder.addFormDataPart("Token", Token);
        builder.addFormDataPart("Share", Share);
        builder.addFormDataPart("TracePoi", TracePoi);
        if (!File.equals("")) {
            if (fileType == 1) {
                for (int i = 0; i < picList.size(); i++) {
                    builder.addFormDataPart("file" + i, picList.get(i), RequestBody.create(MEDIA_TYPE_PNG, file));
                }
            }
        }else {
            Log.i("UploadPoi", "这里执行了");
            builder.addFormDataPart("File", File);
        }
        RequestBody requestBody = builder.build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        return super.handleData(obj);
    }
}
