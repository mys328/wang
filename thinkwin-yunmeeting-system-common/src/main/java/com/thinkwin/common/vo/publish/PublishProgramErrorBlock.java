package com.thinkwin.common.vo.publish;

import java.io.Serializable;
import java.util.List;

public class PublishProgramErrorBlock implements Serializable {
	private static final long serialVersionUID = -5838775784375273374L;

	private List<String> terminals;
	private String msg;

	public List<String> getTerminals() {
		return terminals;
	}

	public void setTerminals(List<String> terminals) {
		this.terminals = terminals;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
