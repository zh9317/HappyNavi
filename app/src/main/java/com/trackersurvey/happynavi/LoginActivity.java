package com.trackersurvey.happynavi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.http.HttpUtil;
import com.trackersurvey.http.LoginRequest;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.model.LoginModel;
import com.trackersurvey.util.AppManager;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.DESUtil;
import com.trackersurvey.util.HMAC_SHA1_Util;
import com.trackersurvey.util.MD5Util;
import com.trackersurvey.util.ToastUtil;

import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    public static final String times ="timeCount";
    public static final String winWidth = "winWidth";
    public static final String winHeight = "winHeight";
    public static final String PPISCALE = "ppiScale";
    public String login_url = null;
    public String register_information_url = null;
    public static final String mobConnectFirst = "mobConnectFirstUse";
    public String result;
    private LoginModel loginModel;
    public String information;
    public EditText uidEt;
    public EditText pwdEt;
    public CheckBox remeber_pwd;
    public CheckBox agree_protocol;
    public TextView appVersion;
    //public TextView forgetpassword;
    public TextView protocal;
    public Button register;
    public Button login;
    String show;
    public String uid; // 用户账号（手机号）
    private SharedPreferences sp; // android系统下用于数据存贮的一个方便的API
    //private SharedPreferences loginSp; // 用于存放登录后返回的用户信息和Token
    long lastClick; //用户上次单击时间
    private ProgressDialog proDialog = null;
    public static String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            //结束你的activity
            finish();
            return;
        }
        setContentView(R.layout.activity_login);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        AppManager.getAppManager().addActivity(this);
        // sp 初始化
        sp = getSharedPreferences("config",MODE_PRIVATE);//私有参数
        //loginSp = getSharedPreferences("login", MODE_PRIVATE);
        //是否第一次使用应用
        int timeCount = sp.getInt(times, 0);
        if(timeCount == 0) {
            firstTimeDone();
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(times, timeCount+1);
        editor.apply();
//        String lastId = sp.getString("userPhone", "0");
//        String nickname = sp.getString("nickname", "");
//        String headphoto = sp.getString("headphoto", "");
//        String deviceid = sp.getString("deviceid", "null");
//        if(deviceid.equals("null")){//没有设置设备id，需要设置
//            editor.putString("deviceid", Common.setDeviceId(getApplicationContext(), LoginActivity.this));
//            editor.commit();
//        }
        Log.i("phonelog", "deviceID:" + Common.getDeviceId(getApplicationContext()));
        Log.i("phonelog","getDeviceName :"+Common.getDeviceName());
        if (!sp.getString("token", "").equals("")) {
            // token不为空字符串则挑过登录界面
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

//        if(!lastId.equals("0")){
//            //Common.userId=lastId;
//            if(!nickname.equals("")){
//                Common.NickName = nickname;
//            }
//            if(!headphoto.equals("")){
//                //Common.pic=headphoto;
//            }
//            Log.i("LoginActivity", "检测上次登录的id，存在id和密码，跳转");
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);//检验上次登录的id，存在id和密码，跳转
//            startActivity(intent);
//        }
        uidEt = (EditText) findViewById(R.id.et_userid);
        pwdEt = (EditText) findViewById(R.id.et_password);
        //remeber_pwd=(CheckBox) findViewById(R.id.checkBox);
        agree_protocol = (CheckBox) findViewById(R.id.agree);
        //forgetpassword=(TextView) findViewById(R.id.forgetpassword);

        protocal = (TextView) findViewById(R.id.protocal);
        protocal.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        register = (Button) findViewById(R.id.btn_register);
        login = (Button) findViewById(R.id.btn_login);
        register.setOnClickListener(this);
        login.setOnClickListener(this);

//	    forgetpassword.setOnClickListener(new OnClickListener() {
//
//
//			public void onClick(View v) {
//				Intent intent=new Intent(LoginActivity.this,ForgetPassword.class);
//				startActivity(intent);
//
//			}
//		});
        protocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alert =new AlertDialog.Builder(LoginActivity.this).create();
                alert.setTitle(getResources().getString(R.string.tips_dlgtle_protocol));
                alert.setMessage(getResources().getString(R.string.tips_dlgmsg_protocol1)
                        +"\n"+getResources().getString(R.string.tips_dlgmsg_protocol2)
                        +"\n"+getResources().getString(R.string.tips_dlgmsg_protocol3)
                        +"\n"+getResources().getString(R.string.tips_dlgmsg_protocol4)
                        +"\n"+getResources().getString(R.string.tips_dlgmsg_protocol5));
                alert.setButton(DialogInterface.BUTTON_NEGATIVE,getResources().getString(R.string.close),  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();
            }
        });
        //获取SP存储的数据
        String saveId = sp.getString("lastInputID", "");
        //String savePsw = sp.getString("lastInputPSW", "");
        uidEt.setText(saveId);
        //pwdEt.setText(savePsw);

        if(Common.url != null && !Common.url.equals("")) {

        } else {
            Common.url = getResources().getString(R.string.url);
        }
        login_url = Common.url + "userLogin.aspx";
        register_information_url = Common.url + "reqRegInfo.aspx";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
