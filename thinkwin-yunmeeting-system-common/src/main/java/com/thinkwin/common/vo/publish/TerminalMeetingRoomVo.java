package com.thinkwin.common.vo.publish;

import java.io.Serializable;

public class TerminalMeetingRoomVo implements Serializable {
	private static final long serialVersionUID = -1355811695020449599L;

	String id;
	String name;


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
}
