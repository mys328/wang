package com.thinkwin.common.dto.promotion;

import java.io.Serializable;

public class PricingTier implements Serializable{

    private static final long serialVersionUID = -8655033311083601696L;
    private int unit;
    private int discount;
    public void setUnit(int unit) {
         this.unit = unit;
     }
     public int getUnit() {
         return unit;
     }

    public void setDiscount(int discount) {
         this.discount = discount;
     }
     public int getDiscount() {
         return discount;
     }

}