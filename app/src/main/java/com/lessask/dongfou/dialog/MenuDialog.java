package com.lessask.dongfou.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lessask.dongfou.R;
import com.lessask.dongfou.util.ScreenUtil;

/**
 * Created by huangji on 2015/10/16.
 */
public class MenuDialog extends Dialog {

    private Context context;
    private String TAG = MenuDialog.class.getSimpleName();

    public MenuDialog(Context context, String[] titles, OnSelectMenu onSelectMenu) {
        super(context, R.style.menu_dialog);
        this.context = context;
        final OnSelectMenu onSelectMenuListener=onSelectMenu;
        if(onSelectMenuListener==null)
            throw new IllegalArgumentException();

        LinearLayout menu = new LinearLayout(context);
        menu.setOrientation(LinearLayout.VERTICAL);
        menu.setBackgroundResource(R.drawable.menu_dialog_bg);
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        menu.setLayoutParams(layoutParams);
        menu.setMinimumWidth((int) (ScreenUtil.getScreenWidth(context) * 0.78));
        setContentView(menu);
        //setView(menu);

        int size = titles.length;
        for (int i=0;i<size;i++){
            final int position = i;
            TextView item = new TextView(context);
            item.setText(titles[i]);
            item.setTextSize(16);
            item.setMinHeight(ScreenUtil.dp2Px(context, 48));
            item.setBackgroundColor(0x00000000);
            item.setTextColor(context.getResources().getColor(R.color.black_35));
            item.setPadding(ScreenUtil.dp2Px(context,20), 0, 0, 0);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSelectMenuListener.onSelectMenu(position);
                    cancel();
                }
            });
            menu.addView(item);
            //添加分割线
            if(i+1<size){
                LinearLayout line = new LinearLayout(context);
                line.setGravity(LinearLayout.HORIZONTAL);
                line.setBackgroundColor(context.getResources().getColor(R.color.borde_gray));
                line.setMinimumHeight(1);
                line.setLayoutParams(layoutParams);
                menu.addView(line);
            }
        }
        setCanceledOnTouchOutside(true);

    }
}
