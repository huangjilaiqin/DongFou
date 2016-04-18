package com.lessask.dongfou.util;

import android.database.Cursor;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.lessask.dongfou.ArrayListResponse;
import com.lessask.dongfou.Config;
import com.lessask.dongfou.SportRecord;
import com.lessask.dongfou.net.GsonRequest;
import com.lessask.dongfou.net.VolleyHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangji on 2016/4/18.
 */
public class NetHelper {
    /*
    public void downloadRecord(){

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
                        Log.e(TAG, "server record:" + record.getTime());
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
    */
}
