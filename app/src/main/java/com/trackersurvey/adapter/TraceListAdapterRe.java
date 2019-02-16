package com.trackersurvey.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.trackersurvey.bean.StepData;
import com.trackersurvey.bean.TraceListItemData;
import com.trackersurvey.happynavi.TraceDetailActivity;
import com.trackersurvey.happynavi.R;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.GsonHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zh931 on 2018/5/16.
 */

public class TraceListAdapterRe extends RecyclerView.Adapter<TraceListAdapterRe.TraceViewHolder> {

    private List<TraceListItemData> traceDataList;
    private List<StepData> stepList;
    private Context context;
    private String stepStr = "--"; // intent传参
    public HashMap<Integer, Integer> visiblecheck ;//用来记录是否显示checkBox
    public HashMap<Integer, Boolean> ischeck;
    public List<Integer> selectedIdLList;
    public boolean isMulChoice;
    private TextView txtcount;
    private int currentPosition;

    private OnTraceLongClickListener onTraceLongClickListener;

    int[] imageId = new int[]{R.mipmap.ic_walking,
            R.mipmap.ic_cycling,
            R.mipmap.ic_rollerblading,
            R.mipmap.ic_driving,
            R.mipmap.ic_train,
            R.mipmap.others,
    };

    public TraceListAdapterRe(List<TraceListItemData> traceDataList, List<StepData> stepList, Context context, TextView txtcount) {
        this.traceDataList = traceDataList;
        this.stepList = stepList;
        this.context = context;
        this.txtcount = txtcount;

        visiblecheck = new HashMap<Integer, Integer>();
        ischeck = new HashMap<Integer, Boolean>();
        selectedIdLList = new ArrayList<Integer>();
        if(isMulChoice) {
            for(int i = 0; i < traceDataList.size(); i++) {
                ischeck.put(i, false);
                visiblecheck.put(i, CheckBox.VISIBLE);
            }
        } else {
            for(int i = 0; i < traceDataList.size(); i++) {
                ischeck.put(i, false);
                visiblecheck.put(i, CheckBox.INVISIBLE);
            }
        }
    }

    public void setDataSource(ArrayList<TraceListItemData> traceDataList, ArrayList<StepData> stepList){
        this.traceDataList = traceDataList;
        this.stepList = stepList;
    }

