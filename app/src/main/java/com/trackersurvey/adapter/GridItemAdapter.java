package com.trackersurvey.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.trackersurvey.happynavi.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zh931 on 2018/5/19.
 */

public class GridItemAdapter extends BaseAdapter {
    private Context                            context;
    private ArrayList<HashMap<String, String>> imageItems;
    private ProgressBar                        pb;
    private BitmapUtils                        bitmapUtils;
    private int                                colWidth;


    public GridItemAdapter(Context context,
                           ArrayList<HashMap<String, String>> imageItems, int colWidth) {
        this.context = context;
        this.imageItems = imageItems;
        bitmapUtils = new BitmapUtils(context);
        this.colWidth = colWidth;
    }


    public GridItemAdapter setItems(ArrayList<HashMap<String, String>> imageItems) {
        this.imageItems = imageItems;
        return this;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imageItems.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return imageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        convertView = View.inflate(context, R.layout.griditem_downpic, null);
        convertView.setMinimumWidth(colWidth);
        ImageView downImg = (ImageView) convertView.findViewById(R.id.dowm_img);
        pb = (ProgressBar) convertView.findViewById(R.id.down_img_pb);
        downImg.setMaxHeight(colWidth);
        downImg.setMinimumHeight(colWidth);

        Log.i("dongsiyuancolWidth", "getView: " + colWidth);

        Glide.with(context)
                .load(((HashMap<String, String>) getItem(position)).get("itemImage"))
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.app_logo)
                .override(colWidth, colWidth)
                .into(downImg);

//        BitmapDisplayConfig bdc = new BitmapDisplayConfig();
//        bdc.setBitmapMaxSize(new BitmapSize(colWidth, colWidth).scaleDown(2));
//        bitmapUtils.display(downImg, ((HashMap<String, String>) getItem(position)).get("itemImage"), bdc);
        pb.setVisibility(View.GONE);
        return convertView;
    }
}
