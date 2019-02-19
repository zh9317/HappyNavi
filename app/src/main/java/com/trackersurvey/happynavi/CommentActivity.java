package com.trackersurvey.happynavi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.bean.CommentMediaFilesData;
import com.trackersurvey.bean.InterestMarkerData;
import com.trackersurvey.model.TraceData;
import com.trackersurvey.db.PhotoDBHelper;
import com.trackersurvey.db.MyTraceDBHelper;
import com.trackersurvey.photoview.SelectedTreeMap;
import com.trackersurvey.util.AppManager;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.CustomDialog;
import com.trackersurvey.util.DropDownListView;
import com.trackersurvey.util.DropDownListViewShort;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class CommentActivity extends BaseActivity implements View.OnClickListener, GeocodeSearch.OnGeocodeSearchListener{

    // 用于onActivityResult()方法判断由那个页面返回当前页面
    public  final int REQUEST_PICTURE = 1;//从相册中选照片
    public  final int REQUEST_TAKEPIC = 2;//调用相机拍照
    public  final int REQUEST_PLACE = 3;//选择或编辑地名
    public  final int REQUEST_VIDEO = 4;//在相册中选择录像
    public  final int REQUEST_CAMERA = 5;//调用相机录像
    public  final String SHAREDFILES = "uploadFiles";
    // gridView使用的多个HashMap的一个常用key
    private  final String keyOfBitmap = "itemImage";
    private  final int gviewCol = 3; // gridView列数

    private GridView gview; // 网格显示缩略图
    private ArrayList<HashMap<String, Object>> imageItem; // gridView 组件集
    private int itemNo; // gridView中组件个数
    private SimpleAdapter simpleAdapter;

    // 选择的多张照片的id和Uri
    private SelectedTreeMap selectPictures;
    private  ArrayList<String> pathImage; // 选择的所有图片路径
    private  ArrayList<String> cacheImages;
    private ArrayList<String> durationData, behaviourData, partnerNumData, relationData;
    private  String cacheVideo;
    private ArrayList<String> selectImages; // 选择的图片的绝对路径
    private int hasVideo = -1;
    private int clickPosition;

    private InterestMarkerData comment; // 用户事件
    private String createTime = null;
    private double longitude = 117.1466090000; // 需要地图功能获得的4个数据
    private double latitude = 36.6731060000;
    private double altitude = 114;
    private LatLng markLatLng;
    private LatLonPoint markLatLonPoint;
    private GeocodeSearch geocodeSearch;
    private String country;
    private String province;
    private String city;
    private String placeName = "实验室";
    private String commentText;
    private int feeling = 1; //心情
    private int behaviour; //行为类型
    private int stay;	//停留时长
    private int companion;//同伴人数
    private int relation; //关系
    private int share = 1;
    private long traceID = 0;
    private int stateType;
    private int fileNum;
    private String videoPath = null; // 选择的视频的绝对路径

    // 用户评论媒体文件
    private CommentMediaFilesData files[];

    private int colWidth; // GridVied 列宽

    // 界面上下各个按钮
    private TextView titleTv; // 标题
    private TextView confirm; // 确认
    private ImageView photo; // 调用相机拍照
    private ImageView smallVideo; // 拍摄小视频
    private ImageView imagePlace; // 位置图标
    private TextView textOfPlace; // 选择位置
    private EditText editComment; // 输入评论

    private String recentPhoto;
    private String cacheFileName;

    private PhotoDBHelper dbHelper;
    private MyTraceDBHelper traceHelper;

    private DropDownListView behaviourList;
    private DropDownListViewShort durationList, partnerNumberList, relationList;//短下拉框
    private RadioGroup mood;
    private RadioButton rb_happy, rb_general, rb_unhappy;
    private CheckBox isShareCb;
    private TextView isShareTv;
    private boolean isShare = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        titleTv = findViewById(R.id.title_text);
        titleTv.setText(getResources().getString(R.string.addpoi));
        AppManager.getAppManager().addActivity(this);
        // 获取MainActivity传来的经纬度 海拔 traceno，地点信息
        Intent mainIntent = getIntent();
        longitude = mainIntent.getDoubleExtra("longitude", longitude);
        latitude = mainIntent.getDoubleExtra("latitude", latitude);
        altitude = mainIntent.getDoubleExtra("altitude", altitude);

        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
        markLatLonPoint = new LatLonPoint(latitude, longitude);
        RegeocodeQuery regeocodeQuery = new RegeocodeQuery(markLatLonPoint, 500f, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(regeocodeQuery);

        country = mainIntent.getStringExtra("country");
        province = mainIntent.getStringExtra("province");
        city = mainIntent.getStringExtra("city");
        traceID = mainIntent.getLongExtra("traceID", traceID);
        placeName = mainIntent.getStringExtra("placeName");
        stateType = mainIntent.getIntExtra("stateType", 0);

        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);

        behaviourData = new ArrayList<String>();
        durationData = new ArrayList<String>();
        partnerNumData = new ArrayList<String>();
        relationData = new ArrayList<String>();
        behaviourData = mainIntent.getStringArrayListExtra("behaviour");
        durationData = mainIntent.getStringArrayListExtra("duration");
        partnerNumData = mainIntent.getStringArrayListExtra("partnerNum");
        relationData = mainIntent.getStringArrayListExtra("relation");
        //Log.i("behaviourData", behaviourData.toString());

        if(mainIntent.hasExtra("createTime")){
            createTime = mainIntent.getStringExtra("createTime");
            Log.i("mark", "createTime from marking :"+createTime);
        }else{
            createTime = null;
            Log.i("comment", "createTime from main ");
        }
        colWidth = (Common.winWidth - gviewCol * 8) / gviewCol - 10; // 列宽适配屏幕宽度
        gview = (GridView) findViewById(R.id.gridPicture);

        itemNo = 0;
        pathImage = new ArrayList<String>();
        cacheImages = new ArrayList<String>();
        imageItem = new ArrayList<HashMap<String, Object>>();
        selectImages = new ArrayList<String>();
        addSymbol();

        // 用adapter控制gridView
        simpleAdapter = new SimpleAdapter(getApplicationContext(), imageItem,
                R.layout.griditem_addpic, new String[] { "itemImage" },
                new int[] { R.id.imageViewItem });
/**解释来自：http://blog.csdn.net/gongzhuo1987/article/details/47868355
 * SimpleAdapter里面的数据绑定，功能太过于简单，对于ImageView控件，
 * 默认只能调用setViewImage((ImageView)v,(Integer)data);
 * 以及setViewImage(((ImageView)v,text);，如果想设置一张图片的话，默认的处理是不支持的，
 * 默认的处理只能设置数字或者字符串(数字就是本地的资源id，R.drawable.pic这样的资源文件id，或者String字符串)，
 * 如果是网络图片，也即非本地图片，此时默认的处理就无法满足要求了。
 * 如果需要有高级的设置，那么就必须在ViewBinder里面去设置了。ViewBinder其实就是有用户来接管v的处理。
 * */
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView i = (ImageView) view;
                    i.setMinimumHeight(colWidth);
                    i.setMinimumWidth(colWidth);
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });

        gview.setAdapter(simpleAdapter);
        // 点击监听，点击加号添加新图片，点击缩略图看大图
        gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                clickPosition = position;
                // 动态申请运行时权限
                if (ContextCompat.checkSelfPermission(CommentActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CommentActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    visitPictureBrowserActivity();
                }
            }
        });

        gview.setLongClickable(true);
        // 长按缩略图删除
        gview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                if (position < itemNo) {
                    dialog(position);
                    return true;
                }
                return false;
            }
        });

        editComment = (EditText) findViewById(R.id.editComment);
        // 设置各个按钮
        // 点击返回按钮，结束本Activity
        confirm = findViewById(R.id.title_right_text);
        confirm.setText(getResources().getString(R.string.confirm));
        // 点击确认按钮，上传评论
        photo = (ImageView) findViewById(R.id.comment_photo);
        // 点击photo按钮，打开相机拍照，在onActivityReult方法中将照片加入到GridView中
        // 选择视频
        smallVideo = (ImageView) findViewById(R.id.comment_video);
        // 地点
        imagePlace = (ImageView) findViewById(R.id.imageView_place);
        textOfPlace = (TextView) findViewById(R.id.textView_place);

        textOfPlace.setText(placeName);

        mood = (RadioGroup) findViewById(R.id.rg_mood);
        rb_happy = (RadioButton) findViewById(R.id.rb_happy);
        rb_general = (RadioButton) findViewById(R.id.rb_general);
        rb_unhappy = (RadioButton) findViewById(R.id.rb_unhappy);
        isShareCb = findViewById(R.id.is_share_cb);
        isShareTv = findViewById(R.id.is_share_tv);
        isShareCb.setChecked(true);

        // backoff.setOnClickListener(this);
        confirm.setOnClickListener(this);
        photo.setOnClickListener(this);
        smallVideo.setOnClickListener(this);
        imagePlace.setOnClickListener(this);
        textOfPlace.setOnClickListener(this);
        isShareTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShareCb.isChecked()) {
                    isShareCb.setChecked(false);
                    isShare = false;
                }else {
                    isShareCb.setChecked(true);
                    isShare = true;
                }
            }
        });
        isShareCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isShare = isChecked;
            }
        });

        mood.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rb_happy.getId()) {
                    feeling = 1;
                }
                if (checkedId == rb_general.getId()) {
                    feeling = 2;
                }
                if (checkedId == rb_unhappy.getId()) {
                    feeling = 3;
                }
            }
        });
