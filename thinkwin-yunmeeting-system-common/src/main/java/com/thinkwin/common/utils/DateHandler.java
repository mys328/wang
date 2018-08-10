package com.thinkwin.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * 处理平台的时间转换 
 * @author yyq 
 * 2018-2-26 下午09:21:39 
 */  
public class DateHandler {  
    private static Pattern   p   =   Pattern.compile("\\d{4}-\\d{2}-\\d{2}");//定义整则表达式  
    private DateHandler(){}  
    /** 
     * 计算剩余时间 
     * @param startDateStr 
     * @param endDateStr 
     * @return 
     */  
    public static String calcDateToString(String startDateStr, String endDateStr){  
        java.util.Date startDate = null;  
        java.util.Date endDate= null;  
        Calendar calS=Calendar.getInstance();
        try {  
            startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr); 
            endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);  
        } catch (ParseException e) {  
            e.printStackTrace();  
            return "";  
        }  
        calS.setTime(startDate);  
        int startY = calS.get(Calendar.YEAR);  
        int startM = calS.get(Calendar.MONTH);  
        int startD = calS.get(Calendar.DATE);  
        int startDayOfMonth = calS.getActualMaximum(Calendar.DAY_OF_MONTH);  
          
        calS.setTime(endDate);  
        int endY = calS.get(Calendar.YEAR);  
        int endM = calS.get(Calendar.MONTH);  
        //处理2018-01-10到2018-01-10，认为服务为一天  
        int endD = calS.get(Calendar.DATE)+1;  
        int endDayOfMonth = calS.getActualMaximum(Calendar.DAY_OF_MONTH);  
          
        StringBuilder sBuilder = new StringBuilder();  
        if (endDate.compareTo(startDate)<0) {  
            return sBuilder.append("过期").toString();  
        }  
        int lday = endD-startD;  
        if (lday<0) {  
            endM = endM -1;  
            lday = startDayOfMonth+ lday;  
        }  
        //处理天数问题，如：2018-01-01 到 2020-12-31  2年11个月31天     实际上就是3年  
        if (lday == endDayOfMonth) {  
            endM = endM+1;  
            lday =0;  
        }  
        int mos = (endY - startY)*12 + (endM- startM);  
        int lyear = mos/12;  
        int lmonth = mos%12;  
        if (lyear >0) {  
            sBuilder.append(lyear+"年");  
        }  
        if (lmonth > 0) {  
            sBuilder.append(lmonth+"个月");  
        }  
        if (lday >0 ) {  
            sBuilder.append(lday+"天");  
        }  
        return sBuilder.toString();  
    }  
      
    /* 
     * 转换 dataAndTime 2018-12-31 23:59:59 到 
     * date 2018-12-31 
     */  
    public static String getDate(String dateAndTime){  
        if (dateAndTime != null && !"".equals(dateAndTime.trim())) {  
            Matcher   m   =  p.matcher(dateAndTime);   
            if (m.find()) {  
                  return dateAndTime.subSequence(m.start(), m.end()).toString();  
            }  
        }  
        return "data error";  
    }  
      
    public static void main(String[] args) {  
        System.out.println(calcDateToString("2017-02-27", "2018-04-01"));  
     /*   System.out.println("A");  
        Date time1=new Date();  
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        String tm=sdf.format(time1);//tm就是昨天的日期的字符串表示  
        System.out.println(tm);*/  
    }  
} 