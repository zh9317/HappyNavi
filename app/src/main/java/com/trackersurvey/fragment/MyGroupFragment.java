package com.trackersurvey.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trackersurvey.adapter.GroupAdapter;
import com.trackersurvey.bean.GroupInfoData;
import com.trackersurvey.happynavi.R;
import com.trackersurvey.httpconnection.PostGroupInfo;
import com.trackersurvey.httpconnection.PostJoinOrExitGroup;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.CustomDialog;
import com.trackersurvey.util.GsonHelper;
import com.trackersurvey.util.PullToRefreshView;
import com.trackersurvey.util.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zh931 on 2018/5/21.
 */

public class MyGroupFragment extends Fragment implements View.OnClickListener, PullToRefreshView.OnHeaderRefreshListener {
    private       RelativeLayout           layout;//长按弹出的底部菜单
    private       Button                   cancel;
    private       Button                   exit;
    private       PullToRefreshView        mPullToRefreshView;
    private       ListView                 groupList;
    private       TextView                 selectedcount;
    private       TextView                 tiptxt;
    //private TextView refreshtip;
    private       Context                  context;
    private       GroupAdapter             mAdapter;
    private       ArrayList<GroupInfoData> groups                 = new ArrayList<GroupInfoData>();
    private       ProgressDialog           proDialog              = null;
    private       String                   url_GetMyGroup         = null;
    private       String                   url_ExitGroup          = null;
    private       boolean                  isFirstCreated         = true;
    private       RefreshBroadcastReciver  refreshReciver;
    private final String                   MYGROUPREFRESH_ACTION  = "android.intent.action.MYGROUPREFRESH_RECEIVER";
    private final String                   ALLGROUPREFRESH_ACTION = "android.intent.action.ALLGROUPREFRESH_RECEIVER";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_group, null);
        context = getActivity();
        layout = (RelativeLayout) view.findViewById(R.id.mygroup_relative);
        cancel = (Button) view.findViewById(R.id.cancel);

        exit = (Button) view.findViewById(R.id.exitgroup);
        cancel.setOnClickListener(this);
        exit.setOnClickListener(this);

        mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.main_pull_refresh_view);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        groupList = (ListView) view.findViewById(R.id.listview_mygroup);
        groupList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                showMenu(true, false);

                return true;
            }

        });
        selectedcount = (TextView) view.findViewById(R.id.txtcount);
        tiptxt = (TextView) view.findViewById(R.id.tip);
        tiptxt.setOnClickListener(this);
        //refreshtip=(TextView)view.findViewById(R.id.refreshtip);
        refreshReciver = new RefreshBroadcastReciver();
        IntentFilter pullFilter = new IntentFilter();
        pullFilter.addAction(MYGROUPREFRESH_ACTION);
        context.registerReceiver(refreshReciver, pullFilter);


        showDialog(getResources().getString(R.string.tip), getResources().getString(R.string.tips_initgroup));

        if (Common.url != null && !Common.url.equals("")) {

        } else {
            Common.url = getResources().getString(R.string.url);
        }
        url_GetMyGroup = Common.url + "group.aspx";
        url_ExitGroup = Common.url + "group.aspx";
        init();
        return view;
    }

    private void init() {
        PostGroupInfo groupThread = new PostGroupInfo(handler, url_GetMyGroup, Common.getUserID(context), Common.getDeviceId(context), "MyGroups");
        groupThread.start();


        
    }

    private class RefreshBroadcastReciver extends BroadcastReceiver {
        private RefreshBroadcastReciver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            init();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            switch (msg.what) {
                case 0://获取轨迹列表成功
                    dismissDialog();
                    if (msg.obj != null) {
                        String groupStr = msg.obj.toString().trim();
                        int lastsize = groups.size();
                        groups = (ArrayList<GroupInfoData>) GsonHelper.parseJsonToList(groupStr, GroupInfoData.class);
                        if (isFirstCreated) {
                            Log.i("trailadapter", "groupsize:" + groups.size());
                            mAdapter = new GroupAdapter(context, selectedcount, groups, "quit", groupList);
                            groupList.setAdapter(mAdapter);
                            isFirstCreated = false;

                        } else {


                            showMenu(false, true);

                        }
                        if (groups.size() == 0) {
                            tiptxt.setVisibility(View.VISIBLE);
                            tiptxt.setText(R.string.nojoinedgroup);
                        } else {
                            tiptxt.setVisibility(View.INVISIBLE);
                        }
                        //refreshtip.setVisibility(View.GONE);
                        mPullToRefreshView.onHeaderRefreshComplete("更新于:" + new Date().toLocaleString());
                    }
                    break;
                case 1://获取列表失败
                    dismissDialog();
                    if (groups.size() == 0) {
                        tiptxt.setVisibility(View.VISIBLE);
                        tiptxt.setText(R.string.trytorefresh);
                    } else {
                        tiptxt.setVisibility(View.INVISIBLE);
                    }
                    Toast.makeText(context, getResources().getString(R.string.tips_postfail), Toast.LENGTH_SHORT).show();
                    mPullToRefreshView.onHeaderRefreshComplete("更新失败，请稍后再试");
                    break;
                case 4://退群成功
                    dismissDialog();
                    init();
                    //				Intent intent = new Intent();
                    //		        intent.setAction(ALLGROUPREFRESH_ACTION);
                    //		        context.sendBroadcast(intent);
                    ToastUtil.show(context, getResources().getString(R.string.tips_quitgroupsuccess));
                    break;
                case 5://退群失败
                    dismissDialog();
                    ToastUtil.show(context, getResources().getString(R.string.tips_quitgroupfail));
                    break;
                case 10://网络错误
                    dismissDialog();
                    mPullToRefreshView.onHeaderRefreshComplete("更新失败，请检查网络");
                    ToastUtil.show(context, getResources().getString(R.string.tips_netdisconnect));
                    if (groups.size() == 0) {
                        tiptxt.setVisibility(View.VISIBLE);
                        tiptxt.setText(R.string.trytorefresh);
                    } else {
                        tiptxt.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 11://网络错误
                    dismissDialog();
                    ToastUtil.show(context, getResources().getString(R.string.tips_netdisconnect));
                    break;
            }
        }
    };

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        // TODO Auto-generated method stub
        init();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tip://点此刷新
                showDialog(getResources().getString(R.string.tip), getResources().getString(R.string.tips_initgroup));
                init();
                break;
            case R.id.cancel:
                showMenu(false, false);
                break;
            case R.id.exitgroup:
                final List<Integer> selectid = mAdapter.getSelected();
                final int size = selectid.size();
                if (size > 0) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(context);
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
                            showDialog(getResources().getString(R.string.tip), getResources().getString(R.string.tips_handling));
                            ArrayList<String> tobeExitID = new ArrayList<String>();

                            for (int i = 0; i < size; i++) {
                                tobeExitID.add(groups.get(selectid.get(i)).getGroupID());
                            }
                            String tobeExit = GsonHelper.toJson(tobeExitID);
                            Log.i("trailadapter", "tobeexit:" + tobeExit);
                            PostJoinOrExitGroup exitThread = new PostJoinOrExitGroup(handler, url_ExitGroup,
                                    Common.getUserId(context), tobeExit,
                                    Common.getDeviceId(context), "QuitGroups");
                            exitThread.start();
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();

                } else {
                    ToastUtil.show(context, getResources().getString(R.string.tips_pleasechoosegroup));
                }
                break;
        }

    }

    public void showMenu(boolean isMulChoice, boolean isNew) {
        mAdapter.refresh(isMulChoice, isNew, groups);

        if (isMulChoice) {
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
        selectedcount.setText("");

        mAdapter.notifyDataSetChanged();

    }

    /**
     * 显示进度条对话框
     */
    public void showDialog(String title, String message) {
        if (proDialog == null)
            proDialog = new ProgressDialog(context);
        proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        proDialog.setIndeterminate(false);
        proDialog.setCancelable(true);
        proDialog.setTitle(title);
        proDialog.setMessage(message);
        proDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (proDialog != null) {
            proDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (null != refreshReciver) {
            context.unregisterReceiver(refreshReciver);
        }
    }
}
