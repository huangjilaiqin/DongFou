package com.lessask.dongfou;

import android.app.Application;

import com.lessask.dongfou.net.VolleyHelper;


/**
 * Created by huangji on 2015/8/12.
 */
public class MyApplication extends Application{
    private int userid;
    @Override
    public void onCreate() {
        super.onCreate();
        VolleyHelper.setmCtx(getApplicationContext());
    }

}
