package com.thinkwin.common.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author yangyiqian 时间段融合及重新分段
 *
 */
public class DateSegmentUtil {

	/** 年月日时分秒格式 */
	public String TYPE_DEF = "yyyy-MM-dd HH:mm:ss";
	// 拿到数据
	List<DateSegmentVo> ts = new ArrayList<DateSegmentVo>();
	List<DateSegmentVo> resetList = null;
	List<DateSegmentVo> resultList = new ArrayList<DateSegmentVo>();

	public List<DateSegmentVo> getTs() {
		return ts;
	}

	public void setTs(List<DateSegmentVo> ts) {
		this.ts = ts;
	}

	// 向List中添加时间对象，用于循环灌入数据【支持单会议室
	public void setSegment(Date startDate, Date endDate) {
		DateSegmentVo dvo = new DateSegmentVo();
		dvo.setStartDate(startDate);
		dvo.setEndDate(endDate);
		ts.add(dvo);
	}

	// 向List中添加时间对象，用于循环灌入数据【支持按会议室分组�??
	public void setSegment(Date startDate, Date endDate, String meetingRoomId) {
		DateSegmentVo dvo = new DateSegmentVo();
		dvo.setStartDate(startDate);
		dvo.setEndDate(endDate);
		dvo.setMeetingRoomId(meetingRoomId);
		ts.add(dvo);
	}

	/**
	 * 功能:生成时间段
	 *
	 * @return
	 */
	public List<DateSegmentVo> generatorDateSegment() {
		List<DateSegmentVo> result = null;
		if (ts != null && ts.size() > 0) {
			result = new ArrayList<DateSegmentVo>();
			// 原始List size =1 直接返回
			if (ts.size() == 1) {
				return ts;
			}
			result = check(ts);

		}
		return result;
	}

	/**
	 * 功能：生成时间段  支持多会议室，按会议室ID分组
	 *
	 * @return
	 */
	public Map<String, List<DateSegmentVo>> generatorDateSegmentGroup() {
		Map<String, List<DateSegmentVo>> resultMap = null;
		if (ts != null && ts.size() > 0) {
			resultMap = new HashMap<String, List<DateSegmentVo>>();
			for (int i = 0; i < ts.size(); i++) {
				DateSegmentVo tempDsVo = ts.get(i);
				String meetingRoomIdKey = tempDsVo.getMeetingRoomId();
				List<DateSegmentVo> tmpList = null;
				if (!resultMap.containsKey(meetingRoomIdKey)) {
					tmpList = new ArrayList<DateSegmentVo>();
					tmpList.add(tempDsVo);
					resultMap.put(meetingRoomIdKey, tmpList);
				} else {
					tmpList = resultMap.get(meetingRoomIdKey);
					tmpList.add(tempDsVo);
				}
			}
		}

		if (resultMap != null) {
			Set<String> resultSet = resultMap.keySet();
			if (resultSet != null && resultSet.size() > 0) {

				Iterator it = resultSet.iterator();
				while (it.hasNext()) {
					String meetingRoomId = (String) it.next();
					List<DateSegmentVo> list = resultMap.get(meetingRoomId);
					resultList=new ArrayList<DateSegmentVo>();
					List<DateSegmentVo>  checkList = check(list);
					resultMap.put(meetingRoomId, checkList);
				}

			}

		}
		return resultMap;
	}

