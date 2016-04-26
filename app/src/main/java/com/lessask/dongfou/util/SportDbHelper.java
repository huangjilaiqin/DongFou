package com.lessask.dongfou.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lessask.dongfou.Sport;
import com.lessask.dongfou.SportRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by JHuang on 2016/4/24.
 */
public class SportDbHelper {
    private Context context;
    private GlobalInfo globalInfo = GlobalInfo.getInstance();
    private SharedPreferences baseInfo;
    private SharedPreferences.Editor editor;

    private String TAG = SportDbHelper.class.getSimpleName();

    public SportDbHelper(Context context){
        this.context=context;
        baseInfo = context.getSharedPreferences("BaseInfo", Context.MODE_PRIVATE);
        editor = baseInfo.edit();
    }


    public void addSportRecord(int sportid,float data1,float data2){
        //create table t_sport_record(id int primary key,amount real not null,int arg1 not null default 0,int arg2 not null default 0,time int NOT NULL,seq int not null default 0)");
        ContentValues values = new ContentValues();
        Sport sport = loadSport(sportid);
        //Sport sport = sportMap.get(sportid);

        values.put("sportid", sportid);
        float amount = data1;
        if(sport.getKind()%2==0){
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
        DbHelper.getInstance(context).insert("t_sport_record", null, values);

        /*
        if(globalInfo.getUserid()==0 && !baseInfo.getBoolean("logintip", false)){
            editor.putBoolean("logintip", true);
            editor.commit();
        }
        */
    }

    private Sport loadSport(int sportid){
        Sport sport = loadSportFromDb(sportid);
        return sport;
    }
    private Sport loadSportFromDb(int sportid){
        Sport sport = null;
        SQLiteDatabase db = DbHelper.getInstance(context).getDb();
        Cursor cr = db.rawQuery("select * from t_sport where id=" + sportid, null);
        while (cr.moveToNext()) {
            sport = new Sport(cr.getInt(0), cr.getString(1), cr.getString(2), cr.getInt(3), cr.getString(4), cr.getInt(5), cr.getString(6), cr.getInt(7), cr.getInt(8)
                    , cr.getFloat(9), cr.getFloat(10), cr.getInt(11), new Date(cr.getLong(12)*1000), cr.getInt(13), cr.getInt(14), cr.getInt(15));
        }
        cr.close();
        return sport;
    }

    public Sport loadWeightFromDbById(int sportid){
        Sport sport = null;
        SQLiteDatabase db = DbHelper.getInstance(context).getDb();
        Cursor cr = db.rawQuery("select * from t_sport where id=" + sportid, null);
        while (cr.moveToNext()) {
            sport = new Sport(cr.getInt(0), cr.getString(1), cr.getString(2), cr.getInt(3), cr.getString(4), cr.getInt(5), cr.getString(6), cr.getInt(7), cr.getInt(8)
                    , cr.getFloat(9), cr.getFloat(10), cr.getInt(11), new Date(cr.getLong(12)*1000), cr.getInt(13), cr.getInt(14), cr.getInt(15));
        }
        cr.close();
        return sport;
    }

    public List<SportRecord> loadSportRecord(){

        SQLiteDatabase db = DbHelper.getInstance(context).getDb();
        Cursor cr = db.rawQuery("select * from t_sport_record where userid=" + globalInfo.getUserid() + " and datetime(time)>=datetime('now') order by time desc", null);
        ArrayList<SportRecord> sportRecords=new ArrayList<>();
        while (cr.moveToNext()){
            //t_sport_record(id int primary key,sportid int not null,amount real not null,arg1 int not null default 0,arg2 int not null default 0,time int NOT NULL,seq int not null default 0)");
            SportRecord sportRecord = new SportRecord(cr.getInt(0),cr.getInt(1),cr.getFloat(2),cr.getInt(3),cr.getInt(4),cr.getInt(6),new Date(cr.getLong(5)*1000),cr.getInt(7));
            sportRecords.add(sportRecord);
        }
        cr.close();
        return sportRecords;
    }

    public List<SportRecord> loadWeightRecord(){
        SQLiteDatabase db = DbHelper.getInstance(context).getDb();
        Cursor cr = db.rawQuery("select r.* from t_sport as s inner join t_sport_record as r on (s.id=r.sportid) where r.userid=" + globalInfo.getUserid() + " and s.kind=3 order by r.time", null);
        // select r.* from t_sport as s inner join t_sport_record as r where s.kind=3 order by r.time desc
        ArrayList<SportRecord> sportRecords=new ArrayList<>();
        while (cr.moveToNext()){
            SportRecord sportRecord = new SportRecord(cr.getInt(0),cr.getInt(1),cr.getFloat(2),cr.getInt(3),cr.getInt(4),cr.getInt(6),new Date(cr.getLong(5)*1000),cr.getInt(7));
            sportRecords.add(sportRecord);
        }
        cr.close();
        return sportRecords;
    }

    public ArrayList<Sport> loadWeightFromDb(int userid){
        Sport sport = null;
        SQLiteDatabase db = DbHelper.getInstance(context).getDb();
        ArrayList<Sport> sports =new ArrayList<>();
        Cursor cr = db.rawQuery("select * from t_sport where kind=3 and userid=" + userid, null);
        while (cr.moveToNext()) {
            sport = new Sport(cr.getInt(0), cr.getString(1), cr.getString(2), cr.getInt(3), cr.getString(4), cr.getInt(5), cr.getString(6), cr.getInt(7), cr.getInt(8)
                    , cr.getFloat(9), cr.getFloat(10), cr.getInt(11), new Date(cr.getLong(12)*1000), cr.getInt(13), cr.getInt(14), cr.getInt(15));
            sports.add(sport);
        }
        cr.close();
        return sports;
    }
    public List<SportRecord> loadWeightRecordById(int userid,int sportid){
        SQLiteDatabase db = DbHelper.getInstance(context).getDb();
        Cursor cr = db.rawQuery("select * from t_sport_record where userid=" + userid + " and sportid="+sportid+" order by time", null);
        ArrayList<SportRecord> sportRecords=new ArrayList<>();
        while (cr.moveToNext()){
            SportRecord sportRecord = new SportRecord(cr.getInt(0),cr.getInt(1),cr.getFloat(2),cr.getInt(3),cr.getInt(4),cr.getInt(6),new Date(cr.getLong(5)*1000),cr.getInt(7));
            sportRecords.add(sportRecord);
        }
        cr.close();
        return sportRecords;
    }

    public List<SportRecord> loadTodaySportRecord(int userid){

        SQLiteDatabase db = DbHelper.getInstance(context).getDb();
        Cursor cr = db.rawQuery("select * from t_sport_record where userid=" + userid + " and datetime(time,'unixepoch','localtime')>=datetime('now','start of day') and sportid not in (select id from t_sport where kind=3) order by time desc", null);
        ArrayList<SportRecord> sportRecords=new ArrayList<>();
        while (cr.moveToNext()){
            //t_sport_record(id int primary key,sportid int not null,amount real not null,arg1 int not null default 0,arg2 int not null default 0,time int NOT NULL,seq int not null default 0)");
            SportRecord sportRecord = new SportRecord(cr.getInt(0),cr.getInt(1),cr.getFloat(2),cr.getInt(3),cr.getInt(4),cr.getInt(6),new Date(cr.getLong(5)*1000),cr.getInt(7));
            sportRecords.add(sportRecord);
        }
        cr.close();
        return sportRecords;
    }
    public SportRecord loadSportRecordById(int id){

        SQLiteDatabase db = DbHelper.getInstance(context).getDb();
        Cursor cr = db.rawQuery("select * from t_sport_record where id="+id, null);
        SportRecord sportRecord = null;
        while (cr.moveToNext()){
            //t_sport_record(id int primary key,sportid int not null,amount real not null,arg1 int not null default 0,arg2 int not null default 0,time int NOT NULL,seq int not null default 0)");
            sportRecord = new SportRecord(cr.getInt(0),cr.getInt(1),cr.getFloat(2),cr.getInt(3),cr.getInt(4),cr.getInt(6),new Date(cr.getLong(5)*1000),cr.getInt(7));
        }
        cr.close();
        return sportRecord;
    }
}



