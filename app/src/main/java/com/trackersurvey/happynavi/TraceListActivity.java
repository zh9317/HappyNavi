package com.trackersurvey.happynavi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.adapter.TraceListAdapter;
import com.trackersurvey.db.MyTraceDBHelper;
import com.trackersurvey.http.DeleteTraceRequest;
import com.trackersurvey.http.UpLoadGpsRequest;
import com.trackersurvey.model.GpsData;
import com.trackersurvey.model.StepData;
import com.trackersurvey.model.TraceData;
import com.trackersurvey.bean.TraceListItemData;
import com.trackersurvey.http.DownLoadTraceList;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.util.AppManager;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.CustomDialog;
import com.trackersurvey.util.GsonHelper;
import com.trackersurvey.util.PullToRefreshView;
import com.trackersurvey.util.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TraceListActivity extends BaseActivity implements View.OnClickListener, PullToRefreshView.OnHeaderRefreshListener {

    //private RecyclerView traceListRv;
    private ListView traceList;
    private TextView titleTv;
    private TextView titleRightTv;
    private RelativeLayout menuLayout;//长按弹出的底部菜单
    private Button cancel;//取消
    private Button downupload;//备份
    private Button delete;//删除
    private TextView tv_count;//显示选了几条轨迹
    private TextView tv_tip;//没有轨迹时提示用户
    //private TraceListAdapterRe adapter;//list适配器
    private TraceListAdapter adapter2;
    private PullToRefreshView mPullToRefreshView;//下拉刷新控件
    private MyTraceDBHelper helper = null;
    private RefreshBroadcastReciver refreshReceiver;

    private ArrayList<TraceListItemData> traceItems = new ArrayList<TraceListItemData>();
    private ArrayList<TraceData> trace_Local = new ArrayList<TraceData>();
    private ArrayList<TraceData> trace_Cloud = new ArrayList<TraceData>();
    private ArrayList<StepData> steps_Local = new ArrayList<StepData>();
    private ArrayList<StepData> steps_Cloud = new ArrayList<StepData>();
    private ArrayList<StepData> steps_Both = new ArrayList<StepData>();
    private ProgressDialog proDialog = null;

    private int downloadCount = 0;//用于标识是否所选轨迹均下载完毕
    private String userID,deviceID;
    private boolean isFirstCreateAdatper = true;

    private String URL_ENDTRAIL=null;
    private String URL_GPSDATA=null;
    private String URL_GETTRAIL=null;
    private  final String REFRESH_ACTION="android.intent.action.REFRESH_RECEIVER";
    private int interestNum = 0;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_list);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        AppManager.getAppManager().addActivity(this);
        traceList = (ListView)findViewById(R.id.listview_trace);
        //标题
        titleTv = findViewById(R.id.title_text);
        titleTv.setText(getResources().getString(R.string.mytrace));
        titleRightTv = findViewById(R.id.title_right_text);
        titleRightTv.setVisibility(View.GONE);
        //traceListRv = findViewById(R.id.trace_list_rv);

        mPullToRefreshView = findViewById(R.id.pull_refresh_view_tracelist);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        menuLayout = (RelativeLayout)findViewById(R.id.relative_tracemenu);
        cancel = (Button) findViewById(R.id.tracelist_cancel);
        downupload = (Button) findViewById(R.id.tracelist_downupload);
        delete = (Button) findViewById(R.id.tracelist_delete);
        tv_count = (TextView) findViewById(R.id.tracelist_txtcount);
        tv_tip = (TextView) findViewById(R.id.tracelist_tip);
        cancel.setOnClickListener(this);
        downupload.setOnClickListener(this);
        delete.setOnClickListener(this);
        tv_tip.setOnClickListener(this);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        helper = new MyTraceDBHelper(this);
        // 长按显示操作轨迹列表的菜单，包括轨迹备份和删除
