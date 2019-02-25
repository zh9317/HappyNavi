package com.trackersurvey.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.sip.SipSession;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.trackersurvey.bean.GroupInfoData;
import com.trackersurvey.happynavi.GroupInfoActivity;
import com.trackersurvey.happynavi.R;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.GsonHelper;
import com.trackersurvey.util.MyImageLoader;
import com.trackersurvey.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.trackersurvey.util.UrlHeader.BASE_URL_NEW;

/**
 * Created by zh931 on 2018/5/21.
 */

public class GroupAdapter extends BaseAdapter implements ListView.OnScrollListener {
    private Context                   context;
    private LayoutInflater            mInflater;
    public  ArrayList<GroupInfoData>  groups;
    private MyImageLoader             mLoader;
    public  HashMap<Integer, Integer> visiblecheck;//用来记录是否显示checkBox
    public  HashMap<Integer, Boolean> ischeck;
    public  List<Integer>             selectid;
    public  boolean                   isMulChoice;

    private TextView txtcount;
    private String   handleType;    //加群、退群
    private int      mStart, mEnd;//滑动结束后屏幕上可见的item起止postion
    public static String[] URLArray;//保存所有图片的url
    public static String[] IconName;//保存所有图片的名字

    private boolean isFirstShow;//是否是第一次启动

    private RefreshListener refreshListener;

    private String token;

    public GroupAdapter(Context context, TextView txtcount,
                        ArrayList<GroupInfoData> groups, String handleType, ListView listView, String token) {
        this.context = context;
        this.txtcount = txtcount;
        this.groups = groups;
        this.handleType = handleType;
        this.token = token;
        mInflater = LayoutInflater.from(context); //(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLoader = new MyImageLoader(listView);
        URLArray = new String[groups.size()];
        IconName = new String[groups.size()];
        for (int i = 0; i < URLArray.length; i++) {
            IconName[i] = groups.get(i).getGroupPicUrl().substring(8);       // "/images/XinLab.jpg"
            URLArray[i] = BASE_URL_NEW + groups.get(i).getGroupPicUrl() + "?token=" + token;

            Log.i("dongsiyuanURLArray", "GroupAdapter: " + URLArray[i] + " " + IconName[i]);
        }
        //为listview设置滑动监听
        listView.setOnScrollListener(this);
        isFirstShow = true;
        //mView = new HashMap<Integer, View>();
        visiblecheck = new HashMap<Integer, Integer>();
        ischeck = new HashMap<Integer, Boolean>();
        selectid = new ArrayList<Integer>();
        if (isMulChoice) {
            for (int i = 0; i < groups.size(); i++) {
                ischeck.put(i, false);
                visiblecheck.put(i, CheckBox.VISIBLE);
            }
        } else {
            for (int i = 0; i < groups.size(); i++) {
                ischeck.put(i, false);
                visiblecheck.put(i, CheckBox.INVISIBLE);
            }
        }
    }

    public void setGroups(ArrayList<GroupInfoData> groups) {
        this.groups = groups;
        URLArray = new String[groups.size()];
        IconName = new String[groups.size()];
        for (int i = 0; i < URLArray.length; i++) {
            IconName[i] = groups.get(i).getGroupPicUrl().substring(8);       // "/images/XinLab.jpg"
            URLArray[i] = BASE_URL_NEW + groups.get(i).getGroupPicUrl() + "?token=" + token;

            Log.i("dongsiyuanURLArray", "GroupAdapter: " + URLArray[i] + " " + IconName[i]);
        }
    }

