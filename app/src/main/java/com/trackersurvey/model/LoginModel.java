package com.trackersurvey.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zh931 on 2018/5/21.
 */

public class LoginModel {
    UserInfo userInfo;
    String token;

    public LoginModel(JSONObject object){
        try {
            userInfo = new UserInfo(object.getJSONObject("userInfo"));
            token = object.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public class UserInfo {
        String birthdate;
        String headurl;
        String isdelete;
        String lastlogintime;
        String mobilephone;
        String mobileplatform; // 设备类型
        String nickname;
        String passwordmd5;
        String realname;
        String registeritem2; // 籍贯
        String registeritem3; // 地址
        String registeritem4; // 学历
        String registeritem5; // 收入
        String registeritem6; // 身份
        String registeritem7; // 婚姻
        String registeritem8; //
        String registertime;
        int sex;
        int userid;

        public UserInfo(JSONObject object) {
            try {
                // 注册之后就有的信息
                userid = object.getInt("userid");
                mobilephone = object.getString("mobilephone");
                passwordmd5 = object.getString("passwordmd5");
                headurl = object.getString("headurl");
                isdelete = object.getString("isdelete");
                lastlogintime = object.getString("lastlogintime");
                // 用户修改个人信息后才有的信息
                birthdate = object.getString("birthdate");
                nickname = object.getString("nickname");
                realname = object.getString("realname");
                sex = object.getInt("sex");
                registeritem2 = object.getString("registeritem2");// 籍贯
                registeritem3 = object.getString("registeritem3");// 住址
                registeritem4 = object.getString("registeritem4");// 学历
                registeritem5 = object.getString("registeritem5");// 收入
                registeritem6 = object.getString("registeritem6");// 职业
                registeritem7 = object.getString("registeritem7");// 婚姻
                registeritem8 = object.getString("registeritem8");// 子女数
                registertime = object.getString("registertime");

                mobileplatform = object.getString("mobileplatform");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(String birthdate) {
            this.birthdate = birthdate;
        }

        public String getHeadurl() {
            return headurl;
        }

        public void setHeadurl(String headurl) {
            this.headurl = headurl;
        }

        public String getIsdelete() {
            return isdelete;
        }

        public void setIsdelete(String isdelete) {
            this.isdelete = isdelete;
        }

        public String getLastlogintime() {
            return lastlogintime;
        }

        public void setLastlogintime(String lastlogintime) {
            this.lastlogintime = lastlogintime;
        }

        public String getMobilephone() {
            return mobilephone;
        }

        public void setMobilephone(String mobilephone) {
            this.mobilephone = mobilephone;
        }

        public String getMobileplatform() {
            return mobileplatform;
        }

        public void setMobileplatform(String mobileplatform) {
            this.mobileplatform = mobileplatform;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPasswordmd5() {
            return passwordmd5;
        }

        public void setPasswordmd5(String passwordmd5) {
            this.passwordmd5 = passwordmd5;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
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

        public String getRegistertime() {
            return registertime;
        }

        public void setRegistertime(String registertime) {
            this.registertime = registertime;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public int getUserid() {
            return userid;
        }

        public void setUserid(int userid) {
            this.userid = userid;
        }
    }

}
