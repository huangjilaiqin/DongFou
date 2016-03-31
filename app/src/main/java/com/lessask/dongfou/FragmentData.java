package com.lessask.dongfou;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;
import com.lessask.dongfou.util.DbDeleteListener;
import com.lessask.dongfou.util.TimeHelper;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by JHuang on 2016/3/26.
 */
public class FragmentData extends Fragment {

    private String TAG = FragmentData.class.getSimpleName();
    private View rootView;
    private BarChart mChart;
    private TextView sportName;
    private TextView avg;
    private TextView unit;
    private TextView total;
    private TextView lastTime;

    private String[] mWeeks = new String[]{"", "", "", "", "", "", "今天"};
    private Typeface mTf;
    private int sportid;
    private SportGather sportGather;
    private List<Float> sportValues;
    private Sport sport;


    public void setSportGather(SportGather sportGather) {
        this.sportGather = sportGather;
        this.sportValues = sportGather.getSportRecords();
        this.sport = sportGather.getSport();
        this.sportid=sport.getId();
        String last="";
        for(int i=1;i<7;i++){
            if(last.length()!=0)
                mWeeks[6-i]=last+TimeHelper.getWeekNameOfDay(-i);
            else
                mWeeks[6-i]=TimeHelper.getWeekNameOfDay(-i);
            if(mWeeks[6-i].equals("周一"))
                last="上";
        }
    }

    public void update(){
        if(sport==null || sportValues==null)
            loadData();
        //fragment未初始化
        if(sportName==null)
            return;
        sportName.setText(sport.getName());
        avg.setText(sport.getAvg()+"");
        if(sport.getKind()==1)
            unit.setText(sport.getUnit());
        else if(sport.getKind()==2)
            unit.setText(sport.getUnit2());

        total.setText(sport.getTotal()+"");
        if(!sport.getLastTime().equals(new Date(0)))
            lastTime.setText(TimeHelper.date2Chat(sport.getLastTime()));
        else
            lastTime.setText("无运动记录");
        setData(7);

    }

    /*
    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        outState.putInt("sportid", sport.getId());
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        if(savedInstanceState!=null) {
            sportid = savedInstanceState.getInt("sportid");
        }else
            Log.e(TAG, "sportid:" + sportid);
    }
    */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("sportid", sport.getId());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null) {
            sportid = savedInstanceState.getInt("sportid");
            Log.e(TAG, "sportid:"+sportid);
        }else {
            Log.e(TAG, "savedInstanceState is null");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null) {
            sportid = savedInstanceState.getInt("sportid");
            Log.e(TAG, "sportid:"+sportid);
        }else {
            Log.e(TAG, "savedInstanceState is null");
        }
    }

    private void loadData(){
        Log.e(TAG, "loadData");
        sport = DbDataHelper.loadSportFromDb(getContext(), sportid);
        sportValues = DbDataHelper.loadSportRecordById(getContext(), sportid);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            sportid = savedInstanceState.getInt("sportid");
            Log.e(TAG, "sportid:"+sportid);
        }
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_data, container,false);

            sportName = (TextView)rootView.findViewById(R.id.sport_name);
            avg = (TextView)rootView.findViewById(R.id.avg);
            unit = (TextView)rootView.findViewById(R.id.unit);
            total = (TextView)rootView.findViewById(R.id.total);
            lastTime = (TextView)rootView.findViewById(R.id.last_time);

            /*
            sportName.setText(sport.getName());
            avg.setText(sport.getAvg()+"");
            if(sport.getKind()==1)
                unit.setText(sport.getUnit());
            else if(sport.getKind()==2)
                unit.setText(sport.getUnit2());

            total.setText(sport.getTotal()+"");
            if(!sport.getLastTime().equals(new Date(0)))
                lastTime.setText(TimeHelper.date2Chat(sport.getLastTime()));
            else
                lastTime.setText("无运动记录");
                */


            mChart = (BarChart) rootView.findViewById(R.id.chart);

            mChart.setDrawBarShadow(false);
            mChart.setDrawValueAboveBar(true);

            mChart.setDrawHighlightArrow(false);
            mChart.getXAxis().setDrawGridLines(false);

            // if more than 60 entries are displayed in the chart, no values will be
            // drawn
            mChart.setMaxVisibleValueCount(60);

            // scaling can now only be done on x- and y-axis separately
            mChart.setPinchZoom(false);

            mChart.setDrawGridBackground(false);
            mChart.setTouchEnabled(false);
            mChart.setDescription("");

            /*
            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
            leftAxis.setTypeface(mTf);
            leftAxis.setTextSize(8f);
            leftAxis.setTextColor(Color.DKGRAY);
            leftAxis.setValueFormatter(new PercentFormatter());

            XAxis xAxis = mChart.getXAxis();
            xAxis.setTypeface(mTf);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(8f);
            xAxis.setTextColor(Color.DKGRAY);

            */

            mChart.getAxisRight().setEnabled(false);
            mChart.getAxisLeft().setDrawGridLines(false);
            mChart.getAxisLeft().setEnabled(false);
            //mTf = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Regular.ttf");

            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTypeface(mTf);
            xAxis.setDrawGridLines(false);
            xAxis.setSpaceBetweenLabels(2);

            YAxisValueFormatter custom = new YAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, YAxis yAxis) {
                    return ""+value;
                }
            };

            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.setTypeface(mTf);
            leftAxis.setLabelCount(8, false);
            leftAxis.setValueFormatter(custom);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

            YAxis rightAxis = mChart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setTypeface(mTf);
            rightAxis.setLabelCount(8, false);
            rightAxis.setValueFormatter(custom);
            rightAxis.setSpaceTop(15f);
            rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

            //对每种颜色的柱状图说明
            mChart.getLegend().setEnabled(false);
            /*
            Legend l = mChart.getLegend();
            l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
            l.setForm(Legend.LegendForm.SQUARE);
            l.setFormSize(9f);
            l.setTextSize(11f);
            l.setXEntrySpace(4f);
            */

            //setData(7);
        }
        update();
        return rootView;
    }

    private void setData(int count) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add(mWeeks[i % 7]);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < count; i++) {
            float val = sportValues.get(i);
            yVals1.add(new BarEntry(val, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setLabel("跑步");
        set1.setLabel("");
        set1.setColor(getResources().getColor(R.color.colorAccent));
        set1.setBarSpacePercent(35f);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        //data.setValueTypeface(mTf);

        mChart.setData(data);
    }
}
