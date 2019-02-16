package com.trackersurvey.happynavi;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.http.DownloadUserInfo;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.util.ActivityCollector;
import com.trackersurvey.util.AppManager;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.CustomDialog;
import com.trackersurvey.util.DESUtil;

import org.json.JSONException;
import org.json.JSONObject;

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
        ImageView userHeadIv =findViewById(R.id.user_head_img);
        TextView nicknameTv = findViewById(R.id.user_info_nickname);
        TextView realNameTv = findViewById(R.id.user_info_real_name);
        TextView userIdTv = findViewById(R.id.user_info_id);
        TextView birthDateTv = findViewById(R.id.user_info_birth_date);
        TextView sexTv = findViewById(R.id.user_info_sex);
        TextView occupationTv = findViewById(R.id.user_info_occupation);
        TextView educationTv = findViewById(R.id.user_info_education);
        Button changeInfoBtn = findViewById(R.id.change_user_info_btn);
        Button changePwdBtn = findViewById(R.id.change_password_btn);
        Button logoutBtn = findViewById(R.id.logout_btn);
//        Glide.with(this).load(sp.getString("headurl", "")).into(userHeadIv);
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
        // 测试获取个人信息
        DownloadUserInfo downloadUserInfo = new DownloadUserInfo(String.valueOf(System.currentTimeMillis()),
                sp.getString("Token", ""), "ZH_CN");
        downloadUserInfo.requestHttpData(new ResponseData() {
            @Override
            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                if (isSuccess) {
                    String userInfo = (String) responseObject;
                    Log.i("getUserInfo", "getUserInfo:"+userInfo);
                    try {
                        JSONObject jsonObject = new JSONObject(userInfo);
                        String myUserInfo = jsonObject.getString("userInfo");
                        try {
                            myUserInfo = DESUtil.decrypt(myUserInfo);
                            Log.i("getUserInfo", "myUserInfo:"+myUserInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_user_info_btn:
                Intent intent = new Intent(this, UserInfoChangeActivity.class);
                intent.putExtra("nickname", nicknameStr);
                intent.putExtra("realName", realNameStr);
                intent.putExtra("userID", userIDStr);
                intent.putExtra("birthDate", birthDateStr);
                intent.putExtra("sex", sexStr);
                intent.putExtra("occupation", occupationStr);
                intent.putExtra("education", educationStr);
                startActivity(intent);
                break;
            case R.id.change_password_btn:
                break;
            case R.id.logout_btn:
                String msg=getResources().getString(R.string.exitdlg1);
                if(Common.isRecording){
                    msg=getResources().getString(R.string.exitdlg2);
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
                    public void onClick(DialogInterface dialog, int which) {
                        Common.sendOffline(Common.getDeviceId(getApplicationContext()),UserInfoActivity.this);
                        //Common.userId="0";
                        Common.layerid_main=0;
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("Token", "");
                        editor.putString("userID", "");
                        editor.putString("userPhone", "0");
                        editor.putString("mobilePhone", "");
                        editor.putString("birthDate", "");
                        editor.putString("headurl", "");
                        editor.putString("nickname", "");
                        editor.putString("realName", "");
                        editor.putString("city", "");
                        editor.putString("workPlace", "");
                        editor.putString("education", "");
                        editor.putString("income", "");
                        editor.putString("occupation", "");
                        editor.putString("marriage", "");
                        editor.putString("childCount", "");
                        editor.putInt("sex", 0);
                        editor.apply();
                        dialog.dismiss();
                        //AppManager.getAppManager().finishActivity(MainActivity.class);
                        // 这个Activity之前只有MainActivity
                        ActivityCollector.finishActivity("MainActivity");
                        Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
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
}
