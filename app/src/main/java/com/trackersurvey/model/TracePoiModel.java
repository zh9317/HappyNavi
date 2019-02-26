package com.trackersurvey.model;

/**
 * Created by zh931 on 2018/6/2.
 */

public class TracePoiModel {
    private String CreateTime;
    private int UserID;
    private String DeviceID;
    private long TraceID;
    private int PoiNo;
    private String Comment;
    private String Country;
    private String Province;
    private String City;
    private String PlaceName;
    private double Longitude;
    private double Latitude;
    private double Altitude;
    private int MotionType;
    private int ActivityType;
    private int RetentionType;
    private int CompanionType;
    private int RelationType;
    private int StateType;

    private int ImageCount;
    private int VideoCount;
    private int AudioCount;

    public int getImageCount() {
        return ImageCount;
    }

    public void setImageCount(int imageCount) {
        ImageCount = imageCount;
    }

    public int getVideoCount() {
        return VideoCount;
    }

    public void setVideoCount(int videoCount) {
        VideoCount = videoCount;
    }

    public int getAudioCount() {
        return AudioCount;
    }

    public void setAudioCount(int audioCount) {
        AudioCount = audioCount;
    }

    public TracePoiModel() {
    }

    public TracePoiModel(String createTime, int userID, String deviceID, long traceID, int poiNo, String comment, String country, String province, String city, String placeName, double longitude, double latitude, double altitude, int motionType, int activityType, int retentionType, int companionType, int relationType, int stateType, int imageCount, int videoCount, int audioCount) {
        CreateTime = createTime;
        UserID = userID;
        DeviceID = deviceID;
        TraceID = traceID;
        PoiNo = poiNo;
        Comment = comment;
        Country = country;
        Province = province;
        City = city;
        PlaceName = placeName;
        Longitude = longitude;
        Latitude = latitude;
        Altitude = altitude;
        MotionType = motionType;
        ActivityType = activityType;
        RetentionType = retentionType;
        CompanionType = companionType;
        RelationType = relationType;
        StateType = stateType;
        ImageCount = imageCount;
        VideoCount = videoCount;
        AudioCount = audioCount;
    }

    public TracePoiModel(String createTime, int userID, String deviceID, long traceID, int poiNo, String comment,
                         String country, String province, String city, String placeName, double longitude,
                         double latitude, double altitude, int motionType, int activityType, int retentionType,
                         int companionType, int relationType, int stateType) {
        CreateTime = createTime;
        UserID = userID;
        DeviceID = deviceID;
        TraceID = traceID;
        PoiNo = poiNo;
        Comment = comment;
        Country = country;
        Province = province;
        City = city;
        PlaceName = placeName;
        Longitude = longitude;
        Latitude = latitude;
        Altitude = altitude;
        MotionType = motionType;
        ActivityType = activityType;
        RetentionType = retentionType;
        CompanionType = companionType;
        RelationType = relationType;
        StateType = stateType;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public long getTraceID() {
        return TraceID;
    }

    public void setTraceID(long traceID) {
        TraceID = traceID;
    }

    public int getPoiNo() {
        return PoiNo;
    }

    public void setPoiNo(int poiNo) {
        PoiNo = poiNo;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getPlaceName() {
        return PlaceName;
    }

    public void setPlaceName(String placeName) {
        PlaceName = placeName;
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

    public int getMotionType() {
        return MotionType;
    }

    public void setMotionType(int motionType) {
        MotionType = motionType;
    }

    public int getActivityType() {
        return ActivityType;
    }

    public void setActivityType(int activityType) {
        ActivityType = activityType;
    }

    public int getRetentionType() {
        return RetentionType;
    }

    public void setRetentionType(int retentionType) {
        RetentionType = retentionType;
    }

    public int getCompanionType() {
        return CompanionType;
    }

    public void setCompanionType(int companionType) {
        CompanionType = companionType;
    }

    public int getRelationType() {
        return RelationType;
    }

    public void setRelationType(int relationType) {
        RelationType = relationType;
    }

    public int getStateType() {
        return StateType;
    }

    public void setStateType(int stateType) {
        StateType = stateType;
    }
}
