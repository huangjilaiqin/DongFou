package com.lessask.dongfou.util;

import android.content.Context;
import android.provider.Settings;

import com.lessask.dongfou.BuildConfig;

/**
 * Created by laiqin on 16/4/3.
 */
public class GlobalInfo {
    private GlobalInfo(){}
    public static final GlobalInfo getInstance(){
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final GlobalInfo INSTANCE = new GlobalInfo();
    }

    private int userid;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

}
