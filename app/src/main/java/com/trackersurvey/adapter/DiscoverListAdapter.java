package com.trackersurvey.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trackersurvey.bean.DiscoverListBean;
import com.trackersurvey.happynavi.R;

import java.util.List;

/**
 * Created by zh931 on 2018/1/15.
 */

//DONG
//ZH
public class DiscoverListAdapter extends RecyclerView.Adapter<DiscoverListAdapter.DiscoverViewHolder> {

    private List<DiscoverListBean> dataList;

    public DiscoverListAdapter(List<DiscoverListBean> dataList) {
        this.dataList = dataList;
    }

    @Override
    public DiscoverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_list_item, parent, false);
        DiscoverViewHolder viewHolder = new DiscoverViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DiscoverViewHolder holder, int position) {
        DiscoverListBean listBean = dataList.get(position);
        holder.guessImg.setImageResource(listBean.getImageId());
        holder.description.setText(listBean.getDescription());
        holder.date.setText(listBean.getDate());
        holder.browseNum.setText(listBean.getBrowseNum());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class DiscoverViewHolder extends RecyclerView.ViewHolder{
        private ImageView guessImg;
        private TextView description;
        private TextView date;
        private TextView browseNum;

        public DiscoverViewHolder(View itemView) {
            super(itemView);
            guessImg = (ImageView) itemView.findViewById(R.id.guess_travel_img);
            description = (TextView) itemView.findViewById(R.id.guess_travel_description);
            date = (TextView) itemView.findViewById(R.id.guess_travel_date);
            browseNum = (TextView) itemView.findViewById(R.id.browse_num_text);
        }
    }
}
