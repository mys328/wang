package com.thinkwin.common.dto.promotion;

import java.io.Serializable;

public class CapacityConfig implements Serializable{


    private static final long serialVersionUID = 4899055403092401914L;
    private String sku;
    private int qty;
    public void setSku(String sku) {
         this.sku = sku;
     }
     public String getSku() {
         return sku;
     }

    public void setQty(int qty) {
         this.qty = qty;
     }
     public int getQty() {
         return qty;
     }

}