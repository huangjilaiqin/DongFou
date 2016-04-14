package com.lessask.dongfou;

/**
 * Created by JHuang on 2016/3/27.
 */
public class Config {
    public static String host = BuildConfig.SERVER_HOST;
    public static String wsPath = "/ws/";
    public static String sportUrl = host+"/dongfou/sports/";
    public static String imagePrefix = host+"/imgs/";
    public static String feedbackUrl = host+"/dongfou/feedback";
    public static String registerUrl = host+"/dongfou/register";
    public static String loginUrl = host+"/dongfou/login";
    public static String logoutUrl = host+"/dongfou/logout";
    public static String uploadRecordUrl = host+"/dongfou/upload/sportrecord";
    public static String deleteRecordUrl = host+"/dongfou/sportrecord/delete";
    public static String checkUpdate = host+"/dongfou/checkupdate";
    public static String loadNotices = host+"/dongfou/notices";
}
