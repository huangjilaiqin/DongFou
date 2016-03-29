package com.lessask.dongfou;

import java.util.Date;
import java.util.List;

/**
 * Created by JHuang on 2016/3/27.
 */
public class SportGather {
    private Sport sport;
    private List<Float> amounts;

    public SportGather(Sport sport, List<Float> amounts) {
        this.sport = sport;
        this.amounts= amounts;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public List<Float> getSportRecords() {
        return amounts;
    }

    public void setSportRecords(List<Float> amounts) {
        this.amounts= amounts;
    }
}
