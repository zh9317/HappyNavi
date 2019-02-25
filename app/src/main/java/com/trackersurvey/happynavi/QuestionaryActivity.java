package com.trackersurvey.happynavi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.http.QuestionaryRequest;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.http.TestForWeb;
import com.trackersurvey.util.AppManager;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.JsApi;
import com.trackersurvey.util.MyWebChromeClient;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;

import wendu.dsbridge.DWebView;
import wendu.dsbridge.OnReturnValue;

public class QuestionaryActivity extends BaseActivity {

    private TextView titleTv;
    private WebView webview;
    //private DWebView dWebView;
    private TextView refresh;
    private SharedPreferences sp;
    private String webContent;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionary);
        Log.i("Question", "版本号：" + Common.getAppVersionName(this));
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        titleTv = findViewById(R.id.title_text);
        titleTv.setText(getResources().getString(R.string.questionnaire));
        AppManager.getAppManager().addActivity(this);
        init();
    }
    @SuppressLint("JavascriptInterface")
    public void init(){
        Log.i("Question", "版本号：" + Common.getAppVersionName(this));
        sp = getSharedPreferences("config", MODE_PRIVATE);
        //dWebView = findViewById(R.id.dWebView);
        //dWebView.addJavascriptObject(new JsApi(), null);
        webview = (WebView)findViewById(R.id.question_web_view);
        //settings.setDisplayZoomControls(false);
        //settings.setDomStorageEnabled(true);
        refresh = findViewById(R.id.title_right_text);
        refresh.setText(getResources().getString(R.string.refresh));
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.reload();
            }
        });
//        syncCookie("http://211.87.235.147:8089/footPrint/questionnaire/wx_getQuestionnaireList?_appkey=wx&Token=");
//        syncCookie(context, "http://211.87.235.147:8089/footPrint/questionnaire/test?_appkey=wx&Token=");
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setDomStorageEnabled(true);
        webview.setDrawingCacheEnabled(false);
//        webview.loadUrl("http://211.87.235.147:8089/footPrint/questionnaire/wx_getQuestionnaireList?_appkey=wx&Token="+sp.getString("Token",""));
        webview.loadUrl("http://interface.hptracker.com:8090/questionnaire/wxShowQuestionnaireList?UserID="
                +sp.getString("userID", "")+"&AppID="+Common.getAppVersionName(this));
//        webview.loadUrl("http://211.87.235.147:8089/footPrint/questionnaire/test?_appkey=wx&Token="+sp.getString("Token",""));
//        settings.setUseWideViewPort(true);
        //setLoadWithOverviewMode(true);
        //settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        //webview.loadUrl("http://219.218.118.176:8089/User/TotalNaire.aspx?uid=" + Common.getUserId(this));
//        if(l==0){
//            webview.loadUrl("http://219.218.118.176:8089/User/TotalNaire.aspx?uid="+Common.getUserId(this));
//        }
//        if(l==1){
//            webview.loadUrl("http://219.218.118.176:8089/User/TotalNaireEn.aspx?uid="+Common.getUserId(this));
//        }
//        webview.loadUrl("http://211.87.235.102/User/TotalNaire.aspx?uid="+Common.getUserId(this));
//        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //webview.setBackgroundColor(Color.WHITE);
        /**
         * setWebChromeClient主要处理解析，渲染网页等浏览器做的事情
         * WebChromeClient是辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
         */
        webview.setWebChromeClient(new MyWebChromeClient());
        //webview.addJavascriptInterface(this, "Token");
        webview.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {//当页面加载完成后再调用js函数，否则不执行。
//		    	 webview.loadUrl("javascript:getToken('"+sp.getString("Token","")+"')");
//		    	 Log.i("trailadapter", "发送用户名");
                super.onPageFinished(view, url);
            }
        });

//        dWebView.callHandler("addValue", new Object[]{3, 4}, new OnReturnValue<Integer>() {
//            @Override
//            public void onValue(Integer integer) {
//                Log.i("jsbridge", "call succeed,return value is"+integer);
//            }
//        });

        TestForWeb testForWeb = new TestForWeb(sp.getString("Token", ""));
        testForWeb.requestHttpData(new ResponseData() {
            @Override
            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {

            }
        });

//        QuestionaryRequest questionaryRequest = new QuestionaryRequest(sp.getString("Token", ""));
//        questionaryRequest.requestHttpData(new ResponseData() {
//            @Override
//            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
//                if (isSuccess) {
//                    webContent = (String) responseObject;
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            webview.loadDataWithBaseURL(null, webContent, "text/html", "utf-8", null);
//                            webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//                            webview.setBackgroundColor(Color.WHITE);
//                            webview.setWebChromeClient(new MyWebChromeClient());
//                            //webview.addJavascriptInterface(this, "qusetionnaire");
//                            webview.setWebViewClient(new WebViewClient() {
//                                public void onPageFinished(WebView view, String url) {//当页面加载完成后再调用js函数，否则不执行。
//		       /*
//		    	 webview.loadUrl("javascript:getUserID('"+Common.userId+"')");
//		    	 Log.i("trailadapter", "发送用户名");
//		    	*/
//                                    super.onPageFinished(view, url);
//                                }
//                            });
//                        }
//                    });
//                }
//            }
//        });

    }

    private void syncCookie(String url) {
        CookieSyncManager.createInstance(getApplicationContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.setCookie(url, "Set-Cookie:Token:"+sp.getString("Token", ""));
        CookieSyncManager.getInstance().sync();
        String cookie = cookieManager.getCookie(url);
        Log.i("WebViewCookie", "WebViewCookie:" + cookie);
    }

}
