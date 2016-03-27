package com.lessask.dongfou;

import java.util.Date;

/**
 * Created by JHuang on 2016/3/26.
 */
public class SportRecord {
    private int id;
    private int sportid;
    private float amount;
    private int arg1;
    private int arg2;
    private int seq;
    private Date time;

    public SportRecord(int id, int sportid, float amount, int arg1, int arg2, int seq, Date time) {
        this.id = id;
        this.sportid = sportid;
        this.amount = amount;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.seq = seq;
        this.time = time;
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

    public int getArg1() {
        return arg1;
    }

    public void setArg1(int arg1) {
        this.arg1 = arg1;
    }

    public int getArg2() {
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
