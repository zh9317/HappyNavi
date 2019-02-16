package com.trackersurvey.bean;

/**
 * Created by zh931 on 2018/5/11.
 */

public class TraceData {
    private String userID;
    private String TraceName;
    private long TraceNo;
    private String StartTime;
    private String EndTime;
    private long Duration;
    private double Distance;
    private int SportType;
    private int ShareType;
    private int Calorie;
    private int PoiCount;

    public TraceData() {
    }

    public TraceData(String userID, String traceName, long traceNo, String startTime, String endTime, long duration, double distance, int sportType, int shareType, int calorie, int poiCount) {
        this.userID = userID;
        TraceName = traceName;
        TraceNo = traceNo;
        StartTime = startTime;
        EndTime = endTime;
        Duration = duration;
        Distance = distance;
        SportType = sportType;
        ShareType = shareType;
        Calorie = calorie;
        PoiCount = poiCount;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTraceName() {
        return TraceName;
    }

    public void setTraceName(String traceName) {
        TraceName = traceName;
    }

    public long getTraceNo() {
        return TraceNo;
    }

    public void setTraceNo(long traceNo) {
        TraceNo = traceNo;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public long getDuration() {
        return Duration;
    }

    public void setDuration(long duration) {
        Duration = duration;
    }

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }

    public int getSportType() {
        return SportType;
    }

    public void setSportType(int sportType) {
        SportType = sportType;
    }

    public int getShareType() {
        return ShareType;
    }

    public void setShareType(int shareType) {
        ShareType = shareType;
    }

    public int getCalorie() {
        return Calorie;
    }

    public void setCalorie(int calorie) {
        Calorie = calorie;
    }

    public int getPoiCount() {
        return PoiCount;
    }

    public void setPoiCount(int poiCount) {
        PoiCount = poiCount;
    }
}
