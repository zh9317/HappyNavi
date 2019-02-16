package com.trackersurvey.bean;

import java.io.Serializable;

/**
 * Created by zh931 on 2018/5/11.
 * 文件信息
 */

public class FileInfoData implements Serializable{
    private int id;
    private String url;
    private String fileName;
    private int length;
    private int finished;

    public FileInfoData(int id, String url, String fileName, int length, int finished) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.length = length;
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "FileInfo [id=" + id + ", url=" + url + ", fileName=" + fileName
                + ", length=" + length + ", finished=" + finished + "]";
    }
}
