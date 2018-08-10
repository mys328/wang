package com.thinkwin.common.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;

/**
 * 时间工具类
 * @version 
 * @author 王超
 * @since 2013-1-8 下午2:47:37
 * @see
 */
public class DateUtil {

	/**年月格式*/
	public final static String TYPE_MONTH = "yyyy-MM";
	/**年月日格式*/
	public final static String TYPE_DAY = "yyyy-MM-dd";
	/**年月日时分秒格式*/
	public final static String TYPE_DEF = "yyyy-MM-dd HH:mm:ss";
	
	public final static String TYPE_TIMEONLY = "HH:mm:ss";

	public final static String TYPE_TIME = "HH:mm";
	
	public final static String[] ENGLIST_MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	
	private final static Calendar c = Calendar.getInstance();
	
	public static int getCurYear(){	
	      return c.get(Calendar.YEAR);
	}
	
	
	
	public static int getCurWeekOfYear(){
		      int currentWeekOfYear = c.get(Calendar.WEEK_OF_YEAR);	     
	          if (currentWeekOfYear == 1 && c.get(Calendar.MONTH) == 11) {
	              currentWeekOfYear = 53;
	          }
	          return  currentWeekOfYear;
	}
	public static int getWeekOfYear(Date date){
	    c.setTime(date);
		int currentWeekOfYear = c.get(Calendar.WEEK_OF_YEAR);	     
		if (currentWeekOfYear == 1 && c.get(Calendar.MONTH) == 11) {
			currentWeekOfYear = 53;
		}
		return  currentWeekOfYear;
	}
	
	public static String getWeekAfterByWeek( Date curDay) {
		     c.setTime(curDay);
             int week =  c.get(Calendar.WEEK_OF_YEAR);
             int  year  = c.get(Calendar.YEAR) ;
		   return  year+"-" +week;
	}
	
	
	public static Date getStartDayAfterByWeek( int week) {
		     int  currentDayOfCurrentWeek =  c.get(Calendar.DAY_OF_WEEK);
		     int  diff = -(currentDayOfCurrentWeek -2 ) +  week * 7; 
		     return getDateAfterByDay(new Date(), diff);		     
	}
	
	public static Date getEndDayAfterByWeek( int week) {
	     int  currentDayOfCurrentWeek =  c.get(Calendar.DAY_OF_WEEK);
	     int  diff = ( 7 - currentDayOfCurrentWeek +1 ) +  week * 7; 
	     return getDateAfterByDay(new Date(), diff);		     
}
	
	public static String getTime(Date date, String type){
		if(type==null || type.equals("")) type = TYPE_DEF;
		DateFormat format = new SimpleDateFormat(type);
		String formatDate = format.format(date);
		return formatDate;
	}
	public static String getTime(Date date){
		return getTime(date, null);
	}
	
	public static String getTime(Timestamp date, String type){
		if(type==null || type.equals("")) type = TYPE_DEF;
		DateFormat format = new SimpleDateFormat(type);
		String formatDate = format.format(date);
		return formatDate;
	}
	
	/**
	 * 获取Date类型
	 * @param time
	 * @return
	 */
	public static Date getDate(String time){
		if(time.equals("")){
			time = getTime(new Date(), TYPE_DEF);
		}
		
		String pattern = matchPattern(time);
		if(null == pattern) {
			return null;
		}
		
		DateFormat format = new SimpleDateFormat(pattern);
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static String matchPattern(String time) {
		if(time.matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}")) {
			return TYPE_DEF;
		} else if(time.matches("\\d{4}-\\d{2}-\\d{2}")) {
			return TYPE_DAY;
		} else if(time.matches("\\d{4}-\\d{2}")) {
			return TYPE_MONTH;
		} else if(time.matches("\\d{2}:\\d{2}:\\d{2}")) {
			return TYPE_TIMEONLY;
		}
		
		return null;
	}
	
