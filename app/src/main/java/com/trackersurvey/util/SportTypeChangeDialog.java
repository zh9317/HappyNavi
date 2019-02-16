package com.trackersurvey.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.trackersurvey.happynavi.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zh931 on 2018/6/4.
 */

public class SportTypeChangeDialog extends Dialog {
    //private TextView justtrail;
    //private CheckBox justtrailchecked;
    private Button start;
    private Button cancel;
    private GridView gridview;
    private Context context;
    private Activity activity;
    private View itemview=null;//gridview单个item的view
    private int postion=-1;//记录点击的选项位置
    //private int isopen;//0表示公开，1表示不公开
    public SportTypeChangeDialog(Context context) {

        this(context, android.R.style.Theme_Dialog);
        this.context=context;
    }

    public SportTypeChangeDialog(Context context, int theme) {
        super(context, theme);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sport_type_change_dialog);

        this.activity=(Activity)context;
        start=(Button)findViewById(R.id.sportdialog_starttrail);

        cancel=(Button)findViewById(R.id.sportdialog_canceltrail);
        gridview=(GridView)findViewById(R.id.grid_sporttype);
        gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));//将点击出现的背景色设为透明
        int[] imageId=new int[]{R.mipmap.ic_walking,
                R.mipmap.ic_cycling,
                R.mipmap.ic_rollerblading,
                R.mipmap.ic_driving,
                R.mipmap.ic_train,
                R.mipmap.others,
        };
        String[] title=context.getResources().getStringArray(R.array.sporttype);
        List<Map<String,Object>> listItems=new ArrayList<Map<String,Object>>();
        for(int i=0;i<imageId.length;i++){
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("image", imageId[i]);
            map.put("title", title[i]);
            listItems.add(map);

        }
        SimpleAdapter adapter=new SimpleAdapter(context,listItems,R.layout.sport_dialog_items,new String[]{"title","image"},
                new int[]{R.id.sporttype_title,R.id.sporttype_img});
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                postion=pos+1;
                for(int i=0;i<gridview.getChildCount();i++){
                    itemview=gridview.getChildAt(i);
                    itemview.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                view.setBackgroundColor(Color.parseColor("#99cc33"));
                itemview=view;


            }
        });


        start.setOnClickListener(new android.view.View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(postion==-1){
                    ToastUtil.show(context, context.getResources().getString(R.string.tips_choosetype));
                    return;
                }
                if(postion==1){//步行
                    if(!Common.checkGPS(context)){
                        setGPS(1);
                    }
                    else{
                        SportTypeChangeDialog.this.dismiss();
                    }
                }
                else{
                    if(!Common.checkGPS(context)){
                        setGPS(0);
                    }
                    SportTypeChangeDialog.this.dismiss();
                }

            }

        });
        cancel.setOnClickListener(new android.view.View.OnClickListener(){

            @Override
            public void onClick(View v) {
                postion=-1;
                SportTypeChangeDialog.this.dismiss();
            }

        });
    }
    public int getposition(){
        return postion;
    }


    private void setGPS(int type) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.tips_gpsdlgtle));
        if(type==0){
            builder.setMessage(context.getResources().getString(R.string.tips_gpsdlgmsg10)
                    +"\n"+context.getResources().getString(R.string.tips_gpsdlgmsg2));
        }
        else{
            builder.setMessage(context.getResources().getString(R.string.tips_gpsdlgmsg11)
                    +"\n"+context.getResources().getString(R.string.tips_gpsdlgmsg2));
        }
        builder.setPositiveButton(context.getResources().getString(R.string.confirm),
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        // 转到手机设置界面，用户设置GPS
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                        arg0.dismiss();
                        //强制打开GPS 方法一：
                    	/*Intent GPSIntent = new Intent();
                        GPSIntent.setClassName("com.android.settings",
                                "com.android.settings.widget.SettingsAppWidgetProvider");
                        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
                        GPSIntent.setData(Uri.parse("custom:3"));
                        try {
                        	Log.i("phonelog", "gps open...");
                            PendingIntent.getBroadcast(MainActivity.this, 0, GPSIntent, 0).send();
                        } catch (CanceledException e) {
                        	Log.i("phonelog", "gps open fail");
                            e.printStackTrace();

                        }  */
                        //强制打开GPS 方法二：
                        //Settings.Secure.setLocationProviderEnabled( getContentResolver(), LocationManager.GPS_PROVIDER, true);
                        //强制打开GPS 方法三：
                        //turnGPSOn();
                    }

                });
        builder.setNegativeButton(context.getResources().getString(R.string.cancl), new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();

            }
        } );
        builder.create().show();

    }
    public void turnGPSOn(){
        //Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        //intent.putExtra("enabled", true);
        //this.sendBroadcast(intent);
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")){
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }

    }
}
