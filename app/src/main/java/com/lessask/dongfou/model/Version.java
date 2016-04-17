package com.lessask.dongfou.model;

import com.lessask.dongfou.ResponseError;

/**
 * Created by laiqin on 16/4/9.
 */
public class Version extends ResponseError {
    private int versioncode;
    private String versionname;
    private String url;
    private int minversion;

    public Version(int versioncode, String versionname,String url,int minversion) {
        this.versioncode = versioncode;
        this.versionname = versionname;
        this.url=url;
        this.minversion=minversion;
    }

    public Version(int errno, String error, int versioncode, String versionname,String url,int minversion) {
        super(errno, error);
        this.versioncode = versioncode;
        this.versionname = versionname;
        this.url=url;
        this.minversion=minversion;
    }

    public int getMinversion() {
        return minversion;
    }

    public void setMinversion(int minversion) {
        this.minversion = minversion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public String getVersionname() {
        return versionname;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }
}
