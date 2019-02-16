package com.trackersurvey.bean;

/**
 * Created by zh931 on 2018/5/11.
 */

public class GpsData {
    private String userID;
    private String CreateTime;
    private double Longitude;
    private double Latitude;
    private double Altitude;
    private double Speed;
    private long TraceNo;

    public GpsData() {
    }

    public GpsData(String userID, String createTime, double longitude, double latitude, double altitude, double speed, long traceNo) {
        this.userID = userID;
        CreateTime = createTime;
        Longitude = longitude;
        Latitude = latitude;
        Altitude = altitude;
        Speed = speed;
        TraceNo = traceNo;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getAltitude() {
        return Altitude;
    }

    public void setAltitude(double altitude) {
        Altitude = altitude;
    }

    public double getSpeed() {
        return Speed;
    }

    public void setSpeed(double speed) {
        Speed = speed;
    }

    public long getTraceNo() {
        return TraceNo;
    }

    public void setTraceNo(long traceNo) {
        TraceNo = traceNo;
    }
}
