package com.trackersurvey.bean;

/**
 * Created by zh931 on 2018/1/15.
 */

public class DiscoverListBean {
    private int ImageId;
    private String description;
    private String date;
    private String browseNum;

    public DiscoverListBean(int imageId, String description, String date, String browseNum) {
        ImageId = imageId;
        this.description = description;
        this.date = date;
        this.browseNum = browseNum;
    }

    public int getImageId() {
        return ImageId;
    }

    public void setImageId(int imageId) {
        ImageId = imageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBrowseNum() {
        return browseNum;
    }

    public void setBrowseNum(String browseNum) {
        this.browseNum = browseNum;
    }
}
