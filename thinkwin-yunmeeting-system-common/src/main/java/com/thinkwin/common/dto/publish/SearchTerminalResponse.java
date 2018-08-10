package com.thinkwin.common.dto.publish;

import com.thinkwin.common.vo.publish.PageInfoVo;

import java.io.Serializable;
import java.util.List;

public class SearchTerminalResponse implements Serializable {
	private static final long serialVersionUID = 36125118682334445L;

	private PageInfoVo pageInfo;
	private List<TerminalDto> terminals;
	private TerminalStatsDto stats;

	public PageInfoVo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfoVo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public List<TerminalDto> getTerminals() {
		return terminals;
	}

	public void setTerminals(List<TerminalDto> terminals) {
		this.terminals = terminals;
	}

	public TerminalStatsDto getStats() {
		return stats;
	}

	public void setStats(TerminalStatsDto stats) {
		this.stats = stats;
	}
}
