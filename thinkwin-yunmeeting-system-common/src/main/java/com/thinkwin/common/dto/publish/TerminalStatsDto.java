package com.thinkwin.common.dto.publish;

import java.io.Serializable;

public class TerminalStatsDto implements Serializable {
	private static final long serialVersionUID = -5473718379393928844L;

	private Integer total;
	private Integer online;
	private Integer offline;

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getOnline() {
		if(null == online){
			return 0;
		}
		return online;
	}

	public void setOnline(Integer online) {
		this.online = online;
	}

	public Integer getOffline() {
		if(null == offline){
			return 0;
		}
		return offline;
	}

	public void setOffline(Integer offline) {
		this.offline = offline;
	}
}
