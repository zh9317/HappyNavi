package com.trackersurvey.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zh931 on 2018/5/25.
 */

public class TraceData {
    private String userID;
    private String TraceName;
    private long TraceID;
    private String StartTime;
    private String EndTime;
    private long Duration;
    private double Distance;
    private int SportTypes;
    private int ShareType;
    private int Calorie;
    private int PoiCount;
    private int Steps;

    public TraceData() {
    }

    public TraceData(String userID, String traceName, long traceID, String startTime, String endTime,
                     long duration, double distance, int sportTypes, int shareType, int calorie, int poiCount,
                     int steps) {
        this.userID = userID;
        TraceName = traceName;
        TraceID = traceID;
        StartTime = startTime;
        EndTime = endTime;
        Duration = duration;
        Distance = distance;
        SportTypes = sportTypes;
        ShareType = shareType;
        Calorie = calorie;
        PoiCount = poiCount;
        Steps = steps;
    }

    public TraceData(JSONObject object) {
        try {
            TraceID = object.getLong("traceid");
            TraceName = object.getString("tracename");
            userID = object.getString("userid");
            StartTime = object.getString("starttime");
            EndTime = object.getString("endtime");
            Duration = object.getLong("duration");
            SportTypes = object.getInt("sporttypes");
            ShareType = object.getInt("sharetype");
            Distance = object.getDouble("distance");
            Calorie = object.getInt("calorie");
            Steps = object.getInt("steps");
            PoiCount = object.getInt("poicount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public long getTraceID() {
        return TraceID;
    }

    public void setTraceID(long traceID) {
        TraceID = traceID;
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

    public int getSportTypes() {
        return SportTypes;
    }

    public void setSportTypes(int sportTypes) {
        SportTypes = sportTypes;
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

    public int getSteps() {
        return Steps;
    }

    public void setSteps(int steps) {
        Steps = steps;
    }
}
