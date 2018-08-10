package com.thinkwin.common.dto.publish;

import com.thinkwin.common.vo.publish.TerminalScreenshotVo;

import java.io.Serializable;
import java.util.List;

public class CaptureScreenshotResponse implements Serializable {
	private static final long serialVersionUID = -6972525034873141213L;

	private String requestId;
	private List<TerminalScreenshotVo> terminals;


	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public List<TerminalScreenshotVo> getTerminals() {
		return terminals;
	}

	public void setTerminals(List<TerminalScreenshotVo> terminals) {
		this.terminals = terminals;
	}
}
