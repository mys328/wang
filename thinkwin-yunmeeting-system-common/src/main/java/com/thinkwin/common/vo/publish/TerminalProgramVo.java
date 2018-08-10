package com.thinkwin.common.vo.publish;

import java.io.Serializable;

public class TerminalProgramVo implements Serializable {
	private static final long serialVersionUID = 451926374460466250L;

	String id;
	String name;
	Integer status;
	Long startTime;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
}
