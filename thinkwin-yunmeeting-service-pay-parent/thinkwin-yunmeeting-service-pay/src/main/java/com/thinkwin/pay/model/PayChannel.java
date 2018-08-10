package com.thinkwin.pay.model;


import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class PayChannel {
	@Column(name = "code")
	private Integer code;

	@Column(name = "name")
	private String name;

	@Column(name = "display_name")
	private String displayName;


	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
