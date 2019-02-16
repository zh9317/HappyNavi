package com.trackersurvey.bean;

/**
 * Created by zh931 on 2018/5/12.
 */

public class TraceRoughData {
    private String userID;
    private String traceName;
    private long traceNo;
    private String startTime;
    private String endTime;
    private long duration;
    private double distance;
    private int sportType;
    private int shareType;
    private int calorie;

    public TraceRoughData() {
    }

    public TraceRoughData(String userID, String traceName, long traceNo, String startTime,
                          String endTime, long duration, double distance, int sportType,
                          int shareType, int calorie) {
        this.userID = userID;
        this.traceName = traceName;
        this.traceNo = traceNo;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.distance = distance;
        this.sportType = sportType;
        this.shareType = shareType;
        this.calorie = calorie;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTraceName() {
        return traceName;
    }

    public void setTraceName(String traceName) {
        this.traceName = traceName;
    }

    public long getTraceNo() {
        return traceNo;
    }

    public void setTraceNo(long traceNo) {
        this.traceNo = traceNo;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getSportType() {
        return sportType;
    }

    public void setSportType(int sportType) {
        this.sportType = sportType;
    }

    public int getShareType() {
        return shareType;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }
}
