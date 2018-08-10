package com.thinkwin.common.dto.promotion;

import java.io.Serializable;

public class SkuDiscount implements Serializable{
    private static final long serialVersionUID = -3168555788104375401L;
    private String sku;
    private int qty;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
