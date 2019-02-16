package com.trackersurvey.bean;

/**
 * Created by zh931 on 2018/5/21.
 */

public class GroupInfoData {
    private String GroupID;//群组ID
    private String GroupName;//群组名
    private String GroupDetail;//群组描述
    private String MemberNums;//群成员数
    private String CreateTime;//群创建时间
    private String CreateMan;//群创建人
    private String PhotoUrl;//群头像url
    private String PhotoName;//群头像名字，便于查找缓存
    private String[] ManagerIDs;//管理员
    private String[] UserIDs;//用户

    public GroupInfoData() {
    }

    public GroupInfoData(String groupID, String groupName, String groupDetail, String memberNums,
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

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
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
}
