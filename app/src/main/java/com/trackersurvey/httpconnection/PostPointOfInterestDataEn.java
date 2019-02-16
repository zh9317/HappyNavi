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
import org.apache.http.params.CoreConnectionPNames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zh931 on 2018/5/14.
 */

public class PostPointOfInterestDataEn extends Thread {
    private Handler mHandler;
    private String url;
    private HttpPost httpRequest;
    private List<NameValuePair> params = new ArrayList<NameValuePair>();

    public PostPointOfInterestDataEn(Handler mHandler, String url) {
        this.mHandler = mHandler;
        this.url = url;
    }

    public void run() {
        params.add(new BasicNameValuePair("requestType", "poiChoices"));
        params.add(new BasicNameValuePair("lang", "EN"));
        Post();
    }

    private void Post() {
        Message msg = Message.obtain();
        try {
            HttpEntity httpEntity = new UrlEncodedFormEntity(params, "utf-8");
            httpRequest = new HttpPost(url);
            httpRequest.setEntity(httpEntity);

            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);// 连接时间
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);// 数据传输时间

            HttpResponse httpResponse = httpClient.execute(httpRequest);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                BufferedReader bin = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                String result = bin.readLine();
                Log.i("poiadapter", "PostPointOfInterest,readLine:"+result);
                if (result != null) {
                    if (result.equals("fail")) {
                        msg.what = 1;
                    } else {
                        msg.what = 0;
                    }
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                } else {
                    msg.what = 1;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            } else {
                msg.what = 10;

                msg.obj = "提交失败!";
                mHandler.sendMessage(msg);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            msg.what = 11;
            msg.obj = "提交失败！";
            mHandler.sendMessage(msg);
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            msg.what = 11;
            msg.obj = "提交失败！";
            mHandler.sendMessage(msg);
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            msg.what = 11;
            msg.obj = "提交失败！";
            mHandler.sendMessage(msg);
            e.printStackTrace();
        }
    }
}
