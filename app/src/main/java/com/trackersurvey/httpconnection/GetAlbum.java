package com.trackersurvey.httpconnection;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zh931 on 2018/5/19.
 * 下载历史评论（我的相册）
 */

public class GetAlbum extends Thread {
    private Handler mHandler;
    private String url;
    private String userID;
    private String dateTime;
    //	private String startTime;
//	private String endTime;
    private String deviceId;
    private String requestType = "comment";  //请求类型
    private HttpPost httpRequest;
    private String isGetAll = "no";
    private List<NameValuePair> params = new ArrayList<NameValuePair>();

	/*public GetAlbum(Handler handler, String url, String userID,
			String dateTime,String deviceId,String isGetAll) {
		this.mHandler = handler;
		this.url = url;
		this.userID = userID;
		//this.dateTime = dateTime;
		this.startTime = startTime;
		this.endTime = endTime;
		this.deviceId = deviceId;
		this.isGetAll = isGetAll;
	}*/

    public GetAlbum(Handler handler, String url, String userID,
                    String dateTime, String deviceId, String isGetAll) {
        this.mHandler = handler;
        this.url = url;
        this.userID = userID;
        this.dateTime = dateTime;
        //this.startTime = startTime;
        //this.endTime = endTime;
        this.deviceId = deviceId;
        this.isGetAll = isGetAll;
    }

    public void run() {
        Message msg = Message.obtain();
        params.add(new BasicNameValuePair("userId", userID));
        params.add(new BasicNameValuePair("dateTime", dateTime));
        //params.add(new BasicNameValuePair("startTime", startTime));
        //params.add(new BasicNameValuePair("endTime", endTime));
        params.add(new BasicNameValuePair("requestType", requestType));
        params.add(new BasicNameValuePair("deviceId", deviceId));
        params.add(new BasicNameValuePair("isGetAll", isGetAll));
        params.add(new BasicNameValuePair("version", "new"));
        HttpEntity httpEntity;
        try {
            httpEntity = new UrlEncodedFormEntity(params, "utf-8");
            httpRequest = new HttpPost(url);
            httpRequest.setEntity(httpEntity);
            //Log.i("Eaa", "userId:"+userID+" dateTime:"+dateTime+" requestType:"+requestType);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpRequest);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //Log.i("Eaa", "GetAlbum connect :SC_OK");
                BufferedReader bin = new BufferedReader(new InputStreamReader(
                        httpResponse.getEntity().getContent()));
                String result = bin.readLine();
                //Log.i("album", "getCommentInformation，readLine:" + result);
                if (result != null) {
                    msg.what = 0;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                    Log.i("www", "请求成功？ result = "+result);
                } else {
                    msg.what = 1;
                    msg.obj = result;
                    Log.i("Eaa", "请求失败？ result = "+result);
                    mHandler.sendMessage(msg);
                }

            }

            else {
                // Log.i("LogDemo", "连接失败");
                msg.what = 10;
                msg.obj = "提交失败!";
                Log.i("msg.objjj", "提交失败？ ");
                mHandler.sendMessage(msg);
            }

        } catch (UnsupportedEncodingException e) {
            msg.what = 10;
            msg.obj = "提交失败！";
            mHandler.sendMessage(msg);
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            msg.what = 10;
            msg.obj = "提交失败！";
            mHandler.sendMessage(msg);
            e.printStackTrace();
        } catch (IOException e) {
            msg.what = 10;
            msg.obj = "提交失败！";
            mHandler.sendMessage(msg);
            e.printStackTrace();
        }

    }
}