	public static boolean chkDate(String time, String type){
		DateFormat format = new SimpleDateFormat(type);
		try {
			String year = time.substring(0, 4);
			format.parse(time);
			Integer.parseInt(year);	//验证年份是否可以转换为数字，不是数字说明有问题
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
/**
 * 功能:获取前滚，后滚多少分种的时间对象
 * @param currentDate
 * @param minute
 * @param flag //前滚true    ,后滚false
 * @return
 */
	public static Date getNewDate(Date currentDate, int minute, boolean flag) {
		
		if (currentDate == null || !(currentDate instanceof Date)) {
			throw new RuntimeException("传入的日期参数错误");
		}
	      Calendar cal = Calendar.getInstance();
	      cal.setTime(currentDate);
	      cal.add(Calendar.MINUTE, minute);
	      return cal.getTime();
	}
	
	/**
	 * 获取Date类型
	 * @param time
	 * @return
	 */
	public static Date getDate(String time, String type){
		if(time==null ||  time.equals("")){
			time = getTime(new Date(), TYPE_DEF);
		}
		if(type==null || type.equals("")) type = TYPE_DEF;
		DateFormat format = new SimpleDateFormat(type);
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	public static Date getMaxDayTime(String day){
		return new Date(DateUtil.getDate(DateUtil.getDateAfterByDay(day, 1), DateUtil.TYPE_DAY).getTime()-1);
	}
	/**
	 * 得到两个时间的分钟差
	 */
	public static int compareMinute(Date begin, Date end) {
		int minute = 0;
		double between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
		minute = (int) (between / (60));
		return minute;
	}
	
	/**
	 * 功能：判断某个时间，是否在某个时间段之内
	 */
/*	public static boolean  isBetweenDate(Date begin, Date end,Date compDate) {
		long beginLong = begin.getTime();
		beginLong=beginLong/1000;
		long endLong = end.getTime();
		endLong=endLong/1000;
		long compLong = compDate.getTime();
		if(beginLong<compLong &&compLong<endLong){
			return true;
		}else{
			return false;
		}
		
	}*/
	
	   /**
	    * 比较当前时间是否在beginDate与endDate两个日期之间。
	    * @param beginDate
	    * @param endDate
	    * @return
	    */
	   public static boolean checkCurDateBetween(String beginDate,String endDate){
		   long beginDateTime=convertDateStrToMilliSeconds(beginDate, TYPE_DEF);
		   long endDateTime=convertDateStrToMilliSeconds(endDate, TYPE_DEF);
		   long curDateTime=System.currentTimeMillis();
		   
		   if(curDateTime>=beginDateTime && curDateTime<=endDateTime){
			   return true;
		   }
		   return false;
	   }
	   
	   /**
	    * 比较当前时间是否在beginDate与endDate两个日期之间。
	    * @param beginDate
	    * @param endDate
	    * @return
	    */
	   public static boolean checkCurDateBetween(String beginDate,String endDate,String someDate){
		   long beginDateTime=convertDateStrToMilliSeconds(beginDate, TYPE_DEF);
		   long endDateTime=convertDateStrToMilliSeconds(endDate, TYPE_DEF);
		   long someDateTime=convertDateStrToMilliSeconds(someDate, TYPE_DEF);
		   if(someDateTime>=beginDateTime && someDateTime<=endDateTime){
			   return true;
		   }
		   return false;
	   }
	   
	   /**
	    * 将普通时间 格式的字符串转化成unix时间戳  单位为毫秒
	    * @param timeStamp
	    * @param dateFormat
	    * @return
	    */
	   public static long convertDateStrToMilliSeconds(String dateStr, String dateFormat)
	   {
	      if(StringUtils.isBlank(dateStr)) {
	         return 0;
	      }
	      long timeStamp = DateUtil.convertStrToDate(dateStr, dateFormat).getTime();
	      return timeStamp;
	   }
	   
	   /**
	    * 将指定格式的字符串转换成日期类型
	    * @param date 待转换的日期字符串
	    * @param dateFormat 日期格式字符串
	    * @return Date
	    */
	   public static Date convertStrToDate(String dateStr, String dateFormat)
	   {
	      if(dateStr == null || dateStr.equals("")) {
	         return null;
	      }
	      SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	      try {
	         return sdf.parse(dateStr);
	      } catch(Exception e) {
	         throw new RuntimeException("DateUtil.convertStrToDate():" +
	                                    e.getMessage());
	      }
	   }
	   
	/**
	 * 得到两个时间的分钟差
	 */
	public static int compareMinute(String beginM, String endM) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date begin;
		Date end;
		int minute = 0;
		try {
			begin = df.parse(beginM);
			end = df.parse(endM);
			minute = compareMinute(begin, end);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return minute;
	}
	/**
	 * 得到两个时间的小时差
	 */
	public static int compareHour(Date beginHour, Date endHour) {
		int hour = 0;
		double between = (endHour.getTime() - beginHour.getTime()) / 1000;// 除以1000是为了转换成秒
		hour = (int) (between / (3600));
		return hour;
	}
	public static int compareHour(String beginHourStr, String endHourStr) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH");
		int hour = 0;
		try {
			Date begin = df.parse(beginHourStr);
			Date end = df.parse(endHourStr);
			double between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
			hour = (int) (between / (3600));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return hour;
	}
	/**
	 * 得到两个时间的天数差
	 */
	public static int compareDay(Date beginDay, Date endDay) {
		int day = 0;
		double between = (endDay.getTime() - beginDay.getTime()) / 1000;// 除以1000是为了转换成秒
		day = (int) (between / (24 * 3600));
		return day;
	}
	/**
	 * 得到两个时间的天数差
	 */
	public static int compareDay(String beginDayStr, String endDayStr) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		int day = 0;
		try {
			Date beginDay = df.parse(beginDayStr);
			Date endDay = df.parse(endDayStr);
			day = compareDay(beginDay, endDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return day;
	}

	// 月份差
	public static int compareMonth(String startDay, String endDay) {
		int n = 0;
		DateFormat df = new SimpleDateFormat("yyyy-MM");
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		try {
			c1.setTime(df.parse(startDay));
			c2.setTime(df.parse(endDay));
		} catch (Exception e3) {
			System.out.println("wrong occured");
		}
		while (!c1.after(c2)) { // 循环对比，直到相等，n 就是所要的结果
			n++;
			c1.add(Calendar.MONTH, 1); // 比较月份，月份+1
		}
		n = n - 1;
		System.out.println(startDay + " -- " + endDay + " 相差多少" + ":" + n);
		return n;
	}

	/**
	 * 得到该日期后几小时的日期
	 * 
	 * @param startTime
	 * @param hour
	 * @return
	 */
	public static String getDateAfterByHour(String startTime, int hour) {
		String endTime = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
		Date dateStart;
		try {
			dateStart = sdf.parse(startTime);
			Calendar now = Calendar.getInstance();
			now.setTime(dateStart);
			now.set(Calendar.HOUR, now.get(Calendar.HOUR) + hour);
			endTime = sdf.format(now.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return endTime;
	}
	
	/**
	 * 得到该日期后几小时的日期
	 * 
	 * @param startTime
	 * @param hour
	 * @return
	 */
	public static String getDateAfterByHour(Date startTime, int hour) {
		return getDateProcessByHour(startTime, hour,1);
	}
	
	/**
	 * 得到该日期前几小时的日期
	 * 
	 * @param startTime
	 * @param hour
	 * @return
	 */
	public static String getDateBeforeByHour(Date startTime, int hour) {
		return getDateProcessByHour(startTime, hour,0);
	}
	/**
	 * 得到该日期前几分钟的日期
	 * 
	 * @param startTime
	 * @param minute
	 * @return
	 */
	public static String getDateBeforeByMinute(Date startTime, int minute) {
		return getDateProcessByMinute(startTime, minute,0);
	}
	/**
	 * 得到该日期后几分钟的日期
	 * 
	 * @param startTime
	 * @param minute
	 * @return
	 */
	public static String getDateAfterByMinute(Date startTime, int minute) {
		return getDateProcessByMinute(startTime, minute,1);
	}
	
	/**
	 * 得到该日期前、后几小时的日期
	 * 
	 * @param startTime
	 * @param hour
	 * @return
	 */
	public static String getDateProcessByHour(Date startTime, int hour,int type) {
		String endTime = "";
		SimpleDateFormat sdf = new SimpleDateFormat(TYPE_DEF);
		Calendar now = Calendar.getInstance();
		now.setTime(startTime);
		if(type==1){
			now.set(Calendar.HOUR, now.get(Calendar.HOUR) + hour);
		}else{
			now.set(Calendar.HOUR, now.get(Calendar.HOUR) - hour);
		}
		endTime = sdf.format(now.getTime());
		return endTime;
	}
	/**
	 * 得到该日期前、后多少分种的日期
	 * 
	 * @param startTime
	 * @param minute
	 * @return
	 */
	public static String getDateProcessByMinute(Date startTime, int minute,int type) {
		String endTime = "";
		SimpleDateFormat sdf = new SimpleDateFormat(TYPE_DEF);
		Calendar now = Calendar.getInstance();
		now.setTime(startTime);
		if(type==1){
			now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) + minute);
		}else{
			now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) - minute);
		}
		endTime = sdf.format(now.getTime());
		return endTime;
	}

	/**
	 * 得到该日期后几天的日期
	 * 
	 * @param startTime
	 * @param day
	 * @return
	 */
	public static String getDateAfterByDay(String startTime, int day) {
		String endTime = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dateStart;
		try {
			dateStart = sdf.parse(startTime);
			Calendar now = Calendar.getInstance();
			now.setTime(dateStart);
			now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
			endTime = sdf.format(now.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return endTime;
	}

	public static String getDateStrAfterByDay(Date dateStart, int day) {
		return getDateStrAfterByDay(dateStart, day,null);
	}
	/**
	 * 获取当前日期向后多少天的时间串
	 * @param dateStart
	 * @param day
	 * @param format
	 * @return
	 */
	public static String getDateStrAfterByDay(Date dateStart, int day,String format) {
		String endTime = "";
		SimpleDateFormat sdf = null;
		if(StringUtils.isBlank(format)){
			sdf = new SimpleDateFormat(TYPE_DEF);
		}else{
			sdf = new SimpleDateFormat(format);
		}
		Calendar now = Calendar.getInstance();
		now.setTime(dateStart);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		endTime = sdf.format(now.getTime());
		return endTime;
	}
	
	/**
	 * 功能:当前天向前滚动n天
	 * @param dateStart
	 * @param day
	 * @return
	 */
	public static String getDateStrBeforeByDay(Date dateStart, int day) {
		return getDateStrProcessByDay(dateStart, day,1,null);
	}
	/**
	 * 返回当前日期向前、向后多少天的时间串
	 * @param dateStart
	 * @param day
	 * @param format
	 * @return
	 */
	public static String getDateStrProcessByDay(Date dateStart, int day,int type,String format) {
		String endTime = "";
		SimpleDateFormat sdf = null;
		if(StringUtils.isBlank(format)){
			sdf = new SimpleDateFormat(TYPE_DEF);
		}else{
			sdf = new SimpleDateFormat(format);
		}
		Calendar now = Calendar.getInstance();
		now.setTime(dateStart);
		if(type==1){
			now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
		}else{
			now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		}
		endTime = sdf.format(now.getTime());
		return endTime;
	}
	
	
	public static Date getDateAfterByDay(Date dateStart, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(dateStart);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		return now.getTime();
	}

	/**
	 * 得到该日期后几月的日期
	 * 
	 * @param startTime
	 * @param month
	 * @return
	 */
	public static String getDateAfterByMonth(String startTime, int month) {
		String endTime = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dateStart;
		try {
			dateStart = sdf.parse(startTime);
			Calendar now = Calendar.getInstance();
			now.setTime(dateStart);
			now.add(Calendar.MONTH, month);
			endTime = sdf.format(now.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return endTime;
	}
	
	public static Date getDateAfterByMonth(Date startTime, int month) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStart = sdf.format(startTime);
		String endTime = getDateAfterByMonth(dateStart, month);
        try {
            return sdf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
	
    /*
     * 计算当前日期(String)加上或减去n天后的日期(String),日期格式：yyyy-MM-dd
     * String strDate 字符串形式的日期格式。
     * int days  要加上和减去的天数
     * int type  定义加或减的类型
     * String format 格式
     */
    public static String substract(String strDate, int days,int type, String format)
    {
        DateFormat df =  new SimpleDateFormat(format);
        Date d = getFormatedDate(df, strDate);
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        if(type == 1){
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + days);
        }else{
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - days);
        }
        return df.format(c.getTime());
    }
    
	private static Date getFormatedDate(DateFormat df, String strDate) {
		try {
			return df.parse(strDate);
		} catch (Exception ex) {
			throw new RuntimeException("日期格式不对，无法解析。", ex);
		}
	}
	
	/**
	 * 功能：获取当前日期是星期几
	 * @return
	 */
	public static String  getNoOfWeek() {
	return getNoOfWeek(new Date());
	}
	/**
	 * 功能:获取指定时间是周几
	 * @param date
	 * @return
	 */
	public static String  getNoOfWeek(Date date) {
		Calendar c = java.util.Calendar.getInstance();
		c.setTime(date);
		int weekDay =c.get(java.util.Calendar.DAY_OF_WEEK);
		String weekNo=null;
		switch (weekDay) {
		case 1:
			weekNo = "7";
			break;
		case 2:
			weekNo = "1";
			break;
		case 3:
			weekNo = "2";
			break;
		case 4:
			weekNo = "3";
			break;
		case 5:
			weekNo = "4";
			break;
		case 6:
			weekNo = "5";
			break;
		case 7:
			weekNo = "6";
			break;
		default:
			break;
		}
		return weekNo;
	}
	
	/**
	 * 功能：获取当前日期是几号
	 * @return
	 */
	public static String  getDayOfMonth() {
		int weekDay = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
		return String.valueOf(weekDay);
	}
	/**
	 * 功能：获取当前日期是几号
	 * @return
	 */
	public static String  getDayOfMonth(Date date) {
		Calendar c =java.util.Calendar.getInstance();
		c.setTime(date);
		int monthDay = c.get(java.util.Calendar.DAY_OF_MONTH);
		return String.valueOf(monthDay);
	}
	
	/**
	 * 
	 * @param startTime
	 * @param value
	 * @return  [Date.UTC(2971,  2,  4), 2.49]
	 */
	public static String getUTCData(Date startTime, Object value) {
		StringBuffer r = new StringBuffer();
		Double val = new Double(value.toString());
		if(val > 10000000) value = 10000000;
		if(val < -10000000) value = -10000000;
		r.append("[Date.UTC(") 
			.append(DateUtil.getTime(startTime, "yyyy,"))
			.append(Integer.parseInt(DateUtil.getTime(startTime, "MM"))-1)
			.append(DateUtil.getTime(startTime, ",dd,HH,mm"))
			.append("), ").append(value).append("]");
		return r.toString();
	}
	/**
	 * 一天的开始00:00:00
	 * @param startDay
	 * @return
	 */
	public static Date getDateForDayStart(String startDay) {
		if(StringUtils.isEmpty(startDay)) return null;
		return DateUtil.getDate(startDay + " 00:00:00", DateUtil.TYPE_DEF);
	}
    public static String getStrForDayStart(String startDay) {
        if(StringUtils.isEmpty(startDay)) return null;
        return startDay + " 00:00:00";
    }
	/**
	 * 一天的结束23:59:59
	 * @param endDay
	 * @return
	 */
	public static Date getDateForDayEnd(String endDay) {
		if(StringUtils.isEmpty(endDay)) return null;
		return DateUtil.getDate(endDay + " 23:59:59", DateUtil.TYPE_DEF);
	}
    public static String getStrForDayEnd(String endDay) {
        if(StringUtils.isEmpty(endDay)) return null;
        return endDay + " 23:59:59";
    }
	
    
   public static  String getEnDay(String curDay) {
		   String  year = curDay.substring(0,4);
		   String month = curDay.substring(5,7);
		   String  day = curDay.substring(8);
		   month = DateUtil.ENGLIST_MONTHS[Integer.parseInt(month) -1];
		   return new StringBuffer().append(day).append("/").append(month).append("/").append(year).toString();		   
	   }
   
   /**
    * 功能: 星期翻滚 根据所给日期向前或向后翻滚
    * @param currentDate
    * @param flag : add 向后翻滚;sub 向前翻滚
    * @return
    */
   private static  String processScrollWeek(Date currentDate,String flag,Integer num) {
	   Calendar curr = Calendar.getInstance();
	   curr.setTime(currentDate);
	   int offset =0;
	   if("add".equals(flag)){
		   offset =num*7;
	   }else{
		   offset=-num*7;
	   }
	   curr.set(Calendar.DAY_OF_MONTH,curr.get(Calendar.DAY_OF_MONTH)+offset);
	   Date date=curr.getTime();
	   return DateUtil.getTime(date);
   }
   /**
    * 功能: 月份翻滚 根据所给日期向前或向后翻滚
    * @param currentDate
    * @param flag : add 向后翻滚;sub 向前翻滚
    * @return
    */
   private static  String processScrollMonth(Date currentDate,String flag,Integer num) {
	   Calendar curr = Calendar.getInstance();
	   curr.setTime(currentDate);
	   int offset =0;
	   if("add".equals(flag)){
		   offset =num;
	   }else{
		   offset=-num;
	   }
	   curr.set(Calendar.MONTH,curr.get(Calendar.MONTH)+offset);
	   Date date=curr.getTime();
	   return DateUtil.getTime(date);
   }
   /**
    * 功能: 年翻滚 根据所给日期向前或向后翻滚
    * @param currentDate
    * @param flag : add 向后翻滚;sub 向前翻滚
    * @return
    */
   private static  String processScrollYear(Date currentDate,String flag,Integer num) {
	   Calendar curr = Calendar.getInstance();
	   curr.setTime(currentDate);
	   int offset =0;
	   if("add".equals(flag)){
		   offset =num;
	   }else{
		   offset=-num;
	   }
	   curr.set(Calendar.YEAR,curr.get(Calendar.YEAR)+offset);
	   Date date=curr.getTime();
	   return DateUtil.getTime(date);
   }
   /**
    * 功能: 星期翻滚 向后滚动n星期
    * @param currentDate
    * @param num 滚动的星期数
    * @return
    */
   public static  String addWeek(Date currentDate,Integer num) {
	   return processScrollWeek(currentDate,"add",num);
   }
   /**
    * 功能: 星期翻滚 向前翻滚n星期
    * @param currentDate
    * @param num 滚动的星期数
    * @return
    */
   public static  String subWeek(Date currentDate,Integer num) {
	   return processScrollWeek(currentDate,"sub",num);
   }
   /**
    * 功能: 月份翻滚 向后滚动n月
    * @param currentDate
    * @param num 滚动的月数
    * @return
    */
   public static  String addMonth(Date currentDate,Integer num) {
	   return processScrollMonth(currentDate,"add",num);
   }
   /**
    * 功能: 月份翻滚 向前翻滚n月
    * @param currentDate
    * @param num 滚动的月数
    * @return
    */
   public static  String subMonth(Date currentDate,Integer num) {
	   return processScrollMonth(currentDate,"sub",num);
   }
   /**
    * 功能: 年翻滚 向后滚动n年
    * @param currentDate
    * @param num 滚动的年数
    * @return
    */
   public static  String addYear(Date currentDate,Integer num) {
	   return processScrollMonth(currentDate,"add",num);
   }
   /**
    * 功能: 月份翻滚 向前翻滚n月
    * @param currentDate
    * @param num滚动的年数
    * @return
    */
   public static  String subYear(Date currentDate,Integer num) {
	   return processScrollMonth(currentDate,"sub",num);
   }

   /**
    * 功能:获取月的最后一天
    * @param year
    * @param month
    * @return
    */
   public static String getLastDayOfMonth(int year,int month)
   {
       Calendar cal = Calendar.getInstance();
       //设置年份
       cal.set(Calendar.YEAR,year);
       //设置月份
       cal.set(Calendar.MONTH, month-1);
       //获取某月最大天数
       int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
       //设置日历中月份的最大天数
       cal.set(Calendar.DAY_OF_MONTH, lastDay);
       //格式化日期
       SimpleDateFormat sdf = new SimpleDateFormat(TYPE_DEF);
       String lastDayOfMonth = sdf.format(cal.getTime());
        
       return lastDayOfMonth;
   }
   /**
    * 功能：获取时间差字符串
    * @param startTime
    * @param endTime
    * @param bizType 续费:renew;增容add
    * @return
    */
   public static String getTimeDiff(String startTime,String endTime,String bizType)
   {
	   String resultStr=null;
       if(StringUtils.isBlank(bizType)) {
    	   bizType = "renew";
       }
       
       if(bizType.equals("renew")) {//续费 xx年xx月xx日
    	   resultStr = DateHandler.calcDateToString(startTime,endTime);
       }else if(bizType.equals("add")) {//增容 xx年xx天
    	   int subYear = compareDate(startTime, endTime, 2);
    	   Date startTimeDate = convertStrToDate(startTime,TYPE_DAY);
    	   String newStartTimeDate =processScrollYear(startTimeDate,"add",subYear);
    	   int calcSubDay=compareDay(newStartTimeDate,endTime);
    	   if(subYear>0) {
    		   resultStr = subYear+"年零"+calcSubDay+"天";
    	   }else {
    		   resultStr = calcSubDay+"天";
    	   }
       }else {
    	   throw new RuntimeException("业务类型传入错误：bizType:"+bizType); 
       }
        
       return resultStr;
   }
   
   
   /**  
    * @param date1 需要比较的时间 不能为空(null),需要正确的日期格式  
    * @param date2 被比较的时间  为空(null)则为当前时间  
    * @param stype 返回值类型   0为多少天，1为多少个月，2为多少年  
    * @return  
    */ 
   public static int compareDate(String date1,String date2,int stype){  
       int n = 0;  
          
       String[] u = {"天","月","年"};  
       String formatStyle = stype==1?"yyyy-MM":"yyyy-MM-dd";  
          
       date2 = date2==null?getCurrentDate():date2;  
          
       DateFormat df = new SimpleDateFormat(formatStyle);  
       Calendar c1 = Calendar.getInstance();  
       Calendar c2 = Calendar.getInstance();  
       try {  
           c1.setTime(df.parse(date1));  
           c2.setTime(df.parse(date2));  
       } catch (Exception e3) {  
           System.out.println("wrong occured");  
       }  
       while (!c1.after(c2)) {                     // 循环对比，直到相等，n 就是所要的结果  
           n++;  
           if(stype==1){  
               c1.add(Calendar.MONTH, 1);          // 比较月份，月份+1  
           }  
           else{  
               c1.add(Calendar.DATE, 1);           // 比较天数，日期+1  
           }  
       }  
          
       n = n-1;  
          
       if(stype==2){  
           n = (int)n/365;  
       }     
       System.out.println(date1+" -- "+date2+" 相差多少"+u[stype]+":"+n);        
       return n;  
   }  
      
   /**  
    * 得到当前日期  
    * @return  
    */ 
   public static String getCurrentDate() {  
       Calendar c = Calendar.getInstance();  
       Date date = c.getTime();  
       SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");  
       return simple.format(date);  

   } 
   
	public static void main(String args[]){
		/*Calendar cal =Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); //获取本周一的日期

		System.out.println(df.format(cal.getTime()));
		//这种输出的是上个星期周日的日期，因为老外那边把周日当成第一天
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		//增加一个星期，才是我们中国人理解的本周日的日期
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		   System.out.println(df.format(cal.getTime()));
*/
		System.out.println(getTimeDiff("2018-02-27","2020-05-07","renew"));
		
	}
	
}
