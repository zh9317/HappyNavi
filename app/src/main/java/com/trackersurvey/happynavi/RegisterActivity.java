package com.trackersurvey.happynavi;

import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.http.GetMsgCodeRequest;
import com.trackersurvey.http.RegisterRequest;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.MD5Util;
import com.trackersurvey.util.ToastUtil;

import java.io.IOException;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private static Button getVrfCodeBtn;
    private EditText uidEt;
    private EditText msgEt;
    private EditText pwdEt;
    private EditText pwdConfirmEt;
    private Button registerBtn;
    private MyCountDownTimer myCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        getVrfCodeBtn = findViewById(R.id.btn_get_verification_code);
        uidEt = findViewById(R.id.et_userid);
        msgEt = findViewById(R.id.et_verification_code);
        pwdEt = findViewById(R.id.et_password);
        pwdConfirmEt = findViewById(R.id.et_confirm_pwd);
        registerBtn = findViewById(R.id.register_complete);
        getVrfCodeBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        myCountDownTimer = new MyCountDownTimer(60000,1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_get_verification_code:
                myCountDownTimer.start();
                GetMsgCodeRequest getMsgCodeRequest = new GetMsgCodeRequest(uidEt.getText().toString());
                getMsgCodeRequest.requestHttpData(new ResponseData() {
                    @Override
                    public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                        if (isSuccess) {
                            switch (code) {
                                case "0":
                                    Toast.makeText(RegisterActivity.this, "获取验证码成功！", Toast.LENGTH_SHORT).show();
                                    break;
                                case "12":
                                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    break;
                                case "13":
                                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "请求超时", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.register_complete:
                register();
                break;
        }
    }

    private void register(){
        String uid = uidEt.getText().toString();
        String shortMsg = msgEt.getText().toString();
        String pwd = pwdEt.getText().toString();
        String pwdMD5 = MD5Util.string2MD5(pwd);
        if (uid.equals("")) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
        } else if (shortMsg.equals("")) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
        } else if (pwd.equals("")) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
        } else if (!pwd.equals(pwdConfirmEt.getText().toString())) {
            Toast.makeText(this, "您输入的密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
        }else {
            Log.i("RegisterActivity", "uid : " + uid + " pwdMD5 : " + pwdMD5.toUpperCase()
                    + " deviceID : " + Common.getDeviceId(getApplicationContext()));
            RegisterRequest registerRequest = new RegisterRequest(uid, pwdMD5.toUpperCase(), shortMsg);
            registerRequest.requestHttpData(new ResponseData() {
                @Override
                public void onResponseData(boolean isSuccess, String code, Object responseObject, final String msg) throws IOException {
                    if (isSuccess) {
                        if (code.equals("0")) {
                            Log.i("RegisterActivity", msg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 哈哈哈
                                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                        if (code.equals("14")) {
                            Log.i("RegisterActivity", msg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 手机验证码超时已失效,请重新获取!
                                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if (code.equals("15")) {
                            Log.i("RegisterActivity", msg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 手机验证码不正确,请重新输入!
                                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if (code.equals("16")) {
                            Log.i("RegisterActivity", msg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 该手机号已经注册过,请直接登录!
                                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "请求超时", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    private class MyCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onTick(long millisUntilFinished) {
            //防止计时过程中重复点击
            getVrfCodeBtn.setClickable(false);
            getVrfCodeBtn.setTextColor(getColor(R.color.gray));
            getVrfCodeBtn.setText(millisUntilFinished/1000 + "秒" + "后重新获取");
            getVrfCodeBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_corner_get_vrf_wait_btn));
        }

        //计时完毕的方法
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onFinish() {
            //重新给Button设置文字
            getVrfCodeBtn.setText("重新获取验证码");
            getVrfCodeBtn.setTextColor(getColor(R.color.white));
            getVrfCodeBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_corner_get_vrf_btn));
            //设置可点击
            getVrfCodeBtn.setClickable(true);
        }
    }
}
