package com.lessask.dongfou.model;

import com.lessask.dongfou.ResponseError;

/**
 * Created by laiqin on 16/4/9.
 */
public class Version extends ResponseError {
    private int versioncode;
    private String versionname;

    public Version(int versioncode, String versionname) {
        this.versioncode = versioncode;
        this.versionname = versionname;
    }

    public Version(int errno, String error, int versioncode, String versionname) {
        super(errno, error);
        this.versioncode = versioncode;
        this.versionname = versionname;
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
