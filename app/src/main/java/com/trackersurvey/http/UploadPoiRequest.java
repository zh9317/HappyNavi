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

    private String token;
    private String tracePoi;
    private String File;
    private List<String> picList;
    private int fileType;

    public UploadPoiRequest(String token, String tracePoi, String file) {
        this.token = token;
        this.tracePoi = tracePoi;
        File = file;
    }

    public UploadPoiRequest(String token, String tracePoi, String file, List<String> picList, int fileType) {
        this.token = token;
        this.tracePoi = tracePoi;
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
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File file = new File(File);
        builder.addFormDataPart("token", token);
        builder.addFormDataPart("tracePoi", tracePoi);
        if (!File.equals("")) { // 有文件
            if (fileType == 1) {
                for (int i = 0; i < picList.size(); i++) {
                    builder.addFormDataPart("file" + i, picList.get(i), RequestBody.create(MEDIA_TYPE_PNG, file));
                }
            }
        }else { // 没有文件
            Log.i("UploadPoi", "这里执行了，没有上传文件");
//            builder.addFormDataPart("file", File);
        }
        RequestBody requestBody = builder.build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        return super.handleData(obj);
    }
}
