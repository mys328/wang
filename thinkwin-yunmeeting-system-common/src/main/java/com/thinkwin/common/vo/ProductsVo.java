package com.thinkwin.common.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/1/29 0029.
 */
public class ProductsVo implements Serializable {
    private static final long serialVersionUID = -6969996318464550393L;
    /**
     * 结束时间
     */
    private Date expireTime;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 租户付费剩余时间
     */
    private String surplusTime;

    private List<ProductVo> vos;


    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public List<ProductVo> getVos() {
        return vos;
    }

    public void setVos(List<ProductVo> vos) {
        this.vos = vos;
    }

    public String getSurplusTime() {
        return surplusTime;
    }

    public void setSurplusTime(String surplusTime) {
        this.surplusTime = surplusTime;
    }
}
