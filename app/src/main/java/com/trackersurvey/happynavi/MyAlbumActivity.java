package com.trackersurvey.happynavi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.adapter.MyAlbumListBaseAdapter;
import com.trackersurvey.bean.PointOfInterestData;
import com.trackersurvey.db.PhotoDBHelper;
import com.trackersurvey.db.PointOfInterestDBHelper;
import com.trackersurvey.http.DownloadPoiChoices;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.model.MyCommentModel;
import com.trackersurvey.model.PoiChoiceModel;
import com.trackersurvey.photoview.SlideListView;
import com.trackersurvey.util.AppManager;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.CustomDialog;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 我的相册
 * @author Eaa
 * @version 2015年12月4日 下午2:38:33
 */

public class MyAlbumActivity extends BaseActivity implements View.OnClickListener{

    private static final int REQUEST_PICTURE = 1;
    private TextView titleText; // 顶部文本
    private TextView titleRight; // 顶部确认按钮
    private SlideListView lView; // 界面的评论列表
    private String bgImageName = "bgImage.jpg";

    // 保存我的相册的设置的sharedPreferences
    private SharedPreferences myCommentSetting;
    // 个人相册的数据模型
    private MyCommentModel myComment;
    // listView的适配器
    private MyAlbumListBaseAdapter listAdapter;
    private PointOfInterestData behaviourData,durationData, partnerNumData, relationData;
    private PointOfInterestDBHelper helper = null;
    private String URL_GETPOI = null;
    private Cursor cursor = null;

    private SharedPreferences sp;

