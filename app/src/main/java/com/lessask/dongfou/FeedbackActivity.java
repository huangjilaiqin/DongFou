package com.lessask.dongfou;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.lessask.dongfou.net.GsonRequest;
import com.lessask.dongfou.net.VolleyHelper;
import com.lessask.dongfou.util.GlobalInfo;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    private SharedPreferences feedback;
    private EditText content;
    private Button commitBt;
    private final String TAG = FeedbackActivity.class.getSimpleName();
    private GlobalInfo globalInfo = GlobalInfo.getInstance();
    private String contentStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("改进产品");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBack();
                finish();
            }
        });


        feedback = getSharedPreferences("feedback", MODE_PRIVATE);
        contentStr = feedback.getString("content","");

        //获取意见箱内容
        content = (EditText) findViewById(R.id.content);
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG, "content size:"+s.length());
                if(s.length()==0){
                    commitBt.setEnabled(false);
                }else {
                    commitBt.setEnabled(true);
                    //commitBt.setBackgroundColor(getResources().getColor(R.color.main_color));
                    //commitBt.setTextColor(getResources().getColor(R.color.white));
                }

            }
        });
        commitBt = (Button)findViewById(R.id.commit);
        if(contentStr.length()>0) {
            content.setText(contentStr);
            content.setSelection(contentStr.length());
        }else
            commitBt.setEnabled(false);

        commitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contentStr = content.getText().toString().trim();
                if(contentStr.length()>0){
                    commit();
                }
            }
        });

        final TextView qq = (TextView)findViewById(R.id.qq);
        qq.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cmb = (ClipboardManager) FeedbackActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(qq.getText().toString().trim()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                Toast.makeText(FeedbackActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                Vibrator vib = (Vibrator) FeedbackActivity.this.getSystemService(Service.VIBRATOR_SERVICE);
                vib.vibrate(15);
                return false;

            }
        });

    }

    private void handleBack(){
        contentStr = content.getText().toString().trim();
        SharedPreferences.Editor editor = feedback.edit();
        editor.putString("content", contentStr);
        Log.e(TAG, "content:"+contentStr);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        handleBack();
        super.onBackPressed();
    }

    private void commit(){
        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, Config.feedbackUrl, ResponseError.class, new GsonRequest.PostGsonRequest<ResponseError>() {
            @Override
            public void onStart() {
            }
            @Override
            public void onResponse(ResponseError response) {
                Log.e(TAG, "response:" + response.toString());
                if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                    Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                }else {
                    //
                    //清除意见箱
                    SharedPreferences.Editor editor = feedback.edit();
                    editor.putString("content", "");
                    editor.commit();

                    content.setText("");
                    Toast.makeText(FeedbackActivity.this, "感谢您的宝贵意见", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, error.toString());
                Toast.makeText(FeedbackActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", ""+globalInfo.getUserid());
                datas.put("content", contentStr);
                return datas;
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }
}
