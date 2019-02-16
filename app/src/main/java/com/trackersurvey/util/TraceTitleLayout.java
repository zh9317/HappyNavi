package com.trackersurvey.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.trackersurvey.happynavi.R;

/**
 * Created by zh931 on 2018/5/20.
 */

public class TraceTitleLayout extends LinearLayout{
    public TraceTitleLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.trace_title, this);
        RelativeLayout titleBackBtn = findViewById(R.id.title_back_layout);
        titleBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });
    }
}