    public void refresh(boolean isMulChoice, boolean isNew, ArrayList<GroupInfoData> groups) {
        this.isMulChoice = isMulChoice;
        this.selectid.clear();
        if (isNew) {
            this.groups = groups;

        }


        this.visiblecheck.clear();
        this.ischeck.clear();

        if (isMulChoice) {
            for (int i = 0; i < groups.size(); i++) {
                this.ischeck.put(i, false);
                this.visiblecheck.put(i, CheckBox.VISIBLE);
            }
        } else {
            for (int i = 0; i < groups.size(); i++) {
                this.ischeck.put(i, false);
                this.visiblecheck.put(i, CheckBox.INVISIBLE);
            }
        }

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return groups.size();//不能使用默认返回
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return groups.get(position);//不能使用默认返回
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder = null;
        //View view = mView.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.group_list_item, null);
            holder.groupPhoto = (ImageView) convertView.findViewById(R.id.grouplistitem_grouppic);
            holder.groupName = (TextView) convertView.findViewById(R.id.tv_itemgroupname);
            holder.groupDetail = (TextView) convertView.findViewById(R.id.tv_itemgroupdetail);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.grouplistitem_check);
            // 为view设置标签
            convertView.setTag(holder);

        } else {

            // 取出holder
            holder = (ViewHolder) convertView.getTag();

        }
//        String url = groups.get(position).getPhotoUrl();
        String url = BASE_URL_NEW + groups.get(position).getGroupPicUrl() + "?token=" + token;
        Log.i("dongsiyuanURLArray", "GroupAdapter: url "  + url);
//        String url = "http://219.218.118.176:8090/Image/711.jpg";
        holder.groupPhoto.setTag(url);
        mLoader.showImageIfExist(holder.groupPhoto, url);
        holder.groupName.setText(groups.get(position).getGroupName());
        holder.groupDetail.setText(groups.get(position).getGroupDescription());
        holder.checkbox.setVisibility(visiblecheck.get(position));
        holder.checkbox.setChecked(ischeck.get(position));
        final CheckBox chbox = holder.checkbox;
        final int pos = position;
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isMulChoice) {
                    if (chbox.isChecked()) {
                        chbox.setChecked(false);
                        ischeck.put(pos, false);
                        for (int i = 0; i < selectid.size(); i++) {
                            if (selectid.get(i) == pos) {
                                selectid.remove(i);
                            }
                        }
                    } else {
                        chbox.setChecked(true);
                        ischeck.put(pos, true);
                        selectid.add(pos);
                    }
                    txtcount.setText(context.getResources().getString(R.string.totaltxt1) + selectid.size() + context.getResources().getString(R.string.totaltxt2));
                } else {
                    //Toast.makeText(context, "点击了"+array.get(position), Toast.LENGTH_LONG).show();
                    //Gson gson=new Gson();
                    if (!Common.isNetConnected) {
                        ToastUtil.show(context, context.getResources().getString(R.string.tips_netdisconnect));
                        return;
                    }
                    String groupinfo = GsonHelper.toJson(groups.get(pos));
                    Log.i("dongsiyuanGroupinfo", "onClick: " + groups.get(pos));

//                    if (refreshListener != null) {
//                        refreshListener.clickRefresh();
//                    }
                    Intent intent = new Intent();
                    intent.putExtra("handletype", handleType);
                    intent.putExtra("groupinfo", groupinfo);

                    intent.setClass(context, GroupInfoActivity.class);
                    context.startActivity(intent);
                }
            }

        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub


                return false;
            }

        });
        //mView.put(position, view);


        return convertView;
    }

    public HashMap<Integer, Integer> getVisible() {
        return visiblecheck;
    }

    public HashMap<Integer, Boolean> getIsCheck() {
        return ischeck;
    }

    public List<Integer> getSelected() {
        return selectid;
    }

    public boolean getIsMulti() {
        return isMulChoice;
    }

    public final class ViewHolder {
        ImageView groupPhoto;
        TextView  groupName;
        TextView  groupDetail;
        CheckBox  checkbox;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
        if (scrollState == SCROLL_STATE_IDLE) {//滑动结束
            //加载图片
            if (URLArray.length > 0 && IconName.length > 0) {
                mLoader.loadImagesNonScroll(mStart, mEnd);
            }
        } else {//滑动时停止加载，防止滑动时由于异步任务重绘UI导致的卡顿
            mLoader.cancelAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
        if (isFirstShow && visibleItemCount > 0) {
            if (URLArray.length > 0 && IconName.length > 0) {
                mLoader.loadImagesNonScroll(mStart, mEnd);
            }
            isFirstShow = false;
        }
    }

    public interface RefreshListener {
        void clickRefresh();
    }

    public void setRefreshListener(RefreshListener listener) {
        refreshListener = listener;
    }
}
