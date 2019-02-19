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
            JSONArray retentionTypeArr = jsonObject.getJSONArray("RetentionTimeType");
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
        private String companionTypeName_E;
        private int companionType;
        private String companionTypeName;
        public CompanionType(JSONObject jsonObject){
            try {
                companionTypeName_E = jsonObject.getString("CompanionTypeName_E");
                companionType = jsonObject.getInt("CompanionType");
                companionTypeName = jsonObject.getString("CompanionTypeName");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getCompanionTypeName_E() {
            return companionTypeName_E;
        }

        public void setCompanionTypeName_E(String companionTypeName_E) {
            this.companionTypeName_E = companionTypeName_E;
        }

        public int getCompanionType() {
            return companionType;
        }

        public void setCompanionType(int companionType) {
            this.companionType = companionType;
        }

        public String getCompanionTypeName() {
            return companionTypeName;
        }

        public void setCompanionTypeName(String companionTypeName) {
            this.companionTypeName = companionTypeName;
        }
    }
    public class RelationType {
        private int relationType;
        private String relationTypeName;
        private String relationTypeName_E;

        public RelationType(JSONObject jsonObject) {
            try {
                relationType = jsonObject.getInt("RelationType");
                relationTypeName = jsonObject.getString("RelationTypeName");
                relationTypeName_E = jsonObject.getString("RelationTypeName_E");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public int getRelationType() {
            return relationType;
        }

        public void setRelationType(int relationType) {
            this.relationType = relationType;
        }

        public String getRelationTypeName() {
            return relationTypeName;
        }

        public void setRelationTypeName(String relationTypeName) {
            this.relationTypeName = relationTypeName;
        }

        public String getRelationTypeName_E() {
            return relationTypeName_E;
        }

        public void setRelationTypeName_E(String relationTypeName_E) {
            this.relationTypeName_E = relationTypeName_E;
        }
    }
    public class ActivityType {
        private int activityType;
        private String activityName;
        private String activityName_EN;

        public ActivityType(JSONObject jsonObject) {
            try {
                activityType = jsonObject.getInt("ActivityType");
                activityName = jsonObject.getString("ActivityName_ZH_CN");
                activityName_EN = jsonObject.getString("ActivityName_EN");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public int getActivityType() {
            return activityType;
        }

        public void setActivityType(int activityType) {
            this.activityType = activityType;
        }

        public String getActivityName() {
            return activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }

        public String getActivityName_EN() {
            return activityName_EN;
        }

        public void setActivityName_EN(String activityName_EN) {
            this.activityName_EN = activityName_EN;
        }
    }
    public class MotionType {
        private int motionType;
        private String motionTypeName;

        public MotionType(JSONObject jsonObject) {
            try {
                motionType = jsonObject.getInt("MotionType");
                motionTypeName = jsonObject.getString("MotionTypeName");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public int getMotionType() {
            return motionType;
        }

        public void setMotionType(int motionType) {
            this.motionType = motionType;
        }

        public String getMotionTypeName() {
            return motionTypeName;
        }

        public void setMotionTypeName(String motionTypeName) {
            this.motionTypeName = motionTypeName;
        }
    }
    public class RetentionType {
        private int retentionType;
        private String retentionTypeName;
        private String retentionTypeName_E;

        public RetentionType(JSONObject jsonObject) {
            try {
                retentionType = jsonObject.getInt("RetentionType");
                retentionTypeName = jsonObject.getString("RetentionTypeName");
                retentionTypeName_E = jsonObject.getString("RetentionTypeName_E");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public int getRetentionType() {
            return retentionType;
        }

        public void setRetentionType(int retentionType) {
            this.retentionType = retentionType;
        }

        public String getRetentionTypeName() {
            return retentionTypeName;
        }

        public void setRetentionTypeName(String retentionTypeName) {
            this.retentionTypeName = retentionTypeName;
        }

        public String getRetentionTypeName_E() {
            return retentionTypeName_E;
        }

        public void setRetentionTypeName_E(String retentionTypeName_E) {
            this.retentionTypeName_E = retentionTypeName_E;
        }
    }
}
