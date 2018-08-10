package com.thinkwin.common.utils;

import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.vo.FastdfsVo;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 对时间进行转换的工具类，
 * 开发人员:daipengkai
 * 创建时间:2017/6/30
 */

public class TimeUtil {


	public static void main(String[] ages) {


		String a ="2018-03-21 17:30:00";
		String b ="2018-03-21 17:45:00";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date da =sdf.parse(a);
			Date de = sdf.parse(b);
			long l = de.getTime() - da.getTime();
			System.out.println(l);
			System.out.println( l / 60 / 1000 );
			System.out.println(15 *60  *1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}


	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 获取两个时间的时间间隔
	 * @param sta
	 * @param end
	 * @param type
	 * @return
	 */
	public static long timeTransformation(long sta ,long end ,String type){
		long result = 0 ;
		//转换成分钟
		if(type.equals("minute")){
			result = (end - sta)/60/1000;
		}
		//转换成小时
		if(type.equals("hour")){
			result = (end - sta)/60/1000/60;
		}

		return result;
	}

	/**
	 * 获取两个时间的时间间隔
	 * @param sta
	 * @param end
	 * @param type
	 * @return
	 */
	public static long timeMillisecond(Integer num,String type){
		long result = 0 ;
		//分钟转换毫秒
		if(type.equals("minute")){
			result = num * 60 * 1000;
		}
		//小时转换毫秒
		if(type.equals("hour")){
			result = num * 60 * 1000 * 60;
		}

		return result;
	}

	/**
	 * 返回年的后 两位+月日
	 */
	public static String timeString() {
		String str = format.format(new Date());
		String time = str.substring(2, str.length()).replace("-", "");
		return time;
	}

	/**
	 * 生成不同的随机数
	 */
	public static List<Integer> getArray(int len) {  //len为需要产生的不重复随机数个数
		List<Integer> list = new ArrayList<Integer>();
		Set<Integer> set = new HashSet<Integer>();
		Random random = new Random();
		int num = 0;
		for (; true; ) {
			num = random.nextInt(9999) % (9999 - 1000 + 1) + 1000;
			set.add(num);
			System.out.println(num);
			if (set.size() >= len) {
				break;
			}
		}
		for (int a : set) {
			list.add(a);
		}
		return list;
	}

	//生成随机数字和字母
	public static String getStringRandom(int length) {

		String val = "";
		Random random = new Random();

		//参数length，表示生成几位随机数
		for(int i = 0; i < length; i++) {

			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			//输出字母还是数字
			if( "char".equalsIgnoreCase(charOrNum) ) {
				//输出是大写字母还是小写字母
				int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val += (char)(random.nextInt(26) + temp);
			} else if( "num".equalsIgnoreCase(charOrNum) ) {
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val.toUpperCase();
	}


	/**
	 * 获取过去第几天的日期
	 *
	 * @param past
	 * @return
	 */
	public static String getPastDate(int past) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
		Date today = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String result = format.format(today);
		return result;
	}


	/**
	 * 获取过去或者未来 任意天内的日期数组
	 *
	 * @param intervals intervals天内
	 * @return 日期数组
	 */
	public static ArrayList<String> timeArray(int intervals) {
		ArrayList<String> pastDaysList = new ArrayList<>();
		ArrayList<String> fetureDaysList = new ArrayList<>();
		for (int i = 0; i < intervals; i++) {
			pastDaysList.add(getPastDate(i));
			fetureDaysList.add(getFetureDate(i));
		}
		return fetureDaysList;
	}


	/**
	 * 获取未来 第 past 天的日期
	 *
	 * @param past
	 * @return
	 */
	public static String getFetureDate(int past) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
		Date today = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String result = format.format(today);
		return result;
	}

	public static Date addTimes(Date date, Long time, TimeUnit timeUnit) {
		if(TimeUnit.MILLISECONDS.equals(timeUnit)){
			return DateUtils.addMilliseconds(date, time.intValue());
		} else if(TimeUnit.SECONDS.equals(timeUnit)){
			return DateUtils.addSeconds(date, time.intValue());
		} else if(TimeUnit.MINUTES.equals(timeUnit)){
			return DateUtils.addMinutes(date, time.intValue());
		} else if(TimeUnit.HOURS.equals(timeUnit)){
			return DateUtils.addHours(date, time.intValue());
		} else if(TimeUnit.DAYS.equals(timeUnit)){
			return DateUtils.addDays(date, time.intValue());
		}

		return null;
	}

	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

    public static Integer getTimeDifference(String staTime,String endTime) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long days=0;
		try {
			Date d1 = df.parse(staTime);
			Date d2 = df.parse(endTime);
			long diff = d2.getTime() - d1.getTime();
			days = diff / (1000 * 60 * 60 * 24);
		}catch (Exception e){

		}
		return (int)days;
	}

	/**
	 * 把时间戳转换为毫秒
	 * @param l
	 * @return
	 */
	public static String dateTimeMs(long l){
//		long days = str / (1000 * 60 * 60 * 24);
//		long hours = (str % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
//		long minutes = (str % (1000 * 60 * 60)) / (1000 * 60);
//		long seconds = (str % (1000 * 60)) / 1000;
//		return  hours+"时"+minutes +"分";
		String str = "";
		int hour = 0;
		int minute = 0;
		int second = 0;
		second = (int) (l / 1000);
		if (second > 60) {
			minute = second / 60;
			second = second % 60;
		}
		if (minute > 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		String strtime = "";
		if(minute != 0){
			if(hour == 0){
				strtime = minute+"分钟";
			}else{
				strtime = hour+"小时"+minute+"分钟";
			}
		}else{
			strtime = hour+"小时";
		}

		return strtime;

	}

	/**
	 * 获取两个日期之间的所有日期（yyyy-MM-dd）
	 * @Description TODO
	 * @param begin
	 * @param end
	 * @return
	 */
	public static  List<String> getBetweenDates(Date begin, Date end) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		List<String> result = new ArrayList<String>();
		Calendar tempStart = Calendar.getInstance();
		tempStart.setTime(begin);
		while(begin.getTime()<=end.getTime()){
			result.add(sdf.format(tempStart.getTime()));
			tempStart.add(Calendar.DAY_OF_YEAR, 1);
			begin = tempStart.getTime();
		}
		return result;
	}

	/**
	 * 把时间戳转换为毫秒
	 * @param l
	 * @return
	 */
	public static String dateTimeMsNum(long l){
		String str = "";
		int hour = 0;
		int minute = 0;
		int second = 0;
		second = (int) (l / 1000);
		if (second > 60) {
			minute = second / 60;
			second = second % 60;
		}
		if (minute > 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		String strtime = "";
		strtime = hour+"."+minute;
		return strtime;

	}

	/**
	 * 时间转字符串
	 * @param date
	 * @return
	 */
	public static String getTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String format = sdf.format(date);
		return format;
	}








}

