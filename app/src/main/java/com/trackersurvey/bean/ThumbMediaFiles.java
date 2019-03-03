package com.trackersurvey.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class ThumbMediaFiles {

    private String fileName;
    private String fileNo;
    private String fileBase64;
    private String fileType;

    public ThumbMediaFiles(String fileName, String fileNo, String fileBase64, String fileType) {
        this.fileName = fileName;
        fileNo = fileNo;
        fileBase64 = fileBase64;
        fileType = fileType;
    }

    public ThumbMediaFiles(JSONObject jsonObject) {
        try {
            fileName = jsonObject.getString("fileName");
            fileNo = jsonObject.getString("fileNo");
            fileBase64 = jsonObject.getString("fileBase64");
            fileType = jsonObject.getString("fileType");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileNo;
    }

    public void setFileId(String fileNo) {
        fileNo = fileNo;
    }

    public String getFileBase64() {
        return fileBase64;
    }

    public void setFileBase64(String fileBase64) {
        this.fileBase64 = fileBase64;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        fileType = fileType;
    }
}
