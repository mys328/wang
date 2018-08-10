package com.thinkwin.pay.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
public class PromotionRule {
	@Column(name = "code")
	private String code;

	@Column(name = "category")
	private String category;

	@Column(name = "accept_code")
	private String acceptCode;

	@Column(name = "except_code")
	private String exceptCode;

	@Column(name = "inspire_time")
	private Date inspireTime;

	@Column(name = "expire_time")
	private Date expireTime;

	@Column(name = "effective_time_filter")
	private String effectiveTimeFilter;

	@Column(name = "status")
	private Integer status;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAcceptCode() {
		return acceptCode;
	}

	public void setAcceptCode(String acceptCode) {
		this.acceptCode = acceptCode;
	}

	public String getExceptCode() {
		return exceptCode;
	}

	public void setExceptCode(String exceptCode) {
		this.exceptCode = exceptCode;
	}

	public Date getInspireTime() {
		return inspireTime;
	}

	public void setInspireTime(Date inspireTime) {
		this.inspireTime = inspireTime;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public String getEffectiveTimeFilter() {
		return effectiveTimeFilter;
	}

	public void setEffectiveTimeFilter(String effectiveTimeFilter) {
		this.effectiveTimeFilter = effectiveTimeFilter;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
