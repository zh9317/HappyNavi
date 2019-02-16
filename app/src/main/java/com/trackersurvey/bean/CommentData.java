package com.trackersurvey.bean;

/**
 * Created by zh931 on 2018/5/12.
 * 用户评论信息
 * @author Eaa
 * @version 2015年12月3日 上午10:49:56
 */

public class CommentData {
    private String CreateTime;    //创建时间
    private double Longitude;  //经度
    private double Latitude;  //纬度
    private double Altitude;  //海拔
    private String PlaceName; //地点名称
    private String Cmt;  //评论文字
    private long TraceNo;  //轨迹编号
    private int PicCount;  //图片数量
    private int VideoCount;  //视频
    private int SoundCount;  //音频
    private String UserId;  //用户ID
    private String CommentId;  //同步云端个人评论时用到的id

    public CommentData() {
    }

    public CommentData(String createTime, double longitude, double latitude, double altitude,
                       String placeName, String cmt, long traceNo, int picCount, int videoCount,
                       int soundCount, String userId) {
        CreateTime = createTime;
        Longitude = longitude;
        Latitude = latitude;
        Altitude = altitude;
        PlaceName = placeName;
        Cmt = cmt;
        TraceNo = traceNo;
        PicCount = picCount;
        VideoCount = videoCount;
        SoundCount = soundCount;
        UserId = userId;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
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

    public String getPlaceName() {
        return PlaceName;
    }

    public void setPlaceName(String placeName) {
        PlaceName = placeName;
    }

    public String getCmt() {
        return Cmt;
    }

    public void setCmt(String cmt) {
        Cmt = cmt;
    }

    public long getTraceNo() {
        return TraceNo;
    }

    public void setTraceNo(long traceNo) {
        TraceNo = traceNo;
    }

    public int getPicCount() {
        return PicCount;
    }

    public void setPicCount(int picCount) {
        PicCount = picCount;
    }

    public int getVideoCount() {
        return VideoCount;
    }

    public void setVideoCount(int videoCount) {
        VideoCount = videoCount;
    }

    public int getSoundCount() {
        return SoundCount;
    }

    public void setSoundCount(int soundCount) {
        SoundCount = soundCount;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getCommentId() {
        return CommentId;
    }

    public void setCommentId(String commentId) {
        CommentId = commentId;
    }
}
