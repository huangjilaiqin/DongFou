package com.lessask.dongfou;

/**
 * Created by laiqin on 16/4/3.
 */
public class User extends ResponseError{
    private int userid;

    public User(int userid) {
        this.userid = userid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