//        traceListRv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                showMenu(true, true);
//                return true;
//            }
//        });
        traceList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showMenu(true, false);
                return true;
            }
        });
        refreshReceiver = new RefreshBroadcastReciver();
        IntentFilter filter=new IntentFilter();//生成一个IntentFilter对象
        filter.addAction(REFRESH_ACTION);
        registerReceiver(refreshReceiver, filter);
        if (proDialog == null)
            proDialog = new ProgressDialog(this);

        if(Common.url != null && !Common.url.equals("")){

        }else{
            Common.url = getResources().getString(R.string.url);
        }
        URL_ENDTRAIL = Common.url+"reqTraceNo.aspx";
        URL_GPSDATA = Common.url+"upLocation.aspx";
        URL_GETTRAIL = Common.url+"reqTraceHistory.aspx";
        userID = Common.getUserID(this);
        deviceID = Common.getDeviceId(this);
        init();
    }

    private void init(){
        initLocalTrace();
        if(Common.isNetConnected){//有网络状态下才请求云端
            showDialog(getResources().getString(R.string.tips_dlgtle_init),
                    getResources().getString(R.string.tips_dlgmsg_inittracelist));
            initCloudTrace();
        } else {
            initBothTrace();
            mPullToRefreshView.onHeaderRefreshComplete("更新失败，请检查网络");
        }
    }

    private void initLocalTrace() {
        Log.i("TraceList", "userID" + userID);
        trace_Local = helper.getallTrail(userID);
        if(trace_Cloud != null) {
            for(int i = 0; i < trace_Local.size(); i++) {
                for(int j = 0; j < trace_Cloud.size();j++) {
                    if(trace_Local.get(i).getTraceID() == trace_Cloud.get(j).getTraceID()) {
                        helper.updatetrail(trace_Cloud.get(j),
                                trace_Cloud.get(j).getTraceID(), trace_Cloud.get(j).getUserID());
                        trace_Local = helper.getallTrail(userID);
                    }
                }
            }
        }
        steps_Local = helper.getallSteps(userID);
    }

    private void refreshLocalTrace(){
        trace_Local = helper.getallTrail(userID);
        steps_Local = helper.getallSteps(userID);
    }
    private void initCloudTrace(){

        // 测试请求轨迹列表
        DownLoadTraceList downLoadTraceList = new DownLoadTraceList(sp.getString("token",""),
                String.valueOf(1), String.valueOf(100));
        downLoadTraceList.requestHttpData(new ResponseData() {
            @Override
            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                if (isSuccess) {
                    trace_Cloud = (ArrayList<TraceData>) responseObject;
                    Log.i("TraceListActivity", "请求到的轨迹条数："+trace_Cloud.size());
                    for (int i = 0; i < trace_Cloud.size(); i++) {
                        StepData stepData = new StepData();
                        stepData.setUserID(trace_Cloud.get(i).getUserID());
                        stepData.setTraceID(trace_Cloud.get(i).getTraceID());
                        stepData.setSteps(trace_Cloud.get(i).getSteps());
                        steps_Cloud.add(stepData);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog();
                            //Toast.makeText(TraceListActivity.this, "获取轨迹列表成功", Toast.LENGTH_SHORT).show();
                            if (trace_Cloud != null) {
                                initBothTrace();
                            }
                            //adapter.notifyDataSetChanged();
                            mPullToRefreshView.onHeaderRefreshComplete("更新于:"+new Date().toLocaleString());
                        }
                    });
                }
            }
        });

    }
    /**
     * 合并云端和本地轨迹，并分析某条轨迹在本地or云端or二者都有
     * */
    private void initBothTrace() {
        int lastItemsSize = traceItems.size();
        traceItems.clear();
        ArrayList<Long> dealedTraceNo = new ArrayList<Long>();//保存已处理过的轨迹号（其实就是本地的轨迹号！）
        for(int i = 0;i < trace_Local.size();i++){
            long traceNo = trace_Local.get(i).getTraceID();
            dealedTraceNo.add(traceNo);
            boolean isCloud = false;
            for(int j = 0;j < trace_Cloud.size();j++){
                if(traceNo == trace_Cloud.get(j).getTraceID()){//云端本地都有
                    isCloud = true;
                    break;
                }
            }
            TraceListItemData item = new TraceListItemData();
            item.setTrace(trace_Local.get(i));
            item.setLocal(true);
            item.setCloud(isCloud);
            traceItems.add(item);
        }
        for(int j = 0; j < trace_Cloud.size();j++){
            long traceNo = trace_Cloud.get(j).getTraceID();
            boolean isLocal = false;
            for(int k = 0;k<dealedTraceNo.size();k++){
                if(traceNo == dealedTraceNo.get(k).longValue()){//在处理本地轨迹时已处理过该轨迹，跳过
                    isLocal = true;
                    break;
                }
            }
            if(!isLocal){//云端有本地没有
                TraceListItemData item = new TraceListItemData();
                item.setTrace(trace_Cloud.get(j));
                item.setLocal(false);
                item.setCloud(true);
                traceItems.add(item);
            }
        }
        if(traceItems.size() == 0) {
            //没有轨迹，可能原因：1、用户的轨迹都在云端，没联网，固然得不到；2、新用户，没有记录过轨迹。
            //对于这两种情况，分别显示不同的提示性文字，供用户参考
            if(!Common.isNetConnected){//原因1
                tv_tip.setText(getResources().getString(R.string.tips_cloudnotrace_nonet));
            }else{//原因2
                tv_tip.setText(getResources().getString(R.string.tips_localnotrace));
            }
            tv_tip.setVisibility(View.VISIBLE);
            return;
        }else{
            tv_tip.setVisibility(View.INVISIBLE);
        }
        //步数信息操作同上
        steps_Both.clear();
        ArrayList<Long> dealedStepsNo = new ArrayList<Long>();
        for(int i = 0; i < steps_Local.size(); i++){
            steps_Both.add(steps_Local.get(i));
            dealedStepsNo.add(steps_Local.get(i).getTraceID());
        }
        for(int j = 0;j < steps_Cloud.size(); j++){
            long traceNo = steps_Cloud.get(j).getTraceID();
            boolean isOnlyCloud = true;
            for(int k = 0; k < dealedStepsNo.size(); k++){
                if(traceNo == dealedStepsNo.get(k).longValue()){
                    isOnlyCloud = false;
                    break;
                }
            }
            if(isOnlyCloud){
                steps_Both.add(steps_Cloud.get(j));
            }
        }
        Collections.sort(traceItems, new SortByTraceNo());//按时间排序
        if(isFirstCreateAdatper){
            initAdapter();//首次加载时执行
            isFirstCreateAdatper = false;
        }else{
            if(lastItemsSize == traceItems.size()){//数据数量不变，考虑正在记录时轨迹数据的刷新
                //adapter.setDataSource(traceItems, steps_Both);
                //adapter.notifyDataSetChanged();

                adapter2.setDataSource(traceItems, steps_Both);
                adapter2.notifyDataSetChanged();
            }else{//数据数量变化，关闭菜单
                showMenu(false, true);
            }
        }
    }

    private void initAdapter() {
        //adapter = new TraceListAdapterRe(traceItems, steps_Both, this, tv_count);
        //traceListRv.setAdapter(adapter);
        adapter2 = new TraceListAdapter(this, tv_count, traceItems, steps_Both);
        traceList.setAdapter(adapter2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tracelist_cancel:
                showMenu(false, false);
                break;
            case R.id.tracelist_tip:
                init();
                break;
            case R.id.tracelist_delete:
                final List<Integer> selectid = adapter2.getSelected();
                if(selectid.size() > 0){
                    CustomDialog.Builder builder = new CustomDialog.Builder(TraceListActivity.this);
                    builder.setTitle(getResources().getString(R.string.tip));
                    builder.setMessage(getResources().getString(R.string.tips_deletedlgmsg_trace));
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
                            ArrayList<Long> tobedeleteNo=new ArrayList<Long>();//要删除的云端轨迹，打包一次请求

                            for(int i=0;i<selectid.size();i++){
                                TraceListItemData item = traceItems.get(selectid.get(i));
                                Log.i("trailadapter", "删除的轨迹名："+item.getTrace().getTraceName());
                                if(item.isLocal()){//删本地
                                    helper.deleteTrailByTraceNo(item.getTrace().getTraceID(),userID);
                                }
                                if(item.isCloud()){//删云端
                                    tobedeleteNo.add(item.getTrace().getTraceID());
                                }
                            }
                            if(tobedeleteNo.size() > 0){
                                showDialog(getResources().getString(R.string.tip),getResources().getString(R.string.tips_deletedlgmsg));

                                String tobedelete = GsonHelper.toJson(tobedeleteNo);
                                Log.i("trailadapter","云端删除:"+tobedelete);
                                //PostDeleteTrail deletetrail=new PostDeleteTrail(traceHandler, URL_GETTRAIL, userID, tobedelete, deviceID);
                                //deletetrail.start();
                                // 测试删除轨迹
                                DeleteTraceRequest deleteTraceRequest = new DeleteTraceRequest(
                                        sp.getString("Token", ""),
                                        String.valueOf(tobedeleteNo.get(0)));
                                Log.i("TraceList", String.valueOf(tobedeleteNo.get(0)));
                                deleteTraceRequest.requestHttpData(new ResponseData() {
                                    @Override
                                    public void onResponseData(boolean isSuccess, String code, Object responseObject,
                                                               String msg) throws IOException {
                                        if (isSuccess) {
                                            if (code.equals("0")) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dismissDialog();
                                                        init();
                                                        ToastUtil.show(TraceListActivity.this,getResources().getString(R.string.tips_deletesuccess));
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }else{
                                init();
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }else{
                    ToastUtil.show(TraceListActivity.this, getResources().getString(R.string.tips_notraceselected));
                }
                break;
            case R.id.tracelist_downupload://备份  本地->云端  ; 云端->本地
                final List<Integer> choosedid = adapter2.getSelected();
                if(choosedid.size() > 0){
                    showDialog(getResources().getString(R.string.tip),getResources().getString(R.string.tips_downupmsg));

                    ArrayList<TraceData> trace_Upload = new ArrayList<TraceData>();
                    ArrayList<StepData> steps_Upload = new ArrayList<StepData>();
                    int bothSize = 0;//所选的轨迹中云端和本地都有的个数
                    for(int i=0;i<choosedid.size();i++){
                        TraceListItemData item = traceItems.get(choosedid.get(i));
                        if(item.isLocal() && !item.isCloud()){
                            //本地有云端没有，备份到云端
                            TraceData traceData = new TraceData();
                            StepData stepData=new StepData();
                            ArrayList<GpsData> gpsData=new ArrayList<GpsData>();

                            long traceNo = item.getTrace().getTraceID();
                            traceData = item.getTrace();
                            if(traceData.getSportTypes() == 1){
                                stepData=helper.querryformstepsbyTraceNo(traceNo,userID);
                                steps_Upload.add(stepData);
                            }
                            trace_Upload.add(traceData);
                            gpsData=helper.queryfromGpsbytraceID(traceNo,userID);
//                            PostGpsData gpsthread = new PostGpsData(uploadHandler,
//                                    URL_GPSDATA,
//                                    GsonHelper.toJson(gpsData),
//                                    deviceID);
//                            gpsthread.start();
                            UpLoadGpsRequest upLoadGpsRequest = new UpLoadGpsRequest(sp.getString("token",""),
                                    GsonHelper.toJson(gpsData));
                            upLoadGpsRequest.requestHttpData(new ResponseData() {
                                @Override
                                public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                                    if (isSuccess) {
                                        if (code.equals("0")) {
                                            Log.i("ShowTraceFragment","上传位置数据成功");
                                        }
                                    }
                                }
                            });
                        } else if(!item.isLocal() && item.isCloud()){
                            //本地没有，云端有，下载到本地
                            downloadCount++;
                            helper.insertintoTrail(item.getTrace());
                            long traceNo = item.getTrace().getTraceID();
                            if(item.getTrace().getSportTypes() == 1){
                                for(int j = 0;j<steps_Cloud.size();j++){
                                    if(traceNo == steps_Cloud.get(j).getTraceID()){
                                        helper.insertintoSteps(steps_Cloud.get(j));
                                    }
                                }
                            }
                            // 下载轨迹，请求轨迹详细数据
                            // 原来的代码这里调用了下载轨迹接口PostEndTrail

                        }else{
                            bothSize++;
                        }
                    }
                    if(bothSize == choosedid.size()){
                        //所选的轨迹都已备份
                        dismissDialog();
                    }
                    if(trace_Upload.size() > 0){
                        String traceinfo=GsonHelper.toJson(trace_Upload);
                        String stepinfo="";
                        if(steps_Upload.size()>0){
                            stepinfo=GsonHelper.toJson(steps_Upload);
                        }
                        //Log.i("trailadapter","上传的轨迹："+traceinfo+","+stepinfo);
//                        PostEndTrail endTrailThread = new PostEndTrail(uploadHandler,
//                                URL_ENDTRAIL,traceinfo,stepinfo,deviceID); // 2,3
//                        endTrailThread.start();

                    }
                }else{
                    ToastUtil.show(TraceListActivity.this, getResources().getString(R.string.tips_notraceselected));
                }
                break;
        }
    }
    // 用于处理请求轨迹信息、轨迹详细信息、删除轨迹的返回数据
    @SuppressLint("HandlerLeak")
    private Handler traceHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0://获取轨迹列表成功
                    dismissDialog();
                    if(msg.obj!=null){
                        final String[] tracestr = msg.obj.toString().trim().split("!");
                        if(tracestr.length == 2) {
                            Toast.makeText(TraceListActivity.this, "获取轨迹列表成功", Toast.LENGTH_SHORT).show();
                            String trace = tracestr[0];
                            String step = tracestr[1];
                            Log.i("TraceListActivity", "trace:" + trace);
                            Log.i("TraceListActivity", "step:" + step);
                            trace_Cloud = (ArrayList<TraceData>) GsonHelper.parseJsonToList(trace, TraceData.class);
                            steps_Cloud = (ArrayList<StepData>) GsonHelper.parseJsonToList(step,StepData.class);
                            initBothTrace();
                            //adapter.notifyDataSetChanged();
                            mPullToRefreshView.onHeaderRefreshComplete("更新于:"+new Date().toLocaleString());
                        }
                    }
                    break;
                case 1://获取列表失败
                    dismissDialog();
                    initBothTrace();
                    mPullToRefreshView.onHeaderRefreshComplete("更新失败，请稍后再试");
                    Toast.makeText(TraceListActivity.this,R.string.tips_postfail, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    downloadCount--;
                    if(msg.obj!=null){
                        ArrayList<GpsData> gpsData = new ArrayList<GpsData>();
                        gpsData=(ArrayList<GpsData>) GsonHelper.parseJsonToList(msg.obj.toString().trim(), GpsData.class);
                        //Toast.makeText(DrawPath.this, "收到轨迹条数："+trails.size(), Toast.LENGTH_LONG).show();
                        if(gpsData.size()>0) {
                            for(int i=0;i<gpsData.size();i++) {
                                helper.insertintoGpswithDate(gpsData.get(i));
                            }
                            if(downloadCount==0){
                                dismissDialog();
                                //ToastUtil.show(TraceListActivity.this,getResources().getString(R.string.tips_downfinish));
                                showMenu(false, false);
                                init();
                            }
                        } else {
                            ToastUtil.show(TraceListActivity.this,getResources().getString(R.string.tips_downfail_nodata));
                        }
                    } else {
                        ToastUtil.show(TraceListActivity.this,getResources().getString(R.string.tips_downfail_nodata));
                    }
                    break;
                case 3://获取详细失败
                    dismissDialog();

                    ToastUtil.show(TraceListActivity.this,getResources().getString(R.string.tips_postfail));
                    break;
                case 4://删除成功
                    dismissDialog();
                    init();
                    ToastUtil.show(TraceListActivity.this,getResources().getString(R.string.tips_deletesuccess));
                    break;
                case 5://删除失败
                    dismissDialog();
                    init();//也要刷新，因为本地轨迹可能已删除
                    ToastUtil.show(TraceListActivity.this, getResources().getString(R.string.tips_deletefail));
                    break;
                default:
                    dismissDialog();
                    mPullToRefreshView.onHeaderRefreshComplete("更新失败，请检查网络");
                    ToastUtil.show(TraceListActivity.this, getResources().getString(R.string.tips_netdisconnect));
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    // 用于处理请求上传位置和上传轨迹的返回数据
    private Handler uploadHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    dismissDialog();
                    showMenu(false, false);
                    init();

                    //ToastUtil.show(TraceListActivity.this,getResources().getString(R.string.tips_uploadsuccess));
                    break;
                case 3:
                    dismissDialog();
                    ToastUtil.show(TraceListActivity.this,getResources().getString(R.string.tips_uploadfail));
                    break;
                default:
                    dismissDialog();
                    ToastUtil.show(TraceListActivity.this,getResources().getString(R.string.tips_netdisconnect));
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(!isFirstCreateAdatper && Common.isRecording){//刷新本地轨迹,考虑正在记录的轨迹数据的刷新
            initLocalTrace();
            initBothTrace();
        }
        initCloudTrace();//重新下载了更新后的所有轨迹列表文字信息
        refreshLocalTrace();//更新本地GPS_DB数据库里的TRAIL表中的信息
        initBothTrace();//更新轨迹列表信息
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=refreshReceiver){
            unregisterReceiver(refreshReceiver);
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        init();
    }

    public class RefreshBroadcastReciver extends BroadcastReceiver {

        public RefreshBroadcastReciver(){

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            init();
        }
    }

    public void showMenu(boolean isMulChoice,boolean isNew){
        adapter2.setIsMulti(isMulChoice);

        if(isNew){
            adapter2.setDataSource(traceItems, steps_Both);

        }
        adapter2.resetList(isMulChoice);

        if(isMulChoice){
            menuLayout.setVisibility(View.VISIBLE);

        }
        else{
            menuLayout.setVisibility(View.GONE);

        }
        tv_count.setText("");
        adapter2.notifyDataSetChanged();

    }

    private class SortByTraceNo implements Comparator<TraceListItemData> {

        @Override
        public int compare(TraceListItemData lhs, TraceListItemData rhs) {
            long t1 = lhs.getTrace().getTraceID();
            long t2 = rhs.getTrace().getTraceID();
            if(t1 > t2){
                return -1;
            }
            return 1;
        }

    }

    /**
     * 显示进度条对话框
     */
    public void showDialog(String title,String message) {
        if (proDialog == null)
            proDialog = new ProgressDialog(this);
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
}
