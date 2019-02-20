package com.trackersurvey.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zh931 on 2018/5/25.
 */

public class TraceData {
    private String userID;
    private String traceName;
    private long traceID;
    private String startTime;
    private String endTime;
    private long duration;
    private double distance;
    private int sportTypes;
    private int shareType;
    private int calorie;
    private int poiCount;
    private int steps;
    // isDelete, deviceID, deleteTime, coverPicUrl

    public TraceData() {
    }

    public TraceData(String userID, String traceName, long traceID, String startTime, String endTime,
                     long duration, double distance, int sportTypes, int shareType, int calorie, int poiCount,
                     int steps) {
        this.userID = userID;
        this.traceName = traceName;
        this.traceID = traceID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.distance = distance;
        this.sportTypes = sportTypes;
        this.shareType = shareType;
        this.calorie = calorie;
        this.poiCount = poiCount;
        this.steps = steps;
    }

    public TraceData(JSONObject object) {
        try {
            traceID = object.getLong("TraceID");
            traceName = object.getString("TraceName");
            userID = object.getString("UserID");
            startTime = object.getString("StartTime");
            endTime = object.getString("EndTime");
            duration = object.getLong("Duration");
            sportTypes = object.getInt("SportTypes");
            shareType = object.getInt("ShareType");
            distance = object.getDouble("Distance");
            calorie = object.getInt("Calorie");
            steps = object.getInt("Steps");
            poiCount = object.getInt("PoiCount");
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
        return traceName;
    }

    public void setTraceName(String traceName) {
        this.traceName = traceName;
    }

    public long getTraceID() {
        return traceID;
    }

    public void setTraceID(long traceID) {
        this.traceID = traceID;
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

    public int getSportTypes() {
        return sportTypes;
    }

    public void setSportTypes(int sportTypes) {
        this.sportTypes = sportTypes;
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

    public int getPoiCount() {
        return poiCount;
    }

    public void setPoiCount(int poiCount) {
        this.poiCount = poiCount;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
