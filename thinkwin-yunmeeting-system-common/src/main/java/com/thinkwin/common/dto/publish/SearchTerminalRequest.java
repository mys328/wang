package com.thinkwin.common.dto.publish;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class SearchTerminalRequest implements Serializable{
	private static final long serialVersionUID = -2805241021561234743L;

	//搜索关键字
	String word;

	//终端状态，与word不同时为空，在线：1；离线：0
	Integer status;

	//页数，默认第一页
	Integer pageNum;

	//每页条数，默认为15
	Integer pageSize;

	//类型 pc时为空
	@JsonIgnore
	private String type;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
