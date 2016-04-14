package com.lessask.dongfou;

/**
 * Created by laiqin on 16/4/3.
 */
public class User extends ResponseError{
    private int userid;
    private String token;

    public User(int userid,String token) {
        this.userid = userid;
        this.token=token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
