package com.trackersurvey.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zh931 on 2018/5/19.
 * 避免地图的缩放手势和viewpager滑动手势冲突，复写canScroll方法
 */

public class ViewPagerCompat extends ViewPager {
    public ViewPagerCompat(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public ViewPagerCompat(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        // TODO Auto-generated method stub
        if(v.getClass().getName().equals("com.amap.api.maps.MapView")){
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }
}
