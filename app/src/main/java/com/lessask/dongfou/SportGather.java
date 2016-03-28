package com.lessask.dongfou;

import java.util.Date;
import java.util.List;

/**
 * Created by JHuang on 2016/3/27.
 */
public class SportGather {
    private Sport sport;
    private List<SportRecord> sportRecords;

    public SportGather(Sport sport, List<SportRecord> sportRecords) {
        this.sport = sport;
        this.sportRecords = sportRecords;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public List<SportRecord> getSportRecords() {
        return sportRecords;
    }

    public void setSportRecords(List<SportRecord> sportRecords) {
        this.sportRecords = sportRecords;
    }
}
