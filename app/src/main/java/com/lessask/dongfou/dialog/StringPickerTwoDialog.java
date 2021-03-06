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
public class StringPickerTwoDialog extends AlertDialog implements DialogInterface.OnClickListener{

    private OnSelectListener mSelectCallBack;
    private NumberPicker numberPicker;
    private NumberPicker numberPicker2;
    private String title;
    private String[] values;
    private float initValue;
    private String unit;
    private String[] values2;
    private float initValue2;
    private String unit2;
    private int currentIndex;
    private int currentIndex2;


    public void setEditable(boolean flag){
        if(flag==false) {
            numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            numberPicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        }
    }

    private void init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.string_picker_two, null);
        setView(view);
        setButton(BUTTON_POSITIVE, "确定", this);
        setButton(BUTTON_NEGATIVE, "取消", this);

        TextView titleView = (TextView)view.findViewById(R.id.title);
        titleView.setText(title);

        numberPicker = (NumberPicker) view.findViewById(R.id.picker);
        numberPicker.setDisplayedValues(values);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        currentIndex = (int)(initValue - 1);
        numberPicker.setValue(currentIndex);
        numberPicker.setDividerDrawable(new ColorDrawable(getContext().getResources().getColor(R.color.main_color)));
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentIndex = newVal;
            }
        });
        TextView unitView = (TextView)view.findViewById(R.id.unit);
        unitView.setText(unit);

        numberPicker2 = (NumberPicker) view.findViewById(R.id.picker2);
        numberPicker2.setDisplayedValues(values2);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(values2.length - 1);
        currentIndex2 = (int)(initValue2 - 1);
        numberPicker2.setValue(currentIndex2);
        numberPicker2.setDividerDrawable(new ColorDrawable(getContext().getResources().getColor(R.color.main_color)));
        numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentIndex2=newVal;
            }
        });
        TextView unitView2 = (TextView)view.findViewById(R.id.unit2);
        unitView2.setText(unit2);

    }
    public StringPickerTwoDialog(Context context,String title,int maxNumber,float initValue,String unit,int maxNumber2,float initValue2,String uint2, OnSelectListener mSelectCallBack) {
        super(context);
        this.title = title;
        this.initValue = initValue;
        this.initValue2 = initValue2;
        currentIndex2=(int)initValue2;
        this.unit=unit;
        this.unit2=uint2;

        values = new String[maxNumber];
        for(int i=0;i<maxNumber;i++){
            values[i]=i+1+"";
        }
        values2 = new String[maxNumber2];
        for(int i=0;i<maxNumber2;i++){
            values2[i]=i+1+"";
        }
        this.mSelectCallBack = mSelectCallBack;
        init(context);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {
            case BUTTON_POSITIVE:
                if (mSelectCallBack != null) {
                    mSelectCallBack.onSelect(Integer.parseInt(values[currentIndex]),Integer.parseInt(values2[currentIndex2]));
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    public interface OnSelectListener {
        void onSelect(int data,int data2);
    }
}