    @Override
    public TraceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trace_list_item, parent, false);
        TraceViewHolder traceViewHolder = new TraceViewHolder(view);
        return traceViewHolder;
    }

    @Override
    public void onBindViewHolder(final TraceViewHolder holder, final int position) {
        currentPosition = position;
        TraceListItemData itemData = traceDataList.get(position);
        // 运动类型
        holder.traceTpyeIv.setImageResource(imageId[itemData.getTrace().getSportTypes()-1]);
        // 是否上传到了服务器
        if (itemData.isCloud()) {
            holder.isCloudIv.setVisibility(View.VISIBLE);
        } else {
            holder.isCloudIv.setVisibility(View.INVISIBLE);
        }
        // 是否存储在本地
        if (itemData.isLocal()) {
            holder.isLocalIv.setVisibility(View.VISIBLE);
        } else {
            holder.isLocalIv.setVisibility(View.INVISIBLE);
        }
        // 轨迹名称
        holder.traceNameTv.setText(itemData.getTrace().getTraceName());
        // 开始时间
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        try {
            date = df.parse(itemData.getTrace().getStartTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.startTimeTv.setText(df.format(date));
        // 距离
        double distance = itemData.getTrace().getDistance();
        holder.distanceTv.setText(Common.transformDistance(distance) + context.getResources().getString(R.string.dis_unit));
        // 耗时
        long duration = itemData.getTrace().getDuration();
        holder.durationTv.setText(Common.transformTime(duration));
        // 兴趣点个数
        holder.poiNumTv.setText(itemData.getTrace().getPoiCount());
        // 步数
        boolean hasSteps = false;
        if (itemData.getTrace().getSportTypes() == 1) {
            for (int i = 0; i < stepList.size(); i++) {
                if (itemData.getTrace().getTraceID() == stepList.get(i).getTraceNo()) {
                    stepStr = stepList.get(i).getSteps() + "";
                    holder.stepCountTv.setText(stepStr);
                    holder.stepLabelTv.setVisibility(View.VISIBLE);
                    holder.stepCountTv.setVisibility(View.VISIBLE);
                    hasSteps = true;
                    break;
                }
            }
        }
        if (!hasSteps) {
            holder.stepLabelTv.setVisibility(View.GONE);
            holder.stepCountTv.setVisibility(View.GONE);
        }
        holder.selectCb.setVisibility(visiblecheck.get(position)); // 选中项的复选框可见
        holder.selectCb.setChecked(ischeck.get(position));
        // 选中项
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMulChoice) { // 多选模式
                    if (holder.selectCb.isChecked()) {
                        holder.selectCb.setChecked(false);
                        ischeck.put(position, false);
                        for (int i = 0; i < selectedIdLList.size(); i++) {
                            if (selectedIdLList.get(i) == position) {
                                selectedIdLList.remove(i);
                            }
                        }
                    } else {
                        holder.selectCb.setChecked(true);
                        ischeck.put(position, true);
                        selectedIdLList.add(position);
                    }
                    txtcount.setText(context.getResources().getString(R.string.totaltxt1) +
                            selectedIdLList.size() + context.getResources().getString(R.string.totaltxt2));
                } else { // 点击进入详情页面
                    String trail = GsonHelper.toJson(traceDataList.get(position).getTrace());
                    stepStr = "--";
                    if(traceDataList.get(position).getTrace().getSportTypes()==1){
                        for(int i = 0; i < stepList.size(); i++){
                            if(traceDataList.get(position).getTrace().getTraceID() == stepList.get(i).getTraceNo()){
                                stepStr = GsonHelper.toJson(stepList.get(i));
                                break;
                            }
                        }
                    }
                    Intent intent=new Intent();
                    intent.putExtra("trail", trail);
                    intent.putExtra("step", stepStr);
                    intent.putExtra("isonline", !traceDataList.get(position).isLocal());
                    intent.setClass(context, TraceDetailActivity.class);
                    context.startActivity(intent);
                }
            }
        });
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onTraceLongClickListener != null) {
                    onTraceLongClickListener.onTraceLongClick();
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return traceDataList.size();
    }

    public void resetList(boolean isMulti){
        selectedIdLList.clear();
        visiblecheck.clear();
        ischeck.clear();
        for(int i = 0;i < traceDataList.size();i++){
            ischeck.put(i, false);
            if(isMulti){
                visiblecheck.put(i, CheckBox.VISIBLE);
            }else{
                visiblecheck.put(i, CheckBox.INVISIBLE);
            }
        }
    }

    public  HashMap<Integer, Integer> getVisible(){
        return visiblecheck;
    }
    public  HashMap<Integer, Boolean> getIsCheck(){
        return ischeck;
    }
    public  List<Integer> getSelected(){
        return selectedIdLList;
    }
    public void setIsMulti(boolean isMulChoice){
        this.isMulChoice = isMulChoice;
    }
    public  boolean getIsMulti(){
        return isMulChoice;
    }

    public class TraceViewHolder extends RecyclerView.ViewHolder {

        private ImageView traceTpyeIv;
        private ImageView isLocalIv;
        private ImageView isCloudIv;
        private TextView traceNameTv;
        private TextView startTimeTv;
        private TextView distanceTv;
        private TextView durationTv;
        private TextView stepCountTv;
        private TextView stepLabelTv;
        private TextView poiNumTv;
        private CheckBox selectCb;
        private View view;

        public TraceViewHolder(View itemView) {
            super(itemView);
            traceTpyeIv = itemView.findViewById(R.id.trace_type_iv);
            isLocalIv = itemView.findViewById(R.id.is_local_iv);
            isCloudIv = itemView.findViewById(R.id.is_cloud_iv);
            traceNameTv = itemView.findViewById(R.id.trace_name_tv);
            startTimeTv = itemView.findViewById(R.id.start_time_tv);
            distanceTv = itemView.findViewById(R.id.distance_tv);
            durationTv = itemView.findViewById(R.id.duration_tv);
            stepCountTv = itemView.findViewById(R.id.step_tv);
            stepLabelTv = itemView.findViewById(R.id.step_label_tv);
            poiNumTv = itemView.findViewById(R.id.poi_num_tv);
            selectCb = itemView.findViewById(R.id.select_trace_cb);
            view = itemView;
        }
    }
    public int getPosition(){
        return currentPosition;
    }
    // 自定义一个长按接口
    public interface OnTraceLongClickListener {
        void onTraceLongClick();
    }
    public void setOnTraceLongClickListener(OnTraceLongClickListener onTraceLongClickListener) {
        this.onTraceLongClickListener = onTraceLongClickListener;
    }
}
