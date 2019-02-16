package com.trackersurvey.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.trackersurvey.happynavi.R;
import com.trackersurvey.photoview.ImageWorker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zh931 on 2018/5/14.
 */

public class ImageAdapter extends BaseAdapter {
    private ImageWorker imageWorker;

    private HashMap<Long, Boolean> seletedMap = new HashMap<Long, Boolean>();
    private ArrayList<Long> origIdArray = new ArrayList<Long>();

    //监听器
    OnPictureClickListener mPicClickListener;
    OnCheckBoxClickListener mCBoxClickListener;


    private int colWidth = 240;

    private LayoutInflater mInflater;

    // 构造器
    public ImageAdapter(int colWidth, ImageWorker imageWorker, Context context, OnPictureClickListener opcl, OnCheckBoxClickListener ocbcl) {
        this.imageWorker = imageWorker;
        this.colWidth = colWidth;
        mInflater = LayoutInflater.from(context);
        mPicClickListener = opcl;
        mCBoxClickListener = ocbcl;
    }

    @Override
    public int getCount() {
        return origIdArray.size();
    }

    @Override
    public Object getItem(int position) {
        return origIdArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return origIdArray.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.griditem_pb, parent,
                    false);

            holder = new ViewHolder();
            holder.img = (ImageView) convertView
                    .findViewById(R.id.pb_ImageItem);
            holder.img.setMinimumHeight(colWidth);
            holder.img.setMinimumWidth(colWidth);
            holder.cbox = (CheckBox) convertView.findViewById(R.id.pb_checkBox);
            convertView.setTag(holder);
        }else{

            // 取出holder
            holder = (ViewHolder) convertView.getTag();

        }
        final long origId = origIdArray.get(position);
        holder.cbox.setChecked(seletedMap.containsKey(origId) ? seletedMap
                .get(origId) : false);

        holder.img.setTag(R.id.tag_position, position);
        holder.img.setTag(R.id.tag_id,origId);
        holder.img.setOnClickListener(mPicClickListener);

        holder.cbox.setTag(R.id.tag_position, position);
        holder.cbox.setTag(R.id.tag_id,origId);
        holder.cbox.setOnClickListener(mCBoxClickListener);

        // 加载图片
        imageWorker.loadImage(origId, holder.img);
        return convertView;
    }

    public ImageAdapter putSelectMap(Long origId, Boolean isChecked) {
        seletedMap.put(origId, isChecked);
        return this;
    }

    public ImageAdapter setOrigIdArray(ArrayList<Long> origIdArray) {
        this.origIdArray = origIdArray;
        return this;
    }

    public static abstract class OnPictureClickListener implements View.OnClickListener{
        public int position;
        public long id;
        @Override
        public void onClick(View v) {
            position = (Integer) v.getTag(R.id.tag_position);
            id= (Long) v.getTag(R.id.tag_id);
            onPictureClick(v, position, id);
        }
        public abstract void onPictureClick(View view, int position, long id);
    }

    public static abstract class OnCheckBoxClickListener implements View.OnClickListener{
        public int position;
        public long id;
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            position = (Integer) v.getTag(R.id.tag_position);
            id= (Long) v.getTag(R.id.tag_id);
            ((CheckBox)v).setChecked(!((CheckBox)v).isChecked());
            onCheckBoxClick(v, position, id);
        }
        public abstract void onCheckBoxClick(View view, int position,long id);
    }


    public class ViewHolder {
        ImageView img;
        CheckBox cbox;
    }
}
