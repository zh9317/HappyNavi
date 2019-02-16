package com.trackersurvey.util;

import android.webkit.JavascriptInterface;

import org.json.JSONObject;

import wendu.dsbridge.CompletionHandler;

/**
 * Created by zh931 on 2018/6/18.
 */

public class JsApi {
    //for synchronous invocation
    @JavascriptInterface
    public String testSyn(Object msg)  {
        return msg + "［syn call］";
    }

    //for asynchronous invocation
    @JavascriptInterface
    public void testAsyn(Object msg, CompletionHandler handler) {
        handler.complete(msg+" [ asyn call]");
    }
    @JavascriptInterface
    public void onAjaxRequest(JSONObject jsonObject, final CompletionHandler handler) {

    }
}
