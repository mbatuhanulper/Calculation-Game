package com.dtsetr.gamekitdemo.huawei;


import android.util.Log;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

public class MyPushService extends HmsMessageService {
    private static final String TAG = "PushDemoLog";
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.i(TAG, "receive token:" + token);
    }
    @Override
    public void onMessageReceived(RemoteMessage var1) {
        var1.getNotification();

    }

    @Override
    public void onMessageSent(String var1) {
    }
}