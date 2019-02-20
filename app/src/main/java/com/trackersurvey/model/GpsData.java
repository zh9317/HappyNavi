package com.trackersurvey.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zh931 on 2018/5/24.
 * 位置数据类型，相比原来，添加了CityID,DeviceID,SportType
 */

public class GpsData {
    private String userID;
    private String createTime;
    private double altitude;
    private double latitude;
    private double longitude;
    private double speed;
    private long traceID;
    private int cityID;
    private String deviceID;
    private int sportType;

    public GpsData(String userID, String createTime, double altitude, double latitude, double longitude,
                   double speed, long traceID) {
        this.userID = userID;
        this.createTime = createTime;
        this.altitude = altitude;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.traceID = traceID;
    }

    public GpsData(String userID, String createTime, double altitude, double latitude, double longitude,
                   double speed, long traceID, int cityID, String deviceID, int sportType) {
        this.userID = userID;
        this.createTime = createTime;
        this.altitude = altitude;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.traceID = traceID;
        this.cityID = cityID;
        this.deviceID = deviceID;
        this.sportType = sportType;
    }

    public GpsData(JSONObject jsonObject) {
        try {
            userID = jsonObject.getString("UserID");
            createTime = jsonObject.getString("CreateTime");
            altitude = jsonObject.getDouble("Altitude");
            latitude = jsonObject.getDouble("Latitude");
            longitude = jsonObject.getDouble("Longitude");
            speed = jsonObject.getDouble("Speed");
            traceID = jsonObject.getLong("TraceID");
            cityID = jsonObject.getInt("CityID");
            deviceID = jsonObject.getString("DeviceId");
            sportType = jsonObject.getInt("SportType");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public GpsData() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getTraceID() {
        return traceID;
    }

    public void setTraceID(long traceID) {
        this.traceID = traceID;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public int getSportType() {
        return sportType;
    }

    public void setSportType(int sportType) {
        this.sportType = sportType;
    }
}
