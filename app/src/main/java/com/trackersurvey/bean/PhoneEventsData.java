package com.trackersurvey.bean;

/**
 * Created by zh931 on 2018/5/11.
 */

public class PhoneEventsData {
    private String userID;
    private String CreateTime;
    private int EventType;
    private double Longitude;
    private double Latitude;
    private double Altitude;

    public PhoneEventsData() {
    }

    public PhoneEventsData(String userID, String createTime, int eventType, double longitude, double latitude, double altitude) {
        this.userID = userID;
        CreateTime = createTime;
        EventType = eventType;
        Longitude = longitude;
        Latitude = latitude;
        Altitude = altitude;
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

    public int getEventType() {
        return EventType;
    }

    public void setEventType(int eventType) {
        EventType = eventType;
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
}
