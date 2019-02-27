package com.trackersurvey.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trackersurvey.happynavi.MyAlbumActivity;
import com.trackersurvey.happynavi.MyGroupActivity;
import com.trackersurvey.happynavi.MySportActivity;
import com.trackersurvey.happynavi.OfflineMapActivity;
import com.trackersurvey.happynavi.R;
import com.trackersurvey.happynavi.SettingActivity;
import com.trackersurvey.happynavi.TraceListActivity;
import com.trackersurvey.happynavi.UserInfoActivity;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.http.TestRequest;
import com.trackersurvey.util.RoundImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

/**
 * Created by lenovo on 2017/9/4.
 * “我的”页面
 */

public class MineFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences sp;
    private RoundImageView    headImg;
    private TextView          nickNameTv;
    private TextView          mobilePhoneTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View meLayout = inflater.inflate(R.layout.fragment_mine, container, false);
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        headImg = meLayout.findViewById(R.id.mine_mian_img);
        Glide.with(this).load("http://211.87.227.204:8089"
                + sp.getString("headurl", "") + "?token="
                + sp.getString("token", "")).into(headImg);
        nickNameTv = meLayout.findViewById(R.id.mine_nickname);
        nickNameTv.setText(sp.getString("nickname", ""));
        mobilePhoneTv = meLayout.findViewById(R.id.mine_mobilePhone);
        mobilePhoneTv.setText(sp.getString("mobilePhone", ""));
        RelativeLayout userInfoLayout = meLayout.findViewById(R.id.user_info_layout);
        LinearLayout myAlbumLayout = meLayout.findViewById(R.id.my_album_layout); // 我的相册
        LinearLayout myGroupLayout = meLayout.findViewById(R.id.my_group_layout); // 我的群组
        LinearLayout settingLayout = meLayout.findViewById(R.id.my_setting_layout); // 设置
        LinearLayout helpLayout = meLayout.findViewById(R.id.my_help_layout); // 帮助
        userInfoLayout.setOnClickListener(this);
        myAlbumLayout.setOnClickListener(this);
        myGroupLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);
        helpLayout.setOnClickListener(this);
//        EventBus.getDefault().register(this);
        return meLayout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_info_layout:
                Intent userInfoIntent = new Intent(getContext(), UserInfoActivity.class);
                startActivityForResult(userInfoIntent, 1);
                break;
            case R.id.my_album_layout:
                Intent myAlbumIntent = new Intent(getContext(), MyAlbumActivity.class);
                startActivity(myAlbumIntent);
                break;
            case R.id.my_group_layout:
                Intent groupIntent = new Intent(getContext(), MyGroupActivity.class);
                startActivity(groupIntent);
                break;
            case R.id.my_setting_layout:
                Intent settingIntent = new Intent(getContext(), SettingActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.my_help_layout:
                TestRequest testRequest = new TestRequest();
                testRequest.requestHttpData(new ResponseData() {
                    @Override
                    public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {

                    }
                });
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Glide.with(this).load("http://211.87.227.204:8089"
                + sp.getString("headurl", "") + "?token="
                + sp.getString("token", "")).into(headImg);
        nickNameTv.setText(sp.getString("nickname", ""));
        mobilePhoneTv.setText(sp.getString("mobilePhone", ""));
    }
}
