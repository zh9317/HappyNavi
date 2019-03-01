package com.trackersurvey.bean;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    private int FileID;
    private ArrayList<PoiFile> poiFiles;

    public InterestMarkerData() {
    }

    public InterestMarkerData(JSONObject jsonObject) {
        try {
            JSONObject poiObj = jsonObject.getJSONObject("poi");
            CreateTime = poiObj.getString("CreateTime");
            PoiID = poiObj.getInt("PoiID");
            UserId = poiObj.getString("UserID");
            PoiNo = poiObj.getInt("PoiNo");
            traceID = poiObj.getLong("TraceID");
            Cmt = poiObj.getString("Comment");
            //            AnalyseWords = jsonObject.getString("AnalyseWords");
            Country = poiObj.getString("Country");
            Province = poiObj.getString("Province");
            City = poiObj.getString("City");
            PlaceName = poiObj.getString("PlaceName");
            Longitude = poiObj.getDouble("Longitude");
            Latitude = poiObj.getDouble("Latitude");
            Altitude = poiObj.getDouble("Altitude");
            MotionType = poiObj.getInt("MotionType");
            ActivityType = poiObj.getInt("ActivityType");
            RetentionType = poiObj.getInt("RetentionType");
            CompanionType = poiObj.getInt("CompanionType");
            RelationType = poiObj.getInt("RelationType");
            StateType = poiObj.getInt("StateType");
            ImageCount = poiObj.getInt("ImageCount");
            VideoCount = poiObj.getInt("VideoCount");
            AudioCount = poiObj.getInt("AudioCount");

            //            DeviceID = jsonObject.getInt("DeviceID");

            JSONArray jsonArray = jsonObject.getJSONArray("poifiles");
            poiFiles = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                PoiFile poiFile = new PoiFile(object);
                poiFiles.add(poiFile);
            }

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

    public ArrayList<PoiFile> getPoiFiles() {
        return poiFiles;
    }

    public void setPoiFiles(ArrayList<PoiFile> poiFiles) {
        this.poiFiles = poiFiles;
    }

    public int getFileID() {
        return FileID;
    }

    public void setFileID(int fileID) {
        FileID = fileID;
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
                ", FileID=" + FileID +
                ", poiFiles=" + poiFiles +
                '}';
    }

    public static class PoiFile {
        private int fileNo;
        private String fileSmallPic;
        private String fileUrl;
        private String fileType;
        private int fileID;

        public PoiFile(JSONObject jsonObject) {
            try {
                fileNo = jsonObject.getInt("FileNo");
                fileSmallPic = jsonObject.getString("FileSmallPic");
                fileUrl = jsonObject.getString("FileUrl");
                fileType = jsonObject.getString("FileType");
                fileID = jsonObject.getInt("FileID");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public PoiFile() {

        }

        public int getFileNo() {
            return fileNo;
        }

        public void setFileNo(int fileNo) {
            this.fileNo = fileNo;
        }

        public String getFileSmallPic() {
            return fileSmallPic;
        }

        public void setFileSmallPic(String fileSmallPic) {
            this.fileSmallPic = fileSmallPic;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public int getFileID() {
            return fileID;
        }

        public void setFileID(int fileID) {
            this.fileID = fileID;
        }

        @Override
        public String toString() {
            return "PoiFile{" +
                    "fileNo=" + fileNo +
                    ", fileSmallPic='" + fileSmallPic + '\'' +
                    ", fileUrl='" + fileUrl + '\'' +
                    ", fileType='" + fileType + '\'' +
                    ", fileID=" + fileID +
                    '}';
        }
    }
}