    /**
     * onCreate从本地数据库读取评论并显示 如果本地数据库空，提示同步云端数据
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_album);
        sp = getSharedPreferences("config", MODE_PRIVATE);

        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

        AppManager.getAppManager().addActivity(this);
        myCommentSetting = getSharedPreferences("myCommentSetting",
                Activity.MODE_PRIVATE);
        myCommentSetting.edit().commit();

        // Log.d("Eaa", "OnCreate");
        // 设置标题栏组件
        // titleBackOff = (ImageButton) findViewById(R.id.header_left_btn);
        // 顶部标题
        titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText(getResources().getString(R.string.mine_album));
        // 右部按钮不可见
        titleRight = findViewById(R.id.title_right_text);
        titleRight.setText(getResources().getString(R.string.refresh));
        // titleButton.setVisibility(View.INVISIBLE);

        // titleBackOff.setOnClickListener(this);
        titleRight.setOnClickListener(this);
        URL_GETPOI = Common.url + "requestInfo.aspx";
        helper = new PointOfInterestDBHelper(this);//创建或打开POI数据库
//        int l = TabHost_Main.l;
//        if(l==0){
//            initPOI();
//        }
//        if(l==1){
//            initPOIEN();
//        }
        initPOI();
        Common.createFileDir();
        initModel();
//		initAlbum();
        initAdapter();

        // 我的评论 界面中的ListView
        lView = (SlideListView) findViewById(R.id.album_lv);
        lView.initSlideMode(SlideListView.MOD_RIGHT);

        lView.setAdapter(listAdapter);
        lView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
                        || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    if ((view.getLastVisiblePosition() == view.getCount() - 1)
                            &&myComment.cloudMore()) {
                        Log.i("Eaa", "自动加载评论,size="+view.getCount() );
                        myComment.autoAddtoList();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub

            }
        });
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.i("album", "mycommentacitvity destory");
        myComment.stopModel();
        myComment = null;
        listAdapter = null;
        super.onDestroy();
    }

    /**
     * 初始化数据模型，设置对Model的监听
     */
    private void initModel() {
        myComment = new MyCommentModel(this,"album");
        // 监听背景改变
        myComment
                .setmChangeBackgroud(new MyCommentModel.ChangeBackgroudListener() {
                    @Override
                    public void onBackgroudChanged() {
                        // TODO Auto-generated method stub
                        updateUI();
                    }
                });

        // 监听下载评论
        myComment.setmDownComment(new MyCommentModel.DownCommentListener() {

            @Override
            public void onCommentDownload(int msg) {
                // TODO Auto-generated method stub
                if(msg ==0){
                    updateUI();
                }else{
                    modelTips(msg);
                }


            }
        });

        //监听删除评论的结果
        myComment.setmDeleteComment(new MyCommentModel.DeleteCommentListener() {
            @Override
            public void onCommentDeleted(int msg) {
                // TODO Auto-generated method stub
                switch (msg) {
                    case 0: {
                        lView.slideBack();
                        updateUI();
                        Toast.makeText(MyAlbumActivity.this,R.string.tips_deletesuccess,
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 1: {
                        Toast.makeText(MyAlbumActivity.this,R.string.tips_deletefail_dberror,
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 2: {
                        Toast.makeText(MyAlbumActivity.this,R.string.tips_postfail,
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 3: {
                        Toast.makeText(MyAlbumActivity.this,R.string.tips_postfail,
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    default:
                        break;
                }
            }
        });

        // 其余两个个监听在ListBaseAdapter中
    }

    /**
     * 初始化适配器，设置ListView的item的子组件的监听
     */
    private void initAdapter() {
        listAdapter = new MyAlbumListBaseAdapter(this, myComment,myComment.getItems(),"album");

        // 监听背景图片点击，点击更换图片
        listAdapter.setOnBackImageChange(new MyAlbumListBaseAdapter.BackImageListener() {

            @Override
            public void backImageClick() {
                // TODO Auto-generated method stub
                changeBackImg();
            }
        });
        //监听删除按钮
        listAdapter.setDeleCommListener(new MyAlbumListBaseAdapter.DeleCommListener() {

            @Override
            public void clickDelete(String dateTime, int position) {
                // TODO Auto-generated method stub
                deleteEvent(dateTime, position);
            }
        });

    }

    private void initAlbum(){
        PhotoDBHelper phelper = new PhotoDBHelper(this, PhotoDBHelper.DBREAD);
        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }
        String dateTime = Common.currentTime();
        cursor = phelper.selectEvent(null,
                PhotoDBHelper.COLUMNS_UE[10] + "=" + Common.getUserId(this),
                null, null, null, "datetime(" + PhotoDBHelper.COLUMNS_UE[0] + ") desc");
        if(Common.isNetConnected&&(cursor.getCount()==0)){
            myComment.downloadAlbum(dateTime);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        // Log.i("Eaa", "onResume");
        super.onResume();
    }

    /**
     * myCommentActivity中各个组件的点击事件
     *
     * @param v
     *            点击的组件
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.title_right_text: {
                // 刷新评论
                String dateTime = Common.currentTime();
                myComment.downloadAlbum(dateTime);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICTURE: {
                    Uri uri = data.getData();
                    if (!uri.getAuthority().isEmpty()
                            || !TextUtils.isEmpty(uri.getScheme())) {
                        // 查询选择图片
                        Cursor cursor = getContentResolver().query(uri,
                                new String[] { MediaStore.Images.Media.DATA },
                                null, null, null);
                        // 返回 没找到选择图片
                        String selectImage = null;
                        if (null != cursor) {
                            // 光标移动至开头 获取图片路径
                            cursor.moveToFirst();
                            selectImage = cursor.getString(cursor
                                    .getColumnIndex(MediaStore.Images.Media.DATA));
                            cursor.close();
                        } else {
                            selectImage = Uri.decode(uri.getEncodedPath());
                        }
                        // 获取选择图片的BitMap
                        try {
                            Bitmap selectBack = Common.scaleBitmap(selectImage,
                                    Common.winWidth, Common.winHeight);
                            selectBack = ThumbnailUtils.extractThumbnail(selectBack,
                                    Common.winWidth, Common.winHeight / 3);
                            // 将Bitmap存到本地

                            FileOutputStream fos = openFileOutput(bgImageName,
                                    Context.MODE_PRIVATE);
                            selectBack
                                    .compress(Bitmap.CompressFormat.JPEG, 70, fos);
                            // Log.d("Eaa", "保存图片至应用空间");
                            fos.flush();
                            fos.close();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (OutOfMemoryError e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }

                        myComment.changeBackgroud(bgImageName);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 调用系统相册选择一张照片替换背景图片
     */
    private void changeBackImg() {
        Toast.makeText(MyAlbumActivity.this, R.string.addpicture,
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        startActivityForResult(intent, REQUEST_PICTURE);
    }

    /**
     * 弹出对话框让用户选择是否确认删除一条评论
     * @param dateTime
     * @param position
     */
    private void deleteEvent(final String dateTime, final int position) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.tip));
        builder.setMessage(
                getResources().getString(
                        R.string.tips_deletedlgmsg_album));
        builder.setPositiveButton(getResources().getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        deleteComment(dateTime, position);
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancl),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
        builder.create().show();

    }

    /**
     * 刷新界面
     */
    private void updateUI(){

        listAdapter.setItems(myComment.getItems());
        listAdapter.notifyDataSetChanged();
    }


    /**
     * 根据Model操作的回调，弹出Toast提醒用户
     */
    private void modelTips(int msg) {
        switch (msg) {
            case 0:
                Toast.makeText(MyAlbumActivity.this,R.string.tips_getcommentsuccess,Toast.LENGTH_SHORT).show();
                break;
            case -2:
                Toast.makeText(MyAlbumActivity.this,R.string.tips_postfail,Toast.LENGTH_SHORT).show();
                break;
            case -1:
                Toast.makeText(MyAlbumActivity.this,R.string.tips_postfail,Toast.LENGTH_SHORT).show();
                break;
            case 1:// 获取数据不成功但连接了
                //Log.i("Eaa", "获取云端评论时提示" + "服务器忙，请稍后再试");
                Toast.makeText(MyAlbumActivity.this,R.string.tips_postfail,Toast.LENGTH_SHORT).show();
                break;
            case 8:
                //查询有误
                Toast.makeText(MyAlbumActivity.this,R.string.tips_postfail,Toast.LENGTH_SHORT).show();
                break;
            case 10:// 连接失败
                Toast.makeText(MyAlbumActivity.this,R.string.tips_netdisconnect,Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }


    /**
     * 通知模型删除一条评论
     */
    private void deleteComment(String dateTime, int listPosition) {
        myComment.deleteComment(dateTime, listPosition);
    }
    //传递给PostPointOfInterestData的handler1
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(msg.obj!=null){
                        String[] poiStr = msg.obj.toString().trim().split("#");
                        String[] behaviourStr = poiStr[1].split("[$]");
                        String[] durationStr = poiStr[0].split("[$]");
                        String[] partnerNumStr = poiStr[2].trim().split("[$]");
                        String[] relationStr = poiStr[3].trim().split("[$]");
                        behaviourData = new PointOfInterestData();
                        durationData = new PointOfInterestData();
                        partnerNumData = new PointOfInterestData();
                        relationData = new PointOfInterestData();
                        //Log.i("poiStr", poiStr[0]);
                        helper.delete();
                        try {
                            for(int i = 0;i<behaviourStr.length;i++){
                                behaviourData.setKey(i);
                                behaviourData.setValue(behaviourStr[i]);
                                helper.insertBehaviour(behaviourData);
                            }
                            for(int i = 0;i<durationStr.length;i++){
                                durationData.setKey(i);
                                durationData.setValue(durationStr[i]);
                                //将数据插入到POI数据库中
                                helper.insertDuration(durationData);
                            }
                            for(int i = 0;i<partnerNumStr.length;i++){
                                partnerNumData.setKey(i);
                                partnerNumData.setValue(partnerNumStr[i]);
                                helper.insertPartnerNum(partnerNumData);
                            }
                            for(int i = 0;i<relationStr.length;i++){
                                relationData.setKey(i);
                                relationData.setValue(relationStr[i]);
                                helper.insertPartnerRelation(relationData);
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                    break;
                case 1:

                    break;
            }
        }
    };
    private void initPOI(){
        //从服务器下载停留时长、行为类型、同伴人数、关系等选项的数据
        DownloadPoiChoices downloadPoiChoices = new DownloadPoiChoices(sp.getString("token", ""));
        downloadPoiChoices.requestHttpData(new ResponseData() {
            @Override
            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                if (isSuccess) {
                    if (code.equals("0")) {
                        PoiChoiceModel poiChoiceModel = (PoiChoiceModel) responseObject;
                        behaviourData = new PointOfInterestData();
                        durationData = new PointOfInterestData();
                        partnerNumData = new PointOfInterestData();
                        relationData = new PointOfInterestData();
                        helper.delete();
                        for (int i = 0; i < poiChoiceModel.getActivityTypeList().size(); i++) {
                            behaviourData.setKey(poiChoiceModel.getActivityTypeList().get(i).getActivityType());
                            behaviourData.setValue(poiChoiceModel.getActivityTypeList().get(i).getActivityName());
                            helper.insertBehaviour(behaviourData);
                        }
                        for (int i = 0; i < poiChoiceModel.getRetentionTypeList().size(); i++) {
                            durationData.setKey(poiChoiceModel.getRetentionTypeList().get(i).getRetentionType());
                            durationData.setValue(poiChoiceModel.getRetentionTypeList().get(i).getRetentionTypeName());
                            helper.insertDuration(durationData);
                        }
                        for (int i = 0; i < poiChoiceModel.getCompanionTypeList().size(); i++) {
                            partnerNumData.setKey(poiChoiceModel.getCompanionTypeList().get(i).getCompanionType());
                            partnerNumData.setValue(poiChoiceModel.getCompanionTypeList().get(i).getCompanionTypeName());
                            helper.insertPartnerNum(partnerNumData);
                        }
                        for (int i = 0; i < poiChoiceModel.getRelationTypeList().size(); i++) {
                            relationData.setKey(poiChoiceModel.getRelationTypeList().get(i).getRelationType());
                            relationData.setValue(poiChoiceModel.getRelationTypeList().get(i).getRelationTypeName());
                            helper.insertPartnerRelation(relationData);
                        }
                    }
                }
            }
        });
    }
}
