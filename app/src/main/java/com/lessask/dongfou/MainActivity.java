package com.lessask.dongfou;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

//import com.getbase.floatingactionbutton.FloatingActionButton;
import com.android.volley.toolbox.ImageLoader;
import com.capricorn.ArcMenu;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.lessask.dongfou.dialog.StringPickerDialog;
import com.lessask.dongfou.dialog.StringPickerTwoDialog;
import com.lessask.dongfou.net.VolleyHelper;
import com.lessask.dongfou.util.DbHelper;
import com.lessask.dongfou.util.DbInsertListener;
import com.lessask.dongfou.util.TimeHelper;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private XRecyclerView mRecyclerView;
    private SportRecordAdapter mRecyclerViewAdapter;

    private FragmentPagerAdapter mFragmentPagerAdapter;
    private ViewPager mViewPager;
    private FloatingActionsMenu menu;
    private ArcMenu arcMenu;

    private final String TAG = MainActivity.class.getSimpleName();
    private final int GET_SPORT = 1;

    private ArrayList<Sport> sports;
    private Map<Integer,Sport> sportMap;
    private ArrayList<SportGather> sportGathers;
    private ArrayList<FragmentData> fragmentDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sports = new ArrayList<>();
        sportMap = new HashMap<>();
        sportGathers = new ArrayList<>();

        loadDatas();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("动否");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);

        mRecyclerView = (XRecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        View mHeaderView = LayoutInflater.from(this).inflate(R.layout.data_header,mRecyclerView,false);

        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager());
        fragmentDatas = new ArrayList<>();
        for(int i=0;i<5;i++) {
            FragmentData fragmentData = new FragmentData();
            fragmentData.setSportGather(sportGathers.get(i));
            mFragmentPagerAdapter.addFragment(fragmentData, "");
            fragmentDatas.add(fragmentData);
        }
        mViewPager = (ViewPager)mHeaderView.findViewById(R.id.viewpager);
        mViewPager.setAdapter(mFragmentPagerAdapter);

        mRecyclerView.addHeaderView(mHeaderView);
        mRecyclerViewAdapter = new SportRecordAdapter(this);
        mRecyclerViewAdapter.setSportMap(sportMap);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        loadSportRecord();
        DbHelper.getInstance(getBaseContext()).appendInsertListener("t_sport_record", chatRecorInsertListener);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator)mHeaderView.findViewById(R.id.indicator);
        circlePageIndicator.setFillColor(getResources().getColor(R.color.main_color));
        circlePageIndicator.setStrokeColor(getResources().getColor(R.color.gray));
        circlePageIndicator.setViewPager(mViewPager);

        arcMenu = (ArcMenu) findViewById(R.id.arc_menu);
        /*
        menu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        FloatingActionButton buttona = (FloatingActionButton) findViewById(R.id.action_a);
        initFloatingActionButton(buttona,sports.get(0));
        FloatingActionButton buttonb = (FloatingActionButton) findViewById(R.id.action_b);
        initFloatingActionButton(buttonb,sports.get(1));
        FloatingActionButton buttonc = (FloatingActionButton) findViewById(R.id.action_c);
        initFloatingActionButton(buttonc,sports.get(2));
        FloatingActionButton buttond = (FloatingActionButton) findViewById(R.id.action_d);
        initFloatingActionButton(buttond,sports.get(3));
        */

        DbHelper.getInstance(this).appendInsertListener("t_sport_record", sportRecordInsertListener);
        initMenu();
    }

    private void initMenu(){
        /*
        ImageView fabContent = new ImageView(this);
        fabContent.setImageDrawable(getResources().getDrawable(R.drawable.button_action));

        FloatingActionButton darkButton = new FloatingActionButton.Builder(this)
                .setTheme(FloatingActionButton.THEME_DARK)
                .setContentView(fabContent)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_CENTER)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this)
                .setTheme(SubActionButton.THEME_DARK);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);
        ImageView rlIcon4 = new ImageView(this);
        ImageView rlIcon5 = new ImageView(this);

        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.button_action));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.button_action));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.button_action));
        rlIcon4.setImageDrawable(getResources().getDrawable(R.drawable.button_action));
        rlIcon5.setImageDrawable(getResources().getDrawable(R.drawable.button_action));

        // Set 4 SubActionButtons
        FloatingActionMenu centerBottomMenu = new FloatingActionMenu.Builder(this)
                .setStartAngle(0)
                .setEndAngle(-180)
                .setAnimationHandler(new SlideInAnimationHandler())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon4).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon5).build())
                .attachTo(darkButton)
                .build();
                */

        for (int i = 0; i < 5; i++) {
            ImageView item = new ImageView(this);
            //item.setImageResource(R.drawable.button_action);
            final Sport sport = sports.get(i);
            String headImgUrl = Config.imagePrefix+sport.getImage();
            ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(item, 0, 0);
            VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener, 100, 100);

            final int position = i;
            arcMenu.addItem(item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
                    switch (sport.getKind()) {
                        case 1:
                            StringPickerDialog stringPickerDialog = new StringPickerDialog(MainActivity.this, sport.getName(), sport.getMaxnum(), (int) sport.getAvg(), sport.getUnit(), new StringPickerDialog.OnSelectListener() {
                                @Override
                                public void onSelect(int data) {
                                    //Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                                    addSportRecord(sport.getId(), data, 0);
                                }
                            });
                            stringPickerDialog.setEditable(false);
                            stringPickerDialog.show();
                            break;
                        case 2:
    //public StringPickerTwoDialog(Context context,String title,int maxNumber,int initValue,String unit,int maxNumber2,int initValue2,String uint2, OnSelectListener mSelectCallBack) {
                            StringPickerTwoDialog stringPickerTwoDialog = new StringPickerTwoDialog(MainActivity.this, sport.getName(), sport.getMaxnum(), sport.getLastValue(), sport.getUnit(), sport.getMaxnum2(), sport.getLastValue2(), sport.getUnit2(), new StringPickerTwoDialog.OnSelectListener() {
                                @Override
                                public void onSelect(int data, int data2) {
                                    Toast.makeText(MainActivity.this, data + ", " + data2, Toast.LENGTH_SHORT).show();
                                    addSportRecord(sport.getId(), data, data2);
                                }
                            });
                            stringPickerTwoDialog.setEditable(false);
                            stringPickerTwoDialog.show();
                            break;
                        }
                }
            });
        }
    }

    private void initFloatingActionButton(FloatingActionButton button, final Sport sport){
        //button.setSize(com.android.FloatingActionButton.SIZE_MINI);
        //button.setColorNormalResId(R.color.colorAccent);
        //button.setColorPressedResId(R.color.colorPrimary);
        //button.setIcon(R.drawable.done);
        //button.setStrokeVisible(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.collapse();
                switch (sport.getKind()) {
                    case 1:
                        StringPickerDialog stringPickerDialog = new StringPickerDialog(MainActivity.this, sport.getName(), sport.getMaxnum(), (int) sport.getAvg(), sport.getUnit(), new StringPickerDialog.OnSelectListener() {
                            @Override
                            public void onSelect(int data) {
                                //Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                                addSportRecord(sport.getId(), data, 0);
                            }
                        });
                        stringPickerDialog.setEditable(false);
                        stringPickerDialog.show();
                        break;
                    case 2:
//public StringPickerTwoDialog(Context context,String title,int maxNumber,int initValue,String unit,int maxNumber2,int initValue2,String uint2, OnSelectListener mSelectCallBack) {
                        StringPickerTwoDialog stringPickerTwoDialog = new StringPickerTwoDialog(MainActivity.this, sport.getName(), sport.getMaxnum(), sport.getLastValue(), sport.getUnit(), sport.getMaxnum2(), sport.getLastValue2(), sport.getUnit2(), new StringPickerTwoDialog.OnSelectListener() {
                            @Override
                            public void onSelect(int data, int data2) {
                                Toast.makeText(MainActivity.this, data + ", " + data2, Toast.LENGTH_SHORT).show();
                                addSportRecord(sport.getId(), data, data2);
                            }
                        });
                        stringPickerTwoDialog.setEditable(false);
                        stringPickerTwoDialog.show();
                        break;
                }

            }
        });
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
            DbHelper.getInstance(MainActivity.this).getDb().update("t_sport", values,"id=?",new String[]{sport.getId()+""});

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

            updateFragments(sportRecord);
            mViewPager.setCurrentItem(0);
            mRecyclerView.scrollToPosition(0);
        }
    };

    private void updateFragments(SportRecord sportRecord){
        int i=0;
        for (;i<sportGathers.size();i++){
            SportGather sportGather = sportGathers.get(i);
            if(sportGather.getSport().getId()==sportRecord.getSportid()){
                sportGathers.remove(i);
                break;
            }
        }
        //不包含的情况
        if(i==sportGathers.size()){
            int pos = sportGathers.size()-1;
            sportGathers.remove(pos);
        }
        sportGathers.add(0, loadSportGatherFromDb(sportRecord.getSportid()));
        //通知fragment更新
        for(i=0;i<fragmentDatas.size();i++){
            FragmentData fragmentData = fragmentDatas.get(i);
            fragmentData.setSportGather(sportGathers.get(i));
            fragmentData.update();
        }
    }

    private void insertSportDay(SportRecord record){
        ContentValues values = new ContentValues();
        values.put("sportid", record.getSportid());
        values.put("amount", record.getAmount());
        //自1970年后的秒数
        long time=TimeHelper.getDateStartOfDay().getTime() / 1000;
        Log.e(TAG, "insertSportDay:"+time);
        values.put("time", time);
        DbHelper.getInstance(this).insert("t_sport_record_day", null, values);
    }
    private void updateSportDay(SportRecord record){
        long time=TimeHelper.getDateStartOfDay().getTime() / 1000;
        Log.e(TAG, "updateSportDay:" + time);
        String sql = "update t_sport_record_day set amount=amount+"+record.getAmount()+" where sportid="+record.getSportid()+" and time="+time;
        DbHelper.getInstance(this).getDb().execSQL(sql);
    }

    private void insertSportMonth(SportRecord record){
        ContentValues values = new ContentValues();
        values.put("sportid", record.getSportid());
        values.put("amount", record.getAmount());
        long time = TimeHelper.getDateStartOfMonth().getTime()/1000;
        Log.e(TAG, "insertSportMonth:"+time);
        values.put("time", time);
        DbHelper.getInstance(this).insert("t_sport_record_month", null, values);
    }
    private void updateSportMonth(SportRecord record){
        long time = TimeHelper.getDateStartOfMonth().getTime()/1000;
        Log.e(TAG, "updateSportMonth:"+time);
        String sql = "update t_sport_record_month set amount=amount+"+record.getAmount()+" where sportid="+record.getSportid()+" and time="+time;
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
        Cursor cr = db.rawQuery("select * from t_sport order by lasttime desc,id limit 5", null);
        while (cr.moveToNext()){
            //int id, String name, String image,int kind,String unit,String unit2,int maxnum,int frequency) {
            /*
            db.execSQL("create table t_sport(id int primary key,name text not null,image text not null,kind int not null,unit text not null,maxnum int not null" +
                    ",unit2 text null,maxnum2 int not null,frequency int default 0,total real default 0,avg real not null,days int not null," +
                    "lasttime int NOT NULL,seq int not null default 0,lastvalue int default 0,lastvalue2 int default 0)");
                    */
            Sport sport = new Sport(cr.getInt(0),cr.getString(1),cr.getString(2),cr.getInt(3),cr.getString(4),cr.getInt(5),cr.getString(6),cr.getInt(7),cr.getInt(8)
            ,cr.getFloat(9),cr.getFloat(10),cr.getInt(11),new Date(cr.getLong(12)*1000),cr.getInt(13),cr.getInt(14),cr.getInt(15));
            sports.add(sport);
            sportMap.put(sport.getId(), sport);
            sportGathers.add(new SportGather(sport,loadSportRecordById(sport.getId())));
        }
        cr.close();
    }

    //过去7天数据汇总
    private List<Float> loadSportRecordById(int sportid){

        SQLiteDatabase db = DbHelper.getInstance(this).getDb();
        Cursor cr = db.rawQuery("select amount,time from t_sport_record_day where sportid="+sportid+" and date(time,'unixepoch','localtime')>date('now','-7 day') order by time ", null);
        Log.e(TAG, "record size:"+cr.getCount());
        List<Float> amounts = new ArrayList<>();
        Date lastTime = TimeHelper.getDateStartOfDay(-6);
        Log.e(TAG, "lasttime: "+lastTime.toString());
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
        builder.append("sportid:" + sportid + ": ");
        for(int i=0;i<amounts.size();i++)
            builder.append(amounts.get(i)+",");
        Log.e(TAG, builder.toString());
        return amounts;
    }

    private void loadSportRecord(){

        SQLiteDatabase db = DbHelper.getInstance(this).getDb();
        Cursor cr = db.rawQuery("select * from t_sport_record where datetime(time)>=datetime('now') order by time desc", null);
        Log.e(TAG, "record size:"+cr.getCount());
        while (cr.moveToNext()){
            //t_sport_record(id int primary key,sportid int not null,amount real not null,arg1 int not null default 0,arg2 int not null default 0,time int NOT NULL,seq int not null default 0)");
            SportRecord sportRecord = new SportRecord(cr.getInt(0),cr.getInt(1),cr.getFloat(2),cr.getInt(3),cr.getInt(4),cr.getInt(6),new Date(cr.getLong(5)*1000));
            mRecyclerViewAdapter.append(sportRecord);
        }
        mRecyclerViewAdapter.notifyDataSetChanged();
        cr.close();
    }

    private DbInsertListener chatRecorInsertListener = new DbInsertListener() {
        @Override
        public void callback(Object obj) {
            SportRecord sportRecord = (SportRecord)obj;
            mRecyclerViewAdapter.appendToTop(sportRecord);
            mRecyclerViewAdapter.notifyItemInserted(0);
            Log.e(TAG, "insert callback");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode);
        if(resultCode==RESULT_OK) {
            switch (requestCode) {
                case GET_SPORT:
                    int sportid = data.getIntExtra("sportid", -1);
                    Log.e(TAG, "sportid:" + sportid);
                    break;
                default:
                    Log.e(TAG, "not match requestCode:"+requestCode);
                    break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.me:
                break;
            case R.id.setting:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
