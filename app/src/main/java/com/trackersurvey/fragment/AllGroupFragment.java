package com.trackersurvey.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trackersurvey.adapter.GroupAdapter;
import com.trackersurvey.bean.GroupInfoData;
import com.trackersurvey.happynavi.GroupInfoActivity;
import com.trackersurvey.happynavi.R;
import com.trackersurvey.http.DownloadGroupList;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.httpconnection.PostGroupInfo;
import com.trackersurvey.httpconnection.PostJoinOrExitGroup;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.GsonHelper;
import com.trackersurvey.util.PullToRefreshView;
import com.trackersurvey.util.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zh931 on 2018/5/21.
 */

public class AllGroupFragment extends Fragment implements View.OnClickListener, PullToRefreshView.OnHeaderRefreshListener {

    private RelativeLayout    layout;//长按弹出的底部菜单
    private ImageButton       search;
    private Button            cancel;
    private Button            join;
    private PullToRefreshView mPullToRefreshView;
    private ListView          groupList;
    private TextView          selectedcount;
    //private TextView tiptxt;
    private EditText          et_search;

    private Context                  context;
    private GroupAdapter             mAdapter;
    private ArrayList<GroupInfoData> groups          = new ArrayList<GroupInfoData>();
    private ProgressDialog           proDialog       = null;
    private String                   url_GetAllGroup = null;
    private String                   url_JoinGroup   = null;
    private String                   key             = null;
    private boolean                  isFirstCreated  = true;

    private       RefreshBroadcastReciver refreshReciver;
    private final String                  ALLGROUPREFRESH_ACTION = "android.intent.action.ALLGROUPREFRESH_RECEIVER";
    private final String                  MYGROUPREFRESH_ACTION  = "android.intent.action.MYGROUPREFRESH_RECEIVER";

    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_all_group, null);

        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);

        context = getActivity();
        layout = (RelativeLayout) view.findViewById(R.id.allgroup_relative);
        cancel = (Button) view.findViewById(R.id.cancel);
        search = (ImageButton) view.findViewById(R.id.bt_search);
        join = (Button) view.findViewById(R.id.joingroup);
        et_search = (EditText) view.findViewById(R.id.et_search);
        search.setOnClickListener(this);
        cancel.setOnClickListener(this);
        join.setOnClickListener(this);

        mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.main_pull_refresh_view);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        groupList = (ListView) view.findViewById(R.id.listview_allgroup);
        groupList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                showMenu(true, false);

                return true;
            }

        });
        selectedcount = (TextView) view.findViewById(R.id.txtcount);
        //tiptxt=(TextView)view.findViewById(R.id.tip);
        //tiptxt.setOnClickListener(this);

        refreshReciver = new RefreshBroadcastReciver();
        IntentFilter pullFilter = new IntentFilter();
        pullFilter.addAction(ALLGROUPREFRESH_ACTION);
        context.registerReceiver(refreshReciver, pullFilter);


        if (Common.url != null && !Common.url.equals("")) {

        } else {
            Common.url = getResources().getString(R.string.url);
        }
        url_GetAllGroup = Common.url + "group.aspx";
        url_JoinGroup = Common.url + "group.aspx";
        //init();
        return view;
    }

    void init(String key) {
        if (key == null || key.equals("")) {
            return;
        }
        DownloadGroupList downloadGroupList = new DownloadGroupList(sp.getString("token", ""), "1", "100", key);
        downloadGroupList.requestHttpData(new ResponseData() {
            @Override
            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                if (isSuccess) {
                    Log.i("dongsiyuanGroupInfoData", "onResponseData: code :" + code);
                    if (code.equals("0")) {
                        groups = (ArrayList<GroupInfoData>) responseObject;
                        for (int i = 0; i < groups.size(); i++) {
                            Log.i("dongsiyuanGroupInfoData", "onResponseData: " + groups.get(i).toString());
                        }
                    }

                    // 通知adapter更新
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                }
            }
        });

