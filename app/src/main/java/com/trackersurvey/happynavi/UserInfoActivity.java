package com.trackersurvey.happynavi;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.http.LogoutRequest;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.util.ActivityCollector;
import com.trackersurvey.util.AppManager;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.CustomDialog;
import com.trackersurvey.util.RoundImageView;

import java.io.IOException;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener{

    private SharedPreferences sp;
    private String sexStr;
    private String nicknameStr;
    private String realNameStr;
    private String userIDStr;
    private String birthDateStr;
    private String occupationStr;
    private String educationStr;

    private RoundImageView userHeadIv;
    private TextView nicknameTv;
    private TextView realNameTv;
    private TextView userIdTv;
    private TextView birthDateTv;
    private TextView sexTv;
    private TextView occupationTv;
    private TextView educationTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        AppManager.getAppManager().addActivity(this);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        userHeadIv = findViewById(R.id.user_info_head_img);
        nicknameTv = findViewById(R.id.user_info_nickname);
        realNameTv = findViewById(R.id.user_info_real_name);
        userIdTv = findViewById(R.id.user_info_id);
        birthDateTv = findViewById(R.id.user_info_birth_date);
        sexTv = findViewById(R.id.user_info_sex);
        occupationTv = findViewById(R.id.user_info_occupation);
        educationTv = findViewById(R.id.user_info_education);
        Button changeInfoBtn = findViewById(R.id.change_user_info_btn);
        Button changePwdBtn = findViewById(R.id.change_password_btn);
        Button logoutBtn = findViewById(R.id.logout_btn);
        Log.i("UserInfo", "headUrl: " + "http://211.87.227.204:8089"
                + sp.getString("headurl", "") + "?token="
                + sp.getString("token", ""));
        Glide.with(this).load("http://211.87.227.204:8089"
                + sp.getString("headurl", "") + "?token="
                + sp.getString("token", "")).into(userHeadIv);
        if (sp.getInt("sex", 0) == 0) {
            sexStr = "保密";
        } else if (sp.getInt("sex", 0) == 1) {
            sexStr = "男";
        } else if (sp.getInt("sex", 0) == 2) {
            sexStr = "女";
        }
        nicknameStr = sp.getString("nickname", "");
        realNameStr = sp.getString("realName", "");
        userIDStr = sp.getString("mobilePhone", "");
        birthDateStr = sp.getString("birthDate", "");
        occupationStr = sp.getString("occupation", "");
        educationStr = sp.getString("education", "");
        nicknameTv.setText(nicknameStr);
        realNameTv.setText(realNameStr);
        userIdTv.setText(userIDStr);
        birthDateTv.setText(birthDateStr);
        sexTv.setText(sexStr);
        occupationTv.setText(occupationStr);
        educationTv.setText(educationStr);
        changeInfoBtn.setOnClickListener(this);
        changePwdBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_user_info_btn:
                Intent intent = new Intent(this, UserInfoChangeActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.change_password_btn:
                break;
            case R.id.logout_btn:
                String msg = getResources().getString(R.string.exitdlg1);
                if(Common.isRecording){
                    msg = getResources().getString(R.string.exitdlg2);
                }
                CustomDialog.Builder builder_logout = new CustomDialog.Builder(this);
                builder_logout.setTitle(getResources().getString(R.string.exit));
                builder_logout.setMessage(msg);
                builder_logout.setNegativeButton(getResources().getString(R.string.cancl),new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder_logout.setPositiveButton(getResources().getString(R.string.confirm),new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
//                        Common.sendOffline(Common.getDeviceId(getApplicationContext()),getApplication());
//                        //Common.userId="0";
//                        Common.layerid_main=0;
                        LogoutRequest logoutRequest = new LogoutRequest(sp.getString("token", ""));
                        logoutRequest.requestHttpData(new ResponseData() {
                            @Override
                            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                                if (isSuccess) {
                                    if (code.equals("0")) {
                                        SharedPreferences.Editor loginEditor = sp.edit();
                                        loginEditor.putString("token", "");
                                        loginEditor.putInt("userID", 0);
                                        loginEditor.putString("userPhone", "");
                                        loginEditor.putString("birthDate", "");
                                        loginEditor.putString("headurl", "");
                                        loginEditor.putString("mobilePhone", "");
                                        loginEditor.putString("nickname", "");
                                        loginEditor.putString("realName", "");
                                        loginEditor.putString("city", "");
                                        loginEditor.putString("workPlace", "");
                                        loginEditor.putString("education", "");
                                        loginEditor.putString("income", "");
                                        loginEditor.putString("occupation", "");
                                        loginEditor.putString("marriage", "");
                                        loginEditor.putString("childCount", "");
                                        loginEditor.putInt("sex", 0);
                                        loginEditor.apply();
                                        dialog.dismiss();
                                    }
                                }
                            }
                        });
                        // 这个Activity之前只有MainActivity
                        ActivityCollector.finishActivity("MainActivity");
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        //intent.setClass(UserInfoActivity.this, SplashActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
                builder_logout.create().show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Glide.with(this).load("http://211.87.227.204:8089"
                + sp.getString("headurl", "") + "?token="
                + sp.getString("token", "")).into(userHeadIv);
        if (sp.getInt("sex", 0) == 0) {
            sexStr = "保密";
        } else if (sp.getInt("sex", 0) == 1) {
            sexStr = "男";
        } else if (sp.getInt("sex", 0) == 2) {
            sexStr = "女";
        }
        nicknameStr = sp.getString("nickname", "");
        realNameStr = sp.getString("realName", "");
        userIDStr = sp.getString("mobilePhone", "");
        birthDateStr = sp.getString("birthDate", "");
        occupationStr = sp.getString("occupation", "");
        educationStr = sp.getString("education", "");
        nicknameTv.setText(nicknameStr);
        realNameTv.setText(realNameStr);
        userIdTv.setText(userIDStr);
        birthDateTv.setText(birthDateStr);
        sexTv.setText(sexStr);
        occupationTv.setText(occupationStr);
        educationTv.setText(educationStr);
    }
}
