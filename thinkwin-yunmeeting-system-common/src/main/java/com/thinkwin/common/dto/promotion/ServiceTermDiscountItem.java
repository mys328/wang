package com.thinkwin.common.dto.promotion;

import java.io.Serializable;

public class ServiceTermDiscountItem implements Serializable{

    private static final long serialVersionUID = 6178280010581111853L;
    private int unit;
    private int uom; //年0  月1
    private int discount;
    public void setUnit(int unit) {
         this.unit = unit;
     }
     public int getUnit() {
         return unit;
     }

    public int getUom() {
        return uom;
    }

    public void setUom(int uom) {
        this.uom = uom;
    }

    public void setDiscount(int discount) {
         this.discount = discount;
     }
     public int getDiscount() {
         return discount;
     }

}