package com.lessask.dongfou;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.lessask.dongfou.dialog.MenuDialog;
import com.lessask.dongfou.dialog.OnSelectMenu;
import com.lessask.dongfou.dialog.WeightPickerDialog;
import com.lessask.dongfou.net.GsonRequest;
import com.lessask.dongfou.net.VolleyHelper;
import com.lessask.dongfou.util.DbHelper;
import com.lessask.dongfou.util.GlobalInfo;
import com.lessask.dongfou.util.SportDbHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SportsActivity extends AppCompatActivity {


    private String TAG = SportsActivity.class.getSimpleName();
    private FloatingActionButton mSearch;
    private RecyclerViewStatusSupport mRecyclerView;
    private SportsAdapter mRecyclerViewAdapter;

    private RecyclerView mWeightRecyclerView;
    private WeightAdapter mWeightAdapter;

    private VolleyHelper volleyHelper = VolleyHelper.getInstance();
    private Intent intent;
    private SearchView searchView;
    private EditText addsport;
    private Button add;
    private GlobalInfo globalInfo = GlobalInfo.getInstance();
    private String contentStr;
    private List<Sport> originSports;
    private OnQueryTextListener onQueryTextListener;
    private SportDbHelper sportDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        originSports = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("选择运动");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        intent = getIntent();

        addsport = (EditText) findViewById(R.id.addsport);
        add = (Button) findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentStr = addsport.getText().toString().trim();
                if(contentStr.length()>0){
                    commit();
                }

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

        mWeightRecyclerView = (RecyclerView) findViewById(R.id.weight_list);
        //用线性的方式显示listview
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mWeightRecyclerView.setLayoutManager(mLinearLayoutManager);

        mWeightAdapter = new WeightAdapter(this);
        mWeightRecyclerView.setAdapter(mWeightAdapter);

        mWeightAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Sport sport = mWeightAdapter.getItem(position);
                //Toast.makeText(SportsActivity.this, sport.getName(), Toast.LENGTH_SHORT).show();
                showDataDialog(sport);
            }
        });

        loadWeight();



        onQueryTextListener = new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG, "ic_search:"+newText);
                List<Sport> searchResult;
                if(newText.length()==0){
                    searchResult = originSports;
                }else {
                    searchResult = new ArrayList<Sport>();
                    for (int i = 0; i < originSports.size(); i++) {
                        Sport sport = originSports.get(i);
                        if (sport.getName().contains(newText))
                            searchResult.add(sport);
                    }
                }
                Log.e(TAG, "result size:"+searchResult.size());
                mRecyclerViewAdapter.setList(searchResult);
                Log.e(TAG, "real result size:"+mRecyclerViewAdapter.getList().size());
                mRecyclerViewAdapter.notifyDataSetChanged();
                return false;
            }
        };
    }

    private void showDataDialog(final Sport sport){
        float lastV;
        lastV = sport.getLastValue();
        Log.e(TAG, "lastV:"+lastV);
        WeightPickerDialog dialog = new WeightPickerDialog(SportsActivity.this, sport.getName(), sport.getMaxnum(),lastV, sport.getUnit(), new WeightPickerDialog.OnSelectListener() {
            @Override
            public void onSelect(float data,float data2) {
                if(sportDbHelper==null)
                    sportDbHelper=new SportDbHelper(SportsActivity.this);
                float result = data+data2/10f;
                sportDbHelper.addSportRecord(sport.getId(), result, data2);
                Toast.makeText(SportsActivity.this,result+"", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setEditable(false);
        dialog.show();
    }

    private void loadSports(){
        mRecyclerView.showLoadingView();
        SQLiteDatabase db = DbHelper.getInstance(this).getDb();
        Cursor cr = db.rawQuery("select * from t_sport where kind!=3 order by `frequency` desc,`lasttime` desc", null);
        //select name,frequency,lasttime from t_sport order by `frequency` desc,`lasttime` desc;
        while (cr.moveToNext()){
            Sport sport = new Sport(cr.getInt(0),cr.getString(1),cr.getString(2),cr.getInt(3),cr.getString(4),cr.getInt(5),cr.getString(6),cr.getInt(7),cr.getInt(8)
            ,cr.getFloat(9),cr.getFloat(10),cr.getInt(11),new Date(cr.getInt(12)),cr.getInt(13),cr.getInt(14),cr.getInt(15));
            mRecyclerViewAdapter.append(sport);
            originSports.add(sport);
        }
        int count = cr.getColumnCount();
        Log.e(TAG, "query db, chatgroup size:" + count);

        if(count==0){
            mRecyclerView.showEmptyView();
        }else {
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void loadWeight(){
        SQLiteDatabase db = DbHelper.getInstance(this).getDb();
        Cursor cr = db.rawQuery("select * from t_sport where kind=3 order by `frequency` desc,`lasttime` desc,id", null);
        //select name,frequency,lasttime from t_sport order by `frequency` desc,`lasttime` desc;
        while (cr.moveToNext()){
            Sport sport = new Sport(cr.getInt(0),cr.getString(1),cr.getString(2),cr.getInt(3),cr.getString(4),cr.getInt(5),cr.getString(6),cr.getInt(7),cr.getInt(8)
            ,cr.getFloat(9),cr.getFloat(10),cr.getInt(11),new Date(cr.getInt(12)),cr.getInt(13),cr.getInt(14),cr.getInt(15));
            mWeightAdapter.append(sport);
        }
        int count = cr.getColumnCount();
        Log.e(TAG, "query db, chatgroup size:" + count);

        mWeightAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sports, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(onQueryTextListener);

        return true;
    }

    private void commit(){
        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, Config.feedbackUrl, ResponseError.class, new GsonRequest.PostGsonRequest<ResponseError>() {
            @Override
            public void onStart() {
            }
            @Override
            public void onResponse(ResponseError response) {
                Log.e(TAG, "response:" + response.toString());
                if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                    Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                    Toast.makeText(SportsActivity.this, "错误:"+response.getError(),Toast.LENGTH_SHORT).show();
                }else {
                    addsport.setText("");
                    Toast.makeText(SportsActivity.this, "我们将以最快的速度添加到运动库", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, error.toString());
                Toast.makeText(SportsActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", ""+globalInfo.getUserid());
                datas.put("content", "运动类型: "+contentStr);
                return datas;
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }
}
