package com.example.asus.test_rest_client;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String getDate(double date){
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd.MM.yy");
        return dateFormat.format(date);
    }

    public static String getTime(double time){
        SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm");
        return dateFormat.format(time);
    }

    public static String getFullDate(double date){
        SimpleDateFormat s= new SimpleDateFormat("dd.MM.yy HH:mm");
        long longDate = Math.round(date)*1000;
        return s.format(new Date(longDate));
    }

}
