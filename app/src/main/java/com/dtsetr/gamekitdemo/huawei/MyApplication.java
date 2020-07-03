package com.dtsetr.gamekitdemo.huawei;
import com.huawei.hms.api.HuaweiMobileServicesUtil;
import android.app.Application;

public class MyApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HuaweiMobileServicesUtil.setApplication(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
