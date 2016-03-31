package com.lessask.dongfou;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.lessask.dongfou.net.GsonRequest;
import com.lessask.dongfou.net.VolleyHelper;
import com.lessask.dongfou.util.DbHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StartupActivity extends AppCompatActivity {
    private String TAG = StartupActivity.class.getSimpleName();
    private SharedPreferences baseInfo;
    private final int HANDLER_MAIN = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "login handler:"+msg.what);
            switch (msg.what){
                case HANDLER_MAIN:
                    Intent intent = new Intent(StartupActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        }
        baseInfo = getSharedPreferences("BaseInfo", MODE_PRIVATE);



        //判断是否是第一次启动
        if(!baseInfo.getBoolean("initDb", false)){
            initDb(baseInfo);
        }
        loadSports(DbHelper.getInstance(this).getDb());

        handler.postDelayed(
        new Runnable(){
            @Override
            public void run() {
                handler.sendEmptyMessage(HANDLER_MAIN);
            }
        },2000);
    }


    private void initDb(SharedPreferences baseInfo){
        SharedPreferences.Editor editor = baseInfo.edit();
        //初始化数据库
        SQLiteDatabase db = DbHelper.getInstance(getBaseContext()).getDb();
        //获取基础信息
        //运动类型信息
        //unit: 运动单位
        //unit2: 运动单位, 如深蹲 30次, 40千克/次, 那unit2为千克
        //frequency: 该类型的活动频次
        //运动汇总
        //total:总数
        //avg:日均
        //lasttime:最后一次运动时间
        //times:总运动天数
        //seq:服务器同步状态, 0表示未同步, 1:表示已同步
        db.execSQL("create table t_sport(id int primary key,name text not null,image text not null,kind int not null,unit text not null,maxnum int not null" +
                    ",unit2 text null,maxnum2 int null,frequency int default 0,total real default 0,avg real default 0,days int default 0," +
                    "lasttime int default 0,seq int default 0,lastvalue int default 0,lastvalue2 int default 0)");
        //运动记录
        //arg1: 第一个单位下的数据
        //arg2: 第二个单位下的数据
        //详细运动记录
        db.execSQL("create table t_sport_record(id int primary key,sportid int not null,amount real not null,arg1 int not null default 0,arg2 int not null default 0,time int NOT NULL,seq int not null default 0)");
        //每天的汇总表
        db.execSQL("create table t_sport_record_day(id int primary key,sportid int not null,amount real not null,time int NOT NULL,seq int not null default 0)");
        //每月的汇总表
        db.execSQL("create table t_sport_record_month(id int primary key,sportid int not null,amount real not null,time int NOT NULL,seq int not null default 0)");

        Log.e(TAG, "create db");

        editor.putBoolean("initDb", true);
        editor.commit();
    }

    private void loadSports(final SQLiteDatabase db){
        Type type = new TypeToken<ArrayListResponse<Sport>>() {}.getType();
        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, Config.sportUrl, type, new GsonRequest.PostGsonRequest<ArrayListResponse>() {
            @Override
            public void onStart() {
                Log.e(TAG, "start "+Config.sportUrl);
            }
            @Override
            public void onResponse(ArrayListResponse response) {
                Log.e(TAG, "response:" + response.toString());
                if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                    Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                }else {
                    ArrayList<Sport> datas = response.getDatas();
                    //入库,本地化
                    for(int i=0;i<datas.size();i++){
                        Sport sport = datas.get(i);

                        ContentValues values = new ContentValues();
                        values.put("id", sport.getId());
                        values.put("name", sport.getName());
                        values.put("image", sport.getImage());
                        values.put("kind", sport.getKind());
                        values.put("unit", sport.getUnit());
                        values.put("maxnum", sport.getMaxnum());
                        values.put("unit2", sport.getUnit2());
                        values.put("maxnum2", sport.getMaxnum2());
                        values.put("frequency", sport.getFrequency());

                        Cursor cr = db.rawQuery("select 1 from t_sport where id="+sport.getId(),null);
                        if(cr.getCount()==0) {
                            db.insert("t_sport", "", values);
                            Log.e(TAG, "insert t_sport:" + sport.getName());
                        }else{
                            db.update("t_sport", values,"id=?",new String[]{sport.getId()+""});
                            Log.e(TAG, "update t_sport:" + sport.getName());
                        }
                    }
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(StartupActivity.this, "网络错误:加载运动列表失败" + error, Toast.LENGTH_SHORT);
            }

            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                Log.e(TAG, "getPostData");
                return datas;
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }
}