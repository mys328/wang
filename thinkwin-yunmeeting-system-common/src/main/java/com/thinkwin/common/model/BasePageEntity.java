package com.thinkwin.common.model;

import java.io.Serializable;

/**
 * 类名: BasePageEntity </br>
 * 描述: 基础分页实体类 </br>
 * 开发人员： weining </br>
 * 创建时间：  2017/7/20 </br>
 */
public class BasePageEntity implements Serializable{

    private static final long serialVersionUID = -4831157451582837743L;
    private Integer currentPage = 1;

    private Integer pageSize = 15;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
