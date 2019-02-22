package com.trackersurvey.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zh931 on 2018/7/2.
 */

public class UserInfoData {
    private int userID;
    private String nickname;
    private String realName;
    private String birthDate;
    private int sex;
    private String registeritem2; // 籍贯
    private String registeritem3; // 住址
    private String registeritem4; // 教育程度
    private String registeritem5; // 年收入
    private String registeritem6; // 职业
    private String registeritem7; // 婚姻
    private String registeritem8; // 子女数

    public UserInfoData(int userID, String nickname, String realName, String birthDate, int sex,
                        String registeritem2, String registeritem3, String registeritem4, String registeritem5,
                        String registeritem6, String registeritem7, String registeritem8) {
        this.userID = userID;
        this.nickname = nickname;
        this.realName = realName;
        this.birthDate = birthDate;
        this.sex = sex;
        this.registeritem2 = registeritem2;
        this.registeritem3 = registeritem3;
        this.registeritem4 = registeritem4;
        this.registeritem5 = registeritem5;
        this.registeritem6 = registeritem6;
        this.registeritem7 = registeritem7;
        this.registeritem8 = registeritem8;
    }

    public UserInfoData(JSONObject object) {
        try {
            nickname = object.getString("nickname");
            realName = object.getString("realname");
            birthDate = object.getString("birthdate");
            sex = object.getInt("sex");
            registeritem2 = object.getString("registeritem2");
            registeritem3 = object.getString("registeritem3");
            registeritem4 = object.getString("registeritem4");
            registeritem5 = object.getString("registeritem5");
            registeritem6 = object.getString("registeritem6");
            registeritem7 = object.getString("registeritem7");
            registeritem8 = object.getString("registeritem8");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getRegisteritem2() {
        return registeritem2;
    }

    public void setRegisteritem2(String registeritem2) {
        this.registeritem2 = registeritem2;
    }

    public String getRegisteritem3() {
        return registeritem3;
    }

    public void setRegisteritem3(String registeritem3) {
        this.registeritem3 = registeritem3;
    }

    public String getRegisteritem4() {
        return registeritem4;
    }

    public void setRegisteritem4(String registeritem4) {
        this.registeritem4 = registeritem4;
    }

    public String getRegisteritem5() {
        return registeritem5;
    }

    public void setRegisteritem5(String registeritem5) {
        this.registeritem5 = registeritem5;
    }

    public String getRegisteritem6() {
        return registeritem6;
    }

    public void setRegisteritem6(String registeritem6) {
        this.registeritem6 = registeritem6;
    }

    public String getRegisteritem7() {
        return registeritem7;
    }

    public void setRegisteritem7(String registeritem7) {
        this.registeritem7 = registeritem7;
    }

    public String getRegisteritem8() {
        return registeritem8;
    }

    public void setRegisteritem8(String registeritem8) {
        this.registeritem8 = registeritem8;
    }
}
