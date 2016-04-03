package com.lessask.dongfou;

/**
 * Created by laiqin on 16/4/3.
 */
public class User extends ResponseError{
    private int id;

    public User(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
