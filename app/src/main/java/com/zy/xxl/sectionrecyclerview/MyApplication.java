package com.zy.xxl.sectionrecyclerview;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Author ： zhangyang
 * Date   ： 2017/11/14
 * Email  :  18610942105@163.com
 * Description  :
 */

public class MyApplication extends Application {

    public static int H,W;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.addLogAdapter(new AndroidLogAdapter());
        getScreen(this);
    }

    public void getScreen(Context aty) {
        DisplayMetrics dm = aty.getResources().getDisplayMetrics();
        H=dm.heightPixels;
        W=dm.widthPixels;
    }
}
