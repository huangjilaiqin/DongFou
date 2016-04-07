package com.lessask.dongfou;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.capricorn.ArcMenu;
import com.google.gson.reflect.TypeToken;
import com.lessask.dongfou.dialog.MenuDialog;
import com.lessask.dongfou.dialog.OnSelectMenu;
import com.lessask.dongfou.dialog.StringPickerDialog;
import com.lessask.dongfou.dialog.StringPickerTwoDialog;
import com.lessask.dongfou.net.GsonRequest;
import com.lessask.dongfou.net.VolleyHelper;
import com.lessask.dongfou.util.DbDeleteListener;
import com.lessask.dongfou.util.DbHelper;
import com.lessask.dongfou.util.DbInsertListener;
import com.lessask.dongfou.util.GlobalInfo;
import com.lessask.dongfou.util.TimeHelper;
import com.viewpagerindicator.CirclePageIndicator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //private XRecyclerView mRecyclerView;
    private RecyclerView mRecyclerView;
    private SportRecordAdapter mRecyclerViewAdapter;

    private FragmentPagerAdapter mFragmentPagerAdapter;
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

    private Map<Integer,Sport> sportMap;
    private ArrayList<SportGather> sportGathers;
    private ArrayList<Fragment> fragmentDatas;
    private ArrayList<ImageView> menuImages;
    private Bundle fragmentBundle = new Bundle();
    private DbHelper dbHelper;
    private SQLiteDatabase dbInstance;

    private Handler handler = new Handler(){
        SportRecord sportRecord;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ADD_SPORT:
                    Log.e(TAG, "ADD_SPORT");
                    sportRecord = (SportRecord)msg.obj;
                    updateFragments(sportRecord);
                    mViewPager.setCurrentItem(0);
                    mRecyclerView.scrollToPosition(0);
                    break;
                case DELETE_SPORT:
                    int deletePostion = msg.arg1;
                    sportRecord = (SportRecord)msg.obj;
                    mRecyclerViewAdapter.remove(deletePostion);
                    mRecyclerViewAdapter.notifyItemRemoved(deletePostion);
                    mRecyclerViewAdapter.notifyItemRangeChanged(deletePostion, mRecyclerViewAdapter.getItemCount());
                    break;
                case DELETE_SPORT_TONGJI:
                    sportRecord = (SportRecord)msg.obj;
                    updateFragmentsIfFirst(sportRecord);
                    break;
                case UPLOAD_RECORD_ERROR:
                    setSyncMenuVisible(true);
                    break;
                case UPLOAD_RECORD_DONE:
                    setSyncMenuVisible(hasNotSyncRecord());
                    break;
            }
        }
    };

    private void setSyncMenuVisible(boolean visible){
        menu.getItem(0).setVisible(visible);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "oncreate savedInstanceState:" + savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = DbHelper.getInstance(this);
        dbInstance = dbHelper.getDb();
        sportMap = new HashMap<>();
        sportGathers = new ArrayList<>();
        menuImages = new ArrayList<>();

        //检查userid
        if(globalInfo.getUserid()==0){

            SharedPreferences baseInfo = getSharedPreferences("BaseInfo", MODE_PRIVATE);
            int useid = baseInfo.getInt("userid", 0);
            globalInfo.setUserid(useid);

        }
        //加载运动列表为空, 可能是globalInfo中的userid获取不到了
        loadDatas();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("动否");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //View mHeaderView = LayoutInflater.from(this).inflate(R.layout.data_header,mRecyclerView,false);
        //View footView = LayoutInflater.from(this).inflate(R.layout.data_foot,mRecyclerView,false);

        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager());
        fragmentDatas = new ArrayList<>();
        for(int i=0;i<5;i++) {
            FragmentData fragmentData = new FragmentData();
            Bundle bundle = new Bundle();
            bundle.putInt("sportid", sportGathers.get(i).getSport().getId());
            fragmentData.setArguments(bundle);
            fragmentDatas.add(fragmentData);
        }
        mFragmentPagerAdapter.setSportGathers(sportGathers);
        mFragmentPagerAdapter.setFragments(fragmentDatas);
        //mViewPager = (ViewPager)mHeaderView.findViewById(R.id.viewpager);
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setAdapter(mFragmentPagerAdapter);

        //mRecyclerView.addHeaderView(mHeaderView);
        //mRecyclerView.addFooterView(footView);
        mRecyclerViewAdapter = new SportRecordAdapter(this);
        mRecyclerViewAdapter.setSportMap(sportMap);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
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
                                            //删除课程
                                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                            builder.setMessage("确认删除吗？" + TimeHelper.date2Chat(sportRecord.getTime())+ ", name:" + sport.getName());
                                            builder.setTitle("提示");
                                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    //网络协议
                                                    deleteSportRecord(position);
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
            });

        loadSportRecord();
        DbHelper.getInstance(getBaseContext()).appendInsertListener("t_sport_record", chatRecorInsertListener);

        //CirclePageIndicator circlePageIndicator = (CirclePageIndicator)mHeaderView.findViewById(R.id.indicator);
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        circlePageIndicator.setFillColor(getResources().getColor(R.color.main_color));
        circlePageIndicator.setStrokeColor(getResources().getColor(R.color.gray));
        circlePageIndicator.setViewPager(mViewPager);

        arcMenu = (ArcMenu) findViewById(R.id.arc_menu);


        DbHelper.getInstance(this).appendInsertListener("t_sport_record", sportRecordInsertListener);
        DbHelper.getInstance(this).appendDeleteListener("t_sport_record", sportRecordDeleteListener);
        initMenu();
    }

    private void deleteSportRecord(final int deletePostion){
        GsonRequest deleteActionRequest = new GsonRequest<>(Request.Method.POST, Config.deleteRecordUrl, SportRecord.class, new GsonRequest.PostGsonRequest<SportRecord>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(SportRecord response) {
                if(response.getError()!=null){
                    Toast.makeText(MainActivity.this, "error:"+response.getError(), Toast.LENGTH_SHORT).show();
                    return;
                }

                SportRecord sportRecord = mRecyclerViewAdapter.getItem(deletePostion);
                //删除数据库记录
                deleteSportRecordDb(sportRecord);

                //更新界面
                Message msg = new Message();
                msg.what=DELETE_SPORT;
                msg.arg1=deletePostion;
                msg.obj = sportRecord;
                handler.sendMessage(msg);

            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(MainActivity.this, "error:"+error.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                SportRecord record = mRecyclerViewAdapter.getItem(deletePostion);
                record = loadOneSportRecordById(record.getId());
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
                startActivityForResult(intent,GET_SPORT);
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
                stringPickerDialog.setEditable(false);
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
                stringPickerTwoDialog.setEditable(false);
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
        Log.e(TAG, "amount:"+amount);
        values.put("arg1", data1);
        values.put("arg2", data2);
        values.put("time", new Date().getTime() / 1000);
        values.put("userid", "" + globalInfo.getUserid());
        //long id = dbInstance.insert("t_sport_record", null, values);
        DbHelper.getInstance(this).insert("t_sport_record", null, values);
    }

    private DbInsertListener sportRecordInsertListener = new DbInsertListener() {
        @Override
        public void callback(Object obj) {
            SportRecord sportRecord = (SportRecord) obj;
            //查询上一次的更新时间
            Sport sport = loadSport(sportRecord.getSportid());
            boolean isSameDay = true;
            boolean isSameMonth = true;
            if(sport.getLastTime().equals(new Date(0))){
                isSameDay=false;
                isSameMonth=false;
            }

            if(!TimeHelper.isSameDay(sport.getLastTime(),sportRecord.getTime())) {
                sport.setDays(sport.getDays() + 1);
                isSameDay=false;
            }
            if(!TimeHelper.isSameMonth(sport.getLastTime(),sportRecord.getTime()))
                isSameMonth=false;

            sport.setLastTime(sportRecord.getTime());
            sport.setFrequency(sport.getFrequency()+1);
            sport.setTotal(sport.getTotal()+sportRecord.getAmount());
            sport.setSeq(0);
            sport.setLastValue(sportRecord.getArg1());
            sport.setLastValue2(sportRecord.getArg2());
            //日均
            sport.setAvg(sport.getTotal()/sport.getDays());
            Log.e(TAG, "insert record callback,updat sport");
            ContentValues values = new ContentValues();
            values.put("total", sport.getTotal());
            values.put("frequency", sport.getFrequency());
            values.put("avg", sport.getAvg());
            values.put("days", sport.getDays());
            values.put("lasttime", sport.getLastTime().getTime()/1000);
            values.put("seq", sport.getSeq());
            values.put("lastvalue", sport.getLastValue());
            values.put("lastvalue2", sport.getLastValue2());
            DbHelper.getInstance(MainActivity.this).getDb().update("t_sport", values,"id=? and userid=?",new String[]{sport.getId()+"",""+globalInfo.getUserid()});

            if(isSameDay){
                updateSportDay(sportRecord);
            }else {
                insertSportDay(sportRecord);
            }
            if(isSameMonth){
                updateSportMonth(sportRecord);
            }else {
                insertSportMonth(sportRecord);
            }

            Message message = new Message();
            message.what=ADD_SPORT;
            message.obj = sportRecord;
            handler.sendMessage(message);

            uploadRecord();

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

    private void updateFragments(SportRecord sportRecord){
        Log.e(TAG, "updateFragments");
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
        for(i=0;i<fragmentDatas.size();i++){
            FragmentData fragmentData = (FragmentData) fragmentDatas.get(i);
            fragmentData.setSportGather(sportGathers.get(i));
            Log.e(TAG, "total:"+sportGathers.get(i).getSport().getTotal());
            fragmentData.update();
            Log.e(TAG, "update fragment:"+fragmentData);
        }

        mFragmentPagerAdapter.setFragments(fragmentDatas);
        mFragmentPagerAdapter.notifyDataSetChanged();
        updateMenu();
    }

    private void updateFragmentsIfFirst(SportRecord sportRecord){
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

            mFragmentPagerAdapter.setFragments(fragmentDatas);
            mFragmentPagerAdapter.notifyDataSetChanged();
        }
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
        long time=TimeHelper.getDateStartOfDay().getTime() / 1000;
        values.put("time", time);
        values.put("userid", ""+globalInfo.getUserid());
        DbHelper.getInstance(this).insert("t_sport_record_day", null, values);
    }
    private void updateSportDay(SportRecord record){
        long time=TimeHelper.getDateStartOfDay().getTime() / 1000;
        ContentValues values = new ContentValues();
        String sql = "update t_sport_record_day set amount=amount+"+record.getAmount()+" where sportid="+record.getSportid()+" and time="+time+" and userid="+globalInfo.getUserid();
        DbHelper.getInstance(this).getDb().execSQL(sql);
    }

    private void deleteSportRecordDb(SportRecord record){
        Log.e(TAG, "delete record:"+record.getId());
        DbHelper.getInstance(this).delete("t_sport_record", "id=?",new String[]{record.getId()+""},record);
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
        long time = TimeHelper.getDateStartOfMonth().getTime()/1000;
        values.put("time", time);
        values.put("userid", ""+globalInfo.getUserid());
        DbHelper.getInstance(this).insert("t_sport_record_month", null, values);
    }
    private void updateSportMonth(SportRecord record){
        long time = TimeHelper.getDateStartOfMonth().getTime()/1000;
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
                    , cr.getFloat(9), cr.getFloat(10), cr.getInt(11), new Date(cr.getLong(12)*1000), cr.getInt(13), cr.getInt(14), cr.getInt(15));
        }
        sportMap.put(sport.getId(),sport);
        cr.close();
        return sport;
    }

    private SportGather loadSportGatherFromDb(int sportid){
        return new SportGather(loadSportFromDb(sportid),loadSportRecordById(sportid));
    }

    private void loadDatas(){
        SQLiteDatabase db = DbHelper.getInstance(this).getDb();

        //id int primary key,name text not null,image text not null,type int not null,unit text not null,unit2 text null,int maxnum not null,frequency int not null default 0)");
        Cursor cr = db.rawQuery("select * from t_sport where userid="+globalInfo.getUserid()+" order by lasttime desc,id limit 5", null);
        while (cr.moveToNext()){
            //int id, String name, String image,int kind,String unit,String unit2,int maxnum,int frequency) {
            /*
            db.execSQL("create table t_sport(id int primary key,name text not null,image text not null,kind int not null,unit text not null,maxnum int not null" +
                    ",unit2 text null,maxnum2 int not null,frequency int default 0,total real default 0,avg real not null,days int not null," +
                    "lasttime int NOT NULL,seq int not null default 0,lastvalue int default 0,lastvalue2 int default 0)");
                    */
            Sport sport = new Sport(cr.getInt(0),cr.getString(1),cr.getString(2),cr.getInt(3),cr.getString(4),cr.getInt(5),cr.getString(6),cr.getInt(7),cr.getInt(8)
            ,cr.getFloat(9),cr.getFloat(10),cr.getInt(11),new Date(cr.getLong(12)*1000),cr.getInt(13),cr.getInt(14),cr.getInt(15));
            sportMap.put(sport.getId(), sport);
            sportGathers.add(new SportGather(sport, loadSportRecordById(sport.getId())));
            Log.e(TAG, "load sport:" + sport.getName());
        }
        Log.e(TAG, "load sports:"+sportGathers.size());
        cr.close();
    }

    //过去7天数据汇总
    private List<Float> loadSportRecordById(int sportid){

        SQLiteDatabase db = DbHelper.getInstance(this).getDb();
        Cursor cr = db.rawQuery("select amount,time from t_sport_record_day where sportid="+sportid+" and date(time,'unixepoch','localtime')>date('now','-7 day') order by time ", null);
        List<Float> amounts = new ArrayList<>();
        Date lastTime = TimeHelper.getDateStartOfDay(-6);
        int index=0;
        while (cr.moveToNext()){
            float amount = cr.getInt(0);
            Date time = new Date(cr.getLong(1)*1000);
            int deltaDays = TimeHelper.getDateDelta(lastTime,time);
            for(int i=0;i<deltaDays;i++)
                amounts.add(0f);
            amounts.add(amount);
            lastTime=TimeHelper.getDateStartOfDay(time,1);
        }
        cr.close();
        int size = 7-amounts.size();
        for(int i=0;i<size;i++)
            amounts.add(0f);
        StringBuilder builder = new StringBuilder();
        /*
        builder.append("sportid:" + sportid + ": ");
        for(int i=0;i<amounts.size();i++)
            builder.append(amounts.get(i)+",");
        Log.e(TAG, builder.toString());
        */
        return amounts;
    }

    private void loadSportRecord(){

        SQLiteDatabase db = DbHelper.getInstance(this).getDb();
        Cursor cr = db.rawQuery("select * from t_sport_record where datetime(time)>=datetime('now') order by time desc", null);
        while (cr.moveToNext()){
            //t_sport_record(id int primary key,sportid int not null,amount real not null,arg1 int not null default 0,arg2 int not null default 0,time int NOT NULL,seq int not null default 0)");
            SportRecord sportRecord = new SportRecord(cr.getInt(0),cr.getInt(1),cr.getFloat(2),cr.getInt(3),cr.getInt(4),cr.getInt(6),new Date(cr.getLong(5)*1000),cr.getInt(7));
            mRecyclerViewAdapter.append(sportRecord);
        }
        mRecyclerViewAdapter.notifyDataSetChanged();
        cr.close();
    }

    private SportRecord loadOneSportRecordById(int id){

        SQLiteDatabase db = DbHelper.getInstance(this).getDb();
        Cursor cr = db.rawQuery("select * from t_sport_record where id="+id, null);
        SportRecord sportRecord = null;
        while (cr.moveToNext()){
            //t_sport_record(id int primary key,sportid int not null,amount real not null,arg1 int not null default 0,arg2 int not null default 0,time int NOT NULL,seq int not null default 0)");
            sportRecord = new SportRecord(cr.getInt(0),cr.getInt(1),cr.getFloat(2),cr.getInt(3),cr.getInt(4),cr.getInt(6),new Date(cr.getLong(5)*1000),cr.getInt(7));
        }
        cr.close();
        return sportRecord;
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
                    //记录userid
                    globalInfo.setUserid(userid);
                    SharedPreferences baseInfo = getSharedPreferences("BaseInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = baseInfo.edit();
                    editor.putInt("userid", userid);
                    editor.commit();

                    //更新userid为0的记录
                    String sql = "update t_sport set userid="+userid+" where userid=0";
                    dbInstance.execSQL(sql);
                    sql = "update t_sport_record set userid="+userid+" where userid=0";
                    dbInstance.execSQL(sql);
                    sql = "update t_sport_record_day set userid="+userid+" where userid=0";
                    dbInstance.execSQL(sql);
                    sql = "update t_sport_record_month set userid="+userid+" where userid=0";
                    dbInstance.execSQL(sql);

                    uploadRecord();
                    break;
                default:
                    break;
            }
        }
    }

    private void uploadRecord(){
        String sql = "select * from t_sport_record where seq=0;";
        //select * from t_sport_record;
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
            Log.e(TAG, "upload time:"+time);
            SportRecord record = new SportRecord(id,sportid,amount,arg1,arg2,seq,time,globalInfo.getUserid());
            sportRecords.add(record);
        }
        cr.close();

        Type type = new TypeToken<ArrayListResponse<SportRecord>>() {}.getType();
        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, Config.uploadRecordUrl, type, new GsonRequest.PostGsonRequest<ArrayListResponse>() {
            @Override
            public void onStart() {
                //显示同步转圈圈动画
            }
            @Override
            public void onResponse(ArrayListResponse response) {
                Log.e(TAG, "response:" + response.toString());
                if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                    Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                    handler.sendEmptyMessage(UPLOAD_RECORD_ERROR);
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
                    handler.sendEmptyMessage(UPLOAD_RECORD_DONE);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "同步记录, 网络错误:"+error);
                handler.sendEmptyMessage(UPLOAD_RECORD_ERROR);
            }

            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                Log.e(TAG, "getPostData");
                datas.put("records",TimeHelper.gsonWithDate().toJson(sportRecords));
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
        if(globalInfo.getUserid()==0)
            setSyncMenuVisible(true);
        return true;
    }

    private boolean hasNotSyncRecord(){
        String sql = "select count(*) from t_sport_record where seq=0;";
        //select * from t_sport_record;
        Cursor cr = dbInstance.rawQuery(sql, null);

        if(cr.moveToNext()){
            int count = cr.getInt(0);
            Log.e(TAG, "not sync record size:"+count);
            if(count>0)
                return true;
            else
                return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.sync:
                if(globalInfo.getUserid()==0){
                    Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                    startActivityForResult(intent, LOGIN_REGISTER);
                }else {
                    uploadRecord();
                }
                break;
            case R.id.feedback:
                Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.setting:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
