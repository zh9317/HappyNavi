package com.trackersurvey.bean;

/**
 * Created by zh931 on 2018/5/12.
 */

public class HealthData {
    private String UserID;
    private String YearMonth;//月份 格式：201604
    private String[] Day;//一个月中每天的步数统计
    private String Total;//当月总步数

    public HealthData() {
    }

    public HealthData(String userID, String yearMonth, String[] day, String totalStep) {
        this.UserID = userID;
        this.YearMonth = yearMonth;
        this.Day = new String[31];
        for(int i = 0; i<day.length; i++){
            this.Day[i] = day[i];
        }
        this.Total = totalStep;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        this.UserID = userID;
    }

    public String getYearMonth() {
        return YearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.YearMonth = yearMonth;
    }

    public String[] getDay() {
        return Day;
    }

    public void setDay(String[] day) {
        for(int i = 0; i<day.length; i++){
            this.Day[i] = day[i];
        }
    }

    public String getTotalStep() {
        return Total;
    }

    public void setTotalStep(String totalStep) {
        this.Total = totalStep;
    }
}
