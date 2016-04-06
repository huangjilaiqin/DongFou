package com.lessask.dongfou.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.lessask.dongfou.SportRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    private static int DB_VERSION = 2;

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
        if(oldVersion==1){
            //增加userid字段
            Log.e(TAG ,"db onUpgrade version:"+oldVersion);
            db.execSQL("alter table t_sport add column userid integer not null default 0");
            db.execSQL("alter table t_sport_record add column userid integer not null default 0");
            db.execSQL("alter table t_sport_record_day add column userid integer not null default 0");
            db.execSQL("alter table t_sport_record_month add column userid integer not null default 0");
        }
    }

    public void appendInsertListener(String table, DbInsertListener listener){
        if(!insertCallbacks.containsKey(table))
            insertCallbacks.put(table, new ArrayList<DbInsertListener>());
        insertCallbacks.get(table).add(listener);
    }
    public void removeInsertListener(String table,DbInsertListener listener){
        ArrayList list = insertCallbacks.get(table);
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
                obj = new SportRecord(0,values.getAsInteger("sportid"),values.getAsFloat("amount"),values.getAsInteger("arg1"),values.getAsInteger("arg2"),0,new Date(values.getAsLong("time")*1000),values.getAsInteger("userid"));
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
}
