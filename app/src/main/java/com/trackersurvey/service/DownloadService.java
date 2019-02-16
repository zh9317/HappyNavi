package com.trackersurvey.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.trackersurvey.bean.FileInfoData;
import com.trackersurvey.happynavi.R;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.ToastUtil;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * 类注释
 */
public class DownloadService extends Service {
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloads/";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String ACTION_FINISHED = "ACTION_FINISHED";
    public static final String ACTION_ERROR = "ACTION_ERROR";
    public static final int MSG_INIT = 0;
    private String TAG = "phonelog";
    private String fileName=null;
    private Map<Integer, DownloadTaskService> mTasks = new LinkedHashMap<Integer, DownloadTaskService>();
    private BroadcastReceiver updateReceiver = null; //监听更新进度的广播
    //通知栏进度条
    private NotificationManager mNotificationManager = null;
    private Notification mNotification;
    private PendingIntent updatePendingIntent = null;
    public DownloadService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 获得Activity传过来的参数
        if (ACTION_START.equals(intent.getAction())) {
            FileInfoData fileInfo = (FileInfoData) intent.getSerializableExtra("fileInfo");
            if(fileInfo!=null) {
                Log.i(TAG , "Start:" + fileInfo.toString());
                fileName=fileInfo.getFileName();
                // 启动初始化线程
                new InitThread(fileInfo).start();
            } else{
                Log.i(TAG , "Start error,file is null");
            }
        } else if (ACTION_STOP.equals(intent.getAction())) {
            FileInfoData fileInfo = (FileInfoData) intent.getSerializableExtra("fileInfo");
            Log.i(TAG , "Stop:" + fileInfo.toString());

            // 从集合中取出下载任务
            DownloadTaskService task = mTasks.get(fileInfo.getId());
            if (task != null) {
                task.isPause = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //定义更新广播
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (DownloadService.ACTION_UPDATE.equals(intent.getAction()))
                {
                    int finised = intent.getIntExtra("finished", 0);
                    int id = intent.getIntExtra("id", 0);
                    mNotification.contentView.setTextViewText(R.id.download_view_count,getResources().getString(R.string.tips_downprogress)+"："+finised+"%");
                    mNotification.contentView.setProgressBar(R.id.download_view_progress,100,finised,false);
                    mNotificationManager.notify(0, mNotification);
                    Log.i("phonelog","收到进度广播：" +id + "-finised = " + finised);
                }
                else if (DownloadService.ACTION_FINISHED.equals(intent.getAction()))
                {
                    // 下载结束
                    //FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                    //mAdapter.updateProgress(fileInfo.getId(), 0);
                    Common.isUpdationg=false;

                    mNotification.contentView.setTextViewText(R.id.download_view_count,100+"% "+getResources().getString(R.string.tips_downfinish));
                    mNotification.contentView.setProgressBar(R.id.download_view_progress,100,100,false);
                    mNotificationManager.notify(0, mNotification);
                    mNotificationManager.cancel(0);
                    ToastUtil.show(getApplicationContext(), getResources().getString(R.string.tips_newapkdown));
                    Log.i("phonelog","收到完成广播，下载完毕");

                    Uri uri=Uri.fromFile(new File(DOWNLOAD_PATH+fileName));
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                    updatePendingIntent = PendingIntent.getActivity(DownloadService.this, 0, installIntent, 0);
                    Notification notification = new Notification.Builder(DownloadService.this)
                            .setContentTitle("100% "+getResources().getString(R.string.tips_downfinish))
                            .setContentText(getResources().getString(R.string.tips_install))
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(updatePendingIntent)
                            .getNotification();
                    notification.flags|=Notification.FLAG_AUTO_CANCEL;
                    mNotificationManager.notify(1, notification);
                    Common.isUpdationg=false;//下载更新结束
                }
            }
        };
        // 注册更新广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        filter.addAction(DownloadService.ACTION_FINISHED);
        filter.addAction(DownloadService.ACTION_ERROR);
        this.registerReceiver(updateReceiver, filter);
        //显示下载进度通知栏
        mNotificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotification = new Notification.Builder(DownloadService.this)
                .setContentTitle(getResources().getString(R.string.tips_downnewapk))

                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(new RemoteViews(getPackageName(),R.layout.download_notification))
                .getNotification();
        mNotificationManager.notify(0,mNotification);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(updateReceiver);
        mNotificationManager.cancelAll();
        super.onDestroy();
    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_INIT:
                    FileInfoData fileInfo = (FileInfoData) msg.obj;
                    Log.i(TAG, "准备下载--->Init:" + fileInfo);
                    // 启动下载任务
                    DownloadTaskService task = new DownloadTaskService(DownloadService.this, fileInfo, 1);
                    task.downLoad();
                    // 把下载任务添加到集合中
                    mTasks.put(fileInfo.getId(), task);
                    break;
                default:
                    break;
            }
        };
    };

    private class InitThread extends Thread {
        private FileInfoData fileInfo = null;
        public InitThread(FileInfoData fileInfo) {
            this.fileInfo = fileInfo;
        }
        /**
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            HttpURLConnection connection = null;
            RandomAccessFile raf = null;
            try {
                // 连接网络文件
                URL url = new URL(fileInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                int length = -1;
                if (connection.getResponseCode() == HttpStatus.SC_OK) {
                    // 获得文件的长度
                    length = connection.getContentLength();
                }
                if (length <= 0) {
                    Log.i(TAG, "file length<=0,取消下载操作");
                    return;
                }
                File dir = new File(DOWNLOAD_PATH);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                // 在本地创建文件
                File file = new File(dir, fileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                // 设置文件长度
                raf.setLength(length);
                fileInfo.setLength(length);
                mHandler.obtainMessage(MSG_INIT, fileInfo).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
