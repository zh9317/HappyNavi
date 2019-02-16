package com.trackersurvey.bean;

/**
 * Created by zh931 on 2018/5/11.
 */

public class TimeValueData {
    private int UploadTime;
    private int RecTime;
    private int NoRecTime;
    private String LastTime;

    public TimeValueData() {
    }

    public TimeValueData(int uploadTime, int recTime, int noRecTime, String lastTime) {
        UploadTime = uploadTime;
        RecTime = recTime;
        NoRecTime = noRecTime;
        LastTime = lastTime;
    }

    public int getUploadTime() {
        return UploadTime;
    }

    public void setUploadTime(int uploadTime) {
        UploadTime = uploadTime;
    }

    public int getRecTime() {
        return RecTime;
    }

    public void setRecTime(int recTime) {
        RecTime = recTime;
    }

    public int getNoRecTime() {
        return NoRecTime;
    }

    public void setNoRecTime(int noRecTime) {
        NoRecTime = noRecTime;
    }

    public String getLastTime() {
        return LastTime;
    }

    public void setLastTime(String lastTime) {
        LastTime = lastTime;
    }
}
