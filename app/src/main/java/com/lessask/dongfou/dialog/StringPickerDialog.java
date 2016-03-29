package com.lessask.dongfou.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.lessask.dongfou.R;

import java.util.ArrayList;

/**
 * Created by huangji on 2015/12/18.
 */
public class StringPickerDialog extends AlertDialog implements DialogInterface.OnClickListener{

    private OnSelectListener mSelectCallBack;
    private NumberPicker numberPicker;
    private String title;
    private String[] values;
    private int initValue;
    private String unit;


    /*
    public StringPickerDialog(Context context, String[] values, OnSelectListener mSelectCallBack) {
        super(context);
        this.values = values;
        this.mSelectCallBack = mSelectCallBack;
        init(context);
    }
    */

    public void setEditable(boolean flag){
        if(flag==false)
            numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    private void init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.string_picker, null);
        TextView titleView = (TextView)view.findViewById(R.id.title);
        titleView.setText(title);
        TextView unitView = (TextView)view.findViewById(R.id.unit);
        unitView.setText(unit);

        setView(view);
        setButton(BUTTON_POSITIVE, "确定", this);
        setButton(BUTTON_NEGATIVE, "取消", this);

        numberPicker = (NumberPicker) view.findViewById(R.id.picker);
        numberPicker.setDisplayedValues(values);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        numberPicker.setValue(initValue-1);
        numberPicker.setDividerDrawable(new ColorDrawable(getContext().getResources().getColor(R.color.main_color)));


    }
    public StringPickerDialog(Context context,String title,int maxNumber,int initValue,String unit,OnSelectListener mSelectCallBack) {
        super(context);
        values = new String[maxNumber];
        for(int i=0;i<maxNumber;i++){
            values[i]=i+1+"";
        }
        this.title = title;
        this.unit = unit;
        this.initValue=initValue;
        this.mSelectCallBack = mSelectCallBack;
        init(context);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {
            case BUTTON_POSITIVE:
                if (mSelectCallBack != null) {
                    mSelectCallBack.onSelect(numberPicker.getValue()+1);
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    public interface OnSelectListener {
        void onSelect(int data);
    }
}
