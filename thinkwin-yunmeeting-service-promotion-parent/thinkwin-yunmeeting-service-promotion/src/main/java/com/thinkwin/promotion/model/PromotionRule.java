package com.thinkwin.promotion.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "promotion_rule")
public class PromotionRule {

	@Id
	@Column(name = "rule_code")
	private String ruleCode;

	@Column(name = "category")
	private Integer category;

	@Column(name = "accept_code")
	private String acceptCode;

	@Column(name = "except_code")
	private String exceptCode;

	@Column(name = "create_time")
	private Date createTime;

	@Column(name = "update_time")
	private Date updateTime;

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
