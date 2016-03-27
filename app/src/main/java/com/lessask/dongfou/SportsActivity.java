package com.lessask.dongfou;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.lessask.dongfou.net.VolleyHelper;
import com.lessask.dongfou.util.DbHelper;

import java.util.Date;


public class SportsActivity extends AppCompatActivity {


    private String TAG = SportsActivity.class.getSimpleName();
    private FloatingActionButton mSearch;
    private RecyclerViewStatusSupport mRecyclerView;
    private SportsAdapter mRecyclerViewAdapter;

    private VolleyHelper volleyHelper = VolleyHelper.getInstance();
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("通讯录");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        intent = getIntent();

        mSearch = (FloatingActionButton)findViewById(R.id.search);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SportsActivity.this, "search", Toast.LENGTH_SHORT).show();
            }
        });

        mRecyclerView = (RecyclerViewStatusSupport) findViewById(R.id.show_list);
        mRecyclerView.setStatusViews(findViewById(R.id.loading_view), findViewById(R.id.empty_view), findViewById(R.id.error_view));
        //用线性的方式显示listview
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mRecyclerViewAdapter = new SportsAdapter(this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Sport sport = mRecyclerViewAdapter.getItem(position);
                intent.putExtra("sportid", sport.getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mRecyclerView.showLoadingView();
        loadSports();
    }

    private void loadSports(){
        mRecyclerView.showLoadingView();
        SQLiteDatabase db = DbHelper.getInstance(this).getDb();
        Cursor cr = db.rawQuery("select * from t_sport", null);
        while (cr.moveToNext()){
            Sport sport = new Sport(cr.getInt(0),cr.getString(1),cr.getString(2),cr.getInt(3),cr.getString(4),cr.getInt(5),cr.getString(6),cr.getInt(7),cr.getInt(8)
            ,cr.getFloat(9),cr.getFloat(10),cr.getInt(11),new Date(cr.getInt(12)),cr.getInt(13),cr.getInt(14),cr.getInt(15));
            mRecyclerViewAdapter.append(sport);
        }
        int count = cr.getColumnCount();
        Log.e(TAG, "query db, chatgroup size:" + count);

        if(count==0){
            mRecyclerView.showEmptyView();
        }else {
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
