package com.trackersurvey.happynavi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.http.LogoutRequest;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.util.ActivityCollector;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.CustomDialog;
import com.trackersurvey.util.DataCleanManager;
import com.trackersurvey.util.StoreLanguageSP;
import com.trackersurvey.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView          titleTv;
    private TextView          titleRightTv;
    private TextView          cacheSizeTv;
    private SharedPreferences sp;

    private int checkedItem = 0;
    private SharedPreferences languageSelected;     // 默认 0 为 zh ； 1 为 en



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp = getSharedPreferences("coffig", MODE_PRIVATE);
        languageSelected = getSharedPreferences("languageSet", MODE_PRIVATE);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        titleTv = findViewById(R.id.title_text);
        titleTv.setText(getResources().getString(R.string.item_settings));
        titleRightTv = findViewById(R.id.title_right_text);
        titleRightTv.setVisibility(View.GONE);
        cacheSizeTv = findViewById(R.id.cache_size);
        try {
            cacheSizeTv.setText(DataCleanManager.getCacheSize(new File(Common.APPLICATION_DIR)));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        LinearLayout parameterLayout = findViewById(R.id.parameter_setting_layout);
        LinearLayout languageLayout = findViewById(R.id.select_language_layout);
        LinearLayout clearCacheLayout = findViewById(R.id.clear_cache_layout);
        LinearLayout backgroundRunLayout = findViewById(R.id.background_run_layout);
        Button logoutBtn = findViewById(R.id.logout_btn);
        parameterLayout.setOnClickListener(this);
        languageLayout.setOnClickListener(this);
        clearCacheLayout.setOnClickListener(this);
        backgroundRunLayout.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

        checkedItem = Integer.parseInt(languageSelected.getString("language", "0"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parameter_setting_layout:
                Intent intent = new Intent(this, SetParameterActivity.class);
                startActivity(intent);
                break;
            case R.id.select_language_layout:
                final String[] items = {getResources().getString(R.string.language_chinese),
                        getResources().getString(R.string.language_english)};
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle(getResources().getString(R.string.language_title));
                builder2.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0: // 中文
                                dialog.dismiss();
                                SharedPreferences preferences1 = getSharedPreferences("language", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences1.edit();
                                editor.putString("language", "zh");
                                editor.apply();

                                SharedPreferences.Editor editor1 = languageSelected.edit();
                                editor1.putString("language", "0");
                                editor1.apply();
                                finish();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
                                        LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(LaunchIntent);
                                    }
                                },1);
                                break;
                            case 1: // 英文
                                dialog.dismiss();

                                //                                EventBus.getDefault().post("EVENT_REFRESH_LANGUAGE");
                                SharedPreferences preferences = getSharedPreferences("language", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = preferences.edit();
                                editor2.putString("language", "en");
                                editor2.apply();

                                SharedPreferences.Editor editor3 = languageSelected.edit();
                                editor3.putString("language", "1");
                                editor3.apply();
                                finish();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
                                        LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(LaunchIntent);
                                    }
                                },1);
                                break;
                            default:
                                break;
                        }
                        checkedItem = which;
                    }
                });
                builder2.create().show(); // 创建对话框并显示
                break;
            case R.id.clear_cache_layout:
                CustomDialog.Builder builder = new CustomDialog.Builder(SettingActivity.this);
                builder.setTitle(getResources().getString(R.string.tip));
                builder.setMessage(getResources().getString(R.string.tips_clean));
                builder.setNegativeButton(getResources().getString(R.string.cancl), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        DataCleanManager.cleanApplicationData(getApplicationContext(),
                                Common.LOG_PATH, Common.PHOTO_PATH,
                                Common.GROUPHEAD_PATH, Common.CACHEPHOTO_PATH,
                                Common.DOWNLOAD_APP_PATH);
                        ToastUtil.show(SettingActivity.this, getResources().getString(R.string.tips_cleanok));
                        try {
                            cacheSizeTv.setText(DataCleanManager.getCacheSize(new File(Common.APPLICATION_DIR)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case R.id.background_run_layout:
                if (Common.isNetConnected) {
                    startActivity(new Intent(this, BGRunningGuideActivity.class));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.tips_netdisconnect), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.logout_btn:
                String msg = getResources().getString(R.string.exitdlg1);
                if (Common.isRecording) {
                    msg = getResources().getString(R.string.exitdlg2);
                }
                CustomDialog.Builder builder_logout = new CustomDialog.Builder(this);
                builder_logout.setTitle(getResources().getString(R.string.exit));
                builder_logout.setMessage(msg);
                builder_logout.setNegativeButton(getResources().getString(R.string.cancl), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder_logout.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {

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

    private void set(String lauType) {
        // 本地语言设置
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        configuration.locale = Locale.ENGLISH;
        resources.updateConfiguration(configuration, dm);
    }
}
