package com.lessask.dongfou;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.lessask.dongfou.dialog.LoadingDialog;
import com.lessask.dongfou.net.GsonRequest;
import com.lessask.dongfou.net.VolleyHelper;
import com.lessask.dongfou.util.DbHelper;
import com.lessask.dongfou.util.DbInsertListener;
import com.lessask.dongfou.util.GlobalInfo;
import com.lessask.dongfou.util.TimeHelper;

import java.awt.font.TextAttribute;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginRegisterActivity extends AppCompatActivity {

    private String TAG = LoginRegisterActivity.class.getSimpleName();
    private EditText mail;
    private EditText passwd;
    private GlobalInfo globalInfo = GlobalInfo.getInstance();
    private final int VOLLEY_ERROR = 1;
    private final int DOWNLOAD_RECORD_ERROR = 2;
    private final int DOWNLOAD_RECORD_DONE = 3;
    private final int UPLOAD_RECORD_ERROR = 4;
    private final int UPLOAD_RECORD_DONE = 5;
    private final SQLiteDatabase dbInstance = DbHelper.getInstance(this).getDb();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what){
                case UPLOAD_RECORD_DONE:
                    //重新加载MainActivity
                    Log.e(TAG, "UPLOAD_RECORD_DONE");
                    DbHelper.getInstance(LoginRegisterActivity.this).clearInsertListener("t_sport_record");
                    Intent intent = new Intent(LoginRegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    break;
                case UPLOAD_RECORD_ERROR:
                    int mode = (int)msg.arg1;
                    String errorStr = (String)msg.obj;
                    //setSyncMenuVisible(true);
                    if(mode==1){
                        if(msg.arg2>=301 && msg.arg2<=303){
                            Toast.makeText(LoginRegisterActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(LoginRegisterActivity.this, errorStr, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case DOWNLOAD_RECORD_DONE:
                    uploadRecord(2);
                    break;
                case DOWNLOAD_RECORD_ERROR:
                    Toast.makeText(LoginRegisterActivity.this, "登录过期,请重新登录",Toast.LENGTH_SHORT).show();
                    break;
                case VOLLEY_ERROR:
                    VolleyError error = (VolleyError)msg.obj;
                    if(error instanceof TimeoutError){
                        Toast.makeText(LoginRegisterActivity.this, "连接超时,请检查网络", Toast.LENGTH_SHORT).show();
                    }else if(error instanceof com.android.volley.NoConnectionError){
                        Toast.makeText(LoginRegisterActivity.this, "网络出问题了,请检查网络", Toast.LENGTH_SHORT).show();
                    }else if(error instanceof com.android.volley.NetworkError){
                        Toast.makeText(LoginRegisterActivity.this, "网络出问题了,请检查网络", Toast.LENGTH_SHORT).show();
                    }else if(error instanceof com.android.volley.ServerError){
                        Toast.makeText(LoginRegisterActivity.this, "服务器异常,我们在紧急抢修中", Toast.LENGTH_SHORT).show();
                    }else if(error instanceof com.android.volley.ParseError){
                        Toast.makeText(LoginRegisterActivity.this, "数据错误,请反馈或联系官方qq群", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(LoginRegisterActivity.this, "请升级到最新版再试,或联系我们", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("登录/注册");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mail = (EditText) findViewById(R.id.mail);
        passwd = (EditText) findViewById(R.id.passwd);



        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String mailStr = mail.getText().toString().trim();
                final String passwdStr = passwd.getText().toString().trim();
                if(mailStr.length()==0){
                    Toast.makeText(LoginRegisterActivity.this, "告诉我你的邮箱好吗",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(passwdStr.length()<6 || passwdStr.length()>18){
                    Toast.makeText(LoginRegisterActivity.this, "请用6-18位数字或字母作为密码哟",Toast.LENGTH_SHORT).show();
                    return;
                }
                final LoadingDialog loadingDialog = new LoadingDialog(LoginRegisterActivity.this);
                loadingDialog.setTip("登录中...");
                GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST,Config.loginUrl,User.class, new GsonRequest.PostGsonRequest<User>() {
                    @Override
                    public void onStart() {
                        loadingDialog.show();
                    }
                    @Override
                    public void onResponse(User response) {
                        loadingDialog.dismiss();
                        if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                            Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                            Toast.makeText(LoginRegisterActivity.this, response.getError(), Toast.LENGTH_SHORT).show();
                        }else {
                            //清除数据库的监听器
                            DbHelper.getInstance(LoginRegisterActivity.this).clearInsertListener("t_sport_record");
                            DbHelper.getInstance(LoginRegisterActivity.this).appendInsertListener("t_sport_record", sportRecordInsertListener);

                            //同步服务器记录到本地
                            downloadRecord();

                            SharedPreferences baseInfo;
                            SharedPreferences.Editor editor;
                            baseInfo = getSharedPreferences("BaseInfo", MODE_PRIVATE);
                            editor = baseInfo.edit();

                            int userid = response.getUserid();
                            String token = response.getToken();
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
                        datas.put("mail", mail.getText().toString().trim());
                        datas.put("passwd", passwd.getText().toString().trim());
                        return datas;
                    }
                });
                gsonRequest.setGson(TimeHelper.gsonWithNodeDate());
                VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
            }
        });

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mailStr = mail.getText().toString().trim();
                final String passwdStr = passwd.getText().toString().trim();
                if(mailStr.length()==0){
                    Toast.makeText(LoginRegisterActivity.this, "告诉我你的邮箱好吗",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(passwdStr.length()<6 || passwdStr.length()>18){
                    Toast.makeText(LoginRegisterActivity.this, "请用6-18位数字或字母作为密码哟",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!checkEmail(mailStr)){
                    Toast.makeText(LoginRegisterActivity.this, "亲爱de,你的邮箱好像不对哟",Toast.LENGTH_SHORT).show();
                    return;
                }
                final LoadingDialog loadingDialog = new LoadingDialog(LoginRegisterActivity.this);
                GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST,Config.registerUrl,User.class, new GsonRequest.PostGsonRequest<User>() {
                    @Override
                    public void onStart() {
                        loadingDialog.show();
                    }
                    @Override
                    public void onResponse(User response) {
                        loadingDialog.dismiss();
                        if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                            Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                            Toast.makeText(LoginRegisterActivity.this, response.getError(), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(LoginRegisterActivity.this, "注册成功,请登录", Toast.LENGTH_SHORT).show();
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
                        datas.put("mail", mailStr);
                        datas.put("passwd", passwdStr);
                        return datas;
                    }
                });
                VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
            }
        });
    }

    private void downloadRecord(){

        //下载服务的所有记录到本机
        final ArrayList<SportRecord> sportRecords = new ArrayList<>();
        /*
        String sql = "select * from t_sport_record as s inner join (select sportid,userid,max(seq) as seq from t_sport_record where userid="+globalInfo.getUserid()+" group by sportid) s1 on s.sportid=s1.sportid and s.seq=s1.seq and s.userid=s1.userid";
        //select * from t_sport_record as s inner join (select sportid,userid,max(seq) as seq from t_sport_record where userid=6 group by sportid) s1 on s.sportid=s1.sportid and s.seq=s1.seq and s.userid=s1.userid;
        SQLiteDatabase dbInstance = DbHelper.getInstance(this).getDb();
        Cursor cr = dbInstance.rawQuery(sql, null);
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
        */

        Type type = new TypeToken<ArrayListResponse<SportRecord>>() {}.getType();
        final LoadingDialog loadingDialog = new LoadingDialog(LoginRegisterActivity.this);
        loadingDialog.setTip("加载数据...");
        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, Config.downloadRecordUrl, type, new GsonRequest.PostGsonRequest<ArrayListResponse>() {
            @Override
            public void onStart() {
                //显示同步转圈圈动画
                loadingDialog.show();
            }
            @Override
            public void onResponse(ArrayListResponse response) {
                loadingDialog.dismiss();
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
                        DbHelper.addSportRecordFromServer(LoginRegisterActivity.this,record);
                    }
                    handler.sendEmptyMessage(DOWNLOAD_RECORD_DONE);
                }
            }

            @Override
            public void onError(VolleyError error) {
                loadingDialog.dismiss();
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

    public boolean checkEmail(String email) {
        email = email.replace("@", "@");
        //if (email.matches("[\\w\\.\\-]{3,18}@([A-Za-z0-9]{1}[A-Za-z0-9\\-]{0,}[A-Za-z0-9]{1}\\.)+[A-Za-z]+")) {
        Log.e(TAG, "#" + email + "#");
        if (email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
            Log.e(TAG, "mail match");
            return true;
        }else {
            Log.e(TAG, "mail not match");
        }
        /*
        IsEMailResult result = IsEMail.is_email_verbose(email, true);
        switch (result.getState()) {
            case OK:
                return true;
            default:
                return false;
        }
        */
        return false;
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
        final SQLiteDatabase dbInstance = DbHelper.getInstance(this).getDb();
        Cursor cr = dbInstance.rawQuery(sql, null);
        Log.e(TAG, "uploadRecord siez:" + cr.getCount());
        if(cr.getCount()==0){
            Message msg = new Message();
            msg.what=UPLOAD_RECORD_DONE;
            msg.arg1=mode;
            handler.sendMessage(msg);
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

        final LoadingDialog loadingDialog = new LoadingDialog(LoginRegisterActivity.this);
        loadingDialog.setTip("上传本地记录...");
        Type type = new TypeToken<ArrayListResponse<SportRecord>>() {}.getType();
        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, Config.uploadRecordUrl, type, new GsonRequest.PostGsonRequest<ArrayListResponse>() {
            @Override
            public void onStart() {
                //显示同步转圈圈动画
                loadingDialog.show();
            }
            @Override
            public void onResponse(ArrayListResponse response) {
                loadingDialog.dismiss();
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
                loadingDialog.dismiss();
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

    private DbInsertListener sportRecordInsertListener = new DbInsertListener() {
        @Override
        public void callback(Object obj) {
            SportRecord sportRecord = (SportRecord) obj;

            DbHelper.handleSportDay(dbInstance, sportRecord);
            int createNewDay = DbHelper.handleSportMonth(dbInstance,sportRecord);

            //查询上一次的更新时间
            Sport sport = DbHelper.loadSportFromDb(getBaseContext(),sportRecord.getSportid());

            sport.setDays(sport.getDays() + createNewDay);

            //使用最新时间
            sport.setLastTime(sportRecord.getTime());

            sport.setFrequency(sport.getFrequency()+1);
            sport.setTotal(sport.getTotal()+sportRecord.getAmount());
            sport.setSeq(sportRecord.getSeq());
            sport.setLastValue(sportRecord.getArg1());
            sport.setLastValue2(sportRecord.getArg2());
            //日均
            sport.setAvg(sport.getTotal()/sport.getDays());
            Log.e(TAG, "insert record callback,update sport");
            ContentValues values = new ContentValues();
            values.put("total", sport.getTotal());
            values.put("frequency", sport.getFrequency());
            values.put("avg", sport.getAvg());
            values.put("days", sport.getDays());
            values.put("lasttime", sport.getLastTime().getTime() / 1000);
            values.put("seq", sport.getSeq());
            values.put("lastvalue", sport.getLastValue());
            values.put("lastvalue2", sport.getLastValue2());
            DbHelper.getInstance(LoginRegisterActivity.this).getDb().update("t_sport", values, "id=? and userid=?", new String[]{sport.getId() + "", "" + globalInfo.getUserid()});

        }
    };
}
