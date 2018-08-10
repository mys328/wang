package com.thinkwin.common.model.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "`info_program_components`")
public class InfoProgramComponent implements Serializable {

	@Id
	@Column(name = "`id`")
	private String id;

	@Column(name = "`comp_name`")
	private String compName;

	@Column(name = "`c_code`")
	private String code;

	@Column(name = "`creater`")
	private String creater;

	@Column(name = "`creat_time`")
	private Date creatTime;

	@Column(name = "`modifier`")
	private String modifier;

	@Column(name = "`modify_time`")
	private Date modifyTime;

	@Column(name = "`ver`")
	private String ver;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}
}
