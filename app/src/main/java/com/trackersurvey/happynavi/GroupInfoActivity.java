package com.trackersurvey.happynavi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.bean.GroupInfoData;
import com.trackersurvey.http.JoinGroupRequest;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.httpconnection.PostJoinOrExitGroup;
import com.trackersurvey.util.ActivityCollector;
import com.trackersurvey.util.AppManager;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.CustomDialog;
import com.trackersurvey.util.GsonHelper;
import com.trackersurvey.util.MyImageLoader;
import com.trackersurvey.util.TitleLayout;
import com.trackersurvey.util.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;

public class GroupInfoActivity extends BaseActivity {

    private TextView  title;
    private TextView  titleRight;
    private ImageView iv_groupphoto;
    private TextView  tv_groupname;
    private TextView  tv_createman;
    private TextView  tv_managerids;
    private TextView  tv_groupdetail;
    private TextView  tv_membernums;
    private TextView  tv_createtime;

    //private Button GroupButton;
    private       String url_JoinGroup          = null;
    private       String url_ExitGroup          = null;
    private       String handleType             = null;
    private final String ALLGROUPREFRESH_ACTION = "android.intent.action.ALLGROUPREFRESH_RECEIVER";
    private final String MYGROUPREFRESH_ACTION  = "android.intent.action.MYGROUPREFRESH_RECEIVER";

    private MyImageLoader mLoader;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        AppManager.getAppManager().addActivity(this);
        init();
    }

    public void init() {

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);

        title = (TextView) findViewById(R.id.title_text);
        title.setText(getResources().getString(R.string.groupinfo));
        titleRight = findViewById(R.id.title_right_text);
        titleRight.setVisibility(View.VISIBLE);
        iv_groupphoto = (ImageView) findViewById(R.id.iv_groupimage);
        tv_groupname = (TextView) findViewById(R.id.tv_groupname);
        tv_createman = (TextView) findViewById(R.id.tv_createman);
        tv_managerids = (TextView) findViewById(R.id.tv_managerids);
        tv_groupdetail = (TextView) findViewById(R.id.tv_groupdetail);
        tv_membernums = (TextView) findViewById(R.id.tv_membernums);
        tv_createtime = (TextView) findViewById(R.id.tv_createtime);


        //GroupButton = (Button) findViewById(R.id.handlegroup);
        if (Common.url != null && !Common.url.equals("")) {

        } else {
            Common.url = getResources().getString(R.string.url);
        }
        url_ExitGroup = Common.url + "group.aspx";
        url_JoinGroup = Common.url + "group.aspx";
        Intent intent = getIntent();
        handleType = intent.getStringExtra("handletype");
        String groupStr = intent.getStringExtra("groupinfo");
        Log.i("dongisyuanhandleType", "init: " + handleType);
        if (handleType == null || handleType.equals("") ||
                groupStr == null || groupStr.equals("")) {
            ToastUtil.show(this, getResources().getString(R.string.tips_errorversion));
            return;
        }
        if (handleType.equals("join")) {
            titleRight.setText(R.string.joingroup);
        } else {
            titleRight.setText(R.string.exitgroup);
        }
        final GroupInfoData groupInfo = GsonHelper.parseJson(groupStr, GroupInfoData.class);
        Log.i("trailadapter", groupStr);
        if (groupInfo != null) {
            mLoader = new MyImageLoader();
            //            mLoader.loadOneImage(iv_groupphoto, groupInfo.getGroupPicUrl(), groupInfo.getGroupPicUrl().substring(8));
            mLoader.loadOneImage(iv_groupphoto, "http://219.218.118.176:8090/Image/711.jpg", groupInfo.getGroupPicUrl().substring(8));
            tv_groupname.setText(groupInfo.getGroupName());
            tv_createman.setText(groupInfo.getCreateAdminID() + "");
            String[] ManagerIDs = groupInfo.getManagerIDs();
            StringBuffer ManagerIDs_Str = new StringBuffer();
            //            for (int i = 0; i < ManagerIDs.length; i++) {
            //                ManagerIDs_Str.append(ManagerIDs[i]);
            //                ManagerIDs_Str.append(" ");
            //            }
            //            tv_managerids.setText(ManagerIDs_Str);
            tv_groupdetail.setText(groupInfo.getGroupDescription());
            tv_createtime.setText(groupInfo.getCreateTime());
            tv_membernums.setText(groupInfo.getMemberCount() + "");
            titleRight.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (handleType.equals("join")) {
                        ArrayList<String> tobeJoinedID = new ArrayList<String>();
                        //                        tobeJoinedID.add(groupInfo.getGroupID());

                        String tobeJoined = GsonHelper.toJson(tobeJoinedID);
                        Log.i("trailadapter", "tobeJoined:" + tobeJoined);

                        JoinGroupRequest joinGroupRequest = new JoinGroupRequest(sp.getString("token", ""), groupInfo.getGroupID());
                        joinGroupRequest.requestHttpData(new ResponseData() {
                            @Override
                            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                                if (isSuccess) {
                                    Log.i("trailadapterCode", "onResponseData: " + code);
                                    if (code.equals("0")) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.show(GroupInfoActivity.this, getResources().getString(R.string.tips_applygroupsuccess));
                                                Message message = new Message();
                                                message.what = 4;
                                                handler.sendMessage(message);
                                            }
                                        });
                                    }
                                    if (code.equals("100") || code.equals("101")) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(GroupInfoActivity.this, "登录信息过期，请重新登录！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    if (code.equals("310")) {
//                                        ToastUtil.show(GroupInfoActivity.this, getResources().getString(R.string.tips_joinedgroup));
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(GroupInfoActivity.this, getResources().getString(R.string.tips_joinedgroup), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }
                        });

