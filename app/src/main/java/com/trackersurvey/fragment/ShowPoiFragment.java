package com.trackersurvey.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.trackersurvey.adapter.ListBaseAdapter;
import com.trackersurvey.happynavi.R;
import com.trackersurvey.model.MyCommentModel;
import com.trackersurvey.photoview.SlideListView;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.CustomDialog;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zh931 on 2018/5/20.
 * 用于展示兴趣点列表
 */

public class ShowPoiFragment extends Fragment {
    private        SlideListView                      lView; // 界面的评论列表
    private        PoiEventReceiver                   poiReceiver;
    // 个人相册的数据模型
    //private MyCommentModel ShowTraceFragment.myComment;
    // listView的适配器
    private        ListBaseAdapter                    listAdapter;
    private        Context                            context;
    private        ArrayList<HashMap<String, Object>> items;
    public final   String                             UPDATEUI_ACTION    = "android.intent.action.UPDATEUI_RECEIVER";
    public final   String                             INITADAPTER_ACTION = "android.intent.action.ADAPTER_RECEIVER";
    private static MyCommentModel                     myComment;
    private        SharedPreferences                  sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_show_poi, container, false);

        //        Resources resources = getResources();
        //        Configuration configure = resources.getConfiguration();
        //        DisplayMetrics dm = resources.getDisplayMetrics();
        //        if(TabHost_Main.l==0){
        //            configure.locale = Locale.CHINESE;
        //        }
        //        if(TabHost_Main.l==1){
        //            configure.locale = Locale.ENGLISH;
        //        }
        //        resources.updateConfiguration(configure, dm);

        context = getActivity();
        myComment = new MyCommentModel(context, "mark");

        Common.createFileDir();
        //initModel();
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        items = new ArrayList<HashMap<String, Object>>();
        lView = (SlideListView) view.findViewById(R.id.poi_lv);
        initAdapter();

        poiReceiver = new PoiEventReceiver();
        IntentFilter pullFilter = new IntentFilter();
        pullFilter.addAction(UPDATEUI_ACTION);
        pullFilter.addAction(INITADAPTER_ACTION);
        context.registerReceiver(poiReceiver, pullFilter);
        return view;
    }

    private class PoiEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(UPDATEUI_ACTION)) {
                Log.i("dongsiyuansendBroadcast", "onReceive: sendBroadcast");
                updateUI();
            }
        }

    }


    /**
     * 初始化适配器，设置ListView的item的子组件的监听
     */
    private void initAdapter() {
        listAdapter = new ListBaseAdapter(context, ShowTraceFragment.myComment, items, "mark");

        //		myComment = new MyCommentModel(context, "album");

        //监听删除按钮
        listAdapter.setDeleCommListener(new ListBaseAdapter.DeleCommListener() {
            @Override
            public void clickDelete(String dateTime, int position, long traceID) {
                // TODO Auto-generated method stub
                deleteEvent(dateTime, position, traceID);
            }
        });
        initListView();

    }

    private void initListView() {
        // 我的评论 界面中的ListView
        //				lView.initSlideMode(SlideListView.MOD_RIGHT);

        lView.setAdapter(listAdapter);

        lView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
//                        || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
//                    if ((view.getLastVisiblePosition() == view.getCount() - 1)
//                            && ShowTraceFragment.myComment.cloudMore()) {
//                        Log.i("Eaa", "自动加载评论,size=" + view.getCount());
//                        ShowTraceFragment.myComment.autoAddtoList();
//                    }
//                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        // Log.i("Eaa", "onResume");
        super.onResume();
    }

    /**
     * 弹出对话框让用户选择是否确认删除一条评论
     *
     * @param dateTime
     * @param position
     */
    private void deleteEvent(final String dateTime, final int position, final long traceID) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.tip));
        builder.setMessage(
                getResources().getString(
                        R.string.tips_deletedlgmsg_album));
        builder.setPositiveButton(getResources().getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        deleteComment(dateTime, position, traceID);
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancl),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
        builder.create().show();

    }

    /**
     * 刷新界面
     */
    public void updateUI() {
        listAdapter.setItems(ShowTraceFragment.myComment.getItems());
        listAdapter.notifyDataSetChanged();
        Log.i("dongsiyuanUpdateUI", "ShowPoiFragment:" + ShowTraceFragment.myComment.getItems().toString());

    }

    /**
     * 通知模型删除一条评论
     */
    private void deleteComment(String dateTime, int listPosition, long traceID) {
//        ShowTraceFragment.myComment.deleteComment(dateTime, listPosition);

        Log.i("dongisyuanDelete", "deleteComment: dateTime: " + dateTime + "traceID: " + traceID + "poiID: " + listPosition);
        //token poiID
        ShowTraceFragment.myComment.deletePOI(sp.getString("token", ""), listPosition, dateTime, traceID);

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Log.i("mark", "PoiFragment destory");
        //ShowTraceFragment.myComment.stopModel();
        //ShowTraceFragment.myComment = null;
        listAdapter = null;
        if (null != poiReceiver) {
            context.unregisterReceiver(poiReceiver);
        }
        super.onDestroy();
    }

    public int getItemsNum() {
        int itemsNum = items.size();
        return itemsNum;
    }

    public ArrayList<HashMap<String, Object>> getItems() {
        return items;
    }
}
