package com.trackersurvey.bean;

import com.amap.api.maps.model.LatLng;

/**
 * Created by zh931 on 2018/6/5.
 */

public class TraceLatLng {
    private LatLng latLng;
    private int sportType;
    private String createTime;

    public TraceLatLng(LatLng latLng, int sportType, String createTime) {
        this.latLng = latLng;
        this.sportType = sportType;
        this.createTime = createTime;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public int getSportType() {
        return sportType;
    }

    public void setSportType(int sportType) {
        this.sportType = sportType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
