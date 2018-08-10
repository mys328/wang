package com.thinkwin.common.vo.publish;

import java.io.Serializable;

public class PageInfoVo implements Serializable {
	private static final long serialVersionUID = 3274922744773557283L;

	Integer pageNum;
	Integer pages;
	Long total;
	boolean more;


	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public boolean isMore() {
		return more;
	}

	public void setMore(boolean more) {
		this.more = more;
	}
}
