package com.lessask.dongfou;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.capricorn.ArcMenu;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.reflect.TypeToken;
import com.lessask.dongfou.dialog.LoadingDialog;
import com.lessask.dongfou.dialog.MenuDialog;
import com.lessask.dongfou.dialog.OnSelectMenu;
import com.lessask.dongfou.dialog.StringPickerDialog;
import com.lessask.dongfou.dialog.StringPickerTwoDialog;
import com.lessask.dongfou.dialog.WeightPickerDialog;
import com.lessask.dongfou.model.Version;
import com.lessask.dongfou.net.GsonRequest;
import com.lessask.dongfou.net.VolleyHelper;
import com.lessask.dongfou.util.DbDeleteListener;
import com.lessask.dongfou.util.DbHelper;
import com.lessask.dongfou.util.DbInsertListener;
import com.lessask.dongfou.util.GlobalInfo;
import com.lessask.dongfou.util.SportDbHelper;
import com.lessask.dongfou.util.TimeHelper;
import com.viewpagerindicator.CirclePageIndicator;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private XRecyclerView mRecyclerView;
    private SportRecordAdapter mRecyclerViewAdapter;

    private FragmentDataPagerAdapter mFragmentDataPagerAdapter;
    private ViewPager mViewPager;
    private Menu menu;
    private ArcMenu arcMenu;

    private GlobalInfo globalInfo = GlobalInfo.getInstance();
    private VolleyHelper volleyHelper = VolleyHelper.getInstance();

    private final String TAG = MainActivity.class.getSimpleName();
    private final int GET_SPORT = 1;
    private final int ADD_SPORT = 2;
    private final int DELETE_SPORT = 3;
    private final int DELETE_SPORT_TONGJI = 4;
    private final int LOGIN_REGISTER = 5;
    private final int UPLOAD_RECORD_ERROR = 6;
    private final int UPLOAD_RECORD_DONE = 7;
    private final int CHECK_VERSION = 8;
    private final int LOGOUT = 9;
    private final int DOWNLOAD_RECORD_ERROR = 10;
    private final int DOWNLOAD_RECORD_DONE = 11;
    private final int VOLLEY_ERROR = 12;


    private Map<Integer,Sport> sportMap;
    private ArrayList<SportGather> sportGathers;
    private ArrayList<Fragment> fragmentDatas;
    private ArrayList<ImageView> menuImages;
    private Bundle fragmentBundle = new Bundle();
    private DbHelper dbHelper;
    private SQLiteDatabase dbInstance;


    SharedPreferences baseInfo;
    SharedPreferences.Editor editor;

    //头部数据
    private BarChart mChart;
    private AppCompatSpinner sportName;
    private TextView unit;
    private TextView total;

    //体重围度头部数据
    private LineChart mWeightChart;
    private AppCompatSpinner sportName1;
    private TextView weightKind;
    private TextView weight;
    private TextView weightUnit;

    private ArrayList<String> sportNameData;
    //private MySpinnerAdapter sportNameAdapter;
    private ArrayAdapter sportNameAdapter;
    private int currentPosition=0;
    private View mHeaderView;
    private RelativeLayout dataHeader;
    private RelativeLayout weightDataHeader;
    //private View mWeightHeaderView;
    private ProgressBar targeProgressBar1;

    private SportDbHelper sportDbHelper;

    private ArrayList<Sport> weights;

    private RecyclerView mWeightRecyclerView;
    private WeightAdapter mWeightAdapter;

    private Handler handler = new Handler(){
        SportRecord sportRecord;
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what){
                case ADD_SPORT:
                    Log.e(TAG, "ADD_SPORT");
                    sportRecord = (SportRecord)msg.obj;
                    //updateFragments(sportRecord);
                    //mViewPager.setCurrentItem(0);
                    updateHeader(sportRecord);
                    mRecyclerView.scrollToPosition(0);
                    break;
                case DELETE_SPORT:
                    int deletePostion = msg.arg1;
                    sportRecord = (SportRecord)msg.obj;
                    mRecyclerViewAdapter.remove(deletePostion);
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    //mRecyclerViewAdapter.notifyItemRemoved(deletePostion);
                    //mRecyclerViewAdapter.notifyItemRangeChanged(deletePostion, mRecyclerViewAdapter.getItemCount());
                    break;
                case DELETE_SPORT_TONGJI:
                    sportRecord = (SportRecord)msg.obj;
                    updateFragmentsIfFirst(sportRecord);
                    break;
                case UPLOAD_RECORD_ERROR:
                    int mode = (int)msg.arg1;
                    setSyncMenuVisible(true);
                    if(mode==1){
                        if(msg.arg2>=301 && msg.arg2<=303){
                            Toast.makeText(MainActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                            startActivityForResult(intent, LOGIN_REGISTER);
                        }
                    }
                    break;
                case UPLOAD_RECORD_DONE:
                    setSyncMenuVisible(hasNotSyncRecord());
                    mode = msg.arg1;
                    if(mode==2){
                        reloadData();
                    }
                    break;
                case DOWNLOAD_RECORD_DONE:
                    setSyncMenuVisible(hasNotSyncRecord());
                    uploadRecord(2);
                    break;
                case DOWNLOAD_RECORD_ERROR:
                    Toast.makeText(MainActivity.this, "登录过期,请重新登录",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                    startActivityForResult(intent, LOGIN_REGISTER);
                    break;
                case CHECK_VERSION:
                    if(msg.arg1==1){
                        setNotificationStatus(true);
                    }else if(msg.arg1==2){
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("已经停止对该版本支持,请升级到最新版本");
                        builder.setTitle("公告");
                        final String url = (String)msg.obj;
                        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 dialog.dismiss();
                                 Log.e(TAG, "url:"+url);
                                 Uri uri = Uri.parse(url);
                                 Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                 startActivity(intent);
                             }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 dialog.dismiss();
                             }
                        });
                        builder.create().show();

                    }
                    break;
                case VOLLEY_ERROR:
                    VolleyError error = (VolleyError)msg.obj;
                    if(error instanceof TimeoutError){
                        Toast.makeText(MainActivity.this, "连接超时,请检查网络", Toast.LENGTH_SHORT).show();
                    }else if(error instanceof com.android.volley.NoConnectionError){
                        Toast.makeText(MainActivity.this, "网络出问题了,请检查网络", Toast.LENGTH_SHORT).show();
                    }else if(error instanceof com.android.volley.NetworkError){
                        Toast.makeText(MainActivity.this, "网络出问题了,请检查网络", Toast.LENGTH_SHORT).show();
                    }else if(error instanceof com.android.volley.ServerError){
                        Toast.makeText(MainActivity.this, "服务器异常,我们在紧急抢修中", Toast.LENGTH_SHORT).show();
                    }else if(error instanceof com.android.volley.ParseError){
                        Toast.makeText(MainActivity.this, "数据错误,请反馈或联系官方qq群", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "请升级到最新版再试,或联系我们", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    private void setSyncMenuVisible(boolean visible){
        menu.findItem(R.id.sync).setVisible(visible);
    }
    private void setNotificationStatus(boolean active){
        if(active){
            menu.findItem(R.id.notifications).setIcon(R.drawable.ic_notifications_active);
        }else {
            menu.findItem(R.id.notifications).setIcon(R.drawable.ic_notifications);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "oncreate savedInstanceState:" + savedInstanceState);
        setContentView(R.layout.activity_main);
        baseInfo = getSharedPreferences("BaseInfo", MODE_PRIVATE);
        editor = baseInfo.edit();

        sportDbHelper = new SportDbHelper(this);

        dbHelper = DbHelper.getInstance(this);
        dbInstance = dbHelper.getDb();
        sportMap = new HashMap<>();
        sportGathers = new ArrayList<>();
        menuImages = new ArrayList<>();
        sportNameData = new ArrayList<>();

        //程序后台太久，单例的内容也会被删除, 检查userid
        if(globalInfo.getUserid()==0){
            int useid = baseInfo.getInt("userid", 0);
            globalInfo.setUserid(useid);
            String token = baseInfo.getString("token", "");
            globalInfo.setToken(token);
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("动否");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);

        mRecyclerView = (XRecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mHeaderView = LayoutInflater.from(this).inflate(R.layout.data_header,mRecyclerView,false);
        mRecyclerView.addHeaderView(mHeaderView);
        dataHeader = (RelativeLayout)mHeaderView.findViewById(R.id.data_header);
        weightDataHeader = (RelativeLayout)mHeaderView.findViewById(R.id.weight_header);
        mChart=(BarChart)mHeaderView.findViewById(R.id.chart);
        sportName=(AppCompatSpinner)mHeaderView.findViewById(R.id.sport_name);
        unit=(TextView)mHeaderView.findViewById(R.id.unit);
        total=(TextView)mHeaderView.findViewById(R.id.total);
        targeProgressBar1=(ProgressBar)mHeaderView.findViewById(R.id.target);
        //targeProgressBar1.setProgress(30);
        //View footView = LayoutInflater.from(thi.inflate(R.layout.data_foot,mRecyclerView,false);

        initChart();
        mChart.setOnChartValueSelectedListener(this);

        mWeightChart=(LineChart)mHeaderView.findViewById(R.id.weight_chart);
        sportName1=(AppCompatSpinner)mHeaderView.findViewById(R.id.sport_name1);
        weight=(TextView)mHeaderView.findViewById(R.id.weight);
        weightKind = (TextView)mHeaderView.findViewById(R.id.weight_kind);
        weightUnit=(TextView)mHeaderView.findViewById(R.id.weight_unit);
        mWeightRecyclerView = (RecyclerView) mHeaderView.findViewById(R.id.weight_list);
        //用线性的方式显示listview
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mWeightRecyclerView.setLayoutManager(mLinearLayoutManager);

        mWeightAdapter = new WeightAdapter(this);
        mWeightRecyclerView.setAdapter(mWeightAdapter);

        //尾部
        View footView = LayoutInflater.from(this).inflate(R.layout.data_foot, mRecyclerView, false);
        mRecyclerView.addFooterView(footView);

        mWeightAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Sport sport = mWeightAdapter.getItem(position);
                //Toast.makeText(SportsActivity.this, sport.getName(), Toast.LENGTH_SHORT).show();
                mWeightAdapter.updateStatus(sport.getId());
                mWeightAdapter.notifyDataSetChanged();
                setWeightChartData(sport);
            }

        });
        mWeightAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                Sport sport = mWeightAdapter.getItem(position);
                showWeightDialog(sport);
            }
        });


        initWeightChart();
        weightDataHeader.setVisibility(View.GONE);


        DbHelper.getInstance(this).appendInsertListener("t_sport_record", sportRecordInsertListener);
        DbHelper.getInstance(this).appendDeleteListener("t_sport_record", sportRecordDeleteListener);

        Log.e(TAG, "versionName:" + getVersionName());
        Log.e(TAG, "versionCode:" + getVersionCode());
        checkVersionUpdate();

        //加载运动列表为空, 可能是globalInfo中的userid获取不到了
        loadSportDatas();
        setChartData(sportGathers.get(0));


        /*
        mFragmentDataPagerAdapter = new FragmentDataPagerAdapter(getSupportFragmentManager());

        fragmentDatas = new ArrayList<>();
        for(int i=0;i<5;i++) {
            FragmentData fragmentData = new FragmentData();
            Bundle bundle = new Bundle();
            bundle.putInt("sportid", sportGathers.get(i).getSport().getId());
            fragmentData.setArguments(bundle);
            fragmentDatas.add(fragmentData);
        }
        mFragmentDataPagerAdapter.setSportGathers(sportGathers);
        mFragmentDataPagerAdapter.setFragments(fragmentDatas);
        //mViewPager = (ViewPager)mHeaderView.findViewById(R.id.viewpager);
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setAdapter(mFragmentDataPagerAdapter);

        //mRecyclerView.addHeaderView(mHeaderView);
        //mRecyclerView.addFooterView(footView);
        */
        mRecyclerViewAdapter = new SportRecordAdapter(this);
        mRecyclerViewAdapter.setSportMap(sportMap);

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter.setOnItemLongClickListener(onRecordLondClickListener);
        List<SportRecord> sportRecords = sportDbHelper.loadTodaySportRecord(globalInfo.getUserid());
        mRecyclerViewAdapter.clear();
        mRecyclerViewAdapter.appendToList(sportRecords);
        mRecyclerViewAdapter.notifyDataSetChanged();

        /*
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        circlePageIndicator.setFillColor(getResources().getColor(R.color.main_color));
        circlePageIndicator.setStrokeColor(getResources().getColor(R.color.gray));
        circlePageIndicator.setViewPager(mViewPager);
        */

        arcMenu = (ArcMenu) findViewById(R.id.arc_menu);
        initMenu();

        for(int i=0;i<sportGathers.size();i++){
            Sport sport = sportGathers.get(i).getSport();
            sportNameData.add(sport.getName());
        }
        sportNameData.add("体重/围度");
        sportNameAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,sportNameData);
        sportNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportName.setAdapter(sportNameAdapter);
        sportName.setOnItemSelectedListener(spinnerListener);
        sportName1.setAdapter(sportNameAdapter);
        sportName1.setOnItemSelectedListener(spinnerListener);
        //sportName.setSelection(0);
    }

    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //更换头部
            if(currentPosition+1<sportNameData.size() && position+1==sportNameData.size()){
                //运动数据->体重
                if(weights==null || weights.size()==0) {
                    weights = sportDbHelper.loadWeightFromDb(globalInfo.getUserid());
                    if(weights.size()==0){
                        Toast.makeText(MainActivity.this, "请联网,重新打开App同步体重参数", Toast.LENGTH_SHORT).show();
                        sportName.setSelection(currentPosition);
                        sportName1.setSelection(currentPosition);
                        return;
                    }
                    //设置体重和其它围度横向列表
                    weights.get(0).setStatus(1);
                    mWeightAdapter.appendToList(weights);
                    mWeightAdapter.notifyDataSetChanged();
                }
                dataHeader.setVisibility(View.GONE);
                weightDataHeader.setVisibility(View.VISIBLE);


                //更新横向列表
                mWeightAdapter.updateStatusByPosition(0);
                mWeightAdapter.notifyDataSetChanged();

                //更新下面的列表
                List<SportRecord> sportRecords = sportDbHelper.loadWeightRecord(globalInfo.getUserid());
                mRecyclerViewAdapter.clear();
                mRecyclerViewAdapter.appendToList(sportRecords);
                mRecyclerViewAdapter.notifyDataSetChanged();
            }else if(currentPosition+1==sportNameData.size() && position+1<sportNameData.size()){
                //体重->运动数据
                weightDataHeader.setVisibility(View.GONE);
                dataHeader.setVisibility(View.VISIBLE);


                List<SportRecord> sportRecords = sportDbHelper.loadTodaySportRecord(globalInfo.getUserid());
                mRecyclerViewAdapter.clear();
                mRecyclerViewAdapter.appendToList(sportRecords);
                mRecyclerViewAdapter.notifyDataSetChanged();
            }

            //填充数据
            if(currentPosition+1==sportNameData.size()){
                setWeightChartData(weights.get(0));
                weightDataHeader.invalidate();
            }else {
                setChartData(sportGathers.get(position));
                dataHeader.invalidate();
            }

            currentPosition=position;
            sportName.setSelection(position);
            sportName1.setSelection(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void reloadData(){
        //加载运动列表为空, 可能是globalInfo中的userid获取不到了
        sportGathers.clear();
        loadSportDatas();

        for(int i=0;i<fragmentDatas.size();i++) {
            FragmentData fragmentData = (FragmentData)fragmentDatas.get(i);

            fragmentData.setSportid(sportGathers.get(i).getSport().getId());
            fragmentData.setSportGather(sportGathers.get(i));
            Log.e(TAG, "reload update");
            fragmentData.update();
        }
        mFragmentDataPagerAdapter.setSportGathers(sportGathers);
        mRecyclerViewAdapter.setSportMap(sportMap);

        //更新当天运动记录列表
        sportDbHelper.loadTodaySportRecord(globalInfo.getUserid());
        List<SportRecord> sportRecords = sportDbHelper.loadTodaySportRecord(globalInfo.getUserid());
        mRecyclerViewAdapter.clear();
        mRecyclerViewAdapter.appendToList(sportRecords);
        mRecyclerViewAdapter.notifyDataSetChanged();

        updateMenu();
    }

    private OnItemLongClickListener onRecordLondClickListener = new OnItemLongClickListener() {
        @Override
        public void onItemLongClick(View view, final int position) {
            final SportRecord sportRecord = mRecyclerViewAdapter.getItem(position);
            final Sport sport = loadSport(sportRecord.getSportid());
            final MenuDialog menuDialog = new MenuDialog(MainActivity.this, new String[]{"删除"},
                    new OnSelectMenu() {
                        @Override
                        public void onSelectMenu(int menupos) {
                            Intent intent;
                            switch (menupos) {
                                case 0:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    StringBuilder detail = new StringBuilder();
                                    int kind = sport.getKind();
                                    if(kind==1){
                                        String unit = sport.getUnit();
                                        if(unit.equals("次"))
                                            detail.append((int)sportRecord.getArg1());
                                        else
                                            detail.append(sportRecord.getArg1());
                                        detail.append(unit);
                                    }else if(kind==2){
                                        String unit = sport.getUnit();
                                        if(unit.equals("次"))
                                            detail.append((int)sportRecord.getArg1());
                                        else
                                            detail.append(sportRecord.getArg1());
                                        detail.append(unit);
                                        detail.append(" ");
                                        detail.append(sportRecord.getArg2());
                                        detail.append(sport.getUnit2());
                                        detail.append("/");
                                        detail.append(sport.getUnit());
                                    }else if(kind==3){
                                        String unit = sport.getUnit();
                                        String arg1 = new DecimalFormat("0.0").format(sportRecord.getArg1());
                                        detail.append(arg1);
                                        detail.append(unit);
                                    }
                                    builder.setMessage("确认删除？" + sport.getName()+ ":"+detail.toString()+" 的记录");
                                    builder.setTitle("提示");
                                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            //网络协议
                                            if (globalInfo.getUserid() == 0){
                                                deleteLocalSport(position);
                                            }else {
                                                deleteSportRecord(position);
                                            }
                                        }
                                    });
                                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.create().show();
                                    break;
                            }
                        }
                    });
            menuDialog.show();
        }
    };

    private void deleteLocalSport(int deletePostion){
        SportRecord sportRecord = mRecyclerViewAdapter.getItem(deletePostion);
        //删除数据库记录
        deleteSportRecordDb(sportRecord);
        //更新界面
        Message msg = new Message();
        msg.what = DELETE_SPORT;
        msg.arg1 = deletePostion;
        msg.obj = sportRecord;
        handler.sendMessage(msg);
    }

    private void deleteSportRecord(final int deletePostion){
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        GsonRequest deleteActionRequest = new GsonRequest<>(Request.Method.POST, Config.deleteRecordUrl, SportRecord.class, new GsonRequest.PostGsonRequest<SportRecord>() {
            @Override
            public void onStart() {
                loadingDialog.show();
            }

            @Override
            public void onResponse(SportRecord response) {
                loadingDialog.dismiss();
                if(response.getError()!=null){
                    Toast.makeText(MainActivity.this, "删除记录需联网", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainActivity.this, "error:"+response.getError(), Toast.LENGTH_SHORT).show();
                }else {
                    deleteLocalSport(deletePostion);
                    /*
                    SportRecord sportRecord = mRecyclerViewAdapter.getItem(deletePostion);
                    //删除数据库记录
                    deleteSportRecordDb(sportRecord);

                    //更新界面
                    Message msg = new Message();
                    msg.what = DELETE_SPORT;
                    msg.arg1 = deletePostion;
                    msg.obj = sportRecord;
                    handler.sendMessage(msg);
                    */
                }
            }

            @Override
            public void onError(VolleyError error) {
                loadingDialog.dismiss();
                Message msg = new Message();
                msg.what=VOLLEY_ERROR;
                msg.obj = error;
                handler.sendMessage(msg);
            }

            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                SportRecord record = mRecyclerViewAdapter.getItem(deletePostion);
                record = sportDbHelper.loadSportRecordById(record.getId());
                datas.put("token", globalInfo.getToken()+"");
                datas.put("userid", globalInfo.getUserid()+"");
                datas.put("id", record.getId() + "");
                datas.put("sportid", record.getSportid() + "");
                datas.put("seq", record.getSeq()+"");
                return datas;
            }
        });

        volleyHelper.addToRequestQueue(deleteActionRequest);
    }

    @Override
    protected void onDestroy() {
        DbHelper.getInstance(this).removeInsertListener("t_sport_record", sportRecordInsertListener);
        DbHelper.getInstance(this).removeDeleteListener("t_sport_record", sportRecordDeleteListener);
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }

    private void initMenu(){
        for (int i = 0; i < 4; i++) {
            ImageView item = new ImageView(this);
            menuImages.add(item);
            //item.setImageResource(R.drawable.button_action);
            final Sport sport = sportGathers.get(i).getSport();
            String headImgUrl = Config.imagePrefix+sport.getImage();
            ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(item, R.drawable.dongfou, R.drawable.dongfou);
            VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener, 100, 100);

            final int position = i;
            arcMenu.addItem(item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "add sport:"+sportGathers.get(position).getSport().getName());
                    showDataDialog(sportGathers.get(position).getSport());
                }
            });
        }
        ImageView item = new ImageView(this);
        item.setImageResource(R.drawable.more);

        arcMenu.addItem(item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SportsActivity.class);
                startActivityForResult(intent, GET_SPORT);
            }
        });
    }

    private void showDataDialog(final Sport sport){
        float lastV,lastV2;

        switch (sport.getKind()) {
            case 1:
                lastV = sport.getLastValue();
                if(lastV==0)
                    lastV = 1;
                StringPickerDialog stringPickerDialog = new StringPickerDialog(MainActivity.this, sport.getName(), sport.getMaxnum(),lastV, sport.getUnit(), new StringPickerDialog.OnSelectListener() {
                    @Override
                    public void onSelect(int data) {
                        addSportRecord(sport.getId(), data, 0);
                    }
                });
                //stringPickerDialog.setEditable(false);
                stringPickerDialog.show();
                break;
            case 2:
//public StringPickerTwoDialog(Context context,String title,int maxNumber,int initValue,String unit,int maxNumber2,int initValue2,String uint2, OnSelectListener mSelectCallBack) {
                lastV = sport.getLastValue();
                lastV2 = sport.getLastValue2();
                if(lastV==0)
                    lastV = 1;
                if(lastV2==0)
                    lastV2 = 1;
                StringPickerTwoDialog stringPickerTwoDialog = new StringPickerTwoDialog(MainActivity.this, sport.getName(), sport.getMaxnum(),lastV, sport.getUnit(), sport.getMaxnum2(),lastV2, sport.getUnit2(), new StringPickerTwoDialog.OnSelectListener() {
                    @Override
                    public void onSelect(int data, int data2) {
                        addSportRecord(sport.getId(), data, data2);
                    }
                });
                //stringPickerTwoDialog.setEditable(false);
                stringPickerTwoDialog.show();
                break;
        }
    }

    private void addSportRecord(int sportid,int data1,int data2){
        //create table t_sport_record(id int primary key,amount real not null,int arg1 not null default 0,int arg2 not null default 0,time int NOT NULL,seq int not null default 0)");
        ContentValues values = new ContentValues();
        Sport sport = loadSport(sportid);
        //Sport sport = sportMap.get(sportid);

        values.put("sportid", sportid);
        int amount = data1;
        if(sport.getKind()==2){
            amount*=data2;
        }
        values.put("amount", amount);
        Log.e(TAG, "amount:" + amount);
        values.put("arg1", data1);
        values.put("arg2", data2);
        values.put("time", new Date().getTime() / 1000);
        values.put("seq", "0");
        values.put("userid", "" + globalInfo.getUserid());
        //long id = dbInstance.insert("t_sport_record", null, values);
        DbHelper.getInstance(this).insert("t_sport_record", null, values);

        if(globalInfo.getUserid()==0 && !baseInfo.getBoolean("logintip", false)){
            editor.putBoolean("logintip", true);
            editor.commit();
            tipLoginDialog();
        }
    }

    private void tipLoginDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("登录后,您的数据将被永久免费保存");
        builder.setTitle("提示");
        builder.setPositiveButton("立即登录", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                  Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                  startActivityForResult(intent, LOGIN_REGISTER);
             }
        });
        builder.setNegativeButton("稍后", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
             }
        });
        builder.create().show();
    }

    private DbInsertListener sportRecordInsertListener = new DbInsertListener() {
        @Override
        public void callback(Object obj) {
            SportRecord sportRecord = (SportRecord) obj;
            //查询上一次的更新时间
            Sport sport = loadSport(sportRecord.getSportid());
            if(sport.getKind()!=3) {
                boolean isSameDay = true;
                boolean isSameMonth = true;
                if (sport.getLastTime().equals(new Date(0))) {
                    isSameDay = false;
                    isSameMonth = false;
                }

                if (!TimeHelper.isSameDay(sport.getLastTime(), sportRecord.getTime())) {
                    sport.setDays(sport.getDays() + 1);
                    isSameDay = false;
                }
                if (!TimeHelper.isSameMonth(sport.getLastTime(), sportRecord.getTime()))
                    isSameMonth = false;

                sport.setLastTime(sportRecord.getTime());

                sport.setFrequency(sport.getFrequency() + 1);
                sport.setTotal(sport.getTotal() + sportRecord.getAmount());
                sport.setSeq(sportRecord.getSeq());
                sport.setLastValue(sportRecord.getArg1());
                sport.setLastValue2(sportRecord.getArg2());
                //日均
                sport.setAvg(sport.getTotal() / sport.getDays());
                Log.e(TAG, "insert record callback,updat sport");
                ContentValues values = new ContentValues();
                values.put("total", sport.getTotal());
                values.put("frequency", sport.getFrequency());
                values.put("avg", sport.getAvg());
                values.put("days", sport.getDays());
                values.put("lasttime", sport.getLastTime().getTime() / 1000);
                values.put("seq", sport.getSeq());
                values.put("lastvalue", sport.getLastValue());
                values.put("lastvalue2", sport.getLastValue2());
                DbHelper.getInstance(MainActivity.this).getDb().update("t_sport", values, "id=? and userid=?", new String[]{sport.getId() + "", "" + globalInfo.getUserid()});

                if (isSameDay) {
                    updateSportDay(sportRecord);
                } else {
                    insertSportDay(sportRecord);
                }
                if (isSameMonth) {
                    updateSportMonth(sportRecord);
                } else {
                    insertSportMonth(sportRecord);
                }

                mRecyclerViewAdapter.appendToTop(sportRecord);
                mRecyclerViewAdapter.notifyDataSetChanged();

                Message message = new Message();
                message.what = ADD_SPORT;
                message.obj = sportRecord;
                handler.sendMessage(message);
            }else if(sport.getKind()==3){
                boolean isSameDay = true;
                if (sport.getLastTime().equals(new Date(0))) {
                    isSameDay = false;
                }

                if (!TimeHelper.isSameDay(sport.getLastTime(), sportRecord.getTime())) {
                    sport.setDays(sport.getDays() + 1);
                    isSameDay = false;
                }

                sport.setLastTime(sportRecord.getTime());

                sport.setFrequency(sport.getFrequency() + 1);
                sport.setTotal(sport.getTotal() + sportRecord.getAmount());
                sport.setSeq(sportRecord.getSeq());
                sport.setLastValue(sportRecord.getArg1());
                //日均
                sport.setAvg(sport.getTotal() / sport.getDays());
                ContentValues values = new ContentValues();
                values.put("total", sport.getTotal());
                values.put("frequency", sport.getFrequency());
                values.put("avg", sport.getAvg());
                values.put("days", sport.getDays());
                values.put("lasttime", sport.getLastTime().getTime() / 1000);
                values.put("seq", sport.getSeq());
                Log.e(TAG, "update lastValue" + sport.getLastValue());
                values.put("lastvalue", sport.getLastValue());
                DbHelper.getInstance(MainActivity.this).getDb().update("t_sport", values, "id=? and userid=?", new String[]{sport.getId() + "", "" + globalInfo.getUserid()});

                if (isSameDay) {
                    updateSportDay(sportRecord);
                } else {
                    insertSportDay(sportRecord);
                }

                mRecyclerViewAdapter.appendToTop(sportRecord);
                mRecyclerViewAdapter.notifyDataSetChanged();

                Message message = new Message();
                message.what = ADD_SPORT;
                message.obj = sportRecord;
                handler.sendMessage(message);
                setWeightChartData(sport);
            }

            if (globalInfo.getUserid() != 0)
                uploadRecord(0);

        }
    };

    private DbDeleteListener sportRecordDeleteListener = new DbDeleteListener() {
        @Override
        public void callback(Object obj) {
            SportRecord sportRecord = (SportRecord) obj;
            //查询上一次的更新时间
            Sport sport = loadSport(sportRecord.getSportid());

            sport.setFrequency(sport.getFrequency()-1);
            sport.setTotal(sport.getTotal()-sportRecord.getAmount());
            sport.setSeq(0);
            //查询是否还有同一天的记录
            sport.setDays(sport.getDays());
            //日均
            sport.setAvg(sport.getTotal()/sport.getDays());
            ContentValues values = new ContentValues();
            values.put("total", sport.getTotal());
            values.put("frequency", sport.getFrequency());
            values.put("avg", sport.getAvg());
            values.put("days", sport.getDays());
            values.put("seq", sport.getSeq());
            DbHelper.getInstance(MainActivity.this).getDb().update("t_sport", values,"id=? and userid=?",new String[]{sport.getId()+"",""+globalInfo.getUserid()});

            //更新日统计, 月统计数据
            updateSportDayDelete(sportRecord);
            updateSportMonthDelete(sportRecord);

            //更新界面
            Message msg = new Message();
            msg.what=DELETE_SPORT_TONGJI;
            msg.obj = sportRecord;
            handler.sendMessage(msg);


        }
    };

    private class MySpinnerAdapter extends BaseAdapter{
        private List<String> datas;
        private Context mContext;

        MySpinnerAdapter(Context mContext,List datas){
            this.mContext=mContext;
            this.datas=datas;
        }
        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater _LayoutInflater=LayoutInflater.from(mContext);
            convertView=_LayoutInflater.inflate(R.layout.sportname_spinner, null);
            if(convertView!=null) {
                TextView _TextView1 = (TextView) convertView.findViewById(R.id.name);
                _TextView1.setText(datas.get(position));
            }
            return convertView;
        }
    }


    private void updateHeader(SportRecord sportRecord){
        Sport sport = loadSport(sportRecord.getSportid());
        if(sport.getKind()==3)
            return;
        int i=0;
        for (;i<sportGathers.size();i++){
            SportGather sportGather = sportGathers.get(i);
            if(sportGather.getSport().getId()==sportRecord.getSportid()){
                sportGathers.remove(i);
                break;
            }
        }
        //不包含的情况，移除最后一项
        if(i==sportGathers.size()){
            int pos = sportGathers.size()-1;
            sportGathers.remove(pos);
        }
        sportGathers.add(0, loadSportGatherFromDb(sportRecord.getSportid()));
        setChartData(sportGathers.get(0));

        //更新下拉菜单
        sportNameData.clear();
        for(i=0;i<sportGathers.size();i++){
            sport = sportGathers.get(i).getSport();
            sportNameData.add(sport.getName());
        }
        sportNameData.add("体重/围度");
        sportNameAdapter.notifyDataSetChanged();
        sportName.setSelection(0);

        updateMenu();
    }

    private void updateFragments(SportRecord sportRecord){
        int i=0;
        for (;i<sportGathers.size();i++){
            SportGather sportGather = sportGathers.get(i);
            if(sportGather.getSport().getId()==sportRecord.getSportid()){
                sportGathers.remove(i);
                break;
            }
        }
        //不包含的情况，移除最后一项
        if(i==sportGathers.size()){
            int pos = sportGathers.size()-1;
            sportGathers.remove(pos);
        }
        sportGathers.add(0, loadSportGatherFromDb(sportRecord.getSportid()));
        //通知fragment更新
        int minSize = fragmentDatas.size()>sportGathers.size()?sportGathers.size():fragmentDatas.size();
        for(i=0;i<minSize;i++){
            FragmentData fragmentData = (FragmentData) fragmentDatas.get(i);
            fragmentData.setSportGather(sportGathers.get(i));
            Log.e(TAG, "total:"+sportGathers.get(i).getSport().getTotal());
            fragmentData.update();
        }

        mFragmentDataPagerAdapter.setFragments(fragmentDatas);
        mFragmentDataPagerAdapter.notifyDataSetChanged();
        updateMenu();
    }

    private void updateFragmentsIfFirst(SportRecord sportRecord){

        Sport sport = loadSport(sportRecord.getSportid());
        if(sport.getKind()!=3){
            setChartData(loadSportGatherFromDb(sportRecord.getSportid()));
        }else {
            setWeightChartData(sport);
            mWeightAdapter.updateStatus(sportRecord.getSportid());
            mWeightAdapter.notifyDataSetChanged();
        }
        /*
        int position=-1;
        for (int i=0;i<sportGathers.size();i++){
            SportGather sportGather = sportGathers.get(i);
            if(sportGather.getSport().getId()==sportRecord.getSportid()){
                sportGathers.remove(i);
                position=i;
                break;
            }
        }
        if(position!=-1){
            sportGathers.add(position, loadSportGatherFromDb(sportRecord.getSportid()));
            //通知fragment更新
            for(int i=0;i<fragmentDatas.size();i++){
                FragmentData fragmentData = (FragmentData) fragmentDatas.get(i);
                fragmentData.setSportGather(sportGathers.get(i));
                fragmentData.update();
            }

            mFragmentDataPagerAdapter.setFragments(fragmentDatas);
            mFragmentDataPagerAdapter.notifyDataSetChanged();
        }
        */
    }

    private void updateMenu(){
        for (int i=0;i<menuImages.size();i++) {
            ImageView imageView = menuImages.get(i);
            Sport sport = sportGathers.get(i).getSport();
            String headImgUrl = Config.imagePrefix + sport.getImage();
            ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(imageView, R.drawable.dongfou, R.drawable.dongfou);
            VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener, 100, 100);
        }
    }

    private void insertSportDay(SportRecord record){
        ContentValues values = new ContentValues();
        values.put("sportid", record.getSportid());
        values.put("amount", record.getAmount());
        //自1970年后的秒数
        long time=TimeHelper.getDateStartOfDay(record.getTime()).getTime() / 1000;
        values.put("time", time);
        values.put("userid", "" + globalInfo.getUserid());
        DbHelper.getInstance(this).insert("t_sport_record_day", null, values);
    }
    private void updateSportDay(SportRecord record){
        long time=TimeHelper.getDateStartOfDay(record.getTime()).getTime() / 1000;
        ContentValues values = new ContentValues();
        String sql = "update t_sport_record_day set amount=amount+"+record.getAmount()+" where sportid="+record.getSportid()+" and time="+time+" and userid="+globalInfo.getUserid();
        DbHelper.getInstance(this).getDb().execSQL(sql);
    }

    private void deleteSportRecordDb(SportRecord record){
        Log.e(TAG, "delete record:" + record.getId());
        DbHelper.getInstance(this).delete("t_sport_record", "id=?", new String[]{record.getId() + ""}, record);
    }

    private void updateSportDayDelete(SportRecord record){
        //每种运动的每天记录都是用一天开始的时间做索引的
        long time=TimeHelper.getDateStartOfDay(record.getTime()).getTime() / 1000;
        ContentValues values = new ContentValues();
        String sql = "update t_sport_record_day set amount=amount-"+record.getAmount()+" where sportid="+record.getSportid()+" and userid="+globalInfo.getUserid()+" and time="+time;
        DbHelper.getInstance(this).getDb().execSQL(sql);
    }

    private void insertSportMonth(SportRecord record){
        ContentValues values = new ContentValues();
        values.put("sportid", record.getSportid());
        values.put("amount", record.getAmount());
        long time = TimeHelper.getDateStartOfMonth(record.getTime()).getTime()/1000;
        values.put("time", time);
        values.put("userid", "" + globalInfo.getUserid());
        DbHelper.getInstance(this).insert("t_sport_record_month", null, values);
    }
    private void updateSportMonth(SportRecord record){
        long time = TimeHelper.getDateStartOfMonth(record.getTime()).getTime()/1000;
        String sql = "update t_sport_record_month set amount=amount+"+record.getAmount()+" where sportid="+record.getSportid()+" and time="+time+" and userid="+globalInfo.getUserid()+" and time="+time;
        DbHelper.getInstance(this).getDb().execSQL(sql);
    }
    private void updateSportMonthDelete(SportRecord record){
        long time = TimeHelper.getDateStartOfMonth(record.getTime()).getTime()/1000;
        String sql = "update t_sport_record_month set amount=amount-"+record.getAmount()+" where sportid="+record.getSportid()+" and userid="+globalInfo.getUserid();
        DbHelper.getInstance(this).getDb().execSQL(sql);
    }

    private Sport loadSport(int sportid){
        Sport sport = null;
        if(sportMap.containsKey(sportid))
            sport = sportMap.get(sportid);
        else {
            sport = loadSportFromDb(sportid);
        }
        return sport;
    }

    private Sport loadSportFromDb(int sportid){
        Sport sport = null;
        SQLiteDatabase db = DbHelper.getInstance(this).getDb();
        Cursor cr = db.rawQuery("select * from t_sport where id=" + sportid, null);
        while (cr.moveToNext()) {
            sport = new Sport(cr.getInt(0), cr.getString(1), cr.getString(2), cr.getInt(3), cr.getString(4), cr.getInt(5), cr.getString(6), cr.getInt(7), cr.getInt(8)
                    , cr.getFloat(9), cr.getFloat(10), cr.getInt(11), new Date(cr.getLong(12)*1000), cr.getInt(13), cr.getFloat(14), cr.getFloat(15));
        }
        sportMap.put(sport.getId(),sport);
        cr.close();
        return sport;
    }

    private SportGather loadSportGatherFromDb(int sportid){
        return new SportGather(loadSportFromDb(sportid),loadTodaySportRecordById(sportid));
    }

    private void loadSportDatas(){
        SQLiteDatabase db = DbHelper.getInstance(this).getDb();

        //id int primary key,name text not null,image text not null,type int not null,unit text not null,unit2 text null,int maxnum not null,frequency int not null default 0)");
        Cursor cr = db.rawQuery("select * from t_sport where userid="+globalInfo.getUserid()+" and kind!=3 order by lasttime desc,id limit 5", null);
        while (cr.moveToNext()){
            Sport sport = new Sport(cr.getInt(0),cr.getString(1),cr.getString(2),cr.getInt(3),cr.getString(4),cr.getInt(5),cr.getString(6),cr.getInt(7),cr.getInt(8)
            ,cr.getFloat(9),cr.getFloat(10),cr.getInt(11),new Date(cr.getLong(12)*1000),cr.getInt(13),cr.getFloat(14),cr.getFloat(15));
            sportMap.put(sport.getId(), sport);
            sportGathers.add(new SportGather(sport, loadTodaySportRecordById(sport.getId())));
            Log.e(TAG, "load sportid:" + sport.getId() + ", " + sport.getName() + ", " + sport.getTotal());
        }
        cr.close();
    }

    //过去7天数据汇总
    private List<SportRecord> loadTodaySportRecordById(int sportid){

        List<SportRecord> records = new ArrayList<>();
        SQLiteDatabase db = DbHelper.getInstance(this).getDb();
        //Cursor cr = db.rawQuery("select amount,time from t_sport_record_day where userid=" + globalInfo.getUserid() + " and sportid=" + sportid + " and date(time,'unixepoch','localtime')>date('now','-7 day') order by time ", null);
        Cursor cr = db.rawQuery("select amount,time from t_sport_record_day where userid=" + globalInfo.getUserid() + " and sportid=" + sportid + " order by time ", null);
        Date minTime = TimeHelper.getDateStartOfDay(-6);
        Date maxTime = TimeHelper.getDateStartOfDay();
        Log.e(TAG, "sportid:"+sportid+", size:"+cr.getCount());

        List<SportRecord> tmpRecords = new ArrayList<>();
        boolean isFirst = true;
        long timeNum=0;
        while (cr.moveToNext()){
            float amount = cr.getInt(0);
            timeNum = cr.getLong(1)*1000;
            SportRecord record = new SportRecord();
            record.setAmount(amount);
            record.setTime(new Date(timeNum));

            //第一条记录 且 大于最小时间  则 将最小时间作为一个0记录插入
            if(isFirst && timeNum>minTime.getTime()){
                SportRecord minRecord = new SportRecord();
                minRecord.setAmount(0);
                minRecord.setTime(minTime);
                tmpRecords.add(minRecord);
                isFirst=false;
            }
            tmpRecords.add(record);
        }
        cr.close();
        if(tmpRecords.size()==0){
            SportRecord record = new SportRecord();
            record.setAmount(0f);
            record.setTime(minTime);
            tmpRecords.add(record);
        }
        if(timeNum<maxTime.getTime()){
            SportRecord record = new SportRecord();
            record.setAmount(0f);
            record.setTime(maxTime);
            tmpRecords.add(record);
        }

        int tmpSize = tmpRecords.size();
        for(int i=0;i<tmpSize;i++){
            SportRecord record = tmpRecords.get(i);
            timeNum = record.getTime().getTime();
            Log.e(TAG, record.getTime().toString());
            records.add(record);
            if(i+1==tmpSize)
                break;

            int deltaDays = TimeHelper.getDateDelta(record.getTime(),tmpRecords.get(i+1).getTime());
            for(int j=1;j<deltaDays;j++) {
                record = new SportRecord();
                record.setAmount(0f);
                record.setTime(new Date(timeNum+86400000*j));
                Log.e(TAG, new Date(timeNum+86400000*j).toString());
                records.add(record);
            }
        }
        Log.e(TAG, "records size:"+records.size());
        return records;
    }



    private DbInsertListener chatRecorInsertListener = new DbInsertListener() {
        @Override
        public void callback(Object obj) {
            SportRecord sportRecord = (SportRecord)obj;
            mRecyclerViewAdapter.appendToTop(sportRecord);
            mRecyclerViewAdapter.notifyItemInserted(0);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            switch (requestCode) {
                case GET_SPORT:
                    int sportid = data.getIntExtra("sportid", -1);
                    showDataDialog(loadSport(sportid));
                    break;
                case LOGIN_REGISTER:
                    int userid = data.getIntExtra("userid", -1);
                    String token = data.getStringExtra("token");
                    Log.e(TAG, "new token:"+token);
                    //记录userid
                    globalInfo.setUserid(userid);
                    globalInfo.setToken(token);
                    editor.putInt("userid", userid);
                    editor.putString("token", token);
                    editor.commit();

                    //更新userid为0的记录
                    String sql = "update t_sport set userid="+userid+" where userid=0";
                    try {
                        dbInstance.execSQL(sql);
                    }catch (Exception e){
                        Log.e(TAG, e.toString());
                    }
                    sql = "update t_sport_record set userid="+userid+" where userid=0";
                    dbInstance.execSQL(sql);
                    sql = "update t_sport_record_day set userid="+userid+" where userid=0";
                    dbInstance.execSQL(sql);
                    sql = "update t_sport_record_month set userid="+userid+" where userid=0";
                    dbInstance.execSQL(sql);

                    downloadRecord();
                    menu.findItem(R.id.login_logout).setTitle("退出帐号");
                    break;
                default:
                    break;
            }
        }
    }

    /*
    * mode = 0 自动同步
    * mode = 1 手动同步
    * mode = 2 登录后第一次同步
    * */
    private void uploadRecord(final int mode){

        Log.e(TAG, "uploadRecord mode:"+mode);
        String sql = "select * from t_sport_record where userid="+globalInfo.getUserid()+" and seq=0;";
        //select * from t_sport_record;
        Cursor cr = dbInstance.rawQuery(sql, null);
        if(cr.getCount()==0){
            return;
        }
        final ArrayList<SportRecord> sportRecords = new ArrayList<>();
        while (cr.moveToNext()) {
            //t_sport_record(id int primary key,sportid int not null,amount real not null,arg1 int not null default 0,arg2 int not null default 0,time int NOT NULL,seq int not null default 0,userid int not null default 0)");
            int id = cr.getInt(0);
            int sportid=cr.getInt(1);
            float amount = cr.getFloat(2);
            float arg1=cr.getFloat(3);
            float arg2=cr.getFloat(4);
            int seq=cr.getInt(6);
            Date time = new Date(cr.getInt(5)*1000l);
            SportRecord record = new SportRecord(id,sportid,amount,arg1,arg2,seq,time,globalInfo.getUserid());
            sportRecords.add(record);
        }
        cr.close();

        Log.e(TAG, "uploadRecord siez:" + sportRecords.size());

        Type type = new TypeToken<ArrayListResponse<SportRecord>>() {}.getType();
        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, Config.uploadRecordUrl, type, new GsonRequest.PostGsonRequest<ArrayListResponse>() {
            @Override
            public void onStart() {
                //显示同步转圈圈动画
            }
            @Override
            public void onResponse(ArrayListResponse response) {
                if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                    Log.e(TAG, "onResponse upload record error:" + response.getError() + ", " + response.getErrno());
                    Message msg = new Message();
                    msg.what=UPLOAD_RECORD_ERROR;
                    msg.obj = response.getError();
                    msg.arg1=mode;
                    msg.arg2=response.getErrno();
                    handler.sendMessage(msg);
                }else {
                    ArrayList<SportRecord> datas = response.getDatas();
                    //入库,本地化
                    for(int i=0;i<datas.size();i++){
                        SportRecord record = datas.get(i);

                        ContentValues values = new ContentValues();
                        values.put("seq", record.getSeq());

                        String[] args = new String[]{""+record.getId(),""+record.getUserid()};
                        dbInstance.update("t_sport_record", values,"id=? and userid=?",args);
                        Log.e(TAG, "update t_sport_record, seq:" + record.getSeq());
                    }
                    Message msg = new Message();
                    msg.what=UPLOAD_RECORD_DONE;
                    msg.arg1=mode;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onError(VolleyError error) {
                //Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "同步记录, 网络错误:"+error);
                Message msg = new Message();
                msg.what=VOLLEY_ERROR;
                msg.obj = error;
                handler.sendMessage(msg);
            }

            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", globalInfo.getUserid()+"");
                datas.put("token", globalInfo.getToken()+"");
                datas.put("records",TimeHelper.gsonWithDate().toJson(sportRecords));
                return datas;
            }
        });
        gsonRequest.setGson(TimeHelper.gsonWithDate());
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }

    private void downloadRecord(){

        String sql = "select * from t_sport_record as s inner join (select sportid,userid,max(seq) as seq from t_sport_record where userid="+globalInfo.getUserid()+" group by sportid) s1 on s.sportid=s1.sportid and s.seq=s1.seq and s.userid=s1.userid";
        //select * from t_sport_record as s inner join (select sportid,userid,max(seq) as seq from t_sport_record where userid=6 group by sportid) s1 on s.sportid=s1.sportid and s.seq=s1.seq and s.userid=s1.userid;

        Cursor cr = dbInstance.rawQuery(sql, null);
        final ArrayList<SportRecord> sportRecords = new ArrayList<>();
        while (cr.moveToNext()) {
            //t_sport_record(id int primary key,sportid int not null,amount real not null,arg1 int not null default 0,arg2 int not null default 0,time int NOT NULL,seq int not null default 0,userid int not null default 0)");
            int id = cr.getInt(0);
            int sportid=cr.getInt(1);
            float amount = cr.getFloat(2);
            float arg1=cr.getFloat(3);
            float arg2=cr.getFloat(4);
            int seq=cr.getInt(6);
            Date time = new Date(cr.getInt(5)*1000l);
            SportRecord record = new SportRecord(id,sportid,amount,arg1,arg2,seq,time,globalInfo.getUserid());
            sportRecords.add(record);
        }
        cr.close();

        Type type = new TypeToken<ArrayListResponse<SportRecord>>() {}.getType();
        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, Config.downloadRecordUrl, type, new GsonRequest.PostGsonRequest<ArrayListResponse>() {
            @Override
            public void onStart() {
                //显示同步转圈圈动画
            }
            @Override
            public void onResponse(ArrayListResponse response) {
                if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                    Log.e(TAG, "onResponse download record error:" + response.getError() + ", " + response.getErrno());
                    Message msg = new Message();
                    msg.what=DOWNLOAD_RECORD_ERROR;
                    handler.sendMessage(msg);
                }else {
                    ArrayList<SportRecord> datas = response.getDatas();
                    Log.e(TAG, "download record size:"+datas.size());
                    //入库,本地化
                    for(int i=0;i<datas.size();i++){
                        SportRecord record = datas.get(i);
                        Log.e(TAG, "server record:"+record.getTime());
                        addSportRecordFromServer(record);
                    }
                    handler.sendEmptyMessage(DOWNLOAD_RECORD_DONE);
                }
            }

            @Override
            public void onError(VolleyError error) {
                //Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "下载记录, 网络错误:"+error);
                Message msg = new Message();
                msg.what=VOLLEY_ERROR;
                msg.obj = error;
                handler.sendMessage(msg);
            }

            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", globalInfo.getUserid()+"");
                datas.put("token", globalInfo.getToken()+"");
                datas.put("records",TimeHelper.gsonWithDate().toJson(sportRecords));
                return datas;
            }
        });
        gsonRequest.setGson(TimeHelper.gsonWithNodeDate());
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }

    private void addSportRecordFromServer(SportRecord sportRecord){
        //create table t_sport_record(id int primary key,amount real not null,int arg1 not null default 0,int arg2 not null default 0,time int NOT NULL,seq int not null default 0)");
        ContentValues values = new ContentValues();

        values.put("userid", "" + sportRecord.getUserid());
        values.put("sportid", sportRecord.getSportid());
        values.put("amount", ""+sportRecord.getAmount());
        values.put("arg1", "" + sportRecord.getArg1());
        values.put("arg2", "" + sportRecord.getArg2());
        values.put("time", sportRecord.getTime().getTime() / 1000);
        values.put("seq", sportRecord.getSeq());
        DbHelper.getInstance(this).insert("t_sport_record", null, values);
    }

    private void checkVersionUpdate(){
        final int versionCode = getVersionCode();
        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, Config.checkUpdate , Version.class, new GsonRequest.PostGsonRequest<Version>() {
            @Override
            public void onStart() {
                //显示同步转圈圈动画
            }
            @Override
            public void onResponse(Version response) {
                if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                    Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                }else {
                    Message msg = new Message();
                    msg.what=CHECK_VERSION;
                    Log.e(TAG, "url:"+response.getUrl());
                    msg.obj=response.getUrl();
                    msg.arg1=0;
                    int newVersionCode = response.getVersioncode();
                    editor.putInt("newestVersion", newVersionCode);
                    editor.commit();
                    int currentVersionCode = getVersionCode();
                    int minVersion = response.getMinversion();
                    if(currentVersionCode<newVersionCode){
                        int checkVersionCode = baseInfo.getInt("checkVersionCode", 0);
                        if(checkVersionCode==0 || checkVersionCode<newVersionCode){
                            msg.arg1=1;
                        }
                    }
                    if(currentVersionCode<minVersion)
                        msg.arg1=2;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "检查版本更新, 网络错误:" + error);
            }

            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", globalInfo.getUserid()+"");
                datas.put("versioncode", versionCode + "");
                datas.put("deviceid", getDeviceId());
                return datas;
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        setSyncMenuVisible(hasNotSyncRecord());
        if (globalInfo.getUserid() == 0 || globalInfo.getToken()==null || globalInfo.getToken()==""){
            setSyncMenuVisible(true);
            menu.findItem(R.id.login_logout).setTitle("登录/注册");
        }else {
            menu.findItem(R.id.login_logout).setTitle("退出帐号");
        }
        return true;
    }

    private boolean hasNotSyncRecord(){
        String sql = "select count(*) from t_sport_record where userid="+globalInfo.getUserid()+" and seq=0;";
        //select * from t_sport_record;
        Cursor cr = dbInstance.rawQuery(sql, null);

        if(cr.moveToNext()){
            int count = cr.getInt(0);
            Log.e(TAG, "not sync record size:" + count);
            if(count>0)
                return true;
            else
                return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent=null;
        switch (item.getItemId()){
            case R.id.sync:
                if(globalInfo.getUserid()==0 || globalInfo.getToken()==null || globalInfo.getToken()==""){
                    intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                    startActivityForResult(intent, LOGIN_REGISTER);
                }else {
                    uploadRecord(1);
                }
                break;
            case R.id.notifications:
                setNotificationStatus(false);
                editor.putInt("checkVersionCode", baseInfo.getInt("newestVersion", getVersionCode()));
                editor.commit();
                intent = new Intent(MainActivity.this, NotificationActivity.class);
                intent.putExtra("versionCode", getVersionCode());
                startActivity(intent);
                break;
            case R.id.feedback:
                intent = new Intent(MainActivity.this, FeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.update:
                checkVersionUpdateWithHand();
                break;
            case R.id.login_logout:
                Log.e(TAG, "userid:"+globalInfo.getUserid()+", token:"+globalInfo.getToken());
                if(globalInfo.getUserid()==0 || globalInfo.getToken()==null || globalInfo.getToken()==""){
                    intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                    startActivityForResult(intent, LOGIN_REGISTER);
                }else {
                    //清除用户登录信息
                    logout();
                }
                break;
            case R.id.test:
                intent = new Intent(MainActivity.this, NewMainActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        Log.e(TAG, Config.logoutUrl);
        String sql = "select * from t_sport_record where userid=" + globalInfo.getUserid() + " and seq=0;";
        //select * from t_sport_record;
        Cursor cr = dbInstance.rawQuery(sql, null);
        if (cr.getCount() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("退出后,您尚未同步的记录将丢失");
            builder.setTitle("警告");
            builder.setPositiveButton("仍然退出", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                      logoutRequest();
                 }
            });
            builder.setNegativeButton("同步", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                     uploadRecord(1);
                 }
            });
            builder.create().show();
        }else {
            logoutRequest();
        }
    }

    private void logoutRequest() {
        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST,Config.logoutUrl,ResponseError.class, new GsonRequest.PostGsonRequest<ResponseError>() {
            LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);
            @Override
            public void onStart() {
                    loadingDialog.show();
                }
                @Override
                public void onResponse(ResponseError response) {
                    Log.e(TAG, "response:" + response.toString());
                    if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0) {
                        Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());

                        //Toast.makeText(MainActivity.this, response.getError(), Toast.LENGTH_SHORT).show();
                        /*
                        editor.putString("token", "");
                        editor.commit();
                        Toast.makeText(MainActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();

                        loadingDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                        startActivityForResult(intent, LOGIN_REGISTER);
                    }else {
                    */
                    }

                    //清除数据
                    editor.putInt("userid", 0);
                    editor.putString("token", "");
                    editor.remove("logintip");
                    editor.remove("newestVersion");
                    editor.remove("checkVersionCode");
                    editor.commit();

                    //删除运动记录
                    SQLiteDatabase db = DbHelper.getInstance(MainActivity.this).getDb();
                    int userid = globalInfo.getUserid();
                    db.delete("t_sport", "userid=?", new String[]{userid+""});
                    db.delete("t_sport_record", "userid=?", new String[]{userid + ""});
                    db.delete("t_sport_record_day", "userid=?", new String[]{userid + ""});
                    db.delete("t_sport_record_month", "userid=?", new String[]{userid + ""});
                    db.delete("t_notice", null, null);


                    loadingDialog.dismiss();

                    Intent intent = new Intent(MainActivity.this, StartupActivity.class);
                    intent.putExtra("logout", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(VolleyError error) {
                    Toast.makeText(MainActivity.this, "网络错误:" + error, Toast.LENGTH_SHORT);
                    Message msg = new Message();
                    msg.what=VOLLEY_ERROR;
                    msg.obj=error;
                    handler.sendMessage(msg);
                    loadingDialog.dismiss();
                }

                @Override
                public Map getPostData() {
                    Map datas = new HashMap();
                    datas.put("userid", globalInfo.getUserid()+"");
                    datas.put("token", globalInfo.getToken()+"");
                    Log.e(TAG, "logout:"+globalInfo.getUserid()+", "+globalInfo.getToken());
                    return datas;
                }
            });
            VolleyHelper.getInstance().addToRequestQueue(gsonRequest);

    }
    private String getVersionName(){
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        }catch (Exception e){

        }
        if(packInfo!=null)
            return packInfo.versionName;
        else
            return "###";
    }
    private int getVersionCode(){
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo=null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        }catch (Exception e){

        }
        if(packInfo!=null)
            return packInfo.versionCode;
        else
            return 0;
    }

    private String getDeviceId(){
        String deviceId = baseInfo.getString("deviceid", "");
        if(deviceId.length()==0){
            final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId();
            editor.putString("deviceid", deviceId);
            editor.commit();
        }
        return deviceId;
    }

    private void checkVersionUpdateWithHand(){
        final int versionCode = getVersionCode();
        final LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);
        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, Config.checkUpdate , Version.class, new GsonRequest.PostGsonRequest<Version>() {
            @Override
            public void onStart() {
                                        loadingDialog.show();
                                                             }
            @Override
            public void onResponse(Version response) {
                loadingDialog.dismiss();
                if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                    Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                }else {
                    int newVersionCode = response.getVersioncode();
                    int currentVersionCode = getVersionCode();
                    Log.e(TAG, "checkVersionUpdateWithHand newVersionCode:"+newVersionCode+", currentVersionCode:"+currentVersionCode);
                    if(currentVersionCode<newVersionCode){
                        //跳转到更新页面
                        Uri uri = Uri.parse(response.getUrl());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }else {
                        Toast.makeText(MainActivity.this, "已是最新版本", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "检查版本更新, 网络错误:" + error);
                Message msg = new Message();
                msg.what=VOLLEY_ERROR;
                msg.obj=error;
                handler.sendMessage(msg);
                loadingDialog.dismiss();
            }

            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", globalInfo.getUserid()+"");
                datas.put("versioncode", versionCode + "");
                datas.put("deviceid", getDeviceId());
                return datas;
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }

    private void initChart(){

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.setDrawHighlightArrow(false);
        mChart.getXAxis().setDrawGridLines(false);

        //头部点击出现数值
        //mChart.setMarkerView(new MyMarkerView(MainActivity.this,R.layout.my_mark_view));

        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        mChart.setDescription("");

        mChart.getAxisRight().setEnabled(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getAxisLeft().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);

        YAxisValueFormatter custom = new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return ""+value;
            }
        };

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        mChart.setVisibleXRangeMaximum(30);
        mChart.setVisibleXRangeMinimum(7);


        //对每种颜色的柱状图说明
        mChart.getLegend().setEnabled(false);
    }

    private void setChartData(SportGather sportGather) {
        Sport sport = sportGather.getSport();
        String totalStr="XX";
        if(sport.getKind()==1) {
            unit.setText(sport.getUnit());
            totalStr = new DecimalFormat("0").format(sport.getTotal());
        }else if(sport.getKind()==2) {
            unit.setText(sport.getUnit2());
            totalStr = new DecimalFormat("0.0").format(sport.getTotal());
        }

        total.setText(totalStr);

        List<SportRecord> sportRecords = sportGather.getSportRecords();

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        Date todayBegin = TimeHelper.getDateStartOfDay();
        for (int i = 0; i < sportRecords.size(); i++) {
            SportRecord record = sportRecords.get(i);
            if(todayBegin.equals(record.getTime()))
                xVals.add("今天");
            else
                xVals.add(TimeHelper.dateFormat(record.getTime(), "M/d"));
            yVals1.add(new BarEntry(record.getAmount(), i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        //设置显示数值
        //set1.setDrawValues(false);
        //set1.setHighlightEnabled(false);
        //set1.setHighLightAlpha(100);
        //set1.setHighLightColor();

        //set1.setLabel("");
        set1.setColor(getResources().getColor(R.color.colorAccent1));
        set1.setBarSpacePercent(35f);
        set1.setHighlightEnabled(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        //data.setValueTypeface(mTf);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        if(sport.getAvg()>0){
            LimitLine ll = new LimitLine(sport.getAvg(), "");
            ll.setLineColor(getResources().getColor(R.color.white_bg_border));
            ll.setLineWidth(1f);
            ll.enableDashedLine(5,5,0);
            ll.setTextStyle(Paint.Style.STROKE);
            ll.setTextColor(Color.GRAY);
            ll.setTextSize(12f);
            // .. and more styling options
            leftAxis.addLimitLine(ll);
        }

        mChart.setData(data);
        //mChart.setDrawValueAboveBar(false);

        mChart.setScaleYEnabled(false);
        mChart.setVisibleXRangeMaximum(30);
        mChart.setVisibleXRangeMinimum(7);
        int pointSize = yVals1.size();
        mChart.setScaleMinima(pointSize / 7f, 1);
        mChart.moveViewToX(sportRecords.size());

        mChart.animateY(1000);
        mChart.invalidate();
    }

    private void initWeightChart(){

        mWeightChart.getXAxis().setDrawGridLines(false);

        mWeightChart.setPinchZoom(false);

        mWeightChart.setDrawGridBackground(false);

        mWeightChart.setDescription("");

        //mWeightChart.setMaxVisibleValueCount(2);

        mWeightChart.getAxisRight().setEnabled(false);
        mWeightChart.setMarkerView(new MyMarkerView(MainActivity.this, R.layout.my_mark_view));

        XAxis xAxis = mWeightChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);

        YAxisValueFormatter custom = new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return ""+value;
            }
        };

        YAxis leftAxis = mWeightChart.getAxisLeft();
        //设置左边坐标个数， 如果画线将影响横线的条数
        leftAxis.setLabelCount(3, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        //左边轴数值距离表格宽度
        //leftAxis.setXOffset(10);

        //对每种颜色的柱状图说明
        mWeightChart.getLegend().setEnabled(false);
        //mWeightChart.setNoDataTextDescription("没有数据");
        mWeightChart.setDescriptionColor(R.color.black_35);
        mWeightChart.setDescriptionTextSize(18);

        /*
        Paint mInfoPaint = new Paint();
        mInfoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInfoPaint.setColor(Color.rgb(0, 0, 0)); // orange
        mInfoPaint.setTextAlign(Paint.Align.CENTER);
        mInfoPaint.setTextSize(Utils.convertDpToPixel(16f));
        mWeightChart.setPaint(mInfoPaint,LineChart.PAINT_DESCRIPTION);
        */
        mWeightChart.setNoDataText("");
        mWeightChart.setNoDataTextDescription("没有数据，长按添加记录");
        mWeightChart.setScaleEnabled(false);
        mWeightChart.setScaleXEnabled(true);

    }

    private void setAllWeightChartData(){
        /* 同时显示全部维度数据
        Map<Integer,List> everyRecords = new HashMap<>();
        for(int i=0;i<records.size();i++){
            SportRecord record = records.get(i);
            int sportid = record.getSportid();
            if(!everyRecords.containsKey(sportid))
                everyRecords.put(sportid, new ArrayList());
            everyRecords.get(sportid).add(record);
        }
        Date minTime;
        Date maxTime;
        if(records.size()>0){
            minTime = records.get(0).getTime();
            maxTime = records.get(records.size()-1).getTime();
        }else
            minTime=maxTime=null;

        ArrayList<String> xVals = new ArrayList<String>();
        Map<Long,Integer> time2pos = new HashMap<>();
        int pos=0;
        long minTimeNum = TimeHelper.getDateStartOfDay(minTime).getTime();
        long maxTimeNum = TimeHelper.getDateStartOfDay(maxTime).getTime();
        long timeNum = minTimeNum;
        while(timeNum<=maxTimeNum){
            time2pos.put(timeNum,pos);
            xVals.add(TimeHelper.dateFormat(new Date(timeNum), "M/d"));
            timeNum+=86400000;
            pos++;
        }

        //对纬度类型id进行排序
        ArrayList<Integer> sortedKeys = new ArrayList<>();
        Iterator<Integer> iterator = everyRecords.keySet().iterator();
        while (iterator.hasNext())
            sortedKeys.add(iterator.next());
        Collections.sort(sortedKeys);

        //int[] colors = new int[]{0xFF0000,0xFFA500,0xFFFF00,0x008000,0x0000FF,0x4B0082,0x800880};
        int[] colors = new int[]{Color.rgb(255,0,0),Color.rgb(255,165,0),Color.rgb(255,255,0),Color.rgb(0,255,0),Color.rgb(0,0,255),Color.rgb(43,0,255),Color.rgb(87,0,255)};

        ArrayList<ArrayList<Entry>> yVals = new ArrayList<ArrayList<Entry>>();
        //各种类型的参考线
        ArrayList<Float> lines = new ArrayList<>();
        pos=0;
        iterator = sortedKeys.iterator();
        while (iterator.hasNext()){
        //for (Map.Entry<Integer, List> entry : everyRecords.entrySet()) {
            int sportid = iterator.next();
            List<SportRecord> records1 = everyRecords.get(sportid);
            ArrayList<Entry> yVal = new ArrayList<>();
            for (int i=0;i<records1.size();i++){
                SportRecord record = records1.get(i);
                timeNum = TimeHelper.getDateStartOfDay(record.getTime()).getTime();
                int index = time2pos.get(timeNum);
                yVal.add(new Entry(records1.get(i).getAmount(),index));
            }
            lines.add(records1.get(records1.size()-1).getAmount());
            yVals.add(yVal);
            pos++;
        }
        */
    }

    private void setWeightChartData(Sport sport) {
        //加载体重数据
        //设置体重数据

        weightKind.setText(sport.getName());
        weightUnit.setText(sport.getUnit());
        weight.setText("0");

        int[] colors = new int[]{Color.rgb(103,58,185),Color.rgb(255,165,0),Color.rgb(255,255,0),Color.rgb(0,255,0),Color.rgb(0,0,255),Color.rgb(43,0,255),Color.rgb(87,0,255)};
        //加载体重、围度记录
        List<SportRecord> records = sportDbHelper.loadWeightRecordById(globalInfo.getUserid(),sport.getId());
        //如果没有数据的时候
        if(records.size()==0) {
            // set data
            mWeightChart.clear();
            mWeightChart.setData(null);
            mWeightChart.invalidate();
            return;
        }

        ArrayList<Entry> yVal = new ArrayList<Entry>();
        //各种类型的参考线
        //pos=0;
        float maxNum=0,minNum=0;
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i=0;i<records.size();i++){
            SportRecord record = records.get(i);
            float amount = record.getAmount();
            //Log.e(TAG, "amount:"+amount);
            if(maxNum==0 || minNum==0)
                minNum=maxNum=amount;
            if(amount>maxNum)
                maxNum=amount;
            if(amount<minNum)
                minNum=amount;

            long timeNum = TimeHelper.getDateStartOfDay(record.getTime()).getTime();
            xVals.add(TimeHelper.dateFormat(new Date(timeNum), "M/d"));
            //int index = time2pos.get(timeNum);
            yVal.add(new Entry(records.get(i).getAmount(),i));
        }
        float lastValue = records.get(records.size()-1).getAmount();
        weight.setText(lastValue+"");

        float line = lastValue;

        YAxis leftAxis = mWeightChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        int maxNumInt = (int)(maxNum*1.2f);
        int minNumInt = (int)(minNum*0.8f);
        leftAxis.setAxisMaxValue(maxNumInt);
        leftAxis.setAxisMinValue(minNumInt); // this replaces setStartAtZero(true)
        Log.e(TAG, "min:"+minNumInt+", max:"+maxNumInt);

        if (mWeightChart.getData() != null && mWeightChart.getData().getDataSetCount() > 0) {
            LineDataSet set = (LineDataSet) mWeightChart.getData().getDataSetByIndex(0);
            set.setYVals(yVal);
            mWeightChart.getData().setXVals(xVals);
            mWeightChart.notifyDataSetChanged();
        } else {
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

            LineDataSet set = new LineDataSet(yVal, sport.getName());
            //赛贝斯曲线
            set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            //set.setDrawCircles(false);
            set.setDrawValues(false);
            set.setLabel("");

            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            //set.setColor(ColorTemplate.getHoloBlue());
            set.setColor(colors[0]);
            set.setCircleColor(colors[0]);
            set.setLineWidth(3f);
            set.setCircleRadius(3f);
            //set.setFillAlpha(65);
            //set.setFillColor(ColorTemplate.getHoloBlue());
            //set.setHighLightColor(Color.rgb(244, 117, 117));
            //set.setDrawCircleHole(false);

            dataSets.add(set); // add the datasets

            LineData data = new LineData(xVals, dataSets);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);

            // set data
            mWeightChart.setData(data);
        }
        LimitLine ll = new LimitLine(line, "");
        ll.setLineColor(colors[0]);
        ll.setLineWidth(1f);
        ll.enableDashedLine(5, 5, 0);
        ll.setTextStyle(Paint.Style.STROKE);
        ll.setTextColor(Color.GRAY);
        ll.setTextSize(12f);
        // .. and more styling options
        leftAxis.addLimitLine(ll);

        mWeightChart.invalidate();
        //要在这里重新设置才会生效

        mWeightChart.setVisibleXRangeMaximum(30);
        mWeightChart.setVisibleXRangeMinimum(7);
        //缩放到只显示7个数值
        int pointSize = yVal.size();
        mWeightChart.setScaleMinima(pointSize/7f,1);
        //setScaleMinima 是设置最大放大的倍数的，放大后就不能缩放了
        mWeightChart.setScaleEnabled(false);
        mWeightChart.setScaleXEnabled(true);
        mWeightChart.moveViewToX(records.size());

        mWeightChart.animateX(1000);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        //Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }

    private void showWeightDialog(final Sport sport){
        float lastV;
        lastV = sport.getLastValue();
        Log.e(TAG, "lastV:"+lastV);
        WeightPickerDialog dialog = new WeightPickerDialog(MainActivity.this, sport.getName(), sport.getMaxnum(),lastV, sport.getUnit(), new WeightPickerDialog.OnSelectListener() {
            @Override
            public void onSelect(float data,float data2) {
                if(sportDbHelper==null)
                    sportDbHelper=new SportDbHelper(MainActivity.this);
                float result = data+data2;
                Log.e(TAG, "data:"+data+", data2:"+data2+", result:"+result);
                sport.setLastValue(result);
                sportDbHelper.addSportRecord(sport.getId(), result, data2);
            }
        });
        dialog.setEditable(false);
        dialog.show();
    }
}

