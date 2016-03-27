package com.lessask.dongfou;

import java.util.Date;

/**
 * Created by JHuang on 2016/3/27.
 */
public class SportGather {
    int sportid;
    float avg;
    float total;
    Date lastTime;
    int seq;

    public SportGather(int sportid,float total,float avg,Date lastTime, int seq) {
        this.sportid = sportid;
        this.avg = avg;
        this.total = total;
        this.lastTime = lastTime;
        this.seq = seq;
    }

    public int getSportid() {
        return sportid;
    }

    public void setSportid(int sportid) {
        this.sportid = sportid;
    }

    public float getAvg() {
        return avg;
    }

    public void setAvg(float avg) {
        this.avg = avg;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
