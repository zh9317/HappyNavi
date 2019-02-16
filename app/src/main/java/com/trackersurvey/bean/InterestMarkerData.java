package com.trackersurvey.bean;

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
}
