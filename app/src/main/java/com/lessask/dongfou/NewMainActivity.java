package com.lessask.dongfou;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.lessask.dongfou.model.StatictisSportRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewMainActivity extends AppCompatActivity {

    private XRecyclerView mRecyclerView;
    private SportRecordAdapter mRecyclerViewAdapter;
    private BarChart mChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        mRecyclerView = (XRecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewAdapter = new SportRecordAdapter(this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter.appendToList(getRecordDatas());

        View mHeaderView = LayoutInflater.from(this).inflate(R.layout.new_data_header,mRecyclerView,false);
        mChart = (BarChart)mHeaderView.findViewById(R.id.chart);
        initChart();
        setData(365);
        //View footView = LayoutInflater.from(thi.inflate(R.layout.data_foot,mRecyclerView,false);
        /*
        RecyclerView detailList = (RecyclerView)mHeaderView.findViewById(R.id.detail_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        detailList.setLayoutManager(linearLayoutManager);

        StatisticsRecordAdapter statisticsRecordAdapter = new StatisticsRecordAdapter(this);
        detailList.setAdapter(statisticsRecordAdapter);
        statisticsRecordAdapter.appendToList(getDetailDatas());
        */
        mRecyclerView.addHeaderView(mHeaderView);
    }

    private void initChart(){

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
        mChart.setScaleEnabled(false);
        //mChart.setTouchEnabled(false);
        mChart.setDescription("");

        mChart.getAxisRight().setEnabled(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getAxisLeft().setEnabled(false);
        //mTf = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Regular.ttf");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);



        YAxisValueFormatter custom = new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return ""+value;
            }
        };

        YAxis leftAxis = mChart.getAxisLeft();
        //leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        LimitLine ll = new LimitLine(200f, "");
        ll.setLineColor(getResources().getColor(R.color.white_bg_border));
        ll.setLineWidth(1f);
        ll.enableDashedLine(5,5,0);
        ll.setTextStyle(Paint.Style.STROKE);
        ll.setTextColor(Color.GRAY);
        ll.setTextSize(12f);
        // .. and more styling options
        leftAxis.addLimitLine(ll);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        //rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        //对每种颜色的柱状图说明
        mChart.getLegend().setEnabled(false);
    }

    private void setData(int count) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add(i+"");
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < count; i++) {
            yVals1.add(new BarEntry(10+i, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setDrawValues(false);
        set1.setLabel("跑步");
        set1.setLabel("");
        set1.setColor(getResources().getColor(R.color.colorAccent1));
        set1.setBarSpacePercent(35f);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        //data.setValueTypeface(mTf);

        mChart.setData(data);
        mChart.setDrawValueAboveBar(false);
        mChart.setVisibleXRangeMaximum(30);
        mChart.moveViewToX(335);
        mChart.animateY(1000);
    }

    private List getRecordDatas(){
        List<SportRecord> records = new ArrayList<>();
        for(int i=0;i<10;i++) {
            float arg1 = i;
            float arg2 = 0;
            records.add(new SportRecord(i, 2, arg1,arg1,arg2,i,new Date(),1));
        }
        return records;
    }

    private List getDetailDatas(){
        List<StatictisSportRecord> records = new ArrayList<>();
        for(int i=0;i<10;i++) {
            float arg1 = i;
            float arg2 = 0;
            records.add(new StatictisSportRecord());
        }
        return records;
    }

    public class StatisticsRecordAdapter extends BaseRecyclerAdapter<StatictisSportRecord,StatisticsRecordAdapter.ViewHolder>{
        private String TAG = StatisticsRecordAdapter.class.getSimpleName();
        private Context context;
        private OnItemLongClickListener onItemLongClickListener;
        public StatisticsRecordAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistics_sport_record, parent, false);
            return new ViewHolder(view);
        }

        public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
            this.onItemLongClickListener = onItemLongClickListener;
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            Log.e(TAG, "onBindViewHolder");
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            View itemView;


            public ViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
            }
        }
    }
}
