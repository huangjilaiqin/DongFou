package com.lessask.dongfou.model;

import java.util.Date;

/**
 * Created by laiqin on 16/4/9.
 */
public class Notice {
    private int id;
    private String title;
    private int kind;
    private int status;
    private Date time;
    private String url;

    public Notice(int id,String title, int kind, int status, Date time, String url) {
        this.id = id;
        this.title=title;
        this.kind = kind;
        this.status = status;
        this.time = time;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
