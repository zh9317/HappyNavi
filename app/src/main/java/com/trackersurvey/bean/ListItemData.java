package com.trackersurvey.bean;

import java.util.Arrays;

/**
 * Created by zh931 on 2018/5/12.
 */

public class ListItemData {
    private String                time;  //时间
    private String                place;   //地点
    private InterestMarkerData    event;
    private CommentMediaFilesData files[];
    private String                comment;  //评论
    private int                   feeling;     //心情状态
    private int                   behaviour;   //活动类型
    private int                   duration;    //停留时长
    private int                   companion;   //同伴人数
    private int                   relation;    //同伴关系
    private int                   poiID;
    private long                  traceID;

    public ListItemData(InterestMarkerData event, CommentMediaFilesData files[]) {
        this.event = event;
        this.files = files;
        this.time = event.getCreateTime();
        this.place = event.getPlaceName();
        this.comment = event.getCmt();
        this.feeling = event.getMotionType();
        this.behaviour = event.getActivityType();
        this.duration = event.getRetentionType();
        this.companion = event.getCompanionType();
        this.relation = event.getRelationType();
        this.poiID = event.getPoiID();
        this.traceID = event.getTraceID();
    }

    public long getTraceID() {
        return traceID;
    }

    public void setTraceID(long traceID) {
        this.traceID = traceID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public InterestMarkerData getEvent() {
        return event;
    }

    public void setEvent(InterestMarkerData event) {
        this.event = event;
    }

    public CommentMediaFilesData[] getFiles() {
        return files;
    }

    public void setFiles(CommentMediaFilesData[] files) {
        this.files = files;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }

    public int getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(int behaviour) {
        this.behaviour = behaviour;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCompanion() {
        return companion;
    }

    public void setCompanion(int companion) {
        this.companion = companion;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public void setOneFile(int i, CommentMediaFilesData file) {
        files[i] = file;
    }

    public int getPoiID() {
        return poiID;
    }

    public void setPoiID(int poiID) {
        this.poiID = poiID;
    }

    @Override
    public String toString() {
        return "ListItemData{" +
                "time='" + time + '\'' +
                ", place='" + place + '\'' +
                ", event=" + event +
                ", files=" + Arrays.toString(files) +
                ", comment='" + comment + '\'' +
                ", feeling=" + feeling +
                ", behaviour=" + behaviour +
                ", duration=" + duration +
                ", companion=" + companion +
                ", relation=" + relation +
                ", poiID=" + poiID +
                '}';
    }
}
