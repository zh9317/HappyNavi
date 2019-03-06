package com.trackersurvey.happynavi;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.fragment.MapFragment;
import com.trackersurvey.fragment.TraceListFragment;
import com.trackersurvey.fragment.MineFragment;
import com.trackersurvey.fragment.QuestionnaireFragment;
import com.trackersurvey.util.AppManager;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.CustomDialog;
import com.trackersurvey.util.ShareToWeChat;

/**
 * 已有功能：
 *          RegisterActivity(注册页面)：用户名密码注册(短信验证码功能待开发)
 *          LoginActivity(登录页面)：用户名密码登录
 *          MapFragment(地图页面)：1、开启LocationService定位，并定时上传位置信息
 *                                      2、选择运动类型记录轨迹
 *                                      3、记录轨迹过程中切换运动类型
 *     TODO                            4、点击右下角相机按钮上传兴趣点(暂时只能上传文字信息，带文件上传兴趣点正在开发)
 *          QuestionnaireFragment(问卷页面)：点击问卷调查，跳转到问卷调查页面
 *     TODO QuestionnaireFragment(问卷调查页面)：使用WebView嵌入网页(现因未找到合适的方法在WebView中存入Token，无法调用
 *     TODO                                        页面的ajax请求。该问题亟待解决！)
 *          MineFragment(我的页面)：1、点击昵称/账号一栏，进入个人信息展示页面
 *                                  2、轨迹列表：点击查看当前用户的所有轨迹
 *                                  3、离线地图：直接调用的高德离线地图页面
 *                                  4、设置：点击进入设置主页面：(1)清除缓存
 *          TraceListFragment(轨迹列表页面)：1、点击列表的某一项，进入轨迹详情页面
 *                                          2、长按列表的某一项，底部弹出操作菜单，可选择一项删除轨迹
 *     TODO                                 注：删除轨迹现在仅支持单项删除，多项删除后台接口待开发
 *          TraceDetailActivity(轨迹详情页面)：在地图上展示轨迹，点击第四个按钮可查看详细数据
 *          UserInfoActivity(个人信息页面)：1、展示登录后返回的个人信息
 *                                          2、点击“修改个人信息”按钮，进入修改个人信息页面
 *          UserInfoChangeActivity(个人信息修改页面)：填写(或选择)号信息后点击保存，向后台提交个人信息
 *                                          3、获取个人信息接口已调试，数据待处理
 *     TODO  注：个人信息模块页面展示和跳转逻辑正在开发中
 *          新版所有请求代码都在http包中，使用okhttp开发，请求类继承自基类HttpUtil；
 *          httpconnection包中的代码均为老版代码，使用httpclient开发，后续将全部删除。
 */
public class MainActivity extends BaseActivity implements View.OnClickListener{

    private MapFragment mapFragment;
    private TraceListFragment shareFragment;
    private QuestionnaireFragment discoverFragment;
    private MineFragment mineFragment;

    private ImageView homepageImage;
    private ImageView shareImage;
    private ImageView discoverImage;
    private ImageView mineImage;

    private TextView homepageText;
    private TextView shareText;
    private TextView discoverText;
    private TextView mineText;

    private FragmentManager fragmentManager;

