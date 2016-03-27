package com.lessask.dongfou;

import java.util.Date;

/**
 * Created by JHuang on 2016/3/27.
 */
public class Sport {
    int id;
    String name;
    String image;
    int kind;
    String unit;
    int maxnum;
    String unit2;
    int maxnum2;

    //汇总信息
    int frequency;
    float total;
    float avg;
    int days;
    Date lastTime;
    int seq;
    int lastValue;
    int lastValue2;
    //db.execSQL("create table t_sport(id int primary key,name text not null,image text not null,type int not null,unit text not null,unit2 text null" +
     //       ",maxnum int not null,frequency int not null default 0,total real not null,avg real not null,days int not null,last_time int NOT NULL,seq int not null default 0)");
    public Sport(int id, String name, String image,int kind,String unit,int maxnum,String unit2,int maxnum2,int frequency
            ,float total,float avg,int days,Date lastTime,int seq,int lastValue,int lastValue2) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.kind=kind;
        this.unit = unit;
        this.maxnum=maxnum;
        this.unit2=unit2;
        this.maxnum2=maxnum2;
        this.frequency=frequency;
        this.total=total;
        this.avg=avg;
        this.days=days;
        this.lastTime=lastTime;
        this.seq=seq;
        this.lastValue=lastValue;
        this.lastValue2=lastValue2;
    }

    public int getLastValue() {
        return lastValue;
    }

    public void setLastValue(int lastValue) {
        this.lastValue = lastValue;
    }

    public int getLastValue2() {
        return lastValue2;
    }

    public void setLastValue2(int lastValue2) {
        this.lastValue2 = lastValue2;
    }

    public int getMaxnum2() {
        return maxnum2;
    }

    public void setMaxnum2(int maxnum2) {
        this.maxnum2 = maxnum2;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getAvg() {
        return avg;
    }

    public void setAvg(float avg) {
        this.avg = avg;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
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

    public int getMaxnum() {
        return maxnum;
    }

    public void setMaxnum(int maxnum) {
        this.maxnum = maxnum;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public String getUnit2() {
        return unit2;
    }

    public void setUnit2(String unit2) {
        this.unit2 = unit2;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
