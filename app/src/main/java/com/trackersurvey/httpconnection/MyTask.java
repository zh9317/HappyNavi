package com.trackersurvey.httpconnection;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.trackersurvey.db.PhotoDBHelper;
import com.trackersurvey.happynavi.R;
import com.trackersurvey.util.Common;

/**
 * Created by zh931 on 2018/5/13.
 * 上传兴趣点
 */

public class MyTask extends Thread {
    // 用户事件数据
    private String userId;
    private String createTime;
    private double longitude; // 需要地图功能获得的4个数据
    private double latitude;
    private double altitude;
    private String placeName;
    private String commentText;
    private String deviceId;
    private long traceNo;
    private int fileNum;
    private int videoCount;
    private int audioCount;
    private int feeling;
    private int behaviour;
    private int duration;
    private int companion;
    private int relationship;

    private Context context;
    private Handler handler ;
    private Message msg;

    HttpUtils httpSend;



    public MyTask(Context context, String userId, String createTime, Handler handler, String deviceId){
        // 得到评论Activity传来的事件时间
        this.context = context;
        this.createTime = createTime;
        this.userId = userId;
        this.handler = handler;
        this.deviceId = deviceId;
        msg = Message.obtain();
    }


    public void run(){
        doInBackground();
    }

    protected void doInBackground() {
        // 得到评论Activity传来的事件时间
        PhotoDBHelper dbHelper = new PhotoDBHelper(context, PhotoDBHelper.DBREAD);
        // 查找事件时间对应的事件
        Cursor cursor = dbHelper.selectEvent(null, "datetime("
                + PhotoDBHelper.COLUMNS_UE[0] + ")==datetime('" + createTime
                + "') and " + PhotoDBHelper.COLUMNS_UE[10] + "="
                + userId, null, null, null, null);

        Log.i("upfile","cursor长度 = " + cursor.getCount());
        if (cursor.moveToNext()) {

            this.longitude = cursor.getDouble(1);
            this.latitude = cursor.getDouble(2);
            this.altitude = cursor.getDouble(3);
            this.placeName = cursor.getString(4);
            this.commentText = cursor.getString(5);
            this.traceNo = cursor.getLong(6);
            this.fileNum = cursor.getInt(7);
            this.videoCount = cursor.getInt(8);
            this.audioCount = cursor.getInt(9);
            this.feeling = cursor.getInt(11);
            this.behaviour = cursor.getInt(12);
            this.duration = cursor.getInt(13);
            this.companion = cursor.getInt(14);
            this.relationship = cursor.getInt(15);
            cursor.close();
            httpSend = new HttpUtils();
            // 上传事件，事件上传成功后上传文件
            if(Common.URL_UPEVENT == null||Common.URL_UPEVENT.equals("")){
                Common.URL_UPEVENT = context.getResources().getString(R.string.url_upevent);
            }
            uploadComment(Common.URL_UPEVENT);
        }
        dbHelper.closeDB();
    }

    private void uploadComment(final String uploadHost) {

        // 上传的事件参数
        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", userId);
        params.addBodyParameter("createTime", createTime);
        params.addBodyParameter("commentId", "1");
        params.addBodyParameter("comment", commentText);
        params.addBodyParameter("placeName", placeName);
        params.addBodyParameter("longitude", "" + longitude);
        params.addBodyParameter("latitude", "" + latitude);
        params.addBodyParameter("altitude", "" + altitude);
        params.addBodyParameter("traceNo", ""+traceNo);
        params.addBodyParameter("picCount", "" + fileNum);// 这条评论的文件数
        params.addBodyParameter("videoCount", ""+videoCount);
        params.addBodyParameter("soundCount",""+audioCount);// 这条评论的照片数
        params.addBodyParameter("deviceId",deviceId);
        params.addBodyParameter("motionType", ""+feeling);
        params.addBodyParameter("activityType", ""+behaviour);
        params.addBodyParameter("retentionTime", ""+duration);
        params.addBodyParameter("companionCount", ""+companion);
        params.addBodyParameter("relationship", ""+relationship);
        Log.i("upfile", "start upload " + ""+commentText);
        Log.i("upfile", "behaviour:"+""+behaviour+"|duration"+duration+"|companion"+companion+"|relation"+relationship);

        httpSend.send(HttpRequest.HttpMethod.POST, uploadHost, params,
                new eventRequestCallBack());

    }

    /**
     * 上传所有文件
     *
     * @param uploadHost
     */
    private void uploadFiles(final String uploadHost) {
        // 在文件表中查询事件时间为createTime的文件
        PhotoDBHelper dbHelper = new PhotoDBHelper(context, PhotoDBHelper.DBREAD);
        Cursor cursor = dbHelper.selectFiles(null, "datetime("
                + PhotoDBHelper.COLUMNS_FILE[2] + ")==datetime('" + createTime
                + "')", null, null, null, null);
//		Log.i("upfile","cursor长度 = "+ cursor.getCount());
        int fileID = 0;
        while (cursor.moveToNext()) {
            String fileName = cursor.getString(1);
            int fileType = cursor.getInt(3);
//			uploadAFile(uploadHost, fileID, fileType, fileName);
            fileID++;
        }
        Log.i("upfile","cursor长度 = "+ cursor.getCount());
        cursor.close();
        // 关闭数据库
        dbHelper.closeDB();
    }


    // 回调，监听上传是否成功
    class eventRequestCallBack extends RequestCallBack<String> {
        @Override
        public void onFailure(HttpException arg0, String result) {
            Log.i("upfile","上传失败 "+ arg0 + "|||" + result);
            msg.what =2;
            msg.obj = result;
            handler.handleMessage(msg);
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            String result = responseInfo.result;
            // 返回值是一个ID
            if ("ok".equals(result)) {
                Log.i("upfile", "result = "+result + " |*| " + "commentOK");
                // 评论上传成功后上传文件
                msg.what =0;
                msg.obj = result;
                handler.handleMessage(msg);
            }
        }
    }
}
