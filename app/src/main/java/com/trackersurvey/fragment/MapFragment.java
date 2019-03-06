package com.trackersurvey.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.TileOverlay;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.model.TileProvider;
import com.trackersurvey.bean.FileInfoData;
import com.trackersurvey.bean.PointOfInterestData;
import com.trackersurvey.http.DownloadPoiChoices;
import com.trackersurvey.http.EndTraceRequest;
import com.trackersurvey.model.PoiChoiceModel;
import com.trackersurvey.model.StepData;
import com.trackersurvey.db.MyTraceDBHelper;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.http.StartTraceRequest;
import com.trackersurvey.model.TraceData;
import com.trackersurvey.db.PhotoDBHelper;
import com.trackersurvey.db.PointOfInterestDBHelper;
import com.trackersurvey.model.GpsData;
import com.trackersurvey.happynavi.BGRunningGuideActivity;
import com.trackersurvey.happynavi.CommentActivity;
import com.trackersurvey.happynavi.LoginActivity;
import com.trackersurvey.happynavi.R;
import com.trackersurvey.httpconnection.PostCheckVersion;
import com.trackersurvey.service.CommentUploadService;
import com.trackersurvey.service.DownloadService;
import com.trackersurvey.service.LocationService;
import com.trackersurvey.service.StepCounterService;
import com.trackersurvey.util.ActivityCollector;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.CustomDialog;
import com.trackersurvey.util.GsonHelper;
import com.trackersurvey.util.ShareToWeChat;
import com.trackersurvey.util.SportTypeChangeDialog;
import com.trackersurvey.util.SportTypeDialog;
import com.trackersurvey.util.StepDetector;
import com.trackersurvey.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by lenovo on 2017/9/3.
 * 地图页面
 */

