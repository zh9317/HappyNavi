package com.trackersurvey.happynavi;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.CustomDialog;
import com.trackersurvey.util.DataCleanManager;
import com.trackersurvey.util.ToastUtil;

import java.io.File;

public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private TextView titleTv;
    private TextView titleRightTv;
    private TextView cacheSizeTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
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
        parameterLayout.setOnClickListener(this);
        languageLayout.setOnClickListener(this);
        clearCacheLayout.setOnClickListener(this);
        backgroundRunLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parameter_setting_layout:
                Intent intent = new Intent(this, SetParameterActivity.class);
                startActivity(intent);
                break;
            case R.id.select_language_layout:
                break;
            case R.id.clear_cache_layout:
                CustomDialog.Builder builder = new CustomDialog.Builder(SettingActivity.this);
                builder.setTitle(getResources().getString(R.string.tip));
                builder.setMessage(getResources().getString(R.string.tips_clean));
                builder.setNegativeButton(getResources().getString(R.string.cancl),new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(getResources().getString(R.string.confirm),new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        DataCleanManager.cleanApplicationData(getApplicationContext(),
                                Common.LOG_PATH,Common.PHOTO_PATH,
                                Common.GROUPHEAD_PATH,Common.CACHEPHOTO_PATH,
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
        }
    }
}
