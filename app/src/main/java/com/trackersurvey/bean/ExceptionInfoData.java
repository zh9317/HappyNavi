package com.trackersurvey.bean;

/**
 * Created by zh931 on 2018/5/12.
 */

public class ExceptionInfoData {
    public String userID;
    public String CreateTime;
    public String VersionName;
    public String DeviceName;
    public String ExceptionInfo;

    public ExceptionInfoData(String userID, String createTime, String versionName, String deviceName, String exceptionInfo) {
        this.userID = userID;
        CreateTime = createTime;
        VersionName = versionName;
        DeviceName = deviceName;
        ExceptionInfo = exceptionInfo;
    }
}
