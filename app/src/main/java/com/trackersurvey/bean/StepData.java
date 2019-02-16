package com.trackersurvey.bean;

/**
 * Created by zh931 on 2018/5/11.
 */

public class StepData {
    private String userID;
    private long TraceNo;
    private int Steps;

    public StepData(){
        this.Steps=0;
    }

    public StepData(String userID, long traceNo, int steps) {
        this.userID = userID;
        TraceNo = traceNo;
        Steps = steps;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getTraceNo() {
        return TraceNo;
    }

    public void setTraceNo(long traceNo) {
        TraceNo = traceNo;
    }

    public int getSteps() {
        return Steps;
    }

    public void setSteps(int steps) {
        Steps = steps;
    }
}
