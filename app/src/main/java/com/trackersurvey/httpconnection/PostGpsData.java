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
 * Created by zh931 on 2018/5/12.
 *
 */

public class PostGpsData extends Thread {
    private Handler mHandler;
    private String url,userId,gpsData,deviceId;
    private HttpPost httpRequest;
    private List<NameValuePair> params = new ArrayList<NameValuePair>();

    public PostGpsData(Handler mHandler, String url,String gpsData,String deviceId) {//有数据可传


        this.mHandler = mHandler;
        this.url=url;
        this.gpsData=gpsData;
        this.deviceId=deviceId;
        params.add(new BasicNameValuePair("gpsData", gpsData));
        params.add(new BasicNameValuePair("deviceId", deviceId));
        //Log.i("LogDemo", "PostGpsData");
    }
    public PostGpsData(Handler mHandler, String url,String userId ,String gpsData,String deviceId){//没有数据可传，gpsData为特定字符串
        this.mHandler = mHandler;
        this.url=url;
        this.userId=userId;
        this.gpsData=gpsData;
        this.deviceId=deviceId;
        params.add(new BasicNameValuePair("userId", userId));
        params.add(new BasicNameValuePair("gpsData", gpsData));
        params.add(new BasicNameValuePair("deviceId", deviceId));
    }
    public void run() {
        Post();
    }
    private void Post(){
        Message msg = Message.obtain();
        try {
            //Log.i("LogDemo", "建立连接");
            HttpEntity httpEntity = new UrlEncodedFormEntity(params,"utf-8");
            httpRequest = new HttpPost(url);
            httpRequest.setEntity(httpEntity);

            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,10000);//连接时间
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);//数据传输时间
            //HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            // Log.i("LogDemo", "发送连接");
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                //Log.i("LogDemo", "SC_OK");
                BufferedReader bin = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                String result = bin.readLine();
                Log.i("LogDemo", "PostGpsDatareadLine:"+result);
                if(result!=null){
                    if(result.equals("fail")){
                        msg.what = 1;
                    }

                    else{
                        msg.what = 0;
                    }
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }else{
                    msg.what = 1;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }

            }else{
                //Log.i("LogDemo", "连接失败");
                msg.what = 10;

                msg.obj = "HttpStatus="+httpResponse.getStatusLine().getStatusCode()+","+HttpStatus.SC_OK;
                mHandler.sendMessage(msg);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            msg.what = 10;
            msg.obj = "UnsupportedEncodingException";
            mHandler.sendMessage(msg);
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            msg.what = 10;
            msg.obj = "ClientProtocolException";
            mHandler.sendMessage(msg);
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            msg.what = 10;
            msg.obj = "IOException";
            mHandler.sendMessage(msg);
            e.printStackTrace();
        }
    }
}