//        PostGroupInfo groupThread = new PostGroupInfo(handler, url_GetAllGroup, Common.getUserId(context), Common.getDeviceId(context), "Search", key);
//        groupThread.start();
    }

    private class RefreshBroadcastReciver extends BroadcastReceiver {

        private RefreshBroadcastReciver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            init(key);
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
                    if (isFirstCreated) {
                        mAdapter = new GroupAdapter(context, selectedcount, groups, "join", groupList, sp.getString("token", ""));
                        groupList.setAdapter(mAdapter);
//                        mAdapter.setRefreshListener(new GroupAdapter.RefreshListener() {
//                            @Override
//                            public void clickRefresh() {
//                                Intent intent = new Intent(getContext(), GroupInfoActivity.class);
//                                startActivityForResult(intent, 1);
//                            }
//                        });
                        isFirstCreated = false;
                    } else {
                        showMenu(false, true);
                    }
                    if (groups.size() == 0) {
                        ToastUtil.show(context, getResources().getString(R.string.tips_search_nogroup));
                    }
                    mPullToRefreshView.onHeaderRefreshComplete("更新于:" + new Date().toLocaleString());

                    Intent intent = new Intent();
                    intent.setAction(MYGROUPREFRESH_ACTION);
                    Log.i("dongsiyuansendBroadcast", "sendBroadcast: ");
                    context.sendBroadcast(intent);
//                    if (msg.obj != null) {
//                        Log.i("dongsiyuanGroupInfoData", "onResponseData: msg.obj");
//
//                        String groupStr = msg.obj.toString().trim();
//                        int lastsize = groups.size();
//                        groups = (ArrayList<GroupInfoData>) GsonHelper.parseJsonToList(groupStr, GroupInfoData.class);
//                        if (isFirstCreated) {
//                            mAdapter = new GroupAdapter(context, selectedcount, groups, "join", groupList);
//                            groupList.setAdapter(mAdapter);
//                            isFirstCreated = false;
//                        } else {
//                            showMenu(false, true);
//                        }
//                        if (groups.size() == 0) {
//                            ToastUtil.show(context, getResources().getString(R.string.tips_search_nogroup));
//                        }
//                        //				    else{
//                        //						tiptxt.setVisibility(View.INVISIBLE);
//                        //					}
//                        //refreshtip.setVisibility(View.GONE);
//                        mPullToRefreshView.onHeaderRefreshComplete("更新于:" + new Date().toLocaleString());
//                    }
                    break;
                case 1://获取列表失败
                    dismissDialog();
                    Toast.makeText(context, getResources().getString(R.string.tips_postfail), Toast.LENGTH_SHORT).show();
                    mPullToRefreshView.onHeaderRefreshComplete("更新失败，请稍后再试");
                    break;
                case 4://加群申请发送成功，待批复
                    dismissDialog();

                    ToastUtil.show(context, getResources().getString(R.string.tips_applygroupsuccess));
                    break;
                case 5://加群失败
                    dismissDialog();
                    ToastUtil.show(context, getResources().getString(R.string.tips_applygroupfail));
                    break;
                case 10://网络错误
                    dismissDialog();
                    mPullToRefreshView.onHeaderRefreshComplete("更新失败，请检查网络");
                    ToastUtil.show(context, getResources().getString(R.string.tips_netdisconnect));
                    //				if(groups.size()==0){
                    //					tiptxt.setVisibility(View.VISIBLE);
                    //					tiptxt.setText(R.string.trytorefresh);
                    //				}else{
                    //					tiptxt.setVisibility(View.INVISIBLE);
                    //				}
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
        init(key);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_search:
                //ToastUtil.show(context, et_search.getText().toString());
                key = et_search.getText().toString().trim();
                if (key == null || key.equals("")) {
                    ToastUtil.show(context, getResources().getString(R.string.tips_search_cannotnull));
                    return;
                } else {
                    showDialog(getResources().getString(R.string.tip), getResources().getString(R.string.tips_searching));

                    init(key);
                }
                break;
            //		case R.id.tip://点此刷新
            //			showDialog(getResources().getString(R.string.tip), getResources().getString(R.string.tips_initgroup));
            //			init();
            //			break;
            case R.id.cancel:
                showMenu(false, false);
                break;
            case R.id.joingroup:
                final List<Integer> selectid = mAdapter.getSelected();
                final int size = selectid.size();
                if (size > 0) {
                    showDialog(getResources().getString(R.string.tip), getResources().getString(R.string.tips_handling));
                    ArrayList<String> tobeJoinedID = new ArrayList<String>();

                    for (int i = 0; i < size; i++) {
//                        tobeJoinedID.add(groups.get(selectid.get(i)).getGroupID());
                    }
                    String tobeJoined = GsonHelper.toJson(tobeJoinedID);
                    Log.i("trailadapter", "tobeJoined:" + tobeJoined);
                    PostJoinOrExitGroup joinThread = new PostJoinOrExitGroup(handler, url_JoinGroup,
                            Common.getUserId(context), tobeJoined,
                            Common.getDeviceId(context), "JoinGroups");
                    joinThread.start();
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