//                        PostJoinOrExitGroup joinThread = new PostJoinOrExitGroup(handler, url_JoinGroup,
//                                Common.getUserId(GroupInfoActivity.this), tobeJoined,
//                                Common.getDeviceId(GroupInfoActivity.this), "JoinGroups");
//                        joinThread.start();
                    } else if (handleType.equals("quit")) {
                        CustomDialog.Builder builder = new CustomDialog.Builder(GroupInfoActivity.this);
                        builder.setTitle(getResources().getString(R.string.tip));
                        builder.setMessage(getResources().getString(R.string.tips_quitgroupdlg_msg));
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

                                ArrayList<String> tobeExitID = new ArrayList<String>();
                                //                                tobeExitID.add(groupInfo.getGroupID());

                                String tobeExit = GsonHelper.toJson(tobeExitID);
                                Log.i("trailadapter", "tobeexit:" + tobeExit);
                                PostJoinOrExitGroup exitThread = new PostJoinOrExitGroup(handler, url_ExitGroup,
                                        Common.getUserId(GroupInfoActivity.this), tobeExit,
                                        Common.getDeviceId(GroupInfoActivity.this), "QuitGroups");
                                exitThread.start();
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();

                    }
                }
            });

        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            switch (msg.what) {
                case 4://
                    if (handleType.equals("join")) {
                        ToastUtil.show(GroupInfoActivity.this, getResources().getString(R.string.tips_applygroupsuccess));

                    } else {
                        Intent intent = new Intent();
                        intent.setAction(ALLGROUPREFRESH_ACTION);
                        sendBroadcast(intent);
                        intent.setAction(MYGROUPREFRESH_ACTION);
                        sendBroadcast(intent);
                        ToastUtil.show(GroupInfoActivity.this, getResources().getString(R.string.tips_success));
                        finish();
                    }
                    break;
                case 5://

                    ToastUtil.show(GroupInfoActivity.this, getResources().getString(R.string.tips_postfail));
                    break;
                case 11://网络错误

                    ToastUtil.show(GroupInfoActivity.this, getResources().getString(R.string.tips_netdisconnect));
                    break;
            }
        }
    };
}
