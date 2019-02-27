package com.trackersurvey.happynavi;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trackersurvey.util.AppManager;

public class SetParameterActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    //定义相关变量
    String updateSwitchKey;
    String updateFrequencyKey;
    String uploadFrequencyKey;
    String rec_loc_FrequencyKey;
    String norec_loc_FrequencyKey;
    //	String languageKey;
    CheckBoxPreference updateSwitchCheckPref;
    ListPreference updateFrequencyListPref;
    //	ListPreference languageListPref;
//	SharedPreferences languageSharePref;
    Preference uploadFrequencyPref;
    Preference rec_loc_FrequencyPref;
    //Preference norec_loc_FrequencyPref;
    //顶部标题栏
    private LinearLayout back;
    private ImageView titleBackOff; // 顶部回退按钮
    private TextView titleText; // 顶部文本
    private Button titleButton; // 顶部确认按钮
    public static SetParameterActivity instance = null;
    //	public static String languageSummaryTxt = "";
    private SharedPreferences sp;
    private int l;

    private TextView titleTv;
    private TextView titleRightTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 老代码使用这种方法，但现在会报错
//        final boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_set_parameter);
        // 切换中英文
        //-----------------------------------------------------//
        Resources resources = getResources();
        Configuration configure = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
//        if(TabHost_Main.l==0){
//            configure.locale = Locale.CHINESE;
//        }
//        if(TabHost_Main.l==1){
//            configure.locale = Locale.ENGLISH;
//        }
//        if(TabHost_Main.l==2) {
//            configure.locale = new Locale("cs", "CZ");
//        }
        resources.updateConfiguration(configure, dm);
        //-----------------------------------------------------//

//        instance = this;
//        if(isCustom){
//            //自定义顶部标题栏
//            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
//        }
        //从xml文件中添加Preference项

        addPreferencesFromResource(R.xml.preferencesii);
//        final boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        // 添加自定义导航栏
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AppManager.getAppManager().addActivity(this);
//        back = (LinearLayout) findViewById(R.id.title_back);
//        back.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                finish();
//            }
//
//        });

//        // 顶部标题
//        titleText = (TextView) findViewById(R.id.header_text);
//        titleText.setText(getResources().getString(R.string.item_setparameter));
//        // 右部按钮不可见
//        titleButton = (Button) findViewById(R.id.header_right_btn);
//        titleButton.setVisibility(View.INVISIBLE);

        //获取各个Preference
        updateSwitchKey = getResources().getString(R.string.auto_update_switch_key);
        updateFrequencyKey = getResources().getString(R.string.auto_update_frequency_key);
        uploadFrequencyKey = getResources().getString(R.string.auto_upload_frequency_key);
        rec_loc_FrequencyKey = getResources().getString(R.string.auto_record_loc_frequency_key);
        norec_loc_FrequencyKey = getResources().getString(R.string.auto_norecord_loc_frequency_key);
//        languageKey = getResources().getString(R.string.language_key);
        updateSwitchCheckPref = (CheckBoxPreference)findPreference(updateSwitchKey);
        updateFrequencyListPref = (ListPreference)findPreference(updateFrequencyKey);
//        languageListPref = (ListPreference)findPreference(languageKey);
//        languageSharePref = (SharedPreferences) findPreference(languageKey);
        uploadFrequencyPref = (Preference)findPreference(uploadFrequencyKey);
        rec_loc_FrequencyPref = (Preference)findPreference(rec_loc_FrequencyKey);
        //norec_loc_FrequencyPref = (Preference)findPreference(norec_loc_FrequencyKey);
        //为各个Preference注册监听接口
        updateSwitchCheckPref.setOnPreferenceClickListener(this);
        updateFrequencyListPref.setOnPreferenceClickListener(this);
//        languageListPref.setOnPreferenceClickListener(this);
//        languageSharePref.registerOnSharedPreferenceChangeListener(this);
        uploadFrequencyPref.setOnPreferenceClickListener(this);
        rec_loc_FrequencyPref.setOnPreferenceClickListener(this);
        // norec_loc_FrequencyPref.setOnPreferenceClickListener(this);

        updateSwitchCheckPref.setOnPreferenceChangeListener(this);
        updateFrequencyListPref.setOnPreferenceChangeListener(this);
//        languageListPref.setOnPreferenceChangeListener(this);
        uploadFrequencyPref.setEnabled(false);//OnPreferenceChangeListener(this);
        rec_loc_FrequencyPref.setEnabled(false);//OnPreferenceChangeListener(this);
        //norec_loc_FrequencyPref.setEnabled(false);//OnPreferenceChangeListener(this);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        updateFrequencyListPref.setSummary(updateFrequencyListPref.getEntry());
//        languageListPref.setSummary(languageListPref.getEntry());

        uploadFrequencyPref.setSummary(settings.getInt(uploadFrequencyKey, 30)+getResources().getString(R.string.setbyserver));
        rec_loc_FrequencyPref.setSummary(settings.getInt(rec_loc_FrequencyKey, 5)+getResources().getString(R.string.setbyserver));
        //norec_loc_FrequencyPref.setSummary(settings.getInt(norec_loc_FrequencyKey, 10)+"秒  (后台设定)");

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // TODO Auto-generated method stub
        //Log.v("mysetting", preference.getKey()+" is changed");
        //Log.v("mysetting", );
        //判断是哪个Preference改变了
        if(preference.getKey().equals(updateSwitchKey)) {
            //Log.v("mysetting", "checkbox preference is changed");
        } else if(preference.getKey().equals(updateFrequencyKey)) {
            int newSummary=Integer.parseInt(newValue.toString());
            String summaryTxt="";
            if(newSummary>=60){
                newSummary=newSummary/60;
                summaryTxt=newSummary+getResources().getString(R.string.hour);
            }
            else{
                summaryTxt=newSummary+getResources().getString(R.string.minute);
            }
            //Log.v("mysetting", "list preference is changed to--->"+summaryTxt);
            updateFrequencyListPref.setSummary(summaryTxt);
        } else {
            //如果返回false表示不允许被改变
            //Log.v("mysetting", preference.getKey()+" false");
            return false;
        }
        //返回true表示允许改变
        //Log.v("mysetting", preference.getKey()+" true");
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        // TODO Auto-generated method stub
        //Log.v("mysetting", preference.getKey()+" is clicked");
        //Log.v("mysetting", preference.getKey());
        //判断是哪个Preference被点击了
        if(preference.getKey().equals(updateSwitchKey)) {
            //Log.v("mysetting", "checkbox preference is clicked");
        } else if(preference.getKey().equals(updateFrequencyKey)) {
            //Log.v("mysetting", "list preference is clicked");
        } else {
            return false;
        }
        return true;
    }
}
