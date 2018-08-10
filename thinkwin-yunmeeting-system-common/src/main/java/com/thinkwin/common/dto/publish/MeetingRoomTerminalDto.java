package com.thinkwin.common.dto.publish;

import java.io.Serializable;
import java.util.List;

public class MeetingRoomTerminalDto implements Serializable {
	private static final long serialVersionUID = 4594410621235118973L;

	String id;
	String name;
	String location;
	Integer status;
	List<RoomTerminal> terminals;


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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<RoomTerminal> getTerminals() {
		return terminals;
	}

	public void setTerminals(List<RoomTerminal> terminals) {
		this.terminals = terminals;
	}
}
