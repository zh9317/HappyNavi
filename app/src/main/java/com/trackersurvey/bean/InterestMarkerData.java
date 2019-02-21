package com.trackersurvey.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zh931 on 2018/5/11.
 */

public class InterestMarkerData {
    private String CreateTime;    //创建时间
    private int PoiNo; // 兴趣点编号
    private double Longitude;  //经度
    private double Latitude;  //纬度
    private double Altitude;  //海拔
    private String Country; // 国家
    private String Province; // 省
    private String City; // 城市
    private String PlaceName; //地点名称
    private String Cmt;  //评论文字
    private long TraceNo;  //轨迹编号
    private int PicCount;  //图片数量
    private int VideoCount;  //视频
    private int SoundCount;  //音频
    private int MotionType;	//心情
    private int ActivityType;	//活动类型
    private int RetentionTime;	//时长
    private int CompanionCount;	//同伴人数
    private int Relationship;//关系
    private int StateType; // 兴趣点状态？？？
    private int Share;
    private String UserId;  //用户ID
    private String CommentId;  //同步云端个人评论时用到的id

    private long   traceID;
    private int    PoiID;
    private String AnalyseWords;
    private int    RetentionType;
    private int    CompanionType;
    private int    RelationType;
    private int    ImageCount;
    private int    AudioCount;
    private int    DeviceID;

    public InterestMarkerData() {
    }

    public InterestMarkerData(String createTime, int poiNo, double longitude, double latitude,
                              double altitude, String country, String province, String city,
                              String placeName, String cmt, long traceNo, int picCount,
                              int videoCount, int soundCount, int motionType, int activityType,
                              int retentionTime, int companionCount, int relationship,
                              int stateType, int share, String userId, String commentId) {
        CreateTime = createTime;
        PoiNo = poiNo;
        Longitude = longitude;
        Latitude = latitude;
        Altitude = altitude;
        Country = country;
        Province = province;
        City = city;
        PlaceName = placeName;
        Cmt = cmt;
        TraceNo = traceNo;
        PicCount = picCount;
        VideoCount = videoCount;
        SoundCount = soundCount;
        MotionType = motionType;
        ActivityType = activityType;
        RetentionTime = retentionTime;
        CompanionCount = companionCount;
        Relationship = relationship;
        StateType = stateType;
        Share = share;
        UserId = userId;
        CommentId = commentId;
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

    public long getTraceID() {
        return traceID;
    }

    public void setTraceID(long traceID) {
        this.traceID = traceID;
    }

    public String getAnalyseWords() {
        return AnalyseWords;
    }

    public void setAnalyseWords(String analyseWords) {
        AnalyseWords = analyseWords;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        this.CreateTime = createTime;
    }

    public int getPoiNo() {
        return PoiNo;
    }

    public void setPoiNo(int poiNo) {
        PoiNo = poiNo;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        this.Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        this.Latitude = latitude;
    }

    public double getAltitude() {
        return Altitude;
    }

    public void setAltitude(double altitude) {
        this.Altitude = altitude;
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
        this.PlaceName = placeName;
    }

    public String getComment() {
        return Cmt;
    }

    public void setComment(String comment) {
        this.Cmt = comment;
    }

    public long getTraceNo() {
        return TraceNo;
    }

    public void setTraceNo(long traceNo) {
        this.TraceNo = traceNo;
    }

    public int getPicCount() {
        return PicCount;
    }

    public void setPicCount(int picCount) {
        this.PicCount = picCount;
    }

    public int getVideoCount() {
        return VideoCount;
    }

    public void setVideoCount(int videoCount) {
        this.VideoCount = videoCount;
    }

    public int getAudioCount() {
        return SoundCount;
    }

    public void setAudioCount(int audioCount) {
        this.SoundCount = audioCount;
    }

    public int getFeeling() {
        return MotionType;
    }

    public void setFeeling(int feeling) {
        this.MotionType = feeling;
    }

    public int getBehaviour() {
        return ActivityType;
    }

    public void setBehaviour(int behaviour) {
        this.ActivityType = behaviour;
    }

    public int getDuration() {
        return RetentionTime;
    }

    public void setDuration(int duration) {
        this.RetentionTime = duration;
    }

    public int getCompanionCount() {
        return CompanionCount;
    }

    public void setCompanionCount(int companionCount) {
        this.CompanionCount = companionCount;
    }

    public int getRelationship() {
        return Relationship;
    }

    public void setRelationship(int relationship) {
        this.Relationship = relationship;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

    public String getCommentId() {
        return CommentId;
    }

    public void setCommentId(String commentId) {
        this.CommentId = commentId;
    }

    public int getStateType() {
        return StateType;
    }

    public void setStateType(int stateType) {
        StateType = stateType;
    }

    public int getShare() {
        return Share;
    }

    public void setShare(int share) {
        Share = share;
    }

    public String getCmt() {
        return Cmt;
    }

    public void setCmt(String cmt) {
        Cmt = cmt;
    }

    public int getSoundCount() {
        return SoundCount;
    }

    public void setSoundCount(int soundCount) {
        SoundCount = soundCount;
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

    public int getRetentionTime() {
        return RetentionTime;
    }

    public void setRetentionTime(int retentionTime) {
        RetentionTime = retentionTime;
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

    public int getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(int deviceID) {
        DeviceID = deviceID;
    }

    @Override
    public String toString() {
        return "InterestMarkerData{" +
                "CreateTime='" + CreateTime + '\'' +
                ", PoiNo=" + PoiNo +
                ", Longitude=" + Longitude +
                ", Latitude=" + Latitude +
                ", Altitude=" + Altitude +
                ", Country='" + Country + '\'' +
                ", Province='" + Province + '\'' +
                ", City='" + City + '\'' +
                ", PlaceName='" + PlaceName + '\'' +
                ", Cmt='" + Cmt + '\'' +
                ", TraceNo=" + TraceNo +
                ", PicCount=" + PicCount +
                ", VideoCount=" + VideoCount +
                ", SoundCount=" + SoundCount +
                ", MotionType=" + MotionType +
                ", ActivityType=" + ActivityType +
                ", RetentionTime=" + RetentionTime +
                ", CompanionCount=" + CompanionCount +
                ", Relationship=" + Relationship +
                ", StateType=" + StateType +
                ", Share=" + Share +
                ", UserId='" + UserId + '\'' +
                ", CommentId='" + CommentId + '\'' +
                ", traceID=" + traceID +
                ", PoiID=" + PoiID +
                ", AnalyseWords='" + AnalyseWords + '\'' +
                ", RetentionType=" + RetentionType +
                ", CompanionType=" + CompanionType +
                ", RelationType=" + RelationType +
                ", ImageCount=" + ImageCount +
                ", AudioCount=" + AudioCount +
                ", DeviceID=" + DeviceID +
                '}';
    }
}
