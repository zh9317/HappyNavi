package com.trackersurvey.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.trackersurvey.model.StepData;
import com.trackersurvey.bean.TraceListItemData;
import com.trackersurvey.happynavi.TraceDetailActivity;
import com.trackersurvey.happynavi.R;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.GsonHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zh931 on 2018/5/18.
 */

public class TraceListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    //public  ArrayList<TraceData> trails;
    public ArrayList<TraceListItemData> traceItems;
    public  ArrayList<StepData> steps;
    //private HashMap<Integer, View> mView ;
    public HashMap<Integer, Integer> visiblecheck ;//用来记录是否显示checkBox
    public  HashMap<Integer, Boolean> ischeck;
    public List<Integer> selectid;
    public  boolean isMulChoice;
    private String stepstr = "--";//intent传参
    private TextView txtcount;
    private int currentPosition;
    int[] imageId = new int[]{
            R.mipmap.ic_walking,
            R.mipmap.ic_cycling,
            R.mipmap.ic_rollerblading,
            R.mipmap.ic_driving,
            R.mipmap.ic_train,
            R.mipmap.others,
    };
    private static SharedPreferences sp;

    public TraceListAdapter(Context context, TextView txtcount, ArrayList<TraceListItemData> traceItems, ArrayList<StepData> steps){
        this.context = context;
        this.txtcount = txtcount;
        this.traceItems = traceItems;
        this.steps = steps;
        mInflater = LayoutInflater.from(context); //(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //mView = new HashMap<Integer, View>();
        visiblecheck = new HashMap<Integer, Integer>();
        ischeck      = new HashMap<Integer, Boolean>();
        selectid = new ArrayList<Integer>();
        if(isMulChoice){
            for(int i=0;i<traceItems.size();i++){
                ischeck.put(i, false);
                visiblecheck.put(i, CheckBox.VISIBLE);
            }
        }else{
            for(int i=0;i<traceItems.size();i++)
            {
                ischeck.put(i, false);
                visiblecheck.put(i, CheckBox.INVISIBLE);
            }
        }
    }
    public void setDataSource(ArrayList<TraceListItemData> traceItems,ArrayList<StepData> steps){
        this.traceItems = traceItems;
        this.steps=steps;

    }
    @Override
    public int getCount() {
        return traceItems.size();//不能使用默认返回
    }

    @Override
    public Object getItem(int position) {
        return traceItems.get(position);//不能使用默认返回
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        currentPosition = position;
        ViewHolder holder = null;
        //View view = mView.get(position);
        if (convertView == null) {
            holder = new ViewHolder();

            sp = context.getSharedPreferences("languageSet", 0);
            String language = sp.getString("language", "0");
            int l = Integer.parseInt(language);
            if(l==0){
                Log.i("language", "zzzzzzzzzzzzzzzzzzzzzzzzzzzz");
                convertView = mInflater.inflate(R.layout.trace_list_item, null);
            }
            if(l==1){
                Log.i("language", "eeeeeeeeeeeeeeeeeeeeeeeeeeeee");
                convertView = mInflater.inflate(R.layout.trail_list_items_en, null);
            }

            holder.tracepic = (ImageView)convertView.findViewById(R.id.trace_type_iv);
            holder.isLocalimg = (ImageView)convertView.findViewById(R.id.is_local_iv);
            holder.isCloudimg = (ImageView)convertView.findViewById(R.id.is_cloud_iv);
            holder.traceName = (TextView)convertView.findViewById(R.id.trace_name_tv);
            holder.startTime = (TextView)convertView.findViewById(R.id.start_time_tv);
            holder.distance = (TextView)convertView.findViewById(R.id.distance_tv);
            holder.holdTime = (TextView)convertView.findViewById(R.id.duration_tv);
            holder.stepcounts = (TextView)convertView.findViewById(R.id.step_tv);
            holder.lablestep = (TextView)convertView.findViewById(R.id.step_label_tv);
            holder.checkbox = (CheckBox)convertView.findViewById(R.id.select_trace_cb);
            holder.interestPointNum = (TextView) convertView.findViewById(R.id.poi_num_tv);
            // 为view设置标签
            convertView.setTag(holder);
        }else{
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        try{
            holder.tracepic.setBackgroundResource(imageId[traceItems.get(position).getTrace().getSportTypes()-1]);
        }catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        if(traceItems.get(position).isLocal()){
            holder.isLocalimg.setVisibility(View.VISIBLE);

        }else{
            holder.isLocalimg.setVisibility(View.INVISIBLE);
        }
        if(traceItems.get(position).isCloud()){
            holder.isCloudimg.setVisibility(View.VISIBLE);

        }else{
            holder.isCloudimg.setVisibility(View.INVISIBLE);
        }
        holder.traceName.setText(traceItems.get(position).getTrace().getTraceName());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d1 = new Date();
        try{
            d1 = df.parse(traceItems.get(position).getTrace().getStartTime());
        }catch(Exception e){

        }
        holder.startTime.setText(df.format(d1));

        double km=traceItems.get(position).getTrace().getDistance();

        holder.distance.setText(Common.transformDistance(km)+context.getResources().getString(R.string.dis_unit));

        long duration=traceItems.get(position).getTrace().getDuration();

        holder.holdTime.setText(Common.transformTime(duration));

        int poiNum = traceItems.get(position).getTrace().getPoiCount();

        holder.interestPointNum.setText("" + poiNum);

        boolean hasSteps = false;
        if(traceItems.get(position).getTrace().getSportTypes() == 1){
            for(int i = 0; i < steps.size(); i++){
                if(traceItems.get(position).getTrace().getTraceID() == steps.get(i).getTraceID()){
                    stepstr = steps.get(i).getSteps()+"";
                    holder.stepcounts.setText(stepstr);
                    //Log.i("trailadapter", "step:"+steps.get(i).getsteps()+",traceNo:"+steps.get(i).gettraceNo());
//                    holder.lablestep.setVisibility(View.VISIBLE);
//                    holder.stepcounts.setVisibility(View.VISIBLE);
                    hasSteps=true;
                    break;
                }
            }
        }
        if(!hasSteps){
            holder.lablestep.setVisibility(View.GONE);
            holder.stepcounts.setVisibility(View.GONE);
        }
//        if (isMulChoice) {
//            holder.checkbox.setVisibility(View.VISIBLE);
//        }else {
//            holder.checkbox.setVisibility(View.INVISIBLE);
//        }
        //holder.checkbox.setVisibility(View.VISIBLE);
        holder.checkbox.setVisibility(visiblecheck.get(position));
        holder.checkbox.setChecked(ischeck.get(position));
        final CheckBox chbox=holder.checkbox;
        final int pos=position;
        convertView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(isMulChoice){
                    if(chbox.isChecked()){
                        chbox.setChecked(false);
                        ischeck.put(pos, false);
                        for(int i=0; i<selectid.size(); i++){
                            if(selectid.get(i) == pos){
                                selectid.remove(i);
                            }
                        }
                    }else{
                        chbox.setChecked(true);
                        ischeck.put(pos, true);
                        selectid.add(pos);
                    }
                    txtcount.setText(context.getResources().getString(R.string.totaltxt1)+selectid.size()+context.getResources().getString(R.string.totaltxt2));
                }else {

                    String trail= GsonHelper.toJson(traceItems.get(pos).getTrace());
                    stepstr="--";
                    if(traceItems.get(pos).getTrace().getSportTypes()==1){
                        for(int i=0;i<steps.size();i++){
                            if(traceItems.get(pos).getTrace().getTraceID()==steps.get(i).getTraceID()){

                                stepstr=GsonHelper.toJson(steps.get(i));
                                break;
                            }
                        }
                    }
                    Intent intent=new Intent();
                    intent.putExtra("trail", trail);
                    intent.putExtra("step", stepstr);
                    Log.i("TraceListAdapter", "isonline:" + !traceItems.get(pos).isLocal());
                    intent.putExtra("isonline", traceItems.get(pos).isCloud());
                    intent.setClass(context, TraceDetailActivity.class);
                    context.startActivity(intent);
                }
            }

        });
        convertView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub


                return false;
            }

        });
        //mView.put(position, view);


        return convertView;
    }
    public void resetList(boolean isMulti){
        selectid.clear();
        visiblecheck.clear();
        ischeck.clear();
        for(int i = 0;i < traceItems.size();i++){
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
        return selectid;
    }
    public void setIsMulti(boolean isMulChoice){
        this.isMulChoice = isMulChoice;
    }
    public  boolean getIsMulti(){
        return isMulChoice;
    }
    public final class ViewHolder {
        ImageView tracepic;
        ImageView isLocalimg;
        ImageView isCloudimg;
        TextView traceName;
        TextView startTime;
        TextView distance;
        TextView holdTime;
        TextView stepcounts;
        TextView lablestep;
        CheckBox checkbox;
        TextView interestPointNum;
    }
    public int getPosition(){
        return currentPosition;
    }
}