//                Intent intentM = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intentM);
                login(); // 新接口
                // submit(); // 旧接口
                break;
            case R.id.btn_register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }
    public void login(){
        String pwd;
        uid = uidEt.getText().toString();
        pwd = pwdEt.getText().toString();
        String pwdMD5 = MD5Util.string2MD5(pwd);
//        String pwdSHA1 = HMAC_SHA1_Util.genHMAC(pwd, );
        if (uid.equals("") || pwd.equals("")) {
            Toast.makeText(this,getResources().getString(R.string.idpwdcannotnull), Toast.LENGTH_SHORT).show();
        }else {
            //获取到一个参数文件编辑器
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("lastInputID", uid);
            editor.putString("lastInputPSW",pwd);
            editor.apply();//把数据保存到sp里
            //判断是否进行MD5
            //若版本号小于"1.2.5"则不加密
            boolean encrypt = (Common.version.compareTo("1.2.5") >= 0);
            if (encrypt) {
                pwd = pwdMD5;
            }
            if (agree_protocol.isChecked()) {
                showDialog(getResources().getString(R.string.tips_dlgtle_login),
                        getResources().getString(R.string.tips_dlgmsg_login));
                // 调用登录接口
                LoginRequest loginRequest = new LoginRequest(uid, pwd.toUpperCase());
                loginRequest.requestHttpData(new ResponseData() {
                    @Override
                    public void onResponseData(boolean isSuccess, String code, Object responseObject, final String msg) throws IOException {
                        if (msg.equals("无法连接")) {
                            dismissDialog();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            if (isSuccess) {
                                if (code.equals("0")) {
                                    loginModel = (LoginModel) responseObject;
                                    token = loginModel.getToken();
                                    Log.i("HttpUtilDecrypt", "token:" + token);
                                    SharedPreferences.Editor loginEditor = sp.edit();
                                    loginEditor.putString("token", token);
                                    loginEditor.putInt("userID", loginModel.getUserInfo().getUserid());
                                    loginEditor.putString("userPhone", loginModel.getUserInfo().getMobilephone());
                                    loginEditor.putString("birthDate", loginModel.getUserInfo().getBirthdate());
                                    loginEditor.putString("headurl", loginModel.getUserInfo().getHeadurl());
                                    loginEditor.putString("mobilePhone", loginModel.getUserInfo().getMobilephone());
                                    loginEditor.putString("nickname", loginModel.getUserInfo().getNickname());
                                    loginEditor.putString("realName", loginModel.getUserInfo().getRealname());
                                    loginEditor.putString("nativePlace", loginModel.getUserInfo().getRegisteritem2());
                                    loginEditor.putString("address", loginModel.getUserInfo().getRegisteritem3());
                                    loginEditor.putString("education", loginModel.getUserInfo().getRegisteritem4());
                                    loginEditor.putString("income", loginModel.getUserInfo().getRegisteritem5());
                                    loginEditor.putString("occupation", loginModel.getUserInfo().getRegisteritem6());
                                    loginEditor.putString("marriage", loginModel.getUserInfo().getRegisteritem7());
                                    loginEditor.putString("childCount", loginModel.getUserInfo().getRegisteritem8());
                                    loginEditor.putInt("sex", loginModel.getUserInfo().getSex());
                                    loginEditor.apply();
                                    Log.i("LoginActivity", "token:" + sp.getString("token", "")
                                            + "; userID:" + sp.getInt("userID", 0)
                                            + "; userPhone:" + sp.getString("userPhone", "")
                                            + "; birthDate:" + sp.getString("birthDate", "")
                                            + "; headurl:" + sp.getString("headurl", "")
                                            + "; mobilePhone:" + sp.getString("mobilePhone", "")
                                            + "; nickname:" + sp.getString("nickname", "")
                                            + "; realName:" + sp.getString("realName", "")
                                            + "; city:" + sp.getString("city", "")
                                            + "; address:" + sp.getString("address", "")
                                            + "; education:" + sp.getString("education", "")
                                            + "; income:" + sp.getString("income", "")
                                            + "; occupation:" + sp.getString("occupation", "")
                                            + "; marriage:" +sp.getString("marriage", "")
                                            + "; chileCount:" + sp.getString("childCount", "")
                                            + "; sex:" + sp.getInt("sex", 0));
                                    Log.i("LoginActivity", "token:" + token
                                            + "; userID:" + loginModel.getUserInfo().getUserid()
                                            + "; userPhone:" + loginModel.getUserInfo().getMobilephone()
                                            + "; birthDate:" + loginModel.getUserInfo().getBirthdate()
                                            + "; headurl:" + loginModel.getUserInfo().getHeadurl()
                                            + "; mobilePhone:" + loginModel.getUserInfo().getMobilephone()
                                            + "; nickname:" + loginModel.getUserInfo().getNickname()
                                            + "; realName:" + loginModel.getUserInfo().getRealname()
                                            + "; city:" + loginModel.getUserInfo().getRegisteritem2()
                                            + "; address:" + loginModel.getUserInfo().getRegisteritem3()
                                            + "; education:" + loginModel.getUserInfo().getRegisteritem4()
                                            + "; income:" + loginModel.getUserInfo().getRegisteritem5()
                                            + "; occupation:" + loginModel.getUserInfo().getRegisteritem6()
                                            + "; marriage:" + loginModel.getUserInfo().getRegisteritem7()
                                            + "; chileCount:" + loginModel.getUserInfo().getRegisteritem8()
                                            + "; sex:" + loginModel.getUserInfo().getSex());
                                    dismissDialog();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    // 登录成功后跳转到MainActivity，首先显示地图界面
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                if (code.equals("200")) {
                                    // 该用户名不存在或已停用!
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    dismissDialog();
                                }
                                if (code.equals("201")) {
                                    // 密码错误,请重新登录!
                                    dismissDialog();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }else {
                                dismissDialog();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showShortToast(LoginActivity.this, "登录失败");
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
    }

    public void submit() {
        String pwd;
        uid = uidEt.getText().toString();
        pwd = pwdEt.getText().toString();
        String s_pwd_md5 = MD5Util.string2MD5(pwd);
        if(uid.equals("") || pwd.equals(""))//判断账号密码不能为空
        {
            Toast.makeText(this,getResources().getString(R.string.idpwdcannotnull), Toast.LENGTH_SHORT).show();
        } else {
            //勾选 记录用户名和密码
            //获取到一个参数文件编辑器
            SharedPreferences.Editor editor=sp.edit();
            //Log.i("checked", "1");
            editor.putString("id", uid);
            //Log.i("checked", "2");
            editor.putString("psw",pwd);
            //Log.i("checked", "3");
            editor.commit();//把数据保存到sp里
            //Log.i("checked", "4");
            //Toast.makeText(getApplicationContext(), "以保存", 1).show();


            //判断是否进行MD5
            //若版本号小于"1.2.5"则不加密
            boolean jiami = ( Common.version.compareTo("1.2.5") >= 0);
            if(jiami) {
                show = uid + "!" + s_pwd_md5;
            } else {
                show = uid + "!" + pwd;
            }
            if(agree_protocol.isChecked()) {
                showDialog(getResources().getString(R.string.tips_dlgtle_login),
                        getResources().getString(R.string.tips_dlgmsg_login));
                //Log.i("phonelog", s_pwd+"--->"+s_pwd_md5);
                //PostLoginData pld = new PostLoginData(handler_login, login_url, show,Common.getDeviceId(getApplicationContext()));
                //PostLoginData pld=new PostLoginData(handler_login, "http://211.87.235.120:8080/footPrint/user/login", show,Common.getDeviceId(getApplicationContext()));
                Log.i("LoginMsg", show);
                //pld.start();
            } else {
                Toast.makeText(this,getResources().getString(R.string.tips_agreeprotocol), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //登陆接受消息
    @SuppressLint("HandlerLeak")
    private Handler handler_login = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            dismissDialog();
            switch (msg.what) {
                case 0:// 登陆成功
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("lastid", uid);
                    //Common.userId=s_id;
                    result = (msg.obj.toString().trim());
                    try {
                        result = DESUtil.decrypt(result);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    // Log.i("LogDemo", "dereadLine:"+result);
                    String[] get=result.split("!");
                    if(get.length>=3){
                        try {
                            //Common.pic=get[1];//存图片字符串
                            Common.NickName=get[2];//昵称
                            editor.putString("nickname", get[2]);
                            editor.putString("headphoto", get[1]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        editor.putString("nickname", get[2]);
                        editor.putString("headphoto", get[1]);
                    }
                    editor.commit();
                    result = null;
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);//登陆成功跳转
                    startActivity(intent);
                    //finish();
                    break;
                case 1:
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.tips_postfail),Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    //result = (msg.obj.toString().trim());
                    ToastUtil.show(LoginActivity.this,getResources().getString(R.string.tips_loginfail_noid));
                    break;
                case 5:
                    //result = (msg.obj.toString().trim());
                    ToastUtil.show(LoginActivity.this,getResources().getString(R.string.tips_loginfail_nopwd));
                    break;
                case 10:
                    //result = (msg.obj.toString().trim());
                    ToastUtil.show(LoginActivity.this,getResources().getString(R.string.tips_netdisconnect));
                    break;
                case 6:
                    //result = (msg.obj.toString().trim());
                    break;
            }
        }
    };

    /**
     * 显示进度条对话框
     */
    public void showDialog(String title,String message) {
        if (proDialog == null)
            proDialog = new ProgressDialog(this);
        proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        proDialog.setIndeterminate(false);
        proDialog.setCancelable(false);
        proDialog.setTitle(title);
        proDialog.setMessage(message);
        proDialog.show();
    }
    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (proDialog != null) {
            proDialog.dismiss();
        }
    }
    /**
     * 只在安装后第一次使用应用执行
     */
    void firstTimeDone(){
        //获取用户手机屏幕分辨率、ppi与dip比率,写入sharedPreference
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        SharedPreferences.Editor editorSp = sp.edit();
        editorSp.putInt(winWidth, dm.widthPixels);
        editorSp.commit();
        editorSp.putInt(winHeight, dm.heightPixels);
        editorSp.commit();

        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        editorSp.putFloat(PPISCALE, scale);
        editorSp.putInt(mobConnectFirst, 0);
        editorSp.commit();
    }
}
