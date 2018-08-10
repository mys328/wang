package com.thinkwin.common.vo.publish;

import java.io.Serializable;
import java.util.List;

public class CaptureScreenshotRequest implements Serializable {
	private static final long serialVersionUID = 4577397292383285497L;

	private String requestId;
	private List<String> ids;


	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}
}
