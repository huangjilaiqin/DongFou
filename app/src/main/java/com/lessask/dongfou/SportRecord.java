package com.lessask.dongfou;

import java.util.Date;

/**
 * Created by JHuang on 2016/3/26.
 */
public class SportRecord extends ResponseError {
    private int id;
    private int sportid;
    private float amount;
    private float arg1;
    private float arg2;
    private int seq;
    private Date time;
    private int userid;

    public SportRecord(int id, int sportid, float amount, float arg1, float arg2, int seq, Date time,int userid) {
        this.id = id;
        this.sportid = sportid;
        this.amount = amount;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.seq = seq;
        this.time = time;
        this.userid = userid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSportid() {
        return sportid;
    }

    public void setSportid(int sportid) {
        this.sportid = sportid;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getArg1() {
        return arg1;
    }

    public void setArg1(int arg1) {
        this.arg1 = arg1;
    }

    public float getArg2() {
        return arg2;
    }

    public void setArg2(int arg2) {
        this.arg2 = arg2;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
