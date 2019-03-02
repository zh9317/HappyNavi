package com.trackersurvey.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.trackersurvey.happynavi.R;

/**
 * Created by zh931 on 2018/5/19.
 */

public class ShareToWeChat {
    public   static IWXAPI api;

    /**
     * 注册到微信
     */
    public static  void registToWeChat(Context context){
        if(Common.WX_APP_ID == null||Common.WX_APP_ID.equals("")){
            Common.WX_APP_ID = context.getResources().getString(R.string.wxappid);
        }
        api = WXAPIFactory.createWXAPI(context, Common.WX_APP_ID,true);
        api.registerApp(Common.WX_APP_ID);
    }

    /**
     * 分享到微信朋友圈或者会话
     * @param url    分享链接
     * @param isTimeLine   是否分享到朋友圈
     */
    public static void shareWeb(Context context,String url,boolean isTimeLine,String title,String detail){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = detail;
        //设置缩略图
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_wx);
        msg.thumbData = WxUtil.bmpToByteArray(thumb, true);

        //构造一个发送到微信请求
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = isTimeLine? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        if(api.sendReq(req)){
            Log.i("Eaa_wx", "发送到微信");
        }else{
            Log.i("Eaa_wx", "发送到微信返回false");
        }
    }


    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
