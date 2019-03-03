package com.trackersurvey.http;

import com.trackersurvey.util.UrlHeader;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 上传文件
 */
public class UploadFileRequest extends HttpUtil {

    private String token;
    private String poiID;
    private String filePath;
    private String fileNO;

    public UploadFileRequest(String token, String poiID, String filePath, String fileNO) {
        this.token = token;
        this.poiID = poiID;
        this.filePath = filePath;
        this.fileNO = fileNO;
    }

    @Override
    public String getUrl() {
        return UrlHeader.UPLOAD_FILE_URL;
    }

    @Override
    public RequestBody parameter() {
        File file = new File(filePath);
        String fileName = file.getName();
        String fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
        String uploadType = "multipart/form-data";
        if (fileType.equals("jpg")||fileType.equals("png")||fileType.equals("bmp")||fileType.equals("jpeg")
                ||fileType.equals("gif")) {
            uploadType = "image/"+fileType;
        }
        if (fileType.equals("avi")||fileType.equals("mov")||fileType.equals("mp4")||fileType.equals("wmv")
                ||fileType.equals("rmvb")||fileType.equals("3gp")||fileType.equals("mkv")) {
            uploadType = "video/"+fileType;
        }
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", filePath,
                        RequestBody.create(MediaType.parse(uploadType), file))
                .addFormDataPart("token", token)
                .addFormDataPart("poiID", poiID)
                .addFormDataPart("fileNO", fileNO)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        return super.handleData(obj);
    }
}