public class MapFragment extends Fragment implements View.OnClickListener, LocationSource, AMapLocationListener,
        AMap.OnMapClickListener, AMap.OnMarkerClickListener {

    private View           homepageLayout;
    private ImageButton    startTrail;
    private ImageButton    changeSportTypeIb;
    private ImageButton    endTrail;
    private ImageButton    takePhoto;
    private TextView       stepTv;
    private TextView       locationTv;
    private TextView       calculateTv;
    private ProgressDialog proDialog = null;
    private int            sportType;

    private AMap            aMap;
    private MapView         mapView;
    private Marker          interestpoint;           //响应用户点击的位置
    private Marker          startMarker = null;      //起点标记
    private Marker          endMarker   = null;        //终点标记
    private Polyline        polyline    = null;        //轨迹连线
    private PolylineOptions options;        //轨迹线属性

    private OnLocationChangedListener mListener;
    private AMapLocationClient        mlocationClient;

    private LatLng       finalLatLng = new LatLng(0, 0);  //轨迹终点
    private LatLng       currentLatLng;                 //定位到的当前位置
    private AMapLocation currentAlocation;
    private List<LatLng> points;                 //轨迹点的成员

    private boolean isstart              = false;
    private boolean ispause              = false;
    private boolean isend                = true;
    private boolean bound_trace          = false;
    private boolean bound_comment_upload = false;
    private boolean istimeset            = false;
    private boolean iscountstep          = false;
    private boolean isShowNonLocDlg      = false; //无法定位对话框是否正在显示

    private boolean isTraceIDchanged = false;

    public static final int MENU_ROUTE = 0;
    public static final int MENU_NAVI  = 1;

    private long                      traceID            = 0;
    private int                       total_step         = 0;   //走的总步数
    private TraceData                 tracedata          = new TraceData(); // 轨迹信息
    private String                    traceName          = "";
    private StepData                  stepdata           = new StepData(); // 步行轨迹的步数信息
    private List<GpsData>             tracegps           = new ArrayList<GpsData>();
    private MyBroadcastReceiver       myReceiver         = null;//用于接收后台发送的定位广播
    private AccuracyBroadcastReceiver accuracyReciver    = null;
    private BroadcastReceiver         connectionReceiver = null; // 用于监听网络状态变化的广播

    private LocationService      locationService;
    private CommentUploadService commentUploadService;
    private Intent               locationServiceIntent;
    private Intent               commentServiceIntent;
    private Intent               stepCountServiceIntent;
    private Intent               updateServiceIntent;
    private Thread               stepThread;
    private MyTraceDBHelper      traceDBHelper;
    private SharedPreferences    sp;  //存储基本配置信息 如账号、密码
    private SharedPreferences    uploadCache;//存储待上传的评论信息

    private final String              MY_ACTION          = "android.intent.action.LOCATION_RECEIVER";
    private final String              PULLREFRESH_ACTION = "android.intent.action.PULLREFRESH_RECEIVER";
    private final String              ACCURACY_ACTION    = "android.intent.action.ACCURACY_RECEIVER";
    private final String REFRESH_ACTION = "android.intent.action.REFRESH_RECEIVER";
    //private static final String URL_STARTTRAIL = Common.url+"reqTraceNo.aspx";
    private       String              URL_ENDTRAIL       = null;
    //private static final String URL_GET4TIME = Common.url+"request.aspx";
    private       String              URL_CHECKUPDATE    = null;
    private       String              URL_GETPOI         = null;
    public final  int                 REQUSET_COMMENT    = 1;
    private       PointOfInterestData behaviourData, durationData, partnerNumData, relationData;
    private PointOfInterestDBHelper poiDBHelper = null;
    private Cursor                  cursor;
    private int                     checkedItem = 0;
    private double                  currentLongitude, currentLatitude, currentAltitude, currentLongitude1, currentLatitude1, currentAltitude1;
    private String[] degreeLngArr, minuteLngArr, secondLngArr, degreeLatArr, minuteLatArr, secondLatArr;
    private String degreeLngStr, minuteLngStr, secondLngStr, degreeLatStr, minuteLatStr, secondLatStr, currentAltitudeStr;
    private double minuteLng1, secondLng1, minuteLat1, secondLat1;
    private PopupWindow mPopupWindow;

    private RelativeLayout     showDistance;    //测量模式界面
    private RelativeLayout     showTips;
    private TileProvider       tileProvider;        //瓦片提供者，用于转换地图图层
    private TileOverlayOptions tileOverlayOptions;
    private TileOverlay        tileOverlay;

    private ListView                           dialogList;
    private ArrayList<HashMap<String, Object>> dialogListItem;
    //private LinearLayout dialogLayout;
    private View                               view;
    private AlertDialog                        dialog;

    private PoiChoiceModel poiChoiceModel;
    public static int poiCount = 0;

    @Nullable

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homepageLayout = inflater.inflate(R.layout.fragment_map, container, false);
        ShareToWeChat.registToWeChat(getContext());

//        EventBus.getDefault().register(this);
        mapView = (MapView) homepageLayout.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        stepTv = homepageLayout.findViewById(R.id.tv_step);
        stepTv.setVisibility(View.INVISIBLE);
        locationTv = homepageLayout.findViewById(R.id.tv_location);
        // 这里放测量相关的控件
        startTrail = homepageLayout.findViewById(R.id.imgbtn_starttrail); // 开始记录按钮
        changeSportTypeIb = homepageLayout.findViewById(R.id.change_sport_type_ib); // 改变运动状态
        takePhoto = homepageLayout.findViewById(R.id.imgbtn_takephoto);
        endTrail = homepageLayout.findViewById(R.id.imgbtn_endtrail);
        endTrail.setVisibility(View.INVISIBLE);
        startTrail.setOnClickListener(this);
        changeSportTypeIb.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        endTrail.setOnClickListener(this);

        traceDBHelper = new MyTraceDBHelper(getContext()); // 创建轨迹数据库
        poiDBHelper = new PointOfInterestDBHelper(getContext());//创建POI数据库
        // sp 初始化
        sp = getActivity().getSharedPreferences("config", MODE_PRIVATE);//私有参数
        initAMap(); // 初始化地图
        if (Common.url != null && !Common.url.equals("")) {

        } else {
            Common.url = getResources().getString(R.string.url);
        }
        URL_ENDTRAIL = Common.url + "reqTraceNo.aspx";
        URL_CHECKUPDATE = Common.url + "request.aspx";
        URL_GETPOI = Common.url + "requestInfo.aspx";
        initPOI(); // 获取兴趣点列表选项
        return homepageLayout;
    }

    private void initAMap() {
        //获取屏幕分辨率
        //SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        Common.winWidth = sp.getInt(LoginActivity.winWidth, 720);
        Common.winHeight = sp.getInt(LoginActivity.winHeight, 1280);
        Common.ppiScale = sp.getFloat(LoginActivity.PPISCALE, 1.5f);
        //创建相册文件夹
        Common.createFileDir();
        connectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // getActivity()这里不知道改得对不对
                ConnectivityManager connectMgr = (ConnectivityManager) getActivity().
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = connectMgr.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()) {
                    //有网络连接
                    if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        //wifi连接
                        /**后期设置中加入“wifi状态下自动更新”选项，此处加入bool量判断是否自动检查
                         **/
                        Log.i("phonelog", "当前WiFi连接");
                        Common.isWiFiConnected = true;
                        if (Common.isUpdationg && Common.fileInfo != null) {
                            Log.i("phonelog", "WiFi下继续下载");
                            // 通知Service继续下载
                            updateServiceIntent = new Intent(getContext(), DownloadService.class);
                            updateServiceIntent.setAction(DownloadService.ACTION_START);
                            updateServiceIntent.putExtra("fileInfo", Common.fileInfo);
                            getActivity().startService(updateServiceIntent);
                        } else {
                            // 原参数是getApplicationContext()
                            if (Common.isAutoUpdate(getContext())) {
                                Log.i("phonelog", "wifi下检查更新");
                                String version = null;
                                if (Common.version == null || Common.version.equals("")) {
                                    version = Common.getAppVersionName(getContext());
                                } else {
                                    version = Common.version;
                                }
                                // 检查更新app
                                if (version != null && !version.equals("")) {
                                    PostCheckVersion checkVersion = new PostCheckVersion(updatehandler, URL_CHECKUPDATE,
                                            Common.getDeviceId(getContext()), version);
                                    checkVersion.start();
                                }
                            } else {
                                Log.i("phonelog", "自动检查更新关闭");
                            }
                        }
                    } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        // connect network,读取本地sharedPreferences文件，上传之前未完成上传的部分
                        Log.i("phonelog", "当前GPRS数据连接");
                        Common.isWiFiConnected = false;
                        Log.i("phonelog", "WiFi连接断开");
                        if (Common.isUpdationg && Common.fileInfo != null) {
                            // 通知Service暂停下载
                            updateServiceIntent = new Intent(getContext(), DownloadService.class);
                            updateServiceIntent.setAction(DownloadService.ACTION_STOP);
                            updateServiceIntent.putExtra("fileInfo", Common.fileInfo);
                            getActivity().startService(updateServiceIntent);
                            Log.i("phonelog", "发送暂停命令");
                        }
                    } else {
                        Common.isWiFiConnected = false;
                        Log.i("phonelog", "WiFi连接断开");
                    }
                } else {
                    //无网络连接
                    Log.i("phonelog", "Main，当前无网络");
                    Common.isWiFiConnected = false;
                    if (Common.isUpdationg && Common.fileInfo != null) {
                        // 通知Service暂停下载
                        updateServiceIntent = new Intent(getContext(), DownloadService.class);
                        updateServiceIntent.setAction(DownloadService.ACTION_STOP);
                        updateServiceIntent.putExtra("fileInfo", Common.fileInfo);
                        getActivity().startService(updateServiceIntent);
                        Log.i("phonelog", "发送暂停命令");
                    }
                    if (!Common.checkGPS(getContext())) {
                        //网络没开，gps没开，无法定位，提示
                        if (!isShowNonLocDlg) {
                            isShowNonLocDlg = true;
                            boolean isShowBadLoc = sp.getBoolean("isShowBadLoc", true);
                            if (isShowBadLoc) {
                                Log.i("phonelog", "网络没开，gps没开，无法定位，提示");
                                showDlg_badloc();
                            }
                        }
                    }
                }
            }
        };
        //注册监听网络连接状态广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(connectionReceiver, intentFilter);
        uploadCache = getActivity().getSharedPreferences("uploadCache", Activity.MODE_PRIVATE);

        if (aMap == null) {
            aMap = mapView.getMap();
            registerListener();
            setUpMap();
            // 检测是否是游客身份登录
            if (!Common.isVisiter()) {
                // 开启定位Service
                setUpService();
            }
        }
    }

    // 注册监听
    private void registerListener() {
        aMap.setOnMapClickListener(this); // 地图点击监听
        aMap.setOnMarkerClickListener(this); // 标记点击监听
        //aMap.setOnInfoWindowClickListener(MainActivity.this);
        //aMap.setInfoWindowAdapter(MainActivity.this);
    }

    // 设置一些aMap的属性
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.mipmap.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 255, 255, 255));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        //连续定位、且将视角不移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）
        //myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        aMap.setMyLocationStyle(myLocationStyle);

        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setCompassEnabled(true);  //启用罗盘
        aMap.getUiSettings().setScaleControlsEnabled(false);//启用比例尺
        aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);//设置缩放按钮在右侧中间位置
        //aMap.getUiSettings().setZoomGesturesEnabled(false);//屏蔽双击放大地图操作
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE); // 这个方法过时了
        aMap.moveCamera(CameraUpdateFactory.zoomTo(12));
        double lastLongitude = sp.getFloat("lastLongitude", 0);
        double lastLatitude = sp.getFloat("lastLatitude", 0);
        if (lastLongitude > 0 && lastLatitude > 0) {
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lastLatitude, lastLongitude)));
        }
        points = new ArrayList<>();
        interestpoint = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    }

    // 设置Service参数
    public void setUpService() {
        //注册监听后台定位情况的广播，用于更新轨迹显示
        myReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MY_ACTION);//为IntentFilter添加Action
        getActivity().registerReceiver(myReceiver, filter);//注册定位广播
        //注册精度监听广播
        accuracyReciver = new AccuracyBroadcastReceiver();
        IntentFilter accuracyfilter = new IntentFilter();//生成一个IntentFilter对象
        accuracyfilter.addAction(ACCURACY_ACTION);//为IntentFilter添加Action
        getActivity().registerReceiver(accuracyReciver, accuracyfilter);//注册广播
        // 定位服务
        locationServiceIntent = new Intent(getContext(), LocationService.class);
        locationServiceIntent.putExtra("token", sp.getString("token", ""));
        // 上传兴趣点服务
        commentServiceIntent = new Intent(getContext(), CommentUploadService.class); // 上传兴趣点Service
        //getActivity().startService(commentServiceIntent);
        getActivity().bindService(commentServiceIntent, commentConnection, Context.BIND_AUTO_CREATE);
        //commentUploadService.getToComment("");

        // 申请定位权限
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // 已申请则开启定位服务
            getActivity().bindService(locationServiceIntent,
                    connection, Context.BIND_AUTO_CREATE);
        }
        getActivity().bindService(locationServiceIntent,
                connection, Context.BIND_AUTO_CREATE);
        stepCountServiceIntent = new Intent(getContext(), StepCounterService.class);
        /*
         * MainActivity.this.getApplicationContext().
         * 在TabActivy的TabHost中的Activity如果需要bindService的话
         * ，需要先调用getApplicationContext()获取其所属的Activity的上下文环境才能正常bindService
         */
        if (stepThread == null) {
            stepThread = new Thread() {// 子线程用于监听当前步数的变化
                @Override
                public void run() {
                    super.run();
                    while (iscountstep) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (StepCounterService.FLAG) {
                            Message msg = Message.obtain();
                            msg.what = 9;
                            handler.sendMessage(msg);// 通知主线程
                        }
                    }
                }
            };
        }
        /**
         判断上次轨迹记录是否意外中断，如果有意外中断，提醒用户是否继续记录
         */
        traceID = traceDBHelper.getUnStopStatusExists(Common.getUserId(getContext()));
        if (traceID != 0) { //存在中断的轨迹,0是轨迹号
            CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.tip));
            builder.setMessage(getResources().getString(R.string.tips_interrupttrace_msg));
            builder.setNegativeButton(getResources().getString(R.string.cancl), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    traceDBHelper.updateStatus(traceID, 2, Common.getUserID(getContext()));
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tracedata = traceDBHelper.queryfromTrailbytraceID(traceID, Common.getUserId(getContext()));
                    initStartInfo();
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
        boolean isShowBGRGuide = sp.getBoolean("isShowBGRGuide", true);
        if (isShowBGRGuide) {
            //指引用户如何添加白名单
            showBGRunGuide();
        }
    }

    // 权限获取回调函数
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getActivity().bindService(locationServiceIntent,
                            connection, Context.BIND_AUTO_CREATE);
                } else {
                    Toast.makeText(getContext(), "未获取位置权限，无法定位您的位置", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // 实现绑定服务的匿名类ServiceConnection
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            locationService = binder.getService();
            bound_trace = true;
            if (!locationService.isWorking()) {
                locationService.getToWork();
                Log.i("LogDemo", "定位服务开始工作了！当前时间是" + Common.currentTime());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound_trace = false;
        }
    };

    // 实现上传兴趣点的匿名类
    private ServiceConnection commentConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CommentUploadService.CommentBinder commentBinder = (CommentUploadService.CommentBinder) service;
            commentUploadService = commentBinder.getService();
            bound_comment_upload = true;
            commentUploadService.getToComment("");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    // 精度监听广播
    public class AccuracyBroadcastReceiver extends BroadcastReceiver {
        public AccuracyBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Common.isHighAccuracy = false;
            ToastUtil.show(getContext(), getResources().getString(R.string.tips_accuracy_msg));
        }
    }

    // 监听后台定位情况的广播
    public class MyBroadcastReceiver extends BroadcastReceiver {
        public MyBroadcastReceiver() {
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("backloc", "我收到广播了");
            // 判断用户账号长度是否<10
            //            if(Common.getUserId(getContext()).length()<10) {
            //                Intent mIntent = new Intent();
            //                mIntent.setClass(getContext(), LoginActivity.class);
            //                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //                startActivity(mIntent);
            //                getActivity().finish();
            //            }
            if (mListener != null && Common.aLocation != null && Common.aLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(Common.aLocation);// 显示系统小蓝点
                //Log.i("trailadapter", "main----onLocationChanged");
                //currentAlocation=aLocation;
                currentLatLng = new LatLng(Common.aLocation.getLatitude(), Common.aLocation.getLongitude());
                //在地图页面显示当前位置的经纬度信息
                currentLongitude = Common.aLocation.getLongitude();    //经度
                currentLatitude = Common.aLocation.getLatitude();    //纬度
                currentAltitude = Common.aLocation.getAltitude();    //海拔

                currentAltitudeStr = String.valueOf(currentAltitude);
                BigDecimal currentLongitudeTemp = new BigDecimal(currentLongitude);
                currentLongitude1 = currentLongitudeTemp.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();

                BigDecimal currentLatitudeTemp = new BigDecimal(currentLatitude);
                currentLatitude1 = currentLatitudeTemp.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();

                BigDecimal currentAltitudeTemp = new BigDecimal(currentAltitude);
                currentAltitude1 = currentAltitudeTemp.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();

                degreeLngArr = String.valueOf(currentLongitude).split("[.]");    //换算经度
                degreeLngStr = degreeLngArr[0];                                    //度
                minuteLng1 = Double.parseDouble("0." + degreeLngArr[1]) * 60;
                minuteLngArr = String.valueOf(minuteLng1).split("[.]");
                minuteLngStr = minuteLngArr[0];                                    //分
                secondLng1 = Double.parseDouble("0." + minuteLngArr[1]) * 60;
                secondLngArr = String.valueOf(secondLng1).split("[.]");
                secondLngStr = secondLngArr[0];                                    //秒

                degreeLatArr = String.valueOf(currentLatitude).split("[.]");    //换算纬度
                degreeLatStr = degreeLatArr[0];                                    //度
                minuteLat1 = Double.parseDouble("0." + degreeLatArr[1]) * 60;
                minuteLatArr = String.valueOf(minuteLat1).split("[.]");
                minuteLatStr = minuteLatArr[0];                                    //分
                secondLat1 = Double.parseDouble("0." + minuteLatArr[1]) * 60;
                secondLatArr = String.valueOf(secondLat1).split("[.]");
                secondLatStr = secondLatArr[0];                                    //秒
                //显示当前位置的经纬度
                if (currentAltitude > 0) {
                    if (currentLongitude > 0 && currentLatitude > 0) {
                        locationTv.setText(getResources().getString(R.string.longitude) + currentLongitude1 + "E；" +
                                getResources().getString(R.string.latitude) + currentLatitude1 + "N；" + getResources().getString(R.string.altitude) + currentAltitude1 + "m");
                    }
                    if (currentLongitude > 0 && currentLatitude < 0) {
                        locationTv.setText(getResources().getString(R.string.longitude) + currentLongitude1 + "E；" +
                                getResources().getString(R.string.latitude) + currentLatitude1 + "S；" + getResources().getString(R.string.altitude) + currentAltitude1 + "m");
                    }
                    if (currentLatitude < 0 && currentLatitude > 0) {
                        locationTv.setText(getResources().getString(R.string.longitude) + currentLongitude1 + "W；" +
                                getResources().getString(R.string.latitude) + currentLatitude1 + "N；" + getResources().getString(R.string.altitude) + currentAltitude1 + "m");
                    }
                    if (currentLatitude < 0 && currentLatitude < 0) {
                        locationTv.setText(getResources().getString(R.string.longitude) + currentLongitude1 + "W；" +
                                getResources().getString(R.string.latitude) + currentLatitude1 + "S；" + getResources().getString(R.string.altitude) + currentAltitude1 + "m");
                    }
                } else {
                    if (currentLongitude > 0 && currentLatitude > 0) {
                        locationTv.setText(getResources().getString(R.string.longitude) + currentLongitude1 + "E；"
                                + getResources().getString(R.string.latitude) + currentLatitude1 + "N");
                    }
                    if (currentLongitude > 0 && currentLatitude < 0) {
                        locationTv.setText(getResources().getString(R.string.longitude) + currentLongitude1 + "E；"
                                + getResources().getString(R.string.latitude) + currentLatitude1 + "S");
                    }
                    if (currentLatitude < 0 && currentLatitude > 0) {
                        locationTv.setText(getResources().getString(R.string.longitude) + currentLongitude1 + "W；"
                                + getResources().getString(R.string.latitude) + currentLatitude1 + "N");
                    }
                    if (currentLatitude < 0 && currentLatitude < 0) {
                        locationTv.setText(getResources().getString(R.string.longitude) + currentLongitude1 + "W；"
                                + getResources().getString(R.string.latitude) + currentLatitude1 + "S");
                    }
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putFloat("lastLongitude", (float) currentLatLng.longitude);
                editor.putFloat("lastLatitude", (float) currentLatLng.latitude);
                editor.commit();
                // 点击开始记录时，Common.isRecording = true;
                // 这个条件保证先插入一条带有TraceID的位置数据，再执行refreshTrace()，插入一条轨迹数据
                if (Common.isRecording) {
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(currentLatLng));
                    if (isstart && isend) {//开启新的轨迹记录
                        //aMap.clear();
                        //clearTrace();
                        isend = false;
                        //Log.i("LogDemo", "开画");
                    }
                    if (refreshTrace()) {
                        if (!points.isEmpty()) {
                            points.clear();
                        }
                        for (int i = 0; i < tracegps.size(); i++) {
                            LatLng point = new LatLng(tracegps.get(i).getLatitude(), tracegps.get(i).getLongitude());
                            points.add(point);
                            if (points.size() > 1) {
                                if (point.equals(points.get(points.size() - 2))) {//新记录的点与上一个点相同，删除
                                    points.remove(points.size() - 1);
                                    //Log.i("LogDemo", "removepoint");
                                }
                                finalLatLng = points.get(points.size() - 1);
                            }
                        }
                        tracegps.clear();
                        drawPoints(points);//高德绘制轨迹
                    }
                }
            }
        }
    }

    public void drawPoints(List<LatLng> points) {
        int size = points.size();
        //Toast.makeText(MainActivity.this, "drawPoints,size="+size, 0).show();
        LatLng pt = points.get(size - 1);
        if (size == 1) {
            if (startMarker != null) {
                startMarker.remove();
            }
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pt, 16));
            startMarker = aMap.addMarker(new MarkerOptions().position(pt).title(getResources()
                    .getString(R.string.starticon)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.start)));
        } else {
            if (startMarker == null) {
                startMarker = aMap.addMarker(new MarkerOptions().position(points.get(0)).title(getResources()
                        .getString(R.string.starticon)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.start)));
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pt, 16));
            }
            //初始化轨迹属性Color.argb(255, 100, 100, 100)灰色
            options = new PolylineOptions().width(15).geodesic(true).color(Color.GREEN);
            options.addAll(points);
            //polyline.setPoints(options.getPoints());
            if (polyline != null) {
                polyline.remove();
            }
            polyline = aMap.addPolyline(options);
        }
    }

    public void clearTrace() {
        //Log.i("LogDemo", "clear trace");
        if (startMarker != null) {
            startMarker.remove();
        }
        if (endMarker != null) {
            endMarker.remove();
        }
        if (polyline != null) {
            polyline.remove();
            polyline = null;
            //Log.i("LogDemo", "clear polyline!!!");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgbtn_starttrail: // 点击开始记录按钮
                if (Common.isVisiter()) {
                    Common.DialogForVisiter(getContext());
                    return;
                }

                if (isend) {
                    startTrail(); // 弹出开始记录对话框，继续接下来的操作
                } else {//继续记录
                    Toast.makeText(getContext(), "继续记录", Toast.LENGTH_SHORT).show();
                    locationService.setTraceID(traceID);
                    locationService.changeStatus(true); // 改为记录轨迹状态
                    if (tracedata.getSportTypes() == 1) {
                        //轨迹类型为步行，记录步数
                        locationService.changeSportType(true);
                    }
                    //traceService.changeGpsTime(Common.getRecLocFrequenct(getApplicationContext()));
                    startTrail.setVisibility(View.INVISIBLE);
                    changeSportTypeIb.setVisibility(View.VISIBLE);
                    // pauseTrail.setVisibility(View.VISIBLE);
                    // takephoto.setVisibility(View.VISIBLE);
                    endTrail.setVisibility(View.VISIBLE);
                    isstart = true;
                    ispause = false;
                    isend = false;
                }
                break;
            case R.id.change_sport_type_ib:
                changeSportType();
                break;
            case R.id.imgbtn_endtrail:
                //Toast.makeText(MainActivity.this, "结束记录",Toast.LENGTH_SHORT).show();
                CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.end));
                builder.setMessage(getResources().getString(R.string.tips_endtracedlg_msg));
                builder.setNegativeButton(getResources().getString(R.string.cancl), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endTrail();
                        // takephoto.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                //Log.i("LogDemo", "结束记录");
                break;
            case R.id.imgbtn_takephoto:
                if (Common.isVisiter()) {
                    Common.DialogForVisiter(getContext());
                    return;
                }
                //从POI列表项数据库中取数据
                ArrayList<String> behaviour = poiDBHelper.getBehaviour();
                ArrayList<String> duration = poiDBHelper.getDuration();
                ArrayList<String> partnerNum = poiDBHelper.getPartnerNum();
                ArrayList<String> relation = poiDBHelper.getRelation();

                Log.i("dongisyuan兴趣点", "behaviour: " + behaviour.get(1) + "+duration" + duration.get(1)
                        + "partnerNum" + partnerNum.get(1) + "relation" + relation.get(1));
                Intent intent = new Intent();
                intent.setClass(getContext(), CommentActivity.class);
                /**
                 * 传递经纬度 海拔 traceno，地点
                 * */
                if (Common.aLocation != null) { // 首先采用gps位置
                    intent.putExtra("longitude", Common.aLocation.getLongitude());
                    intent.putExtra("latitude", Common.aLocation.getLatitude());
                    intent.putExtra("altitude", Common.aLocation.getAltitude());
                    intent.putExtra("country", Common.aLocation.getCountry());
                    intent.putExtra("province", Common.aLocation.getProvince());
                    intent.putExtra("city", Common.aLocation.getCity());
                    intent.putExtra("placeName", Common.aLocation.getCity() + Common.aLocation.getStreet());
                } else {
                    if (currentAlocation != null) {
                        intent.putExtra("longitude", currentAlocation.getLongitude());
                        intent.putExtra("latitude", currentAlocation.getLatitude());
                        intent.putExtra("altitude", currentAlocation.getAltitude());
                        intent.putExtra("country", currentAlocation.getCountry());
                        intent.putExtra("province", currentAlocation.getProvince());
                        intent.putExtra("city", currentAlocation.getCity());
                        intent.putExtra("placeName", currentAlocation.getCity() + currentAlocation.getStreet());
                    }
                }
                intent.putExtra("traceID", traceID);
                intent.putExtra("stateType", 0);
                //传递兴趣点数据（字符串数组）到添加兴趣点页面
                try {
                    intent.putStringArrayListExtra("behaviour", behaviour);
                    intent.putStringArrayListExtra("duration", duration);
                    intent.putStringArrayListExtra("partnerNum", partnerNum);
                    intent.putStringArrayListExtra("relation", relation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, REQUSET_COMMENT);
                break;
        }
    }

    // 打开记录轨迹对话框
    public void startTrail() {
        final SportTypeDialog dialog = new SportTypeDialog(getContext(), R.style.dlg_sporttype);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface Dialog) {
                int pos = dialog.getposition();
                sportType = pos;
                if (pos != -1) { // 点击确定
                    //Toast.makeText(MainActivity.this, title[pos], 0).show();
                    // 设置轨迹信息
                    traceID = System.currentTimeMillis(); // 手机端暂时生成一个轨迹号(就是一个时间戳)
                    // 初始化轨迹信息，设置它的了6个属性
                    traceName = dialog.gettraceName();
                    tracedata.setTraceName(dialog.gettraceName());          // 轨迹名称
                    tracedata.setSportTypes(pos);                           // 运动类型:1步行，2骑行...
                    tracedata.setStartTime(Common.currentTime());           // 开始时间
                    tracedata.setEndTime(Common.currentTime());             // 结束时间
                    tracedata.setUserID(Common.getUserID(getContext()));    // 新的userID
                    tracedata.setShareType(0);                              // 分享类型
                    tracedata.setTraceID(traceID);                          // 轨迹号
                    locationService.changeCurrentSportType(pos);
                    initStartInfo(); // 初始化轨迹信息
                    ToastUtil.show(getContext(), getResources().getString(R.string.tips_starttrace)); // 开始记录
                    ToastUtil.show(getContext(), getResources().getString(R.string.tips_addmark)); // 点击右侧"相机"按钮可添加标注
                    //showDialog("正在请求..","请求中..请稍后....");
                    //PostStartTrail startTrailThread=new PostStartTrail(handler,URL_STARTTRAIL,Common.userId);
                    //startTrailThread.start();
                    Intent intent = new Intent();
                    intent.setAction(REFRESH_ACTION);
                    Log.i("dongsiyuansendBroadcast", "sendBroadcast: ");
                    getContext().sendBroadcast(intent);
                } else {
//                    Toast.makeText(getContext(), "取消记录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initStartInfo() {
        if (!locationService.isWorking()) {
            locationService.getToWork();
            Log.i("LogDemo", "开始记录轨迹了，此时服务没在工作，所以服务重新开启了！当前时间是" + Common.currentTime());
        }
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.mipmap.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 255, 255, 255));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle); // 改变定位模式为蓝点始终在屏幕中间
        stepdata.setUserID(Common.getUserID(getContext()));
        stepdata.setTraceID(traceID);
        Log.i("LogDemo", "starttrail,localTraceNo：" + traceID + ",id:" + Common.getUserId(getContext()));
        Log.i("LogDemo", "starttrail,traceNo：" + traceID + ",id:" + Common.getUserId(getContext()));
        List<TraceData> traceList = new ArrayList<>();
        traceList.add(tracedata);
        String traceInfo = GsonHelper.toJson(traceList);

        // 先上传一次轨迹信息，获取轨迹号，在service中更改轨迹号。、
        Log.i("mmmmmmmmmmmmmmm", "请求接口获取轨迹号");
        StartTraceRequest startTraceRequest = new StartTraceRequest(sp.getString("token", ""),
                tracedata.getTraceName(), tracedata.getStartTime(), String.valueOf(tracedata.getSportTypes()));
        startTraceRequest.requestHttpData(new ResponseData() {
            @Override
            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                if (isSuccess) {
                    if (code.equals("0")) {
                        try {
                            JSONObject object = new JSONObject((String) responseObject);
                            traceID = Long.parseLong(object.getString("traceID"));
                            Log.i("MapFragment", "开始记录轨迹了，获取了traceID:" + traceID);
                            // 一旦获取到traceID，发送给LocationService
                            tracedata.setTraceID(traceID);
                            stepdata.setTraceID(traceID);
                            locationService.setTraceID(traceID);
                            // 获取到traceID后
                            if (traceID != 0) {
                                traceDBHelper.updatetrail(tracedata, traceID, Common.getUserID(getContext()));
                                Log.i("mmmmmmmmmmmmmmm", "initStartInfo traceDBHelper.updatetrail(tracedata,traceID,Common.getUserID(getContext()));");
                                traceDBHelper.updatesteps(stepdata, traceID, Common.getUserID(getContext()));
                                Log.i("LogDemo", "数据的TraceID替换成功");
                            }
                            Log.i("LogDemo", "获得了轨迹号traceID : " + traceID);
                            traceIDchanged(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (code.equals("100") || code.equals("101")) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "登录信息过期，请重新登录！", Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("token", ""); // 清空token
                                editor.apply();
                                ActivityCollector.finishActivity("MainActivity");
                            }
                        });
                    }
                } else {
                    ToastUtil.show(getContext(), "上传轨迹失败，轨迹保存在本地");
                }
            }
        });
        startTrail.setVisibility(View.INVISIBLE);
        if (sportType == 1) {
            changeSportTypeIb.setBackgroundResource(R.mipmap.ic_walking);
        } else if (sportType == 2) {
            changeSportTypeIb.setBackgroundResource(R.mipmap.ic_cycling);
        } else if (sportType == 3) {
            changeSportTypeIb.setBackgroundResource(R.mipmap.ic_rollerblading);
        } else if (sportType == 4) {
            changeSportTypeIb.setBackgroundResource(R.mipmap.ic_driving);
        } else if (sportType == 5) {
            changeSportTypeIb.setBackgroundResource(R.mipmap.ic_train);
        } else if (sportType == 6) {
            changeSportTypeIb.setBackgroundResource(R.mipmap.others);
        }
        changeSportTypeIb.setVisibility(View.VISIBLE);
        //pauseTrail.setVisibility(View.VISIBLE);
        takePhoto.setVisibility(View.VISIBLE);
        endTrail.setVisibility(View.VISIBLE);
        isstart = true;
        ispause = false;
        //isend=false;

        Log.i("HomePage", "改变了轨迹号traceID : " + traceID);
        locationService.changeStatus(true); // 改为记录轨迹状态
        if (tracedata.getSportTypes() == 1) {
            //轨迹类型为步行，记录步数
            locationService.changeSportType(true);
            StepDetector.CURRENT_STEP = traceDBHelper.querryformstepsbyTraceNo(traceID, Common.getUserId(getContext()))
                    .getSteps();
            total_step = StepDetector.CURRENT_STEP;
            getActivity().startService(stepCountServiceIntent);
            iscountstep = true;
            new Thread(stepThread).start();
//            stepTv.setVisibility(View.VISIBLE);
            stepTv.setText(getResources().getString(R.string.step_label) + "：" + total_step);
        }
    }

    public void changeSportType() {
        final SportTypeChangeDialog dialog = new SportTypeChangeDialog(getContext(), R.style.dlg_sporttype);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface Dialog) {
                int pos = dialog.getposition();
                sportType = pos;
                if (pos != -1) { // 点击确定
                    locationService.changeCurrentSportType(pos);
                    if (sportType == 1) {
                        changeSportTypeIb.setBackgroundResource(R.mipmap.ic_walking);
                    } else if (sportType == 2) {
                        changeSportTypeIb.setBackgroundResource(R.mipmap.ic_cycling);
                    } else if (sportType == 3) {
                        changeSportTypeIb.setBackgroundResource(R.mipmap.ic_rollerblading);
                    } else if (sportType == 4) {
                        changeSportTypeIb.setBackgroundResource(R.mipmap.ic_driving);
                    } else if (sportType == 5) {
                        changeSportTypeIb.setBackgroundResource(R.mipmap.ic_train);
                    } else if (sportType == 6) {
                        changeSportTypeIb.setBackgroundResource(R.mipmap.others);
                    }
                }
            }
        });
    }

    public boolean traceIDchanged(boolean isChanged) {
        isTraceIDchanged = isChanged;
        return isTraceIDchanged;
    }

    // 结束记录
    public void endTrail() {
        /**
         * 本地记录trace内容，非具体位置
         * */
        Log.i("LogDemo", "记录结束了!!!!!!!!!!!!!!!!!!!!!我是记录结束分割线！！！！！！！！！！！！");
        locationService.changeCurrentSportType(0); // 结束记录，运动类型改为0
        if (refreshTrace()) {
            showDialog(getResources().getString(R.string.tip), getResources().getString(R.string.tips_dlgmsg_login));
            points.clear();
            locationService.uploadGPS();
            //            List<TraceData> tracelist = new ArrayList<TraceData>();
            tracedata.setSteps(total_step);
            tracedata.setPoiCount(poiCount);
            //            tracelist.add(tracedata);
            String traceInfo = GsonHelper.toJson(tracedata);
            //            Log.i("HomePageFragment", "tracelist size:" + tracelist.size());
            Log.i("HomePageFragment", "traceInfo:" + traceInfo);
            String stepInfo;
            if (tracedata.getSportTypes() == 1) {
                List<StepData> steplist = new ArrayList<StepData>();
                steplist.add(stepdata);
                stepInfo = GsonHelper.toJson(steplist);
            } else {
                stepInfo = "";
            }
            //Log.i("LogDemo", "endtrail,"+traceInfo+","+stepInfo);
            traceDBHelper.updateStatus(traceID, 2, Common.getUserID(getContext()));

            // 结束轨迹
            EndTraceRequest endTraceRequest = new EndTraceRequest(
                    sp.getString("token", ""), traceInfo);
            endTraceRequest.requestHttpData(new ResponseData() {
                @Override
                public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                    if (isSuccess) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "上传轨迹成功", Toast.LENGTH_SHORT).show();
                                poiCount = 0;
                                dismissDialog();
                            }
                        });
                    } else {
                        dismissDialog();
                        //Toast.makeText(MainActivity.this, getResources().getString(R.string.tips_recordsuccess_postfail),Toast.LENGTH_SHORT).show();
                        CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
                        builder.setTitle(getResources().getString(R.string.tip));
                        builder.setMessage(getResources().getString(R.string.tips_uploadtracedlg_msgfail));
                        builder.setNegativeButton(getResources().getString(R.string.cancl),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.setPositiveButton(getResources().getString(R.string.tryagain),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        locationService.uploadGPS();
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                    }
                }
            });

            endMarker = aMap.addMarker(new MarkerOptions().position(finalLatLng).title(getResources().
                    getString(R.string.endicon)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.end)));
            //bdmap.markEnd(finalLatLng);
        } else {
            ToastUtil.show(getContext(), getResources().getString(R.string.tips_recorderror_nogps));
        }
        traceID = 0;
        locationService.setTraceID(0);
        locationService.changeStatus(false); // 改为非记录状态
        if (tracedata.getSportTypes() == 1) {
            //轨迹类型为步行，结束轨迹时iswalk置为false，停止记录步数
            iscountstep = false;//结束线程
            locationService.changeSportType(false);//是否步行设置为否
            getActivity().stopService(stepCountServiceIntent);//结束计步服务
            //stepThread.stop();
            //handler.removeCallbacks(stepThread);
        }
        //traceService.changeGpsTime(Common.getNoRecLocFrequenct(getApplicationContext()));
        startTrail.setVisibility(View.VISIBLE);
        changeSportTypeIb.setVisibility(View.INVISIBLE);
        //pauseTrail.setVisibility(View.INVISIBLE);
        endTrail.setVisibility(View.INVISIBLE);
        stepTv.setVisibility(View.INVISIBLE);
        isstart = false;
        ispause = false;
        isend = true;
        clearTrace();
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        //bdmap.setLocationStyle(0);
    }

    public boolean refreshTrace() {
        if (isTraceIDchanged) {

        }
        Log.i("LogDemo", Common.getUserID(getContext()));
        Log.i("LogDemo", "refreshTrace: traceID:" + traceID);
        tracegps = traceDBHelper.queryfromGpsbytraceID(traceID, Common.getUserID(getContext()));
        Log.i("LogDemo", "tracegps coontent:" + GsonHelper.toJson(tracedata));
        Log.i("LogDemo", "tracegps size:" + tracegps.size());
        //Gson tracejosn=new Gson();
        if (tracegps.size() > 0) {
            tracedata.setEndTime(Common.currentTime());
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long duration = 0;
            try { //计算时间差
                Date d1 = df.parse(tracedata.getStartTime());
                Date d2 = df.parse(tracedata.getEndTime());
                duration = d2.getTime() - d1.getTime();
            } catch (Exception e) {
                return false;
            }
            tracedata.setDuration(duration);
            //计算距离
            double distance = 0.0;
            if (tracegps.size() > 1) {
                for (int i = 0; i < tracegps.size() - 1; i++) {
                    distance += AMapUtils.calculateLineDistance(new LatLng(tracegps.get(i).getLatitude(),
                                    tracegps.get(i).getLongitude()),
                            new LatLng(tracegps.get(i + 1).getLatitude(), tracegps.get(i + 1).getLongitude()));
                }
            }
            tracedata.setDistance(distance);
            if (tracedata.getSportTypes() == 1) {
                stepdata.setSteps(total_step);
                traceDBHelper.updatesteps(stepdata, traceID, Common.getUserID(getContext()));
                Log.i("LogDemo", "步数表更新数据了");
                Log.i("LogDemo", "stepdata:" + GsonHelper.toJson(stepdata));
                tracedata.setCalorie(calculateCalorie_Walk(distance));
            }
            if (tracedata.getSportTypes() == 2) {
                tracedata.setCalorie(calculateCalorie_Ride(distance, duration));

            }
            PhotoDBHelper photoHelper = new PhotoDBHelper(getContext(), PhotoDBHelper.DBREAD);
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor = photoHelper.selectEvent(null, PhotoDBHelper.COLUMNS_UE[10] + "="
                    + Common.getUserId(getContext()) + " and datetime("
                    + PhotoDBHelper.COLUMNS_UE[0] + ") between '" + tracedata.getStartTime() +
                    "' and '" + tracedata.getEndTime() + "'", null, null, null, null);
            int poiCount = cursor.getCount();
            tracedata.setPoiCount(poiCount);
            Log.i("mmmmmmmmmmmmmmm", "traceID:" + traceID);
            Log.i("mmmmmmmmmmmmmmm", "本地插入了一条轨迹");
            traceDBHelper.updatetrail(tracedata, traceID, Common.getUserID(getContext()));
            Log.i("mmmmmmmmmmmmmmm", "refresh traceDBHelper.updatetrail(tracedata,traceID,Common.getUserID(getContext()));");
            Log.i("LogDemo", "轨迹表更新数据了");
            Log.i("LogDemo", "traceData:" + GsonHelper.toJson(tracedata));
            traceDBHelper.updateStatus(traceID, 1, Common.getUserID(getContext()));
            TraceData traceListTemp = traceDBHelper.queryfromTrailbytraceID(traceID, Common.getUserID(getContext()));
            Log.i("HomePage", "UpdateTrail:" + GsonHelper.toJson(traceListTemp));
            return true;
        } else {
            ToastUtil.show(getContext(), "未采集到位置信息，轨迹不保存");
            return false;
        }
        //        else {
        //            ToastUtil.showShortToast(getContext(), "未获取到traceID");
        //            return false;
        //        }
    }

    /**
     * 实际的步数
     */
    private void countStep() {
        if (StepDetector.CURRENT_STEP % 2 == 0) {
            total_step = StepDetector.CURRENT_STEP;
        } else {
            total_step = StepDetector.CURRENT_STEP + 1;
        }
        total_step = StepDetector.CURRENT_STEP;
    }

    private int calculateCalorie_Walk(double distance) {
        //体重（kg）×距离（公里）×1.036
        return (int) (60 * 1.036 * distance / 1000);
    }

    private int calculateCalorie_Ride(double distance, long duration) {
        //时速(km/h)×体重(kg)×1.05×运动时间(h)
        return (int) (60 * 1.05 * distance / 1000);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://获取轨迹号成功
                    //ToastUtil.show(MainActivity.this, "开始记录");
                    break;
                case 1://获取轨迹号失败
                    //Toast.makeText(MainActivity.this, "开始失败，请重试",Toast.LENGTH_SHORT).show();
                    break;
                case 2://结束记录轨迹成功
                    dismissDialog();
                    traceDBHelper.updateStatus(traceID, 0, Common.getUserID(getContext()));
                    traceDBHelper.deleteStatus();
                    //云端新增轨迹提醒，提示用户下拉刷新
                    Intent intent = new Intent();
                    intent.setAction(PULLREFRESH_ACTION);
                    getActivity().sendBroadcast(intent);
                    ToastUtil.show(getContext(), getResources().getString(R.string.tips_recordsuccess));
                    break;
                case 3://结束记录轨迹失败
                    dismissDialog();
                    //Toast.makeText(MainActivity.this, getResources().getString(R.string.tips_recordsuccess_postfail),Toast.LENGTH_SHORT).show();
                    CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
                    builder.setTitle(getResources().getString(R.string.tip));
                    builder.setMessage(getResources().getString(R.string.tips_uploadtracedlg_msgfail));
                    builder.setNegativeButton(getResources().getString(R.string.cancl),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.setPositiveButton(getResources().getString(R.string.tryagain),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    locationService.uploadGPS();
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                    break;
                case 4://获取4个时间成功
                    /**
                     if(msg.obj.toString().trim()!=null&&msg.obj.toString().trim()!=""){
                     //Gson gson=new Gson();
                     //timevalues=GsonHelper.fromJson(msg.obj.toString().trim(),new TypeToken<TimeValue>(){}.getType());
                     timevalues=GsonHelper.parseJson(msg.obj.toString().trim(), TimeValue.class);
                     //不得在此处进行如下操作，原因：service连接尚未建立,....也有可能连接已建立
                     if(bound_trace&&!istimeset){//service已建立，但是由于没获取时间所以未设置时间
                     initTime();
                     }
                     }
                     */
                    //Toast.makeText(MainActivity.this, "",Toast.LENGTH_SHORT).show();
                    break;
                case 5://获取4个时间失败
                    /**
                     if(!traceService.isworking()){
                     traceService.getToWork();
                     }
                     Log.i("LogDemo", "获取4个时间失败，开启默认时间");
                     */
                    break;
                case 9://更新步数
                    countStep();
                    stepTv.setText(getResources().getString(R.string.step_label) + "：" + total_step);
                    if (tracedata.getSportTypes() == 1) {
                        stepdata.setSteps(total_step);
                        traceDBHelper.updatesteps(stepdata, traceID, Common.getUserID(getContext()));
                    }
                    break;
                case 10:
                    dismissDialog();
                    break;
                case 11://endtrail 上传失败，原因：网络未连接
                    dismissDialog();
                    Toast.makeText(getContext(), getResources().getString(R.string.tips_uploadtracedlg_msgnonet), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler updatehandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    final String[] updatestr = msg.obj.toString().trim().split("&");
                    //Log.i("LogDemo","有更新,"+ url);
                    if (updatestr.length >= 5) {
                        String version = updatestr[0];
                        String time = updatestr[1];
                        String url = updatestr[2];
                        String detail = updatestr[3];
                        String size = updatestr[4];
                        Log.i("LogDemo", "有更新," + version + size + time + url + detail);
                        CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
                        builder.setTitle(getResources().getString(R.string.tips_updatedlg_tle));
                        builder.setMessage(getResources().getString(R.string.tips_updatedlg_msg1) + "\n"
                                + getResources().getString(R.string.tips_updatedlg_msg2) + version + "\n"
                                + getResources().getString(R.string.tips_updatedlg_msg3) + size + "\n"
                                + getResources().getString(R.string.tips_updatedlg_msg4) + time + "\n"
                                + getResources().getString(R.string.tips_updatedlg_msg5) + detail + "\n"
                                + getResources().getString(R.string.tips_updatedlg_msg6));
                        builder.setNegativeButton(getResources().getString(R.string.cancl),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.setPositiveButton(getResources().getString(R.string.confirm),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Common.fileInfo = new FileInfoData(0, updatestr[2]
                                                , "HappyNavi" + updatestr[0] + ".apk", 0, 0);//User/userDownApk.aspx
                                        // 通知Service开始下载
                                        updateServiceIntent = new Intent(getContext(), DownloadService.class);
                                        updateServiceIntent.setAction(DownloadService.ACTION_START);
                                        updateServiceIntent.putExtra("fileInfo", Common.fileInfo);
                                        getActivity().startService(updateServiceIntent);
                                        Common.isUpdationg = true;
                                        ToastUtil.show(getContext(), getResources().getString(R.string.tips_gotodownnewapk));
                                    }
                                });
                        builder.create().show();
                    }
                    break;
                case 1://
                    //Log.i("phonelog", "自动检测，已是最新,"+msg.obj.toString().trim());
                    break;
                case 2://
                    //Log.i("phonelog","检查更新失败,"+ msg.obj.toString().trim());
                    break;
                case 10:
                    //Log.i("phonelog","检查更新异常,"+ msg.obj.toString().trim());
                    break;
            }
        }
    };

    /**
     * 获取兴趣点列表选项
     */
    private void initPOI() {
        //从服务器下载停留时长、行为类型、同伴人数、关系等选项的数据
        DownloadPoiChoices downloadPoiChoices = new DownloadPoiChoices(sp.getString("token", ""));
        downloadPoiChoices.requestHttpData(new ResponseData() {
            @Override
            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                if (isSuccess) {
                    if (code.equals("0")) {
                        poiChoiceModel = (PoiChoiceModel) responseObject;
                        behaviourData = new PointOfInterestData();
                        durationData = new PointOfInterestData();
                        partnerNumData = new PointOfInterestData();
                        relationData = new PointOfInterestData();
                        poiDBHelper.delete();
                        for (int i = 0; i < poiChoiceModel.getActivityTypeList().size(); i++) {
                            behaviourData.setKey(poiChoiceModel.getActivityTypeList().get(i).getActivityType());
                            behaviourData.setValue(poiChoiceModel.getActivityTypeList().get(i).getActivityName());
                            poiDBHelper.insertBehaviour(behaviourData);
                        }
                        for (int i = 0; i < poiChoiceModel.getRetentionTypeList().size(); i++) {
                            durationData.setKey(poiChoiceModel.getRetentionTypeList().get(i).getRetentionType());
                            durationData.setValue(poiChoiceModel.getRetentionTypeList().get(i).getRetentionTypeName());
                            poiDBHelper.insertDuration(durationData);
                        }
                        for (int i = 0; i < poiChoiceModel.getCompanionTypeList().size(); i++) {
                            partnerNumData.setKey(poiChoiceModel.getCompanionTypeList().get(i).getCompanionType());
                            partnerNumData.setValue(poiChoiceModel.getCompanionTypeList().get(i).getCompanionTypeName());
                            poiDBHelper.insertPartnerNum(partnerNumData);
                        }
                        for (int i = 0; i < poiChoiceModel.getRelationTypeList().size(); i++) {
                            relationData.setKey(poiChoiceModel.getRelationTypeList().get(i).getRelationType());
                            relationData.setValue(poiChoiceModel.getRelationTypeList().get(i).getRelationTypeName());
                            poiDBHelper.insertPartnerRelation(relationData);
                        }
                    }
                    if (code.equals("100") || code.equals("101")) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "登录信息过期，请重新登录！", Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("token", ""); // 清空token
                                editor.apply();
                                ActivityCollector.finishActivity("MainActivity");
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                // 如果评论Activity返回的结果，根据该结果进行图片上传
                case REQUSET_COMMENT:
                    Log.i("backloc", "这里执行额？");
                    // 如果有wifi网络，直接开始上传
                    if (Common.checkNetworkState(getContext()) == ConnectivityManager.TYPE_WIFI) {
                        Toast.makeText(getContext(), getResources().getString(R.string.tips_uploadinbgbegin),
                                Toast.LENGTH_SHORT).show();
                        String commentTime = data.getStringExtra("createTime");
                        commentServiceIntent.putExtra("createTime", commentTime);
                        commentUploadService.getToComment(data.getStringExtra("createTime"));
                        //                        getActivity().startService(commentServiceIntent);
                        //commentUploadService.uploadComment(Common.getUserId(getApplicationContext()),
                        //		commentTime);
                    }// 如果是数据流量连接，第一次使用询问是否上传
                    else if (Common.checkNetworkState(getContext()) == ConnectivityManager.TYPE_MOBILE
                            && !Common.isOnlyWifiUploadPic(getContext())) {
                        SharedPreferences sp = getActivity().getSharedPreferences("config",
                                MODE_PRIVATE);
                        if (sp.getInt(LoginActivity.mobConnectFirst, 0) == 0) {
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putInt(LoginActivity.mobConnectFirst, 1);
                            edit.apply();
                            CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
                            builder.setMessage(getResources().getString(R.string.mobconnect_tips));
                            builder.setTitle(getResources().getString(R.string.tip));
                            builder.setPositiveButton(
                                    getResources().getString(R.string.confirm),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String commentTime = data.getStringExtra("createTime");
                                            commentServiceIntent.putExtra("createTime", commentTime);
                                            commentUploadService.getToComment(data.getStringExtra("createTime"));
                                            //                                            getActivity().startService(commentServiceIntent);
                                            //commentUploadService.uploadComment(
                                            //		Common.getUserId(getApplicationContext()),
                                            //		commentTime);
                                            dialog.dismiss();
                                        }
                                    });
                            builder.setNegativeButton(getResources().getString(R.string.cancl),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String commentTime = data.getStringExtra("createTime");
                                            String userId = Common.getUserId(getContext());
                                            SharedPreferences.Editor editor = uploadCache.edit();
                                            editor.putString(commentTime, userId);
                                            editor.commit();
                                            dialog.dismiss();
                                        }
                                    });
                            builder.create().show();
                        } else {
                            String commentTime = data.getStringExtra("createTime");
                            commentServiceIntent.putExtra("createTime", commentTime);
                            commentUploadService.getToComment(data.getStringExtra("createTime"));
                            //                            getActivity().startService(commentServiceIntent);
                            //commentUploadService.uploadComment(
                            //		Common.getUserId(getApplicationContext()),
                            //		commentTime);
                        }
                    } else {
                        // 如果没有网络，将上传相关信息写到cache文件
                        Toast.makeText(getContext(), getResources().getString(R.string.tips_uploadpic_nonet),
                                Toast.LENGTH_SHORT).show();
                        String commentTime = data.getStringExtra("createTime");
                        String userId = Common.getUserId(getContext());
                        SharedPreferences.Editor editor = uploadCache.edit();
                        editor.putString(commentTime, userId);
                        editor.commit();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 自定义info window窗口
     */
    public void render(Marker marker, View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.badge);
        imageView.setImageResource(R.mipmap.ic_photo);
        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null) {
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
                    titleText.length(), 0);
            titleUi.setTextSize(15);
            titleUi.setText(titleText);
            //Log.i("LogDemo", "title"+title);
        } else {
            titleUi.setText("");
        }
        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
                    snippetText.length(), 0);
            snippetUi.setTextSize(20);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("");
        }
		/*Button comment=((Button) view.findViewById(R.id.comments));
		comment.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ToastUtil.show(MainActivity.this, "你点击了评论按钮");
			}

		});*/
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
		/*if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;*/
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    private void showBGRunGuide() {//提示用户将应用加入保护名单
        CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.tip));

        builder.setMessage(getResources().getString(R.string.tips_bgrunguide));
        builder.setIsShowChebox(true);
        builder.setCheckBox(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isShowBGRGuide", !isChecked);
                editor.commit();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.confirm),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (Common.isNetConnected) {
                            startActivity(new Intent(getContext(), BGRunningGuideActivity.class));
                        } else {
                            ToastUtil.show(getContext(), getResources().getString(R.string.tips_netdisconnect));
                        }
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.close),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
        builder.create().show();
    }

    private void showDlg_badloc() { //无法定位时弹出提示
        CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.tip));
        builder.setMessage(getResources().getString(R.string.tips_cannotloc));
        builder.setIsShowChebox(true);
        builder.setCheckBox(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isShowBadLoc", !isChecked);
                editor.commit();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.open),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // 转到手机设置界面，用户设置GPS
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                        arg0.dismiss();
                        isShowNonLocDlg = false;
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancl),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                        isShowNonLocDlg = false;
                    }
                });
        builder.create().show();
    }

    //低精度时弹出提示对话框，用户选择是否采用低精度位置
    private void showDlg_LowAccuracy() {
        CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.tip));
        builder.setMessage(getResources().getString(R.string.tips_accuracydlg_msg));
        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Common.isHighAccuracy = false;
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 显示进度条对话框
     */
    public void showDialog(String title, String message) {
        if (proDialog == null)
            proDialog = new ProgressDialog(getContext());
        proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        proDialog.setIndeterminate(false);
        proDialog.setCancelable(true);
        proDialog.setTitle(title);
        proDialog.setMessage(message);
        proDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (proDialog != null) {
            proDialog.dismiss();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        iscountstep = false;
        deactivate();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
        //bdmap.stop();
        mapView.onDestroy();
        //bdMapView.onDestroy();
        if (bound_trace) {
            getActivity().unbindService(connection);
            bound_trace = false;
            Log.i("HomePage", "解绑定位服务");
        }
		/*if(bound_upload){
			this.getApplicationContext().unbindService(conn_comment);
			bound_upload=false;
		}*/
        if (bound_comment_upload) {
            getActivity().unbindService(commentConnection);
            bound_comment_upload = false;
            Log.i("HomePage", "解绑上传评论服务");
        }
        Common.isUpdationg = false;
        if (null != locationServiceIntent) {
            getActivity().stopService(locationServiceIntent);
            Log.i("HomePage", "停止定位服务");
        }
        if (null != commentServiceIntent) {
            getActivity().stopService(commentServiceIntent);
            Log.i("HomePage", "停止服务");
        }
        if (null != updateServiceIntent) {
            getActivity().stopService(updateServiceIntent);
            Log.i("HomePage", "停止更新服务");
        }
        if (null != stepCountServiceIntent) {
            getActivity().stopService(stepCountServiceIntent);
            Log.i("HomePage", "停止记步服务");
        }
        if (null != myReceiver) {
            getActivity().unregisterReceiver(myReceiver);
        }
        if (null != accuracyReciver) {
            getActivity().unregisterReceiver(accuracyReciver);
        }
        if (null != connectionReceiver) {
            getActivity().unregisterReceiver(connectionReceiver);
        }
        Log.i("LogDemo", "Main-onDestroy");
    }

}
