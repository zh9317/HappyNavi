package com.trackersurvey.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zh931 on 2018/6/2.
 */

public class PoiChoiceModel {

    private List<CompanionType> companionTypeList; // 同伴个数
    private List<RelationType> relationTypeList; // 同伴关系
    private List<ActivityType> activityTypeList; // 行为类型
    private List<MotionType> motionTypeList; // 心情类型
    private List<RetentionType> retentionTypeList; // 停留时间

    public PoiChoiceModel(JSONObject jsonObject) {
        companionTypeList = new ArrayList<>();
        relationTypeList = new ArrayList<>();
        activityTypeList = new ArrayList<>();
        motionTypeList = new ArrayList<>();
        retentionTypeList = new ArrayList<>();
        try {
            JSONArray companionArr = jsonObject.getJSONArray("CompanionType");
            JSONArray relationTypeArr = jsonObject.getJSONArray("RelationType");
            JSONArray activityTypeArr = jsonObject.getJSONArray("ActivityType");
            JSONArray motionTypeArr = jsonObject.getJSONArray("MotionType");
            JSONArray retentionTypeArr = jsonObject.getJSONArray("RetentionType");
            for (int i = 0; i < companionArr.length(); i++) {
                CompanionType companionType = new CompanionType(companionArr.getJSONObject(i));
                companionTypeList.add(companionType);
            }
            for (int i = 0; i < relationTypeArr.length(); i++) {
                RelationType relationType = new RelationType(relationTypeArr.getJSONObject(i));
                relationTypeList.add(relationType);
            }
            for (int i = 0; i < activityTypeArr.length(); i++) {
                ActivityType activityType = new ActivityType(activityTypeArr.getJSONObject(i));
                activityTypeList.add(activityType);
            }
            for (int i = 0; i < motionTypeArr.length(); i++) {
                MotionType motionType = new MotionType(motionTypeArr.getJSONObject(i));
                motionTypeList.add(motionType);
            }
            for (int i = 0; i < retentionTypeArr.length(); i++) {
                RetentionType retentionType = new RetentionType(retentionTypeArr.getJSONObject(i));
                retentionTypeList.add(retentionType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<CompanionType> getCompanionTypeList() {
        return companionTypeList;
    }

    public void setCompanionTypeList(List<CompanionType> companionTypeList) {
        this.companionTypeList = companionTypeList;
    }

    public List<RelationType> getRelationTypeList() {
        return relationTypeList;
    }

    public void setRelationTypeList(List<RelationType> relationTypeList) {
        this.relationTypeList = relationTypeList;
    }

    public List<ActivityType> getActivityTypeList() {
        return activityTypeList;
    }

    public void setActivityTypeList(List<ActivityType> activityTypeList) {
        this.activityTypeList = activityTypeList;
    }

    public List<MotionType> getMotionTypeList() {
        return motionTypeList;
    }

    public void setMotionTypeList(List<MotionType> motionTypeList) {
        this.motionTypeList = motionTypeList;
    }

    public List<RetentionType> getRetentionTypeList() {
        return retentionTypeList;
    }

    public void setRetentionTypeList(List<RetentionType> retentionTypeList) {
        this.retentionTypeList = retentionTypeList;
    }

    public class CompanionType {
        private int companiontype;
        private String companiontypename;
        public CompanionType(JSONObject jsonObject){
            try {
                companiontype = jsonObject.getInt("companiontype");
                companiontypename = jsonObject.getString("companiontypename");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public int getCompaniontype() {
            return companiontype;
        }

        public void setCompaniontype(int companiontype) {
            this.companiontype = companiontype;
        }

        public String getCompaniontypename() {
            return companiontypename;
        }

        public void setCompaniontypename(String companiontypename) {
            this.companiontypename = companiontypename;
        }
    }
    public class RelationType {
        private int relationtype;
        private String relationtypename;

        public RelationType(JSONObject jsonObject) {
            try {
                relationtype = jsonObject.getInt("relationtype");
                relationtypename = jsonObject.getString("relationtypename");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public int getRelationtype() {
            return relationtype;
        }

        public void setRelationtype(int relationtype) {
            this.relationtype = relationtype;
        }

        public String getRelationtypename() {
            return relationtypename;
        }

        public void setRelationtypename(String relationtypename) {
            this.relationtypename = relationtypename;
        }
    }
    public class ActivityType {
        private int activitytype;
        private String activityname;

        public ActivityType(JSONObject jsonObject) {
            try {
                activitytype = jsonObject.getInt("activitytype");
                activityname = jsonObject.getString("activityname");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public int getActivitytype() {
            return activitytype;
        }

        public void setActivitytype(int activitytype) {
            this.activitytype = activitytype;
        }

        public String getActivityname() {
            return activityname;
        }

        public void setActivityname(String activityname) {
            this.activityname = activityname;
        }
    }
    public class MotionType {
        private int motiontype;
        private String motiontypename;

        public MotionType(JSONObject jsonObject) {
            try {
                motiontype = jsonObject.getInt("motiontype");
                motiontypename = jsonObject.getString("motiontypename");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public int getMotiontype() {
            return motiontype;
        }

        public void setMotiontype(int motiontype) {
            this.motiontype = motiontype;
        }

        public String getMotiontypename() {
            return motiontypename;
        }

        public void setMotiontypename(String motiontypename) {
            this.motiontypename = motiontypename;
        }
    }
    public class RetentionType {
        private int retentiontype;
        private String retentiontypename;

        public RetentionType(JSONObject jsonObject) {
            try {
                retentiontype = jsonObject.getInt("retentiontype");
                retentiontypename = jsonObject.getString("retentiontypename");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public int getRetentiontype() {
            return retentiontype;
        }

        public void setRetentiontype(int retentiontype) {
            this.retentiontype = retentiontype;
        }

        public String getRetentiontypename() {
            return retentiontypename;
        }

        public void setRetentiontypename(String retentiontypename) {
            this.retentiontypename = retentiontypename;
        }
    }
}
