package com.trackersurvey.model;

/**
 * Created by zh931 on 2018/5/25.
 */

public class StepData {
    private String userID;
    private long TraceID;
    private int Steps;

    public StepData(){
        this.Steps=0;
    }

    public StepData(String userID, long traceID, int steps) {
        this.userID = userID;
        TraceID = traceID;
        Steps = steps;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getTraceID() {
        return TraceID;
    }

    public void setTraceID(long traceID) {
        TraceID = traceID;
    }

    public int getSteps() {
        return Steps;
    }

    public void setSteps(int steps) {
        Steps = steps;
    }
}