/*--------------------------------------------------------------------------------------*/
        behaviourList = (DropDownListView) findViewById(R.id.drop_down_list_view_behaviour);
        durationList = (DropDownListViewShort) findViewById(R.id.drop_down_list_view_duration);
        partnerNumberList = (DropDownListViewShort) findViewById(R.id.drop_down_list_view_partner_number);
        relationList = (DropDownListViewShort) findViewById(R.id.drop_down_list_view_relationship);

//		ArrayList<String> list_duration = new ArrayList<String>();
//		ArrayList<String> list_behaviour = new ArrayList<String>();
//		ArrayList<String> list_partnerNumber = new ArrayList<String>();
//		ArrayList<String> list_relationship = new ArrayList<String>();
//		String[] durationArray = getResources().getStringArray(R.array.durationarray);
//		String[] behaviourArray = getResources().getStringArray(R.array.behaviourarray);
//		String[] partnerArray = getResources().getStringArray(R.array.partnernumarray);
//		String[] relationArray = getResources().getStringArray(R.array.relationarray);
//		for(int i = 0;i<durationArray.length;i++){
//			list_duration.add(durationArray[i]);
//		}
//		for(int i = 0;i<behaviourArray.length;i++){
//			list_behaviour.add(behaviourArray[i]);
//		}
//		for(int i = 0;i<partnerArray.length;i++){
//			list_partnerNumber.add(partnerArray[i]);
//		}
//		for(int i = 0;i<relationArray.length;i++){
//			list_relationship.add(relationArray[i]);
//		}
//上面的数据原本放在strings.xml文件中，现在通过请求服务器下载，插入数据库，再从数据库中查询，传递到这里

        try {
            behaviourList.setItemsData(behaviourData);
            durationList.setItemsData(durationData);
            partnerNumberList.setItemsData(partnerNumData);
            relationList.setItemsData(relationData);
        } catch (Exception e) {
            e.printStackTrace();
        }

