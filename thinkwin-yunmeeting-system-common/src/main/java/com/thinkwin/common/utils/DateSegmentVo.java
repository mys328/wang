package com.thinkwin.common.utils;

import java.util.Date;

/**
 * 
 * @author yangyiqian
 *
 */

public class DateSegmentVo implements Cloneable{

	//开始时间
	private Date startDate;
	//结束时间
	private Date endDate;
	//会议室ID
	private String meetingRoomId;

	public String getMeetingRoomId() {
		return meetingRoomId;
	}

	public void setMeetingRoomId(String meetingRoomId) {
		this.meetingRoomId = meetingRoomId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
    @Override  
    protected Object clone() throws CloneNotSupportedException {  
        return super.clone();  
    }

}
