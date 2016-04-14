package com.lessask.dongfou;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lessask.dongfou.util.DbHelper;
import com.lessask.dongfou.util.GlobalInfo;
import com.lessask.dongfou.util.TimeHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by laiqin on 16/3/31.
 */
public class DbDataHelper {
    private static String TAG = DbDataHelper.class.getSimpleName();
    private static GlobalInfo globalInfo = GlobalInfo.getInstance();
    public static SportGather loadSportGatherFromDb(Context context,int sportid){
        return new SportGather(loadSportFromDb(context,sportid),loadSportRecordById(context,sportid));
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
    public static List<Float> loadSportRecordById(Context context,int sportid){

        SQLiteDatabase db = DbHelper.getInstance(context).getDb();
        Cursor cr = db.rawQuery("select amount,time from t_sport_record_day where userid="+globalInfo.getUserid() +" and sportid="+sportid+" and date(time,'unixepoch','localtime')>date('now','-7 day') order by time ", null);
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
            lastTime= TimeHelper.getDateStartOfDay(time,1);
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
}
