package com.thinkwin.common.dto.publish;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thinkwin.common.vo.publish.PageInfoVo;

import java.io.Serializable;
import java.util.List;

public class GetProgramResponse<T> implements Serializable {
	private static final long serialVersionUID = -1738948498150264036L;

	private PageInfoVo pageInfo;
	private List<T> programs;

	public PageInfoVo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfoVo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public List<T> getPrograms() {
		return programs;
	}

	public void setPrograms(List<T> programs) {
		this.programs = programs;
	}
}
