package com.trackersurvey.httpconnection;

import android.os.Handler;
import android.os.Message;

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
 * 下载原图或视频
 */

public class GetCloudPicture extends Thread {
    private Handler mHandler;
    private String url;
    private String userID;
    private String dateTime;
    private String fileNo;
    private String deviceId;
    private HttpPost httpRequest;
    private List<NameValuePair> params = new ArrayList<NameValuePair>();

    public GetCloudPicture(Handler handler, String url, String userID,
                           String dateTime,String fileNo,String deviceId) {
        this.mHandler = handler;
        this.url = url;
        this.userID = userID;
        this.dateTime = dateTime;
        this.fileNo = fileNo;
        this.deviceId = deviceId;
    }

    public void run() {
        Message msg = Message.obtain();
        params.add(new BasicNameValuePair("userId", userID));
        params.add(new BasicNameValuePair("dateTime", dateTime));
        params.add(new BasicNameValuePair("fileNo", fileNo));
        params.add(new BasicNameValuePair("deviceId", deviceId));
        HttpEntity httpEntity;
        try {
            httpEntity = new UrlEncodedFormEntity(params, "utf-8");
            httpRequest = new HttpPost(url);
            httpRequest.setEntity(httpEntity);

            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpRequest);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                BufferedReader bin = new BufferedReader(new InputStreamReader(
                        httpResponse.getEntity().getContent()));
                String result = bin.readLine();
                //Log.i("Eaa", "getCloudPicture，length:" + result.length());
                if (result != null && !"".equals(result)) {
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                } else {
                    msg.what = 1;
                    msg.obj = result;

                    mHandler.sendMessage(msg);
                }

            }

            else {
                msg.what = 10;

                msg.obj = "提交失败!";
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
