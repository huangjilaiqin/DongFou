package com.lessask.dongfou;

import java.util.Date;
import java.util.List;

/**
 * Created by JHuang on 2016/3/27.
 */
public class SportGather {
    private Sport sport;
    private List<SportRecord> amounts;

    public SportGather(Sport sport, List<SportRecord> amounts) {
        this.sport = sport;
        this.amounts= amounts;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public List<SportRecord> getSportRecords() {
        return amounts;
    }

    public void setSportRecords(List<SportRecord> amounts) {
        this.amounts= amounts;
    }
}