/*--------------------------------------------------------------------------------------*/
    }

    private void visitPictureBrowserActivity(){
        if (clickPosition == itemNo) {//点击的是+号 选择照片
            Toast.makeText(CommentActivity.this, R.string.addpicture,
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CommentActivity.this,
                    PictureBrowserActivity.class);
            if (selectPictures != null) {//已经选择的照片，在PictureBrowser默认打钩
                intent.putExtra(PictureBrowserActivity.RESULT_URIS,
                        selectPictures);
            }
            intent.putExtra("hasVideo", hasVideo);
            startActivityForResult(intent, REQUEST_PICTURE);
        } else if (clickPosition == hasVideo) {//点击的是视频 （有且只有一个视频）
            Uri uri = Uri.fromFile(new File(videoPath));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "video/*");
            startActivity(intent);
        } else {//点击的是照片
            // 显示大图
            //Toast.makeText(CommentActivity.this, "看大图",
            //		Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CommentActivity.this,
                    SelectedPictureActivity.class);
            intent.putStringArrayListExtra(
                    SelectedPictureActivity.PIC_PATH, pathImage);
            int imgPos =  clickPosition ;
            if(hasVideo != -1 && imgPos >hasVideo){
                imgPos--;
            }
            intent.putExtra(SelectedPictureActivity.PIC_POSITION, imgPos);
            startActivity(intent);
        }
    }

    /**
     * 进入拍照页面
     */
    private void takePic(){
        if (itemNo < 9) {
            String status = Environment.getExternalStorageState();
            Log.i("Eaa", "status:"+status);
            if (status.equals(Environment.MEDIA_MOUNTED)) {
                try {
                    File imgDirs = new File(Common.PHOTO_PATH);
                    if (!imgDirs.exists()) {
                        if (imgDirs.mkdirs()) {
                            Toast.makeText(CommentActivity.this,
                                    getResources().getString(R.string.tips_savepath) + Common.PHOTO_PATH,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CommentActivity.this,
                                    getResources().getString(R.string.tips_norighttomkdir), Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                    }
                    File imageName = new File(imgDirs, "HappyNavi_"
                            + Common.currentTime().trim() + ".jpg");
                    imageName.createNewFile();
                    recentPhoto = imageName.getAbsolutePath();
                    Uri uri = Uri.fromFile(imageName);

                    Log.i("Eaa", "_photoPath_"+recentPhoto);
                    Intent intent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.Images.Media.ORIENTATION, 1);
                    //设定拍照时手机方向为竖向
                    //设定照片保存的路径
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, REQUEST_TAKEPIC);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(CommentActivity.this,getResources().getString(R.string.tips_findsdfail),
                            Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    //e.printStackTrace();
                    Toast.makeText(CommentActivity.this,R.string.tips_createfilefail,
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CommentActivity.this,getResources().getString(R.string.tips_nosd),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(CommentActivity.this, R.string.nomorethan9,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 进入录像页面
     */
    private void takeVideo() {
        int videoNum = 0;
        if (hasVideo >= 0) {
            videoNum = 1;
            Toast.makeText(CommentActivity.this,getResources().getString(R.string.tips_selectvideoagain),
                    Toast.LENGTH_SHORT).show();
        }

        if (itemNo -videoNum < 9) {
//				Toast.makeText(CommentActivity.this, "选择视频", Toast.LENGTH_SHORT)
//						.show();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            CharSequence[] items = getResources().getStringArray(R.array.way_selectvideo);
            builder.setTitle(getResources().getString(R.string.tips_dlgtle_selectvideo));
            builder.setSingleChoiceItems(items, 0,
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int which) {
                            switch (which) {
                                case 0:
                                    // 调用相机摄像
                                    Intent intent = new Intent();
                                    intent.setAction("android.media.action.VIDEO_CAPTURE");
                                    intent.addCategory("android.intent.category.DEFAULT");
                                    intent.putExtra(
                                            MediaStore.EXTRA_VIDEO_QUALITY, 1);
                                    intent.putExtra(
                                            MediaStore.EXTRA_DURATION_LIMIT,
                                            200000);
                                    intent.putExtra(
                                            MediaStore.EXTRA_SIZE_LIMIT,
                                            20 * 1024 * 1024);
                                    startActivityForResult(intent,
                                            REQUEST_CAMERA);

                                    break;
                                case 1: {
                                    // 文件选择
                                    Intent intent2 = new Intent(
                                            Intent.ACTION_GET_CONTENT);
                                    intent2.setType("video/*");
                                    startActivityForResult(intent2,
                                            REQUEST_VIDEO);
                                    break;
                                }
                                default:
                                    break;
                            }
                            dialog.dismiss();
                            ;

                        }

                    });
            builder.create().show();

        } else {
            Toast.makeText(CommentActivity.this, R.string.nomorethan9,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    visitPictureBrowserActivity();
                }else {
                    Toast.makeText(CommentActivity.this, "您禁止了权限，无法查看图片", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePic();
                } else {
                    Toast.makeText(CommentActivity.this, "相机权限未允许，无法拍照", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                takeVideo();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.i("Eaa", " resultCode:" + resultCode + " data=" + data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICTURE: {
                    Intent intent = data;
                    selectPictures = intent.getParcelableExtra(PictureBrowserActivity.RESULT_URIS);
                    TreeMap<Long, Uri> selectPics = selectPictures.getTreeMap();
                    Set<Long> keys = selectPics.keySet();

                    // 清空已有图片，重新添加
                    pathImage.clear();
                    // 删除本地缩略图
                    for (int i = 0; i < cacheImages.size(); i++) {
                        new File(cacheImages.get(i)).delete();
                    }
                    cacheImages.clear();
                    if (hasVideo >= 0) {
                        HashMap<String, Object> video = imageItem.get(hasVideo);
                        imageItem.clear();
                        imageItem.add(video);
                        hasVideo = 0;
                        itemNo = 1;
                    } else {
                        imageItem.clear();
                        itemNo = 0;
                    }
                    selectImages.clear();

                    Cursor cursor = null;
                    String[] projection = { MediaStore.Images.Media.DATA };
                    for (Long key : keys) {
                        Uri uri = selectPics.get(key);
                        cursor = getContentResolver().query(uri, projection, null,
                                null, null);
                        cursor.moveToFirst();
                        String picPath = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Images.Media.DATA));
                        selectImages.add(picPath);
                        cursor.close();
                        Log.i("Eaa", "picPath = " + picPath);
                    }
                    addSymbol();
                    break;
                }
                case REQUEST_TAKEPIC: {
                    String sdStatus = Environment.getExternalStorageState();
                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                        Log.i("TestFile","SD card is not avaiable/writeable right now.");
                        Toast.makeText(CommentActivity.this,
                                R.string.cantusingsdcard, Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    if (recentPhoto!=null && new File(recentPhoto).exists()) {
                        String path = recentPhoto;

                        InsertPicToMediaStore insertImg = new InsertPicToMediaStore();
                        insertImg.execute(new Object[] { getApplicationContext(),
                                path, Common.currentTime() });

                        selectImages.add(recentPhoto);
                    } else {
                        Toast.makeText(CommentActivity.this,R.string.tips_takepicfail,
                                Toast.LENGTH_SHORT).show();
                    }

                    break;
                }
                case REQUEST_PLACE: {
                    placeName = data.getExtras().getString("place");
                    //Log.i("placeName", placeName);
                    textOfPlace.setText(placeName);
                    break;
                }
                case REQUEST_VIDEO: {
                    if (null == data) {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.tips_choosevideofail),
                                Toast.LENGTH_SHORT).show();
                        videoPath = null;
                        return;
                    }
                    //Log.i("Eaa_video", data.toString());

                    try {
                        Uri uri = data.getData();
                        //Log.i("Eaa_Uri", uri.toString());
                        if (!TextUtils.isEmpty(uri.getAuthority())
                                || !TextUtils.isEmpty(uri.getScheme())) {
                            // 查询选择视频
                            Cursor cursor = getContentResolver().query(uri,
                                    new String[] { MediaStore.Video.Media.DATA },
                                    null, null, null);
                            if (null != cursor) {

                                // 光标移动至开头 获取图片路径
                                cursor.moveToFirst();
                                videoPath = cursor
                                        .getString(cursor
                                                .getColumnIndex(MediaStore.Video.Media.DATA));
                                cursor.close();
                                //Log.i("Eaa_videoPaht", videoPath);
                            } else {
                                videoPath = Uri.decode(uri.getEncodedPath());
                            }

                            // 如果选择视频是否大于20M，不能上传
                            long fileSize = new File(videoPath).length();
                            if (fileSize > 1024 * 1024 * 20) {
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.tips_selectvideo), Toast.LENGTH_SHORT).show();
                                videoPath = null;
                                return;
                            }
                            addVideoThumb(videoPath);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case REQUEST_CAMERA: {
                    try {
                        if (null == data) {
                            Toast.makeText(CommentActivity.this,
                                    getResources().getString(R.string.tips_takevideofail), Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        }
                        //Log.i("Eaa_camera", data.toString());
                        Uri uri = data.getData();
                        if (!TextUtils.isEmpty(uri.getAuthority())
                                || !TextUtils.isEmpty(uri.getScheme())) {
                            // 查询选择视频
                            Cursor cursor = getContentResolver().query(uri,
                                    new String[] { MediaStore.Video.Media.DATA },
                                    null, null, null);
                            if (null != cursor) {
                                // 光标移动至开头 获取图片路径
                                cursor.moveToFirst();
                                videoPath = cursor
                                        .getString(cursor
                                                .getColumnIndex(MediaStore.Video.Media.DATA));
                                cursor.close();
                                //Log.i("Eaa_videoPaht", videoPath);
                            } else {
                                videoPath = Uri.decode(uri.getEncodedPath());
                            }

                        }

                        if (new File(videoPath).exists()) {
                            addVideoThumb(videoPath);
                        } else {
                            Toast.makeText(CommentActivity.this,
                                    getResources().getString(R.string.tips_takevideofail), Toast.LENGTH_LONG)
                                    .show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default:
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            //Toast.makeText(CommentActivity.this, getResources().getString(R.string.tips_docancle), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(CommentActivity.this, getResources().getString(R.string.tips_dofail),
                    Toast.LENGTH_LONG).show();
        }
        Log.i("comment", "onActivityResult hasViewo=" + hasVideo + ",itemNo=" + itemNo
                +",selectImages size "+selectImages.size()+",imageItem size"+imageItem.size()+",pathImage size "+pathImage.size());

    }

    @Override
    protected void onResume() {
        //if(selectImages != null && imageItem!=null){
        //Log.i("comment", "before resume hasViewo=" + hasVideo + ",itemNo=" + itemNo
        //	+",selectImages size "+selectImages.size()+",imageItem size "+imageItem.size()+",pathImage size "+pathImage.size());
        //}
        if (selectImages.size() > 0) {
            if (itemNo < 9 && itemNo < imageItem.size()) {
                imageItem.remove(itemNo);
            }
            for (int i = 0; i < selectImages.size(); i++) {
                Bitmap addbmp = null;
                try{
                    addbmp = Common.scaleBitmap(selectImages.get(i),
                            Common.winWidth, Common.winHeight);
                }catch(OutOfMemoryError e){

                }
                // 生成缩略图
                if(null == addbmp){
                    continue;
                }
                addbmp = ThumbnailUtils.extractThumbnail(addbmp, colWidth,
                        colWidth);
                // 将缩略图写入应用的图片缓存文件夹
                String imageName;

                imageName = Common.currentTime().trim();

                cacheFileName = Common.CACHEPHOTO_PATH + "cache" + imageName
                        + i + ".jpg";
                BufferedOutputStream fos;
                try {
                    fos = new BufferedOutputStream(new FileOutputStream(
                            cacheFileName));
                    addbmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 原图与缩略图成对出现，成对添加，成对删除
                pathImage.add(selectImages.get(i));
                cacheImages.add(cacheFileName);

                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(keyOfBitmap, addbmp); // 添加要显示的缩略图到GridView
                imageItem.add(map);
                itemNo++;
            }
            // 使加号后推
            if (itemNo < 9) {
                addSymbol();
            }
            simpleAdapter.notifyDataSetChanged();
            // 刷新后释放防止手机休眠后自动添加
            selectImages.clear();
        }
//		if(selectImages != null && imageItem!=null){
//			Log.i("comment", "after resume hasViewo=" + hasVideo + ",itemNo=" + itemNo
//				+",selectImages size "+selectImages.size()+",imageItem size"+imageItem.size()+",pathImage size "+pathImage.size());
//		}
        super.onResume();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("recentPhoto", recentPhoto);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        recentPhoto = savedInstanceState.getString("recentPhoto");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        // 删除本地缩略图
        for (int i = 0; i < cacheImages.size(); i++) {
            new File(cacheImages.get(i)).delete();
        }
        if (hasVideo >= 0) {
            new File(cacheVideo).delete();
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(int i = 0;i<imageItem.size();i++){
            if(i == hasVideo){
                continue;

            }
            Bitmap bitmap = (Bitmap) imageItem.get(i).get(keyOfBitmap);
            if(bitmap != null){
                bitmap.recycle();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    /*
     * Dialog对话框提示用户删除操作 position为删除图片位置
     */
    protected void dialog(final int position) {
        CustomDialog.Builder builder = new CustomDialog.Builder(CommentActivity.this);
        builder.setMessage(getResources().getString(R.string.areyousuredelete));
        builder.setTitle(getResources().getString(R.string.tips));
        builder.setPositiveButton(getResources().getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (position == hasVideo) {
                            imageItem.remove(position);
                            hasVideo = -1;
                            itemNo--;
                            videoPath = null;
                            new File(cacheVideo).delete();
                            simpleAdapter.notifyDataSetChanged();
                        } else {
                            int pos = position;
                            if(hasVideo != -1 && pos >hasVideo){
                                pos--;
                            }
                            if(pos >= imageItem.size() || pos >= pathImage.size()){
                                Log.i("comment","delete error :"+pos+"/"+pathImage.size());
                                return;
                            }
                            Log.i("comment","delete  :"+pos+"/"+pathImage.size());

                            imageItem.remove(position);
                            // 成对删除
                            String path = pathImage.get(pos);
                            StringBuffer buff = new StringBuffer();
                            buff.append("(").append(MediaStore.Images.ImageColumns.DATA)
                                    .append("=").append("'" + path + "'")
                                    .append(")");
                            Cursor cursor = getContentResolver().query(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    new String[] { MediaStore.Images.ImageColumns._ID },
                                    buff.toString(), null, null);
                            if (cursor.moveToNext()) {
                                long id = cursor.getLong(0);
                                TreeMap<Long, Uri> tree = selectPictures.getTreeMap();
                                tree.remove(id);
                                selectPictures.setTreeMap(tree);
                            }
                            cursor.close();
                            pathImage.remove(pos);
                            new File(cacheImages.get(pos)).delete();
                            cacheImages.remove(pos);
                            simpleAdapter.notifyDataSetChanged();
                            itemNo--;

                            if (pos < hasVideo) {
                                hasVideo--;
                                //Log.d("Eaa", "hasVideo=" + hasVideo);
                            }

                        }
                        if(itemNo == 8){
                            addSymbol();
                        }
                        Log.i("comment", "delete hasViewo=" + hasVideo + ",itemNo=" + itemNo
                                +",selectImages size "+selectImages.size()+",imageItem size"+imageItem.size()+",pathImage size "+pathImage.size());


                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancl),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    // 初始化评论事件
    private void initEvent() {
        behaviour = behaviourList.getSelectedPosition() + 1;
        stay = durationList.getSelectedPosition() + 1;
        companion = partnerNumberList.getSelectedPosition() + 1;
        relation = relationList.getSelectedPosition() + 1;
        comment = new InterestMarkerData();
        // 设置事件属性
        /*if (createTime == null) {
            comment.setCreateTime(String.valueOf(System.currentTimeMillis()));
        }else {
            comment.setCreateTime(createTime);
        }*/
        comment.setCreateTime(createTime);
        comment.setLongitude(longitude);
        comment.setLatitude(latitude);
        comment.setAltitude(altitude);
        comment.setCountry(country);
        comment.setProvince(province);
        comment.setCity(city);
        comment.setPlaceName(placeName);
        comment.setComment(commentText);
        comment.setPicCount(fileNum);
        comment.setTraceNo(traceID);
        comment.setVideoCount(0);
        comment.setAudioCount(0);
        comment.setUserId(Common.getUserID(getApplicationContext()));
        comment.setFeeling(feeling);
        comment.setBehaviour(behaviour);
        comment.setDuration(stay);
        comment.setCompanionCount(companion);
        comment.setRelationship(relation);
        comment.setStateType(stateType);
        if (isShare) {
            comment.setShare(1);
        }else {
            comment.setShare(0);
        }
        // 将要上传的文件添加到sharedPreferences
        SharedPreferences uploadFiles = getSharedPreferences(SHAREDFILES,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = uploadFiles.edit();

        Log.i("comment", "createTime="+createTime+"|longitude="+longitude+"|latitude="+latitude
                +"|altitude="+altitude+"|country="+country+"|province="+province+"|city="+city
                +"|placeName=" + placeName + "|comment=" + commentText + "|fileNum=" + fileNum
                +"|traceID="+traceID+"|videoCount="+0+"|audioCount="+0
                +"|userID="+Common.getUserID(getApplicationContext())+"|feeling=" + feeling
                + "|behaviour="+behaviour+"|stay="+stay +"|companion="+companion+"|relation="+relation
                +"|stateType="+stateType+"|share"+share);

        // 设置文件属性
        for (int i = 0; i < fileNum; i++) { // 图片
            files[i] = new CommentMediaFilesData();
            files[i].setFileNo(i);
            files[i].setDateTime(createTime);
            files[i].setFileName(pathImage.get(i));
            files[i].setThumbnailName(cacheImages.get(i));
            files[i].setFileType(1);
            edit.putString(
                    createTime + File.separator + i + File.separator + 1
                            + File.separator
                            + Common.getUserId(getApplicationContext()),
                    pathImage.get(i));
        }
        edit.apply();
        // 视频
        if (videoPath != null) {
            comment.setVideoCount(1);
            files[fileNum] = new CommentMediaFilesData();
            files[fileNum].setFileNo(fileNum);
            files[fileNum].setDateTime(createTime);
            files[fileNum].setFileName(videoPath);
            files[fileNum].setThumbnailName(cacheVideo);
            files[fileNum].setFileType(2);
            comment.setPicCount(fileNum + 1);
            edit.putString(
                    createTime + File.separator + fileNum + File.separator + 2
                            + File.separator
                            + Common.getUserId(getApplicationContext()),
                    videoPath);
        }
        edit.apply();
    }

    private void commitEvent() {
        dbHelper = new PhotoDBHelper(this, PhotoDBHelper.DBWRITE);//打开兴趣点数据库
        traceHelper = new MyTraceDBHelper(this);//轨迹信息数据库
        TraceData tracedata = new TraceData();
        tracedata = traceHelper.queryfromTrailbytraceID(comment.getTraceNo(), comment.getUserId());
        Log.i("traceee", "comment.traceID:"+comment.getTraceNo() + ", comment.userID:" + comment.getUserId());
        if(tracedata!=null){
            tracedata.setPoiCount(tracedata.getPoiCount()+1);
            Log.i("traceee", "tracedata = "+tracedata.getPoiCount()+tracedata.getCalorie()
                    +tracedata.getDistance()+tracedata.getDuration()+tracedata.getEndTime()
                    +tracedata.getShareType()+tracedata.getSportTypes()+tracedata.getStartTime()
                    +tracedata.getTraceName()+tracedata.getTraceID()+tracedata.getUserID());
            traceHelper.updatetrail(tracedata, comment.getTraceNo(), comment.getUserId());
        }
        int result = dbHelper.insertEvent(comment);
        Log.i("Eaa", "comment result=" + result);
        for (int i = 0; i < itemNo; i++) {
            dbHelper.inserFile(files[i]);
        }
        dbHelper.closeDB();
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        // EventData
        bundle.putString("createTime", comment.getCreateTime());
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);

    }

    // 向gridView中添加＋号
    private void addSymbol() {
        Bitmap bmp;
        // 加载图片
        if (itemNo < 9) {
            bmp = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.add_img);
            // 获得图片缩略图
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(keyOfBitmap, bmp);
            imageItem.add(map);
        }

    }

    private void addVideoThumb(String videoPath) {
        // 添加视频缩略图
        Bitmap bmp;
        HashMap<String, Object> map;
        if (null != videoPath) {
            if (itemNo < 9 && itemNo < imageItem.size()) {
                imageItem.remove(itemNo);//删掉后面的+号
            }
            if (hasVideo < 0) {
                bmp = ThumbnailUtils.createVideoThumbnail(videoPath,
                        MediaStore.Images.Thumbnails.MINI_KIND);
                map = new HashMap<String, Object>();
                hasVideo = itemNo;
                //Log.d("Eaa_addVideo", "add hasVideo=" + hasVideo);
                itemNo++;
            } else {
                map = imageItem.remove(hasVideo);
                bmp = ThumbnailUtils.createVideoThumbnail(videoPath,
                        MediaStore.Images.Thumbnails.MINI_KIND);
            }

            bmp = ThumbnailUtils.extractThumbnail(bmp, colWidth, colWidth);

            String videoName = Common.currentTime().trim();
            cacheFileName = Common.CACHEPHOTO_PATH + "cacheVideo" + videoName
                    + ".jpg";
            Bitmap videoPlay = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.video_play);
            Bitmap newBmp = bmp.copy(Bitmap.Config.RGB_565, true);
            Canvas canvas = new Canvas(newBmp);
            canvas.drawBitmap(bmp, 0, 0, null);
            canvas.drawBitmap(videoPlay,
                    bmp.getWidth() / 2 - videoPlay.getWidth() / 2,
                    bmp.getHeight() / 2 - videoPlay.getHeight() / 2, null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
            bmp = newBmp;
            map.put(keyOfBitmap, bmp);
            imageItem.add(hasVideo, map);
            addSymbol();
            BufferedOutputStream bos;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(
                        cacheFileName));
                bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            simpleAdapter.notifyDataSetChanged();
            cacheVideo = cacheFileName;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 右上角确定按钮
            case R.id.title_right_text:
                // 以提交时间为创建时间
                commentText = editComment.getText().toString().trim();
                if ("".equals(commentText) || commentText == null) {
                    Toast.makeText(CommentActivity.this,getResources().getString(R.string.tips_commentsubmit),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                if(behaviourList.getSelectedPosition() == -1){
                    Toast.makeText(CommentActivity.this, getResources().getString(R.string.tips_selectbehaviour),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                if(durationList.getSelectedPosition() == -1){
                    Toast.makeText(CommentActivity.this, getResources().getString(R.string.tips_selectduration),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                if(partnerNumberList.getSelectedPosition() == -1){
                    Toast.makeText(CommentActivity.this, getResources().getString(R.string.tips_selectpartnernum),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                if(relationList.getSelectedPosition() == -1){
                    Toast.makeText(CommentActivity.this, getResources().getString(R.string.tips_selectrelationship),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                if(createTime == null){
                    createTime = Common.currentTime();

                }

                fileNum = hasVideo < 0 ? itemNo : itemNo - 1;
                files = new CommentMediaFilesData[fileNum + 2];
                initEvent();
                commitEvent();
                this.finish();
                break;
            case R.id.comment_photo://点击拍照按钮
                if (ContextCompat.checkSelfPermission(CommentActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CommentActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 2);
                } else {
                    takePic();
                }
                break;
            case R.id.comment_video:
                if (ContextCompat.checkSelfPermission(CommentActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CommentActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 3);
                }else {
                    takeVideo();
                }
                break;
            case R.id.imageView_place:
                // 点击图标和点击文字效果相同
            case R.id.textView_place:
                Intent intent = new Intent(CommentActivity.this, ConfirmPlaceActivity.class);
                placeName = (String) textOfPlace.getText();
                intent.putExtra("place", placeName);
                startActivityForResult(intent, REQUEST_PLACE);
                break;
            default:
                break;
        }
    }

    /**
     * 逆地理编码，通过经纬度获取所在的国家、省、城市
     * @param regeocodeResult
     * @param i
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        country = regeocodeAddress.getCountry();
        province = regeocodeAddress.getProvince();
        city = regeocodeAddress.getCity();
        /*Log.i("Address", country);
        Log.i("Address", province);
        Log.i("Address", city);*/
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    /**
     * 将图片添加到系统媒体库中
     *
     * @author Eaa
     *
     */
    public class InsertPicToMediaStore extends AsyncTask<Object, Object, Object> {

        @Override
        protected Object doInBackground(Object... params) {
            // 图片路径
            Context context = (Context) params[0];
            String path = (String) params[1];
            long time = Common.timeStamp((String) params[2]);
            File imgFile = new File(path);
            Uri uri = Uri.fromFile(imgFile);

            ContentValues values = new ContentValues();
            ContentResolver resolver = context.getContentResolver();
            values.put(MediaStore.Images.ImageColumns.DATA, path);
            values.put(MediaStore.Images.ImageColumns.TITLE,
                    uri.getLastPathSegment());
            values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME,
                    uri.getLastPathSegment());
            values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, time);
            values.put(MediaStore.Images.ImageColumns.DATE_ADDED, time / 1000);
            values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED,
                    time / 1000);
            values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpg");
            Uri mediaUri = resolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            resolver.update(mediaUri, values, null, null);

            return mediaUri;
        }

        @Override
        protected void onPostExecute(Object result) {
            Uri insertUri = (Uri) result;
            Cursor cursor = getContentResolver().query(insertUri,
                    new String[] { MediaStore.Images.ImageColumns._ID }, null, null, null);
            if (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                if (selectPictures != null) {
                    TreeMap<Long, Uri> tree = selectPictures.getTreeMap();
                    tree.put(id, insertUri);
                    selectPictures.setTreeMap(tree);
                } else {
                    selectPictures = new SelectedTreeMap();
                    TreeMap<Long, Uri> tree = new TreeMap<Long, Uri>();
                    tree.put(id, insertUri);
                    selectPictures.setTreeMap(tree);
                }

            }
            cursor.close();
            super.onPostExecute(result);
        }
    }
}
