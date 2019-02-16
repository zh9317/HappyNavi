package com.trackersurvey.httpconnection;

import com.trackersurvey.util.Common;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zh931 on 2018/5/13.
 * 用户是否上线
 */

public class PostOnOffline extends Thread {
    private String url,userId,location,status,deviceId;
    private HttpPost httpRequest;
    private List<NameValuePair> params = new ArrayList<NameValuePair>();

    public PostOnOffline(String url,String userId,String location,String status,String deviceId) {


        //this.mHandler = mHandler;
        this.url=url;
        this.userId=userId;
        this.location=location;
        this.status=status;
        this.deviceId=deviceId;
    }

    public void run() {

        params.add(new BasicNameValuePair("userId", userId));
        params.add(new BasicNameValuePair("location",location));
        params.add(new BasicNameValuePair("deviceId",deviceId));
        params.add(new BasicNameValuePair("requestType",status));
        if(status.trim().equals("Online")){
            params.add(new BasicNameValuePair("deviceName", Common.getDeviceName()));
        }
        Post();
    }
    private void Post(){

        try {
            HttpEntity httpEntity = new UrlEncodedFormEntity(params,"utf-8");
            httpRequest = new HttpPost(url);
            httpRequest.setEntity(httpEntity);

            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,3000);//连接时间
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,2000);//数据传输时间

            HttpResponse httpResponse = httpClient.execute(httpRequest);
            /*
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                //BufferedReader bin = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                //String result = bin.readLine();
                //Log.i("LogDemo", "readLine:"+result);

            }else{


            }  */
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }
    }
}