    private final String REFRESH_ACTION = "android.intent.action.REFRESH_RECEIVER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        AppManager.getAppManager().addActivity(this);
//        ShareToWeChat.registToWeChat(getApplicationContext() );
        initView();
        fragmentManager = getSupportFragmentManager();
        setTabSelection(0);
    }

    private void initView(){
        View homepageLayout = findViewById(R.id.homepage_layout);
        View shareLayout = findViewById(R.id.trace_layout);
        View discoverLayout = findViewById(R.id.questionnaire_layout);
        View mineLayout = findViewById(R.id.mine_layout);

        homepageImage = (ImageView) findViewById(R.id.homepage_image);
        shareImage = (ImageView) findViewById(R.id.share_image);
        discoverImage = (ImageView) findViewById(R.id.discover_image);
        mineImage = (ImageView) findViewById(R.id.mine_image);

        homepageText = (TextView) findViewById(R.id.homepage_text);
        shareText = (TextView) findViewById(R.id.share_text);
        discoverText = (TextView) findViewById(R.id.discover_text);
        mineText = (TextView) findViewById(R.id.mine_text);

        homepageLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        discoverLayout.setOnClickListener(this);
        mineLayout.setOnClickListener(this);
    }

    private void setTabSelection(int index){
        // 每次选中之前先清除掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所以的Fragment，以防止有多个Fragment显示在界面上
        hideFragment(transaction);
        switch (index){
            case 0:
                homepageImage.setImageResource(R.mipmap.homepage_select);
                homepageText.setTextColor(Color.parseColor("#1296DB"));
                // 如果homepageFragment为空，则创建一个添加到界面上
                if(mapFragment == null){
                    mapFragment = new MapFragment();
                    transaction.add(R.id.fragment_container, mapFragment);
                }else{
                    // 如果不为空则直接将它显示出来
                    transaction.show(mapFragment);
                }
                break;
            case 1:
                shareImage.setImageResource(R.mipmap.share_select);
                shareText.setTextColor(Color.parseColor("#1296DB"));
                if(shareFragment == null){
                    shareFragment = new TraceListFragment();
                    transaction.add(R.id.fragment_container, shareFragment);
                }else {
                    transaction.show(shareFragment);
                }
                break;
            case 2:
                discoverImage.setImageResource(R.mipmap.discover_select);
                discoverText.setTextColor(Color.parseColor("#1296DB"));
                if(discoverFragment == null){
                    discoverFragment = new QuestionnaireFragment();
                    transaction.add(R.id.fragment_container, discoverFragment);
                }else {
                    transaction.show(discoverFragment);
                }
                Intent intent = new Intent();
                intent.setAction(REFRESH_ACTION);
                Log.i("dongsiyuansendBroadcast", "sendBroadcast: ");
                sendBroadcast(intent);
                break;
            case 3:
                mineImage.setImageResource(R.mipmap.mine_select);
                mineText.setTextColor(Color.parseColor("#1296DB"));
                if(mineFragment == null){
                    mineFragment = new MineFragment();
                    transaction.add(R.id.fragment_container, mineFragment);
                }else {
                    transaction.show(mineFragment);
                }
                break;
        }
        transaction.commit();
    }

    private void clearSelection(){
        homepageImage.setImageResource(R.mipmap.homepage_unselect);
        homepageText.setTextColor(Color.parseColor("#82858b"));
        shareImage.setImageResource(R.mipmap.share_unselect);
        shareText.setTextColor(Color.parseColor("#82858b"));
        discoverImage.setImageResource(R.mipmap.discover_unselect);
        discoverText.setTextColor(Color.parseColor("#82858b"));
        mineImage.setImageResource(R.mipmap.mine_unselect);
        mineText.setTextColor(Color.parseColor("#82858b"));
    }

    private void hideFragment(FragmentTransaction transaction){
        if(mapFragment != null){
            transaction.hide(mapFragment);
        }
        if(shareFragment != null){
            transaction.hide(shareFragment);
        }
        if(discoverFragment != null){
            transaction.hide(discoverFragment);
        }
        if(mineFragment != null){
            transaction.hide(mineFragment);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.homepage_layout:
                setTabSelection(0);
                break;
            case R.id.trace_layout:
                setTabSelection(1);
                break;
            case R.id.questionnaire_layout:
                setTabSelection(2);
                break;
            case R.id.mine_layout:
                setTabSelection(3);
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

        //    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
////            exit();
//            return true;
//        } else {
//            return super.dispatchKeyEvent(event);
//        }
//    }
    public void exit(){
        //退出提醒对话框
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.tip));
        if(Common.isRecording) {
            builder.setMessage(getResources().getString(R.string.exitdlg0));
        } else {
            builder.setMessage(getResources().getString(R.string.exitdlg));
        }
        builder.setNegativeButton(getResources().getString(R.string.cancl),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.exit),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                Common.sendOffline(Common.getDeviceId(getApplicationContext()),getApplicationContext());
                AppManager.getAppManager().AppExit(getApplicationContext());
            }
        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("HomePage", "onDestroy");
    }
}
