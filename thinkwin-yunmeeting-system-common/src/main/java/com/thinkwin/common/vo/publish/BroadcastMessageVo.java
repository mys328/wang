package com.thinkwin.common.vo.publish;

import com.thinkwin.common.dto.publish.BroadcastCommand;
import com.thinkwin.common.dto.publish.BroadcastCommandVo;

import java.io.Serializable;
import java.util.List;

public class BroadcastMessageVo implements Serializable {
	private static final long serialVersionUID = 3527805645466365790L;

	private List<String> terminals;
	private BroadcastCommandVo message;


	public BroadcastCommandVo getMessage() {
		return message;
	}

	public void setMessage(BroadcastCommandVo message) {
		this.message = message;
	}

	public List<String> getTerminals() {
		return terminals;
	}

	public void setTerminals(List<String> terminals) {
		this.terminals = terminals;
	}

}
