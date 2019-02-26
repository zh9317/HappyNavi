package com.trackersurvey.httpconnection;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.trackersurvey.db.PhotoDBHelper;

/**
 * Created by zh931 on 2018/5/19.
 * 根据时间点删除评论
 */

public class DeleteCloudComment extends Thread {
    private Handler mHandler;
    private String url;
    private String userID;
    private String dateTime,dateTime_start,dateTime_end;
    private String deviceId;
    private boolean isDelMulti;//是否同时删除多个兴趣点
    private RequestParams params;
    private Message msg;
    PhotoDBHelper dbHelper;
    HttpUtils httpSend;
    Cursor cursor;

    public DeleteCloudComment(Context context, Handler handler, String url, String userID,
                              String dateTime, String deviceId) {
        params = new RequestParams();
        this.mHandler = handler;
        this.url = url;
        this.userID = userID;
        this.dateTime = dateTime;
        this.deviceId = deviceId;
        isDelMulti = false;
        params.addBodyParameter("requestType","delComment");
        params.addBodyParameter("userId", userID);
        params.addBodyParameter("createTime", dateTime);
        params.addBodyParameter("deviceId",deviceId);

        msg = Message.obtain();
        dbHelper = new PhotoDBHelper(context, PhotoDBHelper.DBWRITE);
    }
    public DeleteCloudComment(Context context,Handler handler, String url, String userID,
                              String dateTime_start,String dateTime_end,String deviceId) {
        params = new RequestParams();
        this.mHandler = handler;
        this.url = url;
        this.userID = userID;
        this.dateTime_start = dateTime_start;
        this.dateTime_end = dateTime_end;
        this.deviceId = deviceId;
        isDelMulti = true;
        params.addBodyParameter("requestType","delMultiComment");
        params.addBodyParameter("userId", userID);
        params.addBodyParameter("startTime", dateTime_start);
        params.addBodyParameter("endTime", dateTime_end);
        params.addBodyParameter("deviceId",deviceId);


        msg = Message.obtain();
        dbHelper = new PhotoDBHelper(context, PhotoDBHelper.DBWRITE);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        httpSend = new HttpUtils();

        httpSend.send(HttpRequest.HttpMethod.POST, url, params, new eventDeleteCallBack());

    }

    /**
     * 从数据库中删除该事件
     * @return
     */
    int  deleteFromDB(){
        int result = -1;
        if(isDelMulti){

        }else{
            result =dbHelper.deleteEvent(dateTime,userID);
        }
        dbHelper.closeDB();
        if(result !=0){
            return -1;
        }
        return 0;
    }


    // 回调，监听上传是否成功
    class eventDeleteCallBack extends RequestCallBack<String> {
        @Override
        public void onFailure(HttpException arg0, String result) {
            //Log.i("上传失败", arg0 + "|||" + result);
            msg.what = 3;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            String result = responseInfo.result;
            // 返回值是一个ID
            if ("ok".equals(result)) { // 请求成功
                //Log.i("result", result + " |*| " + "commentOK");
                //删除事件成功
                if(deleteFromDB() == 0){
                    msg.what = 0;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }else{
                    //删除 事件失败
                    msg.what = 1;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            } else {
                //服务器返回值不正确
                msg.what = 2;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }
    }
}
