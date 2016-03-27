package com.lessask.dongfou;

import java.util.ArrayList;

/**
 * Created by JHuang on 2016/3/27.
 */
public class SportDataSet {
    private SportGather sportGather;
    private ArrayList<SportRecord> sportRecords;

    public SportDataSet(SportGather sportGather, ArrayList<SportRecord> sportRecords) {
        this.sportGather = sportGather;
        this.sportRecords = sportRecords;
    }

    public SportGather getSportGather() {
        return sportGather;
    }

    public void setSportGather(SportGather sportGather) {
        this.sportGather = sportGather;
    }

    public ArrayList<SportRecord> getSportRecords() {
        return sportRecords;
    }

    public void setSportRecords(ArrayList<SportRecord> sportRecords) {
        this.sportRecords = sportRecords;
    }
}
