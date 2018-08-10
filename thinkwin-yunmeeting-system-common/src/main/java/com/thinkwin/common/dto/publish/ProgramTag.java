package com.thinkwin.common.dto.publish;

import java.io.Serializable;

public class ProgramTag implements Serializable {
	private static final long serialVersionUID = 2851915853747278588L;

	private String id;
	private String name;


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
