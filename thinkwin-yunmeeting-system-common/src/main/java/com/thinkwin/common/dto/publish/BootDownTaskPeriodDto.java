package com.thinkwin.common.dto.publish;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BootDownTaskPeriodDto implements Serializable {
	private static final long serialVersionUID = -2162552844921535502L;
	private String id;
	private String weekly;
	private List<Map<String,Object>> times;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWeekly() {
		return weekly;
	}

	public void setWeekly(String weekly) {
		this.weekly = weekly;
	}

	public List<Map<String, Object>> getTimes() {
		return times;
	}

	public void setTimes(List<Map<String, Object>> times) {
		this.times = times;
	}
}
