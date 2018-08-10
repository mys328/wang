package com.thinkwin.common.dto.publish;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandMessage implements Serializable {
	private static final long serialVersionUID = 8667764621600941379L;

	private List<String> terminals;
	private String tenantId;
	private String requestId;
	private String cmd;
	private Object data;

	//push服务需要在发送指令后确认回执
	private boolean acknowledge = false;
	private long timestamp;

	public CommandMessage(){
		this.terminals = new ArrayList<>();
	}

	public void addTerminal(String... terminalId){
		this.terminals.addAll(Arrays.asList(terminalId));
	}

	public List<String> getTerminals() {
		return terminals;
	}

	public void setTerminals(List<String> terminals) {
		this.terminals = terminals;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public boolean isAcknowledge() {
		return acknowledge;
	}

	public void setAcknowledge(boolean acknowledge) {
		this.acknowledge = acknowledge;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "CommandMessage{" +
				"terminals=" + terminals +
				", tenantId='" + tenantId + '\'' +
				", requestId='" + requestId + '\'' +
				", cmd='" + cmd + '\'' +
				", data=" + data +
				", acknowledge=" + acknowledge +
				", timestamp=" + timestamp +
				'}';
	}
}
