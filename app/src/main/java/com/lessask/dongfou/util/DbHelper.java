package com.lessask.dongfou.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.lessask.dongfou.Sport;
import com.lessask.dongfou.SportRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by laiqin on 16/2/29.
 */
public class DbHelper extends SQLiteOpenHelper{

    private String TAG = DbHelper.class.getSimpleName();
    private static Context context;
    private static SQLiteDatabase db;

    private Map<String, ArrayList<DbInsertListener>> insertCallbacks;
    private Map<String, ArrayList<DbUpdateListener>> updateCallbacks;
    private Map<String, ArrayList<DbDeleteListener>> deleteCallbacks;
    private static String DB_NAME = "lesask.db";
    private static int DB_VERSION = 6;

    public SQLiteDatabase getDb() {
        return db;
    }

    private DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        insertCallbacks = new HashMap<>();
        updateCallbacks = new HashMap<>();
        deleteCallbacks = new HashMap<>();
        db = getWritableDatabase();
    }

    public static final DbHelper getInstance(Context context){
        if(DbHelper.context==null)
            DbHelper.context = context;
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final DbHelper INSTANCE = new DbHelper(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG ,"db onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:
                //增加userid字段
                Log.e(TAG ,"db onUpgrade version:"+oldVersion);
                db.execSQL("alter table t_sport add column userid integer not null default 0");
                db.execSQL("alter table t_sport_record add column userid integer not null default 0");
                db.execSQL("alter table t_sport_record_day add column userid integer not null default 0");
                db.execSQL("alter table t_sport_record_month add column userid integer not null default 0");
            case 2:
                db.execSQL("create table t_notice(id integer primary key,kind integer not null,status integer not null default 0,title text not null,time integer not null,url text not null)");
            case 3:
                db.execSQL("alter table t_notice add column arg1 integer");
                db.execSQL("alter table t_notice add column arg2 text");
            case 4:
                db.execSQL("drop table t_notice");
                db.execSQL("create table t_notice(id integer primary key,kind integer not null,title text not null,status integer not null default 0,time integer not null,url text not null,arg1 integer,arg2 text)");
            case 5:
                db.execSQL("create table t_sport_tmp(id int,name text not null,image text not null,kind int not null,unit text not null,maxnum int not null" +
                        ",unit2 text null,maxnum2 int null,frequency int default 0,total real default 0,avg real default 0,days int default 0," +
                        "lasttime int default 0,seq int default 0,lastvalue real default 0,lastvalue2 real default 0,userid int not null default 0 , primary key(id, userid))");
                db.execSQL("insert into t_sport_tmp select * from t_sport;");
                db.execSQL("drop table t_sport;");
                db.execSQL("create table t_sport as select * from t_sport_tmp;");

        }
    }

    public void appendInsertListener(String table, DbInsertListener listener){
        if(!insertCallbacks.containsKey(table))
            insertCallbacks.put(table, new ArrayList<DbInsertListener>());
        insertCallbacks.get(table).add(listener);
    }
    public void clearInsertListener(String table){
        List listeners = insertCallbacks.get(table);
        if(listeners!=null)
            listeners.clear();
    }

    public void removeInsertListener(String table,DbInsertListener listener){
        ArrayList list = insertCallbacks.get(table);
        if(list!=null)
            list.remove(listener);
    }
    public void removeDeleteListener(String table,DbDeleteListener listener){
        ArrayList list = deleteCallbacks.get(table);
        if(list!=null)
            list.remove(listener);
    }
    public void appendUpdateListener(String table,DbUpdateListener listener){
        if(!updateCallbacks.containsKey(table))
            updateCallbacks.put(table, new ArrayList<DbUpdateListener>());
        updateCallbacks.get(table).add(listener);
    }
    public void appendDeleteListener(String table,DbDeleteListener listener){
        if(!deleteCallbacks.containsKey(table))
            deleteCallbacks.put(table, new ArrayList<DbDeleteListener>());
        deleteCallbacks.get(table).add(listener);
    }

    public void insert(String table,String nullColumnHack,ContentValues values){
        Object obj=null;
        switch (table){
            case "t_sport_record":
                obj = new SportRecord(0,values.getAsInteger("sportid"),values.getAsFloat("amount"),values.getAsFloat("arg1"),values.getAsFloat("arg2"),values.getAsInteger("seq"),new Date(values.getAsLong("time")*1000),values.getAsInteger("userid"));
                break;
        }
        long rowId = db.insert(table,nullColumnHack,values);
        Log.e(TAG, "insert table:"+table+", rowid:"+rowId);
        if(obj instanceof SportRecord)
            ((SportRecord) obj).setId((int)rowId);

        if(insertCallbacks.containsKey(table)) {
            Log.e(TAG, table + "DbInsertListener size:"+insertCallbacks.get(table).size());
            for (DbInsertListener listener : insertCallbacks.get(table)) {
                listener.callback(obj);
            }
        }
    }

    public void delete(String table,String whereClause,String[] whereArgs,Object obj){
        switch (table){
            case "t_sport_record":
                //obj = new SportRecord(0,values.getAsInteger("sportid"),values.getAsFloat("amount"),values.getAsInteger("arg1"),values.getAsInteger("arg2"),0,new Date(values.getAsLong("time")*1000),values.getAsInteger("userid"));
                break;
        }
        Log.e(TAG, "delete record:"+whereClause+"， "+whereArgs[0]);
        db.delete(table, whereClause, whereArgs);

        if(deleteCallbacks.containsKey(table)) {
            for (DbDeleteListener listener : deleteCallbacks.get(table)) {
                listener.callback(obj);
            }
        }
    }
    public static boolean isChatgroupExist(String chatgoupId){
        boolean isExist = false;
        Cursor cursor = db.rawQuery("select 1 from t_chatgroup where chatgroup_id=?", new String[]{chatgoupId});
        if(cursor.getCount()==1){
            isExist=true;
        }
        return isExist;
    }

    /*
    public static List<ChatMessage> getChatMessage(String chatgroupId, int num){
        String sql = "select * from t_chatrecord where chatgroup_id=? order by id desc limit ?";
        Cursor cursor = db.rawQuery(sql, new String[]{chatgroupId,num+""});
        int count = cursor.getCount();
        ArrayList<ChatMessage> list = new ArrayList<>();
        cursor.moveToLast();
        do {
            int id = cursor.getInt(0);
            int seq = cursor.getInt(1);
            int userid = cursor.getInt(2);
            //String chatgroupId = cursor.getString(3);
            int type = cursor.getInt(4);
            String content = cursor.getString(5);
            Date time = TimeHelper.dateParse(cursor.getString(6));
            int status = cursor.getInt(7);

            ChatMessage chatMessage = new ChatMessage(id,seq,userid,chatgroupId,type,content,time,status);
            list.add(chatMessage);
        }while (cursor.moveToPrevious());

        return list;
    }
    */

    //修改#天#单位下的数据统计
    public static int handleSportDay(SQLiteDatabase db,SportRecord record){
        int userid = record.getUserid();
        long time=TimeHelper.getDateStartOfDay(record.getTime()).getTime() / 1000;
        Cursor cr = db.rawQuery("select * from t_sport_record_day where userid=? and sportid=? and time=?",new String[]{userid+"",record.getSportid()+"",time+""});
        //更新
        int insertRow = 0;
        if(cr.getCount()>0){
            String sql = "update t_sport_record_day set amount=amount+"+record.getAmount()+" where sportid="+record.getSportid()+" and time="+time+" and userid="+userid;
            db.execSQL(sql);
        }else {
            //插入
            ContentValues values = new ContentValues();
            values.put("sportid", record.getSportid());
            values.put("amount", record.getAmount());
            values.put("time", time);
            values.put("userid", ""+userid);
            db.insert("t_sport_record_day", null, values);
            insertRow=1;
        }
        return insertRow;
    }

    //修改#月#单位下的数据统计
    public static int handleSportMonth(SQLiteDatabase db,SportRecord record){
        int userid = record.getUserid();
        long time = TimeHelper.getDateStartOfMonth(record.getTime()).getTime()/1000;
        Cursor cr = db.rawQuery("select * from t_sport_record_month where userid=? and sportid=? and time=?",new String[]{userid+"",record.getSportid()+"",time+""});
        int insertRow = 0;
        if(cr.getCount()>0){
            String sql = "update t_sport_record_month set amount=amount+" + record.getAmount() + " where sportid=" + record.getSportid() + " and time=" + time + " and userid=" + userid + " and time=" + time;
            db.execSQL(sql);
        }else {
            ContentValues values = new ContentValues();
            values.put("sportid", record.getSportid());
            values.put("amount", record.getAmount());
            values.put("time", time);
            values.put("userid", ""+userid);
            db.insert("t_sport_record_month", null, values);
            insertRow=1;
        }
        return insertRow;
    }

    public static void addSportRecordFromServer(Context context,SportRecord sportRecord){
        //create table t_sport_record(id int primary key,amount real not null,int arg1 not null default 0,int arg2 not null default 0,time int NOT NULL,seq int not null default 0)");
        ContentValues values = new ContentValues();

        values.put("userid", "" + sportRecord.getUserid());
        values.put("sportid", sportRecord.getSportid());
        values.put("amount", ""+sportRecord.getAmount());
        values.put("arg1", "" + sportRecord.getArg1());
        values.put("arg2", ""+sportRecord.getArg2());
        values.put("time", sportRecord.getTime().getTime() / 1000);
        values.put("seq", sportRecord.getSeq());
        DbHelper.getInstance(context).insert("t_sport_record", null, values);
    }

    public static Sport loadSportFromDb(Context context,int sportid){
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
}
