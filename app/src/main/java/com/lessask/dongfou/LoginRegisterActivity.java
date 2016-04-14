package com.lessask.dongfou;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.lessask.dongfou.net.GsonRequest;
import com.lessask.dongfou.net.VolleyHelper;
import com.lessask.dongfou.util.GlobalInfo;

import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

public class LoginRegisterActivity extends AppCompatActivity {

    private String TAG = LoginRegisterActivity.class.getSimpleName();
    private EditText mail;
    private EditText passwd;
    private GlobalInfo globalInfo = GlobalInfo.getInstance();

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
                GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST,Config.loginUrl,User.class, new GsonRequest.PostGsonRequest<User>() {
                    @Override
                    public void onStart() {
                        }
                        @Override
                        public void onResponse(User response) {
                            Log.e(TAG, "response:" + response.toString());
                            if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                                Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                                Toast.makeText(LoginRegisterActivity.this, response.getError(), Toast.LENGTH_SHORT).show();
                            }else {
                                Intent intent = new Intent();
                                intent.putExtra("userid", response.getUserid());
                                intent.putExtra("token", response.getToken());
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Toast.makeText(LoginRegisterActivity.this, "网络错误:" + error, Toast.LENGTH_SHORT);
                        }

                        @Override
                        public Map getPostData() {
                            Map datas = new HashMap();
                            datas.put("mail", mail.getText().toString().trim());
                            datas.put("passwd", passwd.getText().toString().trim());
                            return datas;
                        }
                    });
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
                GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST,Config.registerUrl,User.class, new GsonRequest.PostGsonRequest<User>() {
                    @Override
                    public void onStart() {
                    }
                    @Override
                    public void onResponse(User response) {
                        Log.e(TAG, "response:" + response.toString());
                        if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                            Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                            Toast.makeText(LoginRegisterActivity.this, response.getError(), Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent();
                            intent.putExtra("userid", response.getUserid());
                            intent.putExtra("token", response.getToken());
                            intent.putExtra("isregister", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(LoginRegisterActivity.this, "网络错误:" + error, Toast.LENGTH_SHORT);
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

    public boolean checkEmail(String email) {
        email = email.replace("@", "@");
        //if (email.matches("[\\w\\.\\-]{3,18}@([A-Za-z0-9]{1}[A-Za-z0-9\\-]{0,}[A-Za-z0-9]{1}\\.)+[A-Za-z]+")) {
        Log.e(TAG, "#"+email+"#");
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
}
