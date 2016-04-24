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


/**
 * Created by huangji on 2015/12/18.
 */
public class WeightPickerDialog extends AlertDialog implements DialogInterface.OnClickListener{

    private OnSelectListener mSelectCallBack;
    private NumberPicker numberPicker;
    private NumberPicker numberPicker2;
    private String title;
    private String[] values;
    private float initValue;
    //整数部分初始值
    private int initZhengshuValue;
    //小数部分初始值
    private int initXiaoshuValue;
    private String unit;
    private String[] values2;
    private float initValue2;


    public void setEditable(boolean flag){
        if(flag==false)
            numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    private void init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.weight_picker, null);
        setView(view);
        setButton(BUTTON_POSITIVE, "确定", this);
        setButton(BUTTON_NEGATIVE, "取消", this);

        TextView titleView = (TextView)view.findViewById(R.id.title);
        titleView.setText(title);

        numberPicker = (NumberPicker) view.findViewById(R.id.picker);
        numberPicker.setDisplayedValues(values);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        numberPicker.setValue(initZhengshuValue);
        numberPicker.setDividerDrawable(new ColorDrawable(getContext().getResources().getColor(R.color.main_color)));
        TextView unitView = (TextView)view.findViewById(R.id.unit);
        unitView.setText(unit);

        numberPicker2 = (NumberPicker) view.findViewById(R.id.picker2);
        numberPicker2.setDisplayedValues(values2);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(values2.length - 1);
        numberPicker2.setValue(initXiaoshuValue);
        numberPicker2.setDividerDrawable(new ColorDrawable(getContext().getResources().getColor(R.color.main_color)));

    }
    public WeightPickerDialog(Context context, String title, int maxNumber, float initValue, String unit, OnSelectListener mSelectCallBack) {
        super(context);
        this.title = title;
        this.initValue = initValue;
        if(initValue!=0) {
            this.initZhengshuValue = (int) initValue;
            this.initXiaoshuValue = (int) ((initValue - initZhengshuValue) * 10);
        }else {
            if(title.equals("体重"))
                this.initZhengshuValue = 55;
            else if(title.equals("腰围"))
                this.initZhengshuValue = 76;
            else if(title.equals("臀围"))
                this.initZhengshuValue = 90;
            else if(title.equals("大腿"))
                this.initZhengshuValue = 55;
            else if(title.equals("小腿"))
                this.initZhengshuValue = 38;
            else if(title.equals("胸围"))
                this.initZhengshuValue = 96;
            else if(title.equals("臂围"))
                this.initZhengshuValue = 60;
        }
        this.unit=unit;

        values = new String[maxNumber];
        for(int i=0;i<maxNumber;i++){
            values[i]=i+1+"";
        }
        values2 = new String[10];
        for(int i=0;i<10;i++){
            values2[i]=i+"";
        }
        this.mSelectCallBack = mSelectCallBack;
        init(context);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {
            case BUTTON_POSITIVE:
                if (mSelectCallBack != null) {
                    float result = numberPicker.getValue()+1+(numberPicker2.getValue()+1)/10;
                    mSelectCallBack.onSelect(result,0);
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    public interface OnSelectListener {
        void onSelect(float data, float data2);
    }
}
