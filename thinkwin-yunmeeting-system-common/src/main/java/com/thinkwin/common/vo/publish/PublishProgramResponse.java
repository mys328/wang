package com.thinkwin.common.vo.publish;

import java.io.Serializable;
import java.util.List;

public class PublishProgramResponse implements Serializable {
	private static final long serialVersionUID = 1867372417164345424L;

	private String requestId;
	private String msg;
	private List<PublishProgramErrorBlock> errMsg;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<PublishProgramErrorBlock> getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(List<PublishProgramErrorBlock> errMsg) {
		this.errMsg = errMsg;
	}

}
