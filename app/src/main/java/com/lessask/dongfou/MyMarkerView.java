package com.lessask.dongfou;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * Created by JHuang on 2016/4/23.
 */
public class MyMarkerView extends MarkerView{
    TextView value;
    public MyMarkerView(Context context,int layoutResource){
        super(context,layoutResource);
        value = (TextView)findViewById(R.id.value);
    }
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            value.setText("" + ce.getHigh());
        } else {

            value.setText("" +e.getVal());
        }
    }

    @Override
    public int getXOffset(float xpos) {
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        return -getHeight();
    }
}
