package com.trackersurvey.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.trackersurvey.db.PhotoDBHelper;
import com.trackersurvey.fragment.MapFragment;
import com.trackersurvey.happynavi.R;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.http.UploadFileRequest;
import com.trackersurvey.http.UploadPoiRequest;
import com.trackersurvey.model.TracePoiModel;
import com.trackersurvey.util.ActivityCollector;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.GsonHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommentUploadService extends Service {
    private final IBinder binder = new CommentBinder();
    private SharedPreferences uploadCache;
    private SharedPreferences sp;
    private  boolean hasUploadComment = false;
    private int upFileNum = 0;
    private int uploadedNum = 0;
    public final String SHAREDFILES = "uploadFiles";
    private int share;
    private List<String> fileNameList;
    private int poiID;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(getApplicationContext(), "上传兴趣点成功", Toast.LENGTH_SHORT).show();
                    break;
                case 2: // 上传兴趣点时，token失效
                    Log.i("CommentUpload", "上传兴趣点token失效");
                    Toast.makeText(getApplication(), "登录信息过期，请重新登录！", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("token", ""); // 清空token
                    editor.apply();
                    ActivityCollector.finishActivity("MainActivity");
                    break;
                case 3: // 上传文件成功
                    fileUploading(++uploadedNum, upFileNum);
                    break;
                case 4: // 上传文件时token失效
                    Log.i("CommentUpload", "上传文件token失效");
                    Toast.makeText(getApplication(), "登录信息过期，请重新登录！", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor2 = sp.edit();
                    editor2.putString("token", ""); // 清空token
                    editor2.apply();
                    ActivityCollector.finishActivity("MainActivity");
                    break;
                case 5: // 文件上传输错
                    Toast.makeText(getApplication(), "文件上传出错！", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(getApplication(), "未选择文件或文件为空,请选择文件重新上传！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private BroadcastReceiver connectionReceiver = null; // 用于监听网络状态变化的广播

    public class CommentBinder extends Binder {
        public CommentUploadService getService(){
            return CommentUploadService.this;
        }
    }
    public CommentUploadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("upfile", "onCreate");
        uploadCache = getSharedPreferences("uploadCache", Activity.MODE_PRIVATE);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        fileNameList = new ArrayList<>();
        connectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

                NetworkInfo netInfo = connectMgr.getActiveNetworkInfo();
                if(netInfo!=null && netInfo.isConnected()) {
                    //有网络连接
                    if(netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        //wifi连接
                        Log.i("upfile","wifi连接，检查是否有评论未上传");
                        uploadWhenConnect();
                    } else if(netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        // connect network,读取本地sharedPreferences文件，上传之前未完成上传的部
                        if (!Common.isOnlyWifiUploadPic(CommentUploadService.this)) {
                            Log.i("upfile","gprs连接，检查是否有评论未上传");
                            uploadWhenConnect();
                        }
                    }
                }
            }
        };
        //注册监听网络连接状态广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("upfile", "onStartCommand");
        String createTime = intent.getStringExtra("createTime");
        //Log.i("upfile", "createTime:" + createTime);
        if(createTime != null && !createTime.equals("")){
            uploadComment(Common.getUserID(getApplicationContext()), createTime);
        }
        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 上传
     */
    public void getToComment(String createTime) {
        Log.i("upfile", "getToComment");
        if (createTime != null && !createTime.equals("")) {
            uploadComment(Common.getUserID(getApplicationContext()), createTime);
        }
    }
    public void uploadComment(String userID, final String createTime) {
        Log.i("upfile","from service "+ "一次上传,createTime = " + createTime);

        // 得到CommentActivity传来的事件时间
        PhotoDBHelper dbHelper = new PhotoDBHelper(this, PhotoDBHelper.DBREAD);
        // 查找事件时间对应的事件
        Cursor cursor = dbHelper.selectEvent(null, "datetime("
                + PhotoDBHelper.COLUMNS_UE[0] + ")==datetime('" + createTime
                + "') and " + PhotoDBHelper.COLUMNS_UE[14] + "="
                + userID, null, null, null, null);

        Log.i("upfile","cursor长度 = " + cursor.getCount());
        TracePoiModel tracePoiModel = new TracePoiModel();
        if (cursor.moveToNext()) {
            tracePoiModel.setCreateTime(createTime);
            tracePoiModel.setUserID(cursor.getInt(14));
            tracePoiModel.setPoiNo(cursor.getInt(1));
            tracePoiModel.setLatitude(cursor.getDouble(2));
            tracePoiModel.setLongitude(cursor.getDouble(3));
            tracePoiModel.setAltitude(cursor.getDouble(4));
            tracePoiModel.setCountry(cursor.getString(5));
            tracePoiModel.setProvince(cursor.getString(6));
            tracePoiModel.setCity(cursor.getString(7));
            tracePoiModel.setPlaceName(cursor.getString(8));
            tracePoiModel.setComment(cursor.getString(9));
            tracePoiModel.setTraceID(cursor.getLong(10));
            tracePoiModel.setMotionType(cursor.getInt(15));
            tracePoiModel.setActivityType(cursor.getInt(16));
            tracePoiModel.setRetentionType(cursor.getInt(17));
            tracePoiModel.setCompanionType(cursor.getInt(18));
            tracePoiModel.setRelationType(cursor.getInt(19));
            tracePoiModel.setStateType(cursor.getInt(20));
            tracePoiModel.setImageCount(cursor.getInt(11));
            tracePoiModel.setVideoCount(cursor.getInt(12));
            tracePoiModel.setAudioCount(cursor.getInt(13));

            share = cursor.getInt(21);
            cursor.close();
        }
        dbHelper.closeDB();
        String tracePoi = GsonHelper.toJson(tracePoiModel);
        Log.i("upfile", "tracePoi : " + tracePoi);
//        StringBuffer fileNames = new StringBuffer();
//        fileNames.append("");
//        int fileType = 0;
        // 测试上传兴趣点
//        if (fileCache.size() > 0) {
//            Log.i("CommentActivity", "有文件");
//            Iterator<?> iterator = fileCache.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry<String, ?> entry = (Map.Entry<String, ?>) iterator.next();
//                String key = entry.getKey();
//                String para[] = key.split(File.separator);
//                fileType = Integer.parseInt(para[2]);
//                fileNameList.add((String) entry.getValue());
//            }
//        }
//        for (int i = 0; i < fileNameList.size(); i++) {
//            if (i != fileNameList.size() - 1) {
//                fileNames.append(fileNameList.get(i));
//                fileNames.append(",");
//            }else {
//                fileNames.append(fileNameList.get(i));
//            }
//        }
//        Log.i("UpdPoiPic", fileNameList.toString());
        UploadPoiRequest uploadPoiRequest = new UploadPoiRequest(sp.getString("token", ""), tracePoi);
        uploadPoiRequest.requestHttpData(new ResponseData() {
            @Override
            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                if (isSuccess) {
                    if (code.equals("0")) {
                        MapFragment.poiCount++;
                        poiID = (int) responseObject;
                        Log.i("CommentUploadService", "poiID : " + poiID);
                        uploadCache.edit().remove(createTime).commit();
                        hasUploadComment = false;
                        uploadWhenConnect();
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                    if (code.equals("100") || code.equals("101")) {
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                }
            }
        });

        hasUploadComment = true;
    }
    /**
     * 从sp文件中读取信息，上传未完成部分
     */
    protected void uploadWhenConnect() {
        Log.i("upfile", "uploadWhenConnect 这里执行了");
        if (!hasUploadComment) {
            // 存储评论条数的sp
            uploadCache = getSharedPreferences("uploadCache",
                    Activity.MODE_PRIVATE);
            Map<String, ?> cache = uploadCache.getAll();

            // 存储文件个数的sp
//            fileCache = getSharedPreferences(SHAREDFILES, MODE_PRIVATE).getAll();
//            upFileNum = fileCache.size();
//            uploadedNum = 0;

            Log.i("upfile", "cache size:" + cache.size());
            if (!cache.isEmpty() && cache.size() > 0) {
                Map.Entry<String, ?> entry = cache.entrySet().iterator().next();
                uploadComment((String) entry.getValue(), entry.getKey());
            } else {
                upFileWhenConnect();
            }
        }
    }

    /**
     * @author 易 上传评论的回调
     */
    class CommentHandler extends Handler {
        private String createTime;

        public CommentHandler(String createTime) {
            this.createTime = createTime;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    Log.i("upfile","from service "+ "result=" + msg.obj);
                    uploadCache.edit().remove(createTime).commit();
                    hasUploadComment = false;
                    uploadWhenConnect();
                    break;
                }
                case 1: {
                    Log.i("upfile","from service "+ "result=" + msg.obj);
                    break;
                }
                case 2: {
                    Log.i("upfile", "from service "+"result=" + msg.obj);
                    break;
                }
            }
        }
    }

    /**
     * 网络连接时上传文件
     */
    private void upFileWhenConnect() {
        Map<String, ?> fileCache = getSharedPreferences(
                SHAREDFILES, MODE_PRIVATE).getAll();
        upFileNum = fileCache.size();
        uploadedNum = 0;
        uploadFiles(1, fileCache);
    }

    /**
     * 上传所有文件 一次只能有一个文件上传
     *
     * @param command
     * @param cache
     */
    @SuppressWarnings("unchecked")
    private void uploadFiles(int command, Map<String, ?> cache) {
        if (!cache.isEmpty() && cache.size() > 0) {
            Iterator<?> iterator = cache.entrySet().iterator();
            Map.Entry<String, ?> entry = null;
            switch (command) {
                case 1:
                    entry = (Map.Entry<String, ?>) iterator.next();
                    break;
                case 2:
                    entry = (Map.Entry<String, ?>) iterator.next();
                    if (iterator.hasNext()) {
                        entry = (Map.Entry<String, ?>) iterator.next();
                    }else{
                        return;
                    }
                    break;
            }
            String key = entry.getKey();
            String para[] = key.split(File.separator);

            String createTime = para[0];
            int fileID = Integer.parseInt(para[1]);
            int fileType = Integer.parseInt(para[2]);
            String userID = para[3];
            String fileName = (String) entry.getValue();

            uploadOneFile(key,userID, createTime, fileID, fileType, fileName,
                    upFileNum);
            Log.i("upfile","from service "+"upload one file "+ userID + "|" + createTime + "|" + fileID + "|"
                    + fileName);
        } else {
            uploadedNum = 0;
            upFileNum = 0;
        }
    }

    /**
     * 上传一个文件
     *
     * @param createTime
     * @param fileID
     * @param fileType
     * @param fileName
     */
    private void uploadOneFile(final String key, String userID, String createTime,
                               final int fileID, int fileType, String fileName, final int total) {
        if(Common.URL_UPFILE == null||Common.URL_UPFILE.equals("")){
            Common.URL_UPFILE = getResources().getString(R.string.url_upfile);
        }
        Log.i("CommentUpload", "token: " + sp.getString("token", "")
                + "; poiID:" + poiID + "; fileName: " + fileName + "; fileID: " + fileID);
        UploadFileRequest uploadFileRequest = new UploadFileRequest(sp.getString("token", ""),
                String.valueOf(poiID), fileName, String.valueOf(fileID+1));
        uploadFileRequest.requestHttpData(new ResponseData() {
            @Override
            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                if (isSuccess) {
                    if (code.equals("0")) {
                        Message message = new Message();
                        message.what = 3;
                        handler.sendMessage(message);
                        SharedPreferences uploadFiles = getSharedPreferences(SHAREDFILES, MODE_PRIVATE);
                        uploadFiles.edit().remove(key).commit();
                        Map<String, ?> fileCache = uploadFiles.getAll();
//                        Log.i("upfile", "from service "+"" + msg.obj+"file size="+fileCache.size());
                        if (!hasUploadComment) {
                            uploadFiles(1, fileCache);
                        }
                    }
                    if (code.equals("100") || code.equals("101")) {
                        Message message = new Message();
                        message.what = 4;
                        handler.sendMessage(message);
                    }
                    if (code.equals("400")) {
                        Message message = new Message();
                        message.what = 5;
                        handler.sendMessage(message);
                    }
                    if (code.equals("401")) {
                        Message message = new Message();
                        message.what = 6;
                        handler.sendMessage(message);
                    }
                }
            }
        });
//        PostCommentFile pcf = new PostCommentFile(CommentUploadService.this, userID,
//                createTime, new FileHandler(key, fileID, total),
//                Common.URL_UPFILE, fileID, fileType, fileName,Common.getDeviceId(getApplicationContext()));
//        pcf.start();
    }

//    class FileHandler extends Handler {
//        private String fileKey;
//        private int fileID;
//        private int total;
//
//        public FileHandler(String key, int fileID, int total) {
//            this.fileKey = key;
//            this.fileID = fileID;
//            this.total = total;
//        }
//
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0: {
//                    fileUploading(++uploadedNum, total);
//                    SharedPreferences uploadFiles = getSharedPreferences(SHAREDFILES, MODE_PRIVATE);
//                    uploadFiles.edit().remove(fileKey).commit();
//                    Map<String, ?> fileCache = uploadFiles.getAll();
//                    Log.i("upfile", "from service "+"" + msg.obj+"file size="+fileCache.size());
//                    if (!hasUploadComment) {
//                        uploadFiles(1, fileCache);
//                    }
//                    break;
//                }
//                case 1: {
//                    uploadError(fileID, fileKey, getResources().getString(R.string.tips_error));
//                    break;
//                }
//                case 2: {
//                    uploadError(fileID, fileKey, getResources().getString(R.string.tips_fail));
//                    break;
//                }
//                default:
//                    break;
//            }
//        };
//    };

//    public void uploadError(final int fileID, final String key,
//                            final String text) {
//        if (Common.checkNetworkState(getApplicationContext()) < 0) {
//            Toast.makeText(getApplicationContext(), R.string.tips_uploadpic_neterror, Toast.LENGTH_SHORT)
//                    .show();
//            return;
//        }
//        if (hasUploadComment) {
//            return;
//        }
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(CommentUploadService.this);
//                builder.setMessage(getResources().getString(R.string.tips_uploadfaildlg_msg1)
//                        + (fileID + 1) + getResources().getString(R.string.tips_uploadfaildlg_msg2)
//                        + text + getResources().getString(R.string.tips_uploadfaildlg_msg3));
//                builder.setTitle(getResources().getString(R.string.tip));
//                final Map<String, ?> fileCache = getSharedPreferences(
//                        SHAREDFILES, MODE_PRIVATE).getAll();
//                builder.setPositiveButton(getResources().getString(R.string.tryagain), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        uploadFiles(1, fileCache);
//                    }
//                });
//                builder.setNegativeButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        SharedPreferences uploadFiles = getSharedPreferences(SHAREDFILES, MODE_PRIVATE);
//                        uploadFiles.edit().remove(key).commit();
//                        Map<String, ?> cache = uploadFiles.getAll();
//                        Log.i("upfile","from service "+ "删除：" + "file size="+fileCache.size());
//                        uploadedNum--;
//                        uploadFiles(1, cache);
//
//                    }
//                });
//                builder.setNeutralButton(getResources().getString(R.string.cancl), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        uploadFiles(2, fileCache);
//
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                dialog.setCanceledOnTouchOutside(false);//点击对话框外部，对话框不会消失
//                dialog.setCancelable(false);//按返回键不消失
//                dialog.show();
//            }
//        });
//    }

    /**
     * Toast显示文件上传进度
     */
    public void fileUploading(int complete,int total) {
        Log.i("upfile","from service "+ "fileUploading " + complete + "|"+total);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.tips_uploadinbg)+"：" + complete + "/"+total,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != connectionReceiver){
            unregisterReceiver(connectionReceiver);
        }
    }
}
