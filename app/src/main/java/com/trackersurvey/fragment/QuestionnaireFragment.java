package com.trackersurvey.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.happynavi.R;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.http.TestForWeb;
import com.trackersurvey.util.AppManager;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.MyWebChromeClient;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ZhangHao on 2017/9/4.
 */

public class QuestionnaireFragment extends Fragment {

    private View questionnaireLayout;
//    private Banner banner;
//    private List<String> imageUrl;
//    private GridView discoverGv;
//    private SimpleAdapter gridAdapter;
//    private List<Map<String, Object>> gridDataList;
//    private int[] icon = {
//            R.mipmap.ic_travel_diary,
//            R.mipmap.ic_discovery_notice,
//            R.mipmap.ic_questionary,
//            R.mipmap.ic_point_store,};
//    private String[] iconName = {"游记攻略","活动通知","问卷调查","积分商城"};
    private TextView titleTv;
    private WebView webview;
    //private DWebView dWebView;
    private TextView refresh;
    private SharedPreferences sp;
    private String webContent;
    private RelativeLayout titleBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        questionnaireLayout = inflater.inflate(R.layout.fragment_questionnaire, container, false);
//        initView();
        StatusBarCompat.setStatusBarColor(getActivity(), Color.BLACK); // 修改状态栏颜色
        titleTv = questionnaireLayout.findViewById(R.id.title_text);
        titleTv.setText(getResources().getString(R.string.questionnaire));
        titleBack = questionnaireLayout.findViewById(R.id.title_back_layout);
        titleBack.setVisibility(View.GONE);
        AppManager.getAppManager().addActivity(getActivity());
        init();
        return questionnaireLayout;
    }

    @SuppressLint("JavascriptInterface")
    public void init(){
        Log.i("Question", "版本号：" + Common.getAppVersionName(getContext()));
        sp = getActivity().getSharedPreferences("config", MODE_PRIVATE);
        //dWebView = findViewById(R.id.dWebView);
        //dWebView.addJavascriptObject(new JsApi(), null);
        webview = questionnaireLayout.findViewById(R.id.question_web_view);
        //settings.setDisplayZoomControls(false);
        //settings.setDomStorageEnabled(true);
        refresh = questionnaireLayout.findViewById(R.id.title_right_text);
        refresh.setText(getResources().getString(R.string.refresh));
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.reload();
            }
        });
        syncCookie("http://211.87.235.147:8089/footPrint/questionnaire/wx_getQuestionnaireList?_appkey=wx&Token=");
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
        Log.i("Question", "http://interface.hptracker.com:8090/questionnaire/wxShowQuestionnaireList?UserID="
                +sp.getInt("userID", 0)+"&AppID="+Common.version);
//        webview.loadUrl("http://211.87.235.147:8089/footPrint/questionnaire/wx_getQuestionnaireList?_appkey=wx&Token="+sp.getString("Token",""));
        webview.loadUrl("http://interface.hptracker.com:8090/questionnaire/wxShowQuestionnaireList?UserID="
                +sp.getInt("userID", 0)+"&AppID="+Common.version);

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
        CookieSyncManager.createInstance(getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.setCookie(url, "Set-Cookie:Token:"+sp.getString("Token", ""));
        CookieSyncManager.getInstance().sync();
        String cookie = cookieManager.getCookie(url);
        Log.i("WebViewCookie", "WebViewCookie:" + cookie);
    }

    //    private void initBannerData() {
//        //图片地址
//        imageUrl = new ArrayList<>();
//        imageUrl.add("http://img.zcool.cn/community/0197da57bced510000018c1b16d3a7.jpg");
//        imageUrl.add("http://pic.qiantucdn.com/58pic/26/13/39/60a58PICQbc_1024.jpg");
//        imageUrl.add("http://pic3.16pic.com/00/49/43/16pic_4943567_b.jpg");
//        imageUrl.add("http://pic.qiantucdn.com/58pic/18/73/88/04X58PIC3Tk_1024.jpg");
//        imageUrl.add("http://pic.qiantucdn.com/58pic/25/73/84/76658PICvnJ_1024.jpg");
//    }

//    public List<Map<String, Object>> getGridData() {
//        gridDataList = new ArrayList<>();
//        for (int i = 0; i < icon.length; i++) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("image", icon[i]);
//            map.put("text", iconName[i]);
//            gridDataList.add(map);
//        }
//        return gridDataList;
//    }

//    private void initView() {
//        banner = questionnaireLayout.findViewById(R.id.discovery_banner);
//        initBannerData();
//        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
//        banner.setImageLoader(new GlideImageLoader());
//        banner.setImages(imageUrl);
//        //banner.setBannerTitles(bannerTitle);
//        banner.setDelayTime(3000);
//        banner.start();
//
//        discoverGv = questionnaireLayout.findViewById(R.id.discover_gv);
//        getGridData();
//        String[] from ={"image","text"};
//        int[] to = { R.id.grid_image, R.id.grid_text };
//        gridAdapter = new SimpleAdapter(getContext(), gridDataList, R.layout.grid_item, from, to);
//        discoverGv.setAdapter(gridAdapter);
//        discoverGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 2:
//                        Intent questionIntent = new Intent(getContext(), QuestionaryActivity.class);
//                        startActivity(questionIntent);
//                        break;
//                }
//            }
//        });
//    }
}
