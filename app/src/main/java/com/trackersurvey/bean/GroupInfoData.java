package com.trackersurvey.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by zh931 on 2018/5/21.
 */

public class GroupInfoData {

    private String GroupDetail;//群组描述
    private String MemberNums;//群成员数
    private String CreateMan;//群创建人
    private String PhotoUrl;//群头像url
    private String PhotoName;//群头像名字，便于查找缓存
    private String[] ManagerIDs;//管理员
    private String[] UserIDs;//用户

    // 新版接口
    private int GroupID;     //群组ID
    private String GroupName;   //群组名
    private String GroupDescription;    //群组描述
    private int IsNeedIdentify;
    private int CreateAdminID;     //群创建人
    private String CreateTime;  //群创建时间
    private int MemberCount;    //群成员数
    private int IsPositionShared;
    private int MaxMemberCount;
    private String GroupPicUrl;     //群头像url


    public GroupInfoData() {
    }

    public GroupInfoData(int groupID, String groupName, String groupDetail, String memberNums,
                         String createTime, String createMan, String photoUrl, String photoName,
                         String[] managerIDs, String[] userIDs) {
        GroupID = groupID;
        GroupName = groupName;
        GroupDetail = groupDetail;
        MemberNums = memberNums;
        CreateTime = createTime;
        CreateMan = createMan;
        PhotoUrl = photoUrl;
        PhotoName = photoName;
        this.ManagerIDs=new String[managerIDs.length];
        for(int i = 0; i < managerIDs.length; i++){
            this.ManagerIDs[i] = managerIDs[i];
        }
        this.UserIDs = new String[userIDs.length];
        for(int i = 0; i < userIDs.length; i++){
            this.UserIDs[i] = userIDs[i];
        }
    }

    public GroupInfoData(JSONObject jsonObject) {
        try {
            GroupID = jsonObject.getInt("GroupID");
            GroupName = jsonObject.getString("GroupName");
            GroupDescription = jsonObject.getString("GroupDescription");
            CreateAdminID = jsonObject.getInt("CreateAdminID");
            CreateTime = jsonObject.getString("CreateTime");
            MemberCount = jsonObject.getInt("MemberCount");
            MaxMemberCount = jsonObject.getInt("MaxMemberCount");
            GroupPicUrl = jsonObject.getString("GroupPicUrl");
            IsNeedIdentify = jsonObject.getInt("IsNeedIdentify");
            IsPositionShared = jsonObject.getInt("IsPositionShared");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public int getGroupID() {
        return GroupID;
    }

    public void setGroupID(int groupID) {
        GroupID = groupID;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getGroupDetail() {
        return GroupDetail;
    }

    public void setGroupDetail(String groupDetail) {
        GroupDetail = groupDetail;
    }

    public String getMemberNums() {
        return MemberNums;
    }

    public void setMemberNums(String memberNums) {
        MemberNums = memberNums;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getCreateMan() {
        return CreateMan;
    }

    public void setCreateMan(String createMan) {
        CreateMan = createMan;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    public String getPhotoName() {
        return PhotoName;
    }

    public void setPhotoName(String photoName) {
        PhotoName = photoName;
    }

    public String getGroupDescription() {
        return GroupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        GroupDescription = groupDescription;
    }

    public int getIsNeedIdentify() {
        return IsNeedIdentify;
    }

    public void setIsNeedIdentify(int isNeedIdentify) {
        IsNeedIdentify = isNeedIdentify;
    }

    public int getCreateAdminID() {
        return CreateAdminID;
    }

    public void setCreateAdminID(int createAdminID) {
        CreateAdminID = createAdminID;
    }

    public int getMemberCount() {
        return MemberCount;
    }

    public void setMemberCount(int memberCount) {
        MemberCount = memberCount;
    }

    public int getIsPositionShared() {
        return IsPositionShared;
    }

    public void setIsPositionShared(int isPositionShared) {
        IsPositionShared = isPositionShared;
    }

    public int getMaxMemberCount() {
        return MaxMemberCount;
    }

    public void setMaxMemberCount(int maxMemberCount) {
        MaxMemberCount = maxMemberCount;
    }

    public String getGroupPicUrl() {
        return GroupPicUrl;
    }

    public void setGroupPicUrl(String groupPicUrl) {
        GroupPicUrl = groupPicUrl;
    }

    public String[] getManagerIDs() {
        return ManagerIDs;
    }

    public void setManagerIDs(String[] managerIDs) {
        //ManagerIDs = managerIDs;
        // 为什么要这样写？
        this.ManagerIDs = new String[managerIDs.length];
        for(int i = 0; i < managerIDs.length; i++){
            this.ManagerIDs[i] = managerIDs[i];
        }
    }

    public String[] getUserIDs() {
        return UserIDs;
    }

    public void setUserIDs(String[] userIDs) {
        //UserIDs = userIDs;
        this.UserIDs = new String[userIDs.length];
        for(int i = 0; i < userIDs.length; i++){
            this.UserIDs[i] = userIDs[i];
        }
    }

    @Override
    public String toString() {
        return "GroupInfoData{" +
                "GroupDetail='" + GroupDetail + '\'' +
                ", MemberNums='" + MemberNums + '\'' +
                ", CreateMan='" + CreateMan + '\'' +
                ", PhotoUrl='" + PhotoUrl + '\'' +
                ", PhotoName='" + PhotoName + '\'' +
                ", ManagerIDs=" + Arrays.toString(ManagerIDs) +
                ", UserIDs=" + Arrays.toString(UserIDs) +
                ", GroupID=" + GroupID +
                ", GroupName='" + GroupName + '\'' +
                ", GroupDescription='" + GroupDescription + '\'' +
                ", IsNeedIdentify=" + IsNeedIdentify +
                ", CreateAdminID=" + CreateAdminID +
                ", CreateTime='" + CreateTime + '\'' +
                ", MemberCount=" + MemberCount +
                ", IsPositionShared=" + IsPositionShared +
                ", MaxMemberCount=" + MaxMemberCount +
                ", GroupPicUrl='" + GroupPicUrl + '\'' +
                '}';
    }
}