	private List<DateSegmentVo> check(List<DateSegmentVo> paramList) {
		List<DateSegmentVo> listCopy = new ArrayList<DateSegmentVo>(paramList.size());
		resetList = new ArrayList<DateSegmentVo>();
		Iterator<DateSegmentVo> iterator = paramList.iterator();
		while (iterator.hasNext()) {
			try {
				listCopy.add((DateSegmentVo) (iterator.next().clone()));
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		DateSegmentVo firstVo = listCopy.get(0);
		listCopy.remove(0);
		resultList.add(firstVo);//

		Iterator<DateSegmentVo> iteratorCopy = listCopy.iterator();
		while (iteratorCopy.hasNext()) {
			DateSegmentVo copyNextVo = iteratorCopy.next();
			// 和FirstVo比较，是否重�?
			Boolean isSegment = checkTimeSegment(firstVo, copyNextVo);
			if (isSegment) {// 如果重合,reset firstVo
				Long firsts = firstVo.getStartDate().getTime();
				Long firste = firstVo.getEndDate().getTime();
				Long copys = copyNextVo.getStartDate().getTime();
				Long copye = copyNextVo.getEndDate().getTime();
				List<Long> soreList = new ArrayList<Long>();
				Map<Long, Date> tMap = new HashMap<Long, Date>();
				tMap.put(firsts, firstVo.getStartDate());
				tMap.put(firste, firstVo.getEndDate());
				tMap.put(copys, copyNextVo.getStartDate());
				tMap.put(copye, copyNextVo.getEndDate());
				soreList.add(firsts);
				soreList.add(firste);
				soreList.add(copys);
				soreList.add(copye);
				Collections.sort(soreList);
				firstVo.setStartDate(tMap.get(soreList.get(0)));//
				firstVo.setEndDate(tMap.get(soreList.get(3)));//
			} else {// 当前vo 加入到resetList
				resetList.add(copyNextVo);
			}
		}

		if (resetList != null && resetList.size() > 0) {
			check(resetList);
		}

		return resultList;
	}

	/**
	 * 功能：判断时间段是否重合
	 *
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public boolean checkTimeSegment(DateSegmentVo beginDate, DateSegmentVo endDate) {
		if (beginDate == null || endDate == null) {
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(TYPE_DEF);
		// 按开始时间排
		long btlong = Math.min(beginDate.getStartDate().getTime(), beginDate.getEndDate().getTime());
		long otlong = Math.max(beginDate.getStartDate().getTime(), beginDate.getEndDate().getTime());
		long stlong = Math.min(endDate.getStartDate().getTime(), endDate.getEndDate().getTime());
		long edlong = Math.max(endDate.getStartDate().getTime(), endDate.getEndDate().getTime());
		String btlongStr = null;
		String edlongStr = null;
		if (beginDate.getStartDate().before(endDate.getStartDate())) {
			btlongStr = getTime(beginDate.getEndDate(), null);
			edlongStr = getTime(endDate.getStartDate(), null);
		} else {
			btlongStr = getTime(endDate.getEndDate(), null);
			edlongStr = getTime(beginDate.getStartDate(), null);
		}

		if (btlongStr.equals(edlongStr)) {
			return false;
		}

		if ((stlong >= btlong && stlong <= otlong) || (edlong >= btlong && edlong <= otlong)) {
			long sblong = stlong >= btlong ? stlong : btlong;
			long eblong = otlong >= edlong ? edlong : otlong;
			// System.out.println("包含的开始时间是?" + sdf.format(sblong) + "-结束时间是：" +
			return true;
		} else {
			// System.out.println("无重合");
			return false;
		}

	}

	/**
	 * 将指定格式的字符串转换成日期类型
	 *
	 * @param dateStr
	 *            待转换的日期字符
	 * @param dateFormat
	 *            日期格式字符
	 * @return Date
	 */
	public Date convertStrToDate(String dateStr, String dateFormat) {
		if (dateStr == null || dateStr.equals("")) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		try {
			return sdf.parse(dateStr);
		} catch (Exception e) {
			throw new RuntimeException("DateUtil.convertStrToDate():" + e.getMessage());
		}
	}

	public String getTime(Date date, String type) {
		if (type == null || type.equals(""))
			type = "yyyy-MM-dd HH:mm:ss";
		DateFormat format = new SimpleDateFormat(type);
		String formatDate = format.format(date);
		return formatDate;
	}

	/**
	 * 功能:测试单会议室预订时间融合
	 */
	private static void  testSingleMettingRoom(){

		DateSegmentUtil dt = new DateSegmentUtil();
		Date st1 = dt.convertStrToDate("2017-10-19 11:11:11", "yyyy-MM-dd HH:mm:ss");
		Date end1 = dt.convertStrToDate("2017-10-19 12:20:30", "yyyy-MM-dd HH:mm:ss");

		Date st2 = dt.convertStrToDate("2017-10-19 11:30:11", "yyyy-MM-dd HH:mm:ss");
		Date end2 = dt.convertStrToDate("2017-10-19 12:35:30", "yyyy-MM-dd HH:mm:ss");

		Date st3 = dt.convertStrToDate("2017-10-19 12:35:30", "yyyy-MM-dd HH:mm:ss");
		Date end3 = dt.convertStrToDate("2017-10-19 16:50:50", "yyyy-MM-dd HH:mm:ss");

		dt.setSegment(st1, end1);
		dt.setSegment(st2, end2);
		dt.setSegment(st3, end3);

		dt.generatorDateSegment();

	}

	/**
	 * 功能：测试多会议室预订时间融合
	 */
	private static void  testMultMettingRoom(){

		DateSegmentUtil dt = new DateSegmentUtil();
		Date st1 = dt.convertStrToDate("2017-10-19 11:11:11", "yyyy-MM-dd HH:mm:ss");
		Date end1 = dt.convertStrToDate("2017-10-19 12:20:30", "yyyy-MM-dd HH:mm:ss");

		Date st2 = dt.convertStrToDate("2017-10-19 11:30:11", "yyyy-MM-dd HH:mm:ss");
		Date end2 = dt.convertStrToDate("2017-10-19 12:35:30", "yyyy-MM-dd HH:mm:ss");

		Date st3 = dt.convertStrToDate("2017-10-19 12:35:30", "yyyy-MM-dd HH:mm:ss");
		Date end3 = dt.convertStrToDate("2017-10-19 16:50:50", "yyyy-MM-dd HH:mm:ss");

		Date st4 = dt.convertStrToDate("2017-10-19 08:35:30", "yyyy-MM-dd HH:mm:ss");
		Date end4 = dt.convertStrToDate("2017-10-19 10:50:50", "yyyy-MM-dd HH:mm:ss");

		dt.setSegment(st1, end1,"tttddd");
		dt.setSegment(st2, end2,"tttddd");
		dt.setSegment(st3, end3,"tttddd");
		dt.setSegment(st4, end4,"111111");

		dt.generatorDateSegmentGroup();

	}

	public static void main(String[] args) {
		//单会议室数据融合
		testSingleMettingRoom();
		//多会议室数据融合
		testMultMettingRoom();
	}

}
