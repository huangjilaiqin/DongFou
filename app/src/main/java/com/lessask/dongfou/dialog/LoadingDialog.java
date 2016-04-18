package com.lessask.dongfou.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lessask.dongfou.R;


/**
 * Created by huangji on 2015/10/16.
 */
public class LoadingDialog extends Dialog{

    private String TAG = LoadingDialog.class.getSimpleName();
    //private SimpleDraweeView mAnimatedGifView;
    private TextView tip;
    public LoadingDialog(Context context) {
        super(context, R.style.nothing_dialog_transparent);
        //Fresco.initialize(context);
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.loading, null);

        setContentView(view);

        tip = (TextView)view.findViewById(R.id.tip);
        /*
        mAnimatedGifView = (SimpleDraweeView) findViewById(R.id.loading);
        DraweeController animatedGifController = Fresco.newDraweeControllerBuilder()
        .setAutoPlayAnimations(true).setUri(Uri.parse("res://com.lessask/"+R.drawable.loading)).build();
        mAnimatedGifView.setController(animatedGifController);
        */
    }
    public void setTip(String tipContent){
        tip.setText(tipContent);
    }
}
