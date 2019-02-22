package com.trackersurvey.bean;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zh931 on 2018/5/11.
 */

public class InterestMarkerData {

    private int    PoiID;
    private String UserId;  //用户ID
    private long   traceID;
    private int    PoiNo; // 兴趣点编号
    private String Cmt;  //评论文字
    private String AnalyseWords;
    private String Country; // 国家
    private String Province; // 省
    private String City; // 城市
    private String PlaceName; //地点名称
    private double Longitude;  //经度
    private double Latitude;  //纬度
    private double Altitude;  //海拔

    private int MotionType;    //心情
    private int ActivityType;    //活动类型

    private int    RetentionType;
    private int    CompanionType;
    private int    RelationType;
    private int    ImageCount;
    private int    VideoCount;
    private int    AudioCount;
    private int    StateType; // 兴趣点状态？？？
    private int    DeviceID;
    private String CreateTime;    //创建时间


    public InterestMarkerData() {
    }

    public InterestMarkerData(int poiID, String userId, long traceID, int poiNo, String cmt, String analyseWords, String country, String province, String city, String placeName, double longitude, double latitude, double altitude, int motionType, int activityType, int retentionType, int companionType, int relationType, int imageCount, int videoCount, int audioCount, int stateType, int deviceID, String createTime) {
        PoiID = poiID;
        UserId = userId;
        this.traceID = traceID;
        PoiNo = poiNo;
        Cmt = cmt;
        AnalyseWords = analyseWords;
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
        ImageCount = imageCount;
        VideoCount = videoCount;
        AudioCount = audioCount;
        StateType = stateType;
        DeviceID = deviceID;
        CreateTime = createTime;
    }

    public InterestMarkerData(JSONObject jsonObject) {
        try {

            PoiID = jsonObject.getInt("PoiID");
            UserId = jsonObject.getString("UserID");
            PoiNo = jsonObject.getInt("PoiNo");
            traceID = jsonObject.getLong("TraceID");
            Cmt = jsonObject.getString("Comment");
            //            AnalyseWords = jsonObject.getString("AnalyseWords");
            Country = jsonObject.getString("Country");
            Province = jsonObject.getString("Province");
            City = jsonObject.getString("City");
            PlaceName = jsonObject.getString("PlaceName");
            Longitude = jsonObject.getDouble("Longitude");
            Latitude = jsonObject.getDouble("Latitude");
            Altitude = jsonObject.getDouble("Altitude");
            MotionType = jsonObject.getInt("MotionType");
            ActivityType = jsonObject.getInt("ActivityType");
            RetentionType = jsonObject.getInt("RetentionType");
            CompanionType = jsonObject.getInt("CompanionType");
            RelationType = jsonObject.getInt("RelationType");
            ImageCount = jsonObject.getInt("ImageCount");
            VideoCount = jsonObject.getInt("VideoCount");
            AudioCount = jsonObject.getInt("AudioCount");
            StateType = jsonObject.getInt("StateType");
            //            DeviceID = jsonObject.getInt("DeviceID");
            CreateTime = jsonObject.getString("CreateTime");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getPoiID() {
        return PoiID;
    }

    public void setPoiID(int poiID) {
        PoiID = poiID;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public long getTraceID() {
        return traceID;
    }

    public void setTraceID(long traceID) {
        this.traceID = traceID;
    }

    public int getPoiNo() {
        return PoiNo;
    }

    public void setPoiNo(int poiNo) {
        PoiNo = poiNo;
    }

    public String getCmt() {
        return Cmt;
    }

    public void setCmt(String cmt) {
        Cmt = cmt;
    }

    public String getAnalyseWords() {
        return AnalyseWords;
    }

    public void setAnalyseWords(String analyseWords) {
        AnalyseWords = analyseWords;
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

    public int getStateType() {
        return StateType;
    }

    public void setStateType(int stateType) {
        StateType = stateType;
    }

    public int getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(int deviceID) {
        DeviceID = deviceID;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    @Override
    public String toString() {
        return "InterestMarkerData{" +
                "PoiID=" + PoiID +
                ", UserId='" + UserId + '\'' +
                ", traceID=" + traceID +
                ", PoiNo=" + PoiNo +
                ", Cmt='" + Cmt + '\'' +
                ", AnalyseWords='" + AnalyseWords + '\'' +
                ", Country='" + Country + '\'' +
                ", Province='" + Province + '\'' +
                ", City='" + City + '\'' +
                ", PlaceName='" + PlaceName + '\'' +
                ", Longitude=" + Longitude +
                ", Latitude=" + Latitude +
                ", Altitude=" + Altitude +
                ", MotionType=" + MotionType +
                ", ActivityType=" + ActivityType +
                ", RetentionType=" + RetentionType +
                ", CompanionType=" + CompanionType +
                ", RelationType=" + RelationType +
                ", ImageCount=" + ImageCount +
                ", VideoCount=" + VideoCount +
                ", AudioCount=" + AudioCount +
                ", StateType=" + StateType +
                ", DeviceID=" + DeviceID +
                ", CreateTime='" + CreateTime + '\'' +
                '}';
    }
}