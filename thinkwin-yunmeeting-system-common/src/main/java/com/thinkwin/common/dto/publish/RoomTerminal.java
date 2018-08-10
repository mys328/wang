package com.thinkwin.common.dto.publish;

import java.io.Serializable;

public class RoomTerminal implements Serializable {
	private static final long serialVersionUID = 4838179887633985129L;

	String id;
	String type;
	String name;
	String version;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
