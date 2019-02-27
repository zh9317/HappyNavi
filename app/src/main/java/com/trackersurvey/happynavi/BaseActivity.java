package com.trackersurvey.happynavi;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import com.trackersurvey.util.ActivityCollector;
import com.trackersurvey.util.LanguageUtil;
import com.trackersurvey.util.StoreLanguageSP;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

/**
 * Created by zh931 on 2018/6/7.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("BaseActivity", getClass().getSimpleName());

//        EventBus.getDefault().register(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActivityCollector.addActivity(this);
    }

    /**
     * 重写一个 ContextWrapper 类
     *  ContextWrapper 构造函数中必须包含一个真正的 Context 引用，
     * 同时 ContextWrapper 中提供了 attachBaseContext() 用于给 ContextWrapper 对象中指定真正的 Context 对象，
     * 调用 ContextWrapper 的方法都会被转向其所包含的真正的Context对象。
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("language", Context.MODE_PRIVATE);
        String selectedLanguage = preferences.getString("language", "");
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, selectedLanguage));
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(String str) {
//        switch (str) {
//            case "EVENT_REFRESH_LANGUAGE":
////                changeAppLanguage();
//                recreate();
//                break;
//        }
//    }
//
//    private void changeAppLanguage() {
//        String sta = StoreLanguageSP.getLanguageLocal(this);
//        if(sta != null && !"".equals(sta)){
//            // 本地语言设置
//            Locale myLocale = new Locale(sta);
//            Resources res = getResources();
//            DisplayMetrics dm = res.getDisplayMetrics();
//            Configuration conf = res.getConfiguration();
//            conf.locale = myLocale;
//            res.updateConfiguration(conf, dm);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
        ActivityCollector.removeActivity(this);
    }
}
