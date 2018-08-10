package com.thinkwin.common.vo;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/29 0029.
 */
public class ProductVo implements Serializable{
    private static final long serialVersionUID = -8567322298834374612L;
    /**
     * 信息
     */
    private String displayName;
    /**
     * 数量
     */
    private String qty;
    /**
     * 单位
     */
    private String uom;


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }
}
