package com.trackersurvey.photoview;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.TreeMap;

/**
 * Created by zh931 on 2018/5/14.
 * 这个是SelectedTreeMap 的代码，非常简单的一个序列化元素。用于存放已经选中的图片TreeMap<Long, Uri>
 */

public class SelectedTreeMap implements Parcelable {

    private static final long serialVersionUID = 6118012822436702146L;
    private static TreeMap<Long, Uri> treeMap = null;

    public SelectedTreeMap() {

    }

    public static TreeMap<Long, Uri> getTreeMap() {
        return treeMap;
    }

    public static void setTreeMap(TreeMap<Long, Uri> treeMap) {
        SelectedTreeMap.treeMap = treeMap;
    }

    protected SelectedTreeMap(Parcel in) {
    }

    public static final Creator<SelectedTreeMap> CREATOR = new Creator<SelectedTreeMap>() {
        @Override
        public SelectedTreeMap createFromParcel(Parcel in) {
            SelectedTreeMap stm = new SelectedTreeMap();
            in.readMap(treeMap, TreeMap.class.getClassLoader());
            return stm;
        }

        @Override
        public SelectedTreeMap[] newArray(int size) {
            return new SelectedTreeMap[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(treeMap);
    }
}
