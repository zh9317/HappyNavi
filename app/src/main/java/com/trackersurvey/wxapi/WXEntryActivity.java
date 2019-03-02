package com.trackersurvey.wxapi;

import android.app.Activity;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.trackersurvey.util.ShareToWeChat;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api = ShareToWeChat.api;

    @Override
    public void onReq(BaseReq baseReq) {
        api.handleIntent(getIntent(), this);
        Log.i("Eaa_wx_result", baseReq.toString());
    }

    @Override
    public void onResp(BaseResp baseResp) {

    }
}
