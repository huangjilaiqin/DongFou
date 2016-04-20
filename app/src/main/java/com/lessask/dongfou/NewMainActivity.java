package com.lessask.dongfou;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lessask.dongfou.model.StatictisSportRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewMainActivity extends AppCompatActivity {

    private XRecyclerView mRecyclerView;
    private SportRecordAdapter mRecyclerViewAdapter;
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
        //View footView = LayoutInflater.from(thi.inflate(R.layout.data_foot,mRecyclerView,false);
        RecyclerView detailList = (RecyclerView)mHeaderView.findViewById(R.id.detail_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        detailList.setLayoutManager(linearLayoutManager);

        StatisticsRecordAdapter statisticsRecordAdapter = new StatisticsRecordAdapter(this);
        detailList.setAdapter(statisticsRecordAdapter);
        statisticsRecordAdapter.appendToList(getDetailDatas());
        mRecyclerView.addHeaderView(mHeaderView);
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
