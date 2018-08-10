package com.thinkwin.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java中用正则表达式判断日期格式是否正确
 */
public class DateTypeFormat {

    /**
     * 日期转换成字符串
     */
    public static String DateToStr(Date date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        return str;
    }

    //灵活将日期转换字符串
    public static String DateToStr(Date date,String format) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String str = simpleDateFormat.format(date);
        return str;
    }

    public static String getDateDiff(Date begin,Date end){
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = end.getTime() - begin.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        //return day + "天" + hour + "小时" + min + "分钟";
        return hour + "小时" + min + "分钟";
    }

    /**
     * 字符串转换成日期
     */
    public static Date StrToDate(String str) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /*
    * 判断日期格式是否正确
    * */
    public static boolean isDate(String date) {
            /**
             * 判断日期格式和范围
             */
            String rexp = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";

            Pattern pat = Pattern.compile(rexp);

            Matcher mat = pat.matcher(date);

            boolean dateType = mat.matches();

            return dateType;
        }


    public static void main(String[] args)
    {
        /**
         * 日期格式正确
         */
        String date1 = "2014-01-03";
        /**
         * 日期范围不正确---平年二月没有29号
         */
        String date2 = "2014-02-29";
        /**
         * 日期月份范围不正确---月份没有13月
         */
        String date3 = "2014-13-03";
        /**
         * 日期范围不正确---六月没有31号
         */
        String date4 = "2014-06-31";
        /**
         * 日期范围不正确 ----1月超过31天
         */
        String date5 = "2014-01-32";
        /**
         * 这个测试年份
         */
        String date6 = "2017-07-14 10:40:22 ";


        /**
         * 输出结果
         */
        System.out.println(isDate(date6));
    }
}
