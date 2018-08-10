package com.thinkwin.common.vo.publish;

import java.io.Serializable;

public class TerminalMessageVo implements Serializable {
	private static final long serialVersionUID = 6193452946254661020L;

	String id;
	String content;
	Integer status;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
