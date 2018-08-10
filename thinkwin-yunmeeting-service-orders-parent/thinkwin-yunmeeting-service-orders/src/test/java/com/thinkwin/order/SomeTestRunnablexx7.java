package com.thinkwin.order;

public class SomeTestRunnablexx7 implements Runnable {

	public String getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	private String meetingId;

	@Override
	public void run() {
		TestUtil.bytes2hex01(null);
		try{
		} catch (Exception ex){
			ex.printStackTrace();
		}
		finally {
			System.out.println("execute complete.");
		}
	}
}
