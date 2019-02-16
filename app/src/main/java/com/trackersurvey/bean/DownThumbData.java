package com.trackersurvey.bean;

/**
 * Created by zh931 on 2018/5/12.
 * 解析下载评论缩略图json的对应数据类
 */

public class DownThumbData {
    private String FileId;
    private String PicByte;
    private String FileType;

    public String getFileId() {
        return FileId;
    }

    public void setFileId(String fileId) {
        FileId = fileId;
    }

    public String getPicByte() {
        return PicByte;
    }

    public void setPicByte(String picByte) {
        PicByte = picByte;
    }

    public String getFileType() {
        return FileType;
    }

    public void setFileType(String fileType) {
        FileType = fileType;
    }
}
