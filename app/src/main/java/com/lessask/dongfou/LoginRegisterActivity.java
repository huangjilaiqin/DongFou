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
                GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST,globalInfo.getLoginUrl(),User.class, new GsonRequest.PostGsonRequest<User>() {
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
                                intent.putExtra("userid", response.getId());
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
                GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST,globalInfo.getRegisterUrl(),User.class, new GsonRequest.PostGsonRequest<User>() {
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
                            intent.putExtra("userid", response.getId());
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
    }
}
