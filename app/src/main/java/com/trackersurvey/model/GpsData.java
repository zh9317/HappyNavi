package com.trackersurvey.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zh931 on 2018/5/24.
 * 位置数据类型，相比原来，添加了CityID,DeviceID,SportType
 */

public class GpsData {
    private String userID;
    private String CreateTime;
    private double Altitude;
    private double Latitude;
    private double Longitude;
    private double Speed;
    private long TraceID;
    private int CityID;
    private String DeviceID;
    private int SportType;

    public GpsData(String userID, String createTime, double altitude, double latitude, double longitude,
                   double speed, long traceID) {
        this.userID = userID;
        CreateTime = createTime;
        Altitude = altitude;
        Latitude = latitude;
        Longitude = longitude;
        Speed = speed;
        TraceID = traceID;
    }

    public GpsData(String userID, String createTime, double altitude, double latitude, double longitude,
                   double speed, long traceID, int cityID, String deviceID, int sportType) {
        this.userID = userID;
        CreateTime = createTime;
        Altitude = altitude;
        Latitude = latitude;
        Longitude = longitude;
        Speed = speed;
        TraceID = traceID;
        CityID = cityID;
        DeviceID = deviceID;
        SportType = sportType;
    }

    public GpsData(JSONObject jsonObject) {
        try {
            userID = jsonObject.getString("userid");
            CreateTime = jsonObject.getString("createtime");
            Altitude = jsonObject.getDouble("altitude");
            Latitude = jsonObject.getDouble("latitude");
            Longitude = jsonObject.getDouble("longitude");
            Speed = jsonObject.getDouble("speed");
            TraceID = jsonObject.getLong("traceid");
            CityID = jsonObject.getInt("cityid");
            DeviceID = jsonObject.getString("deviceid");
            SportType = jsonObject.getInt("sporttype");
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
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public double getAltitude() {
        return Altitude;
    }

    public void setAltitude(double altitude) {
        Altitude = altitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getSpeed() {
        return Speed;
    }

    public void setSpeed(double speed) {
        Speed = speed;
    }

    public long getTraceID() {
        return TraceID;
    }

    public void setTraceID(long traceID) {
        TraceID = traceID;
    }

    public int getCityID() {
        return CityID;
    }

    public void setCityID(int cityID) {
        CityID = cityID;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public int getSportType() {
        return SportType;
    }

    public void setSportType(int sportType) {
        SportType = sportType;
    }
}
