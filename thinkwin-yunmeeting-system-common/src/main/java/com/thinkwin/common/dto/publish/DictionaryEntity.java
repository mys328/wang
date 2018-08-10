package com.thinkwin.common.dto.publish;

import java.io.Serializable;

public class DictionaryEntity implements Serializable {
	private static final long serialVersionUID = 5795440555170808872L;

	private String name;
	private String code;
	private Integer isDefault;

	public DictionaryEntity(String name, String code){
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
	}
}
