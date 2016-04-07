package com.lessask.dongfou.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;

/**
 * Created by JHuang on 2016/1/11.
 */


public class ScreenUtil {
    public static int dp2Px(Context context,int dp){
        return  (int)(dp*context.getResources().getDisplayMetrics().density+0.5f);
    }
    public static int getScreenWidth(Context context){
        return  context.getResources().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight(Context context){
        return  context.getResources().getDisplayMetrics().heightPixels;
    }
    private static int singleImgWidth=0;
    public static int getSingleImgWidth(Context context){
        if(singleImgWidth==0)
            singleImgWidth=(int)(ScreenUtil.getScreenWidth(context)*0.556);
        return singleImgWidth;
    }
    private static int multiImgWidth=0;
    public static int getMultiImgWidth(Context context, int imageDeltaDp){
        if(multiImgWidth==0)
            multiImgWidth=(int)((ScreenUtil.getScreenWidth(context)-ScreenUtil.dp2Px(context,80+imageDeltaDp))/2);
        return multiImgWidth;
    }

    public static int getStatusBarHeight(Activity activity){
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        return statusBarHeight;
    }
}
