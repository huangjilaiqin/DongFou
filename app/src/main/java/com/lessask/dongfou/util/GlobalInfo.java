package com.lessask.dongfou.util;

import android.content.Context;

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
    private String registerUrl = "http://123.59.40.113/httproute/dongfou/register";
    private String loginUrl = "http://123.59.40.113/httproute/dongfou/login";
    private String uploadRecordUrl = "http://123.59.40.113/httproute/dongfou/upload/sportrecord";

    public String getUploadRecordUrl() {
        return uploadRecordUrl;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getRegisterUrl() {
        return registerUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }
}
