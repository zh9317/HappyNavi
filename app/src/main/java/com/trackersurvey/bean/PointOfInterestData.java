package com.trackersurvey.bean;

/**
 * Created by zh931 on 2018/5/12.
 */

public class PointOfInterestData {
    private int key;
    private String value;

    public PointOfInterestData() {
    }

    public PointOfInterestData(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
