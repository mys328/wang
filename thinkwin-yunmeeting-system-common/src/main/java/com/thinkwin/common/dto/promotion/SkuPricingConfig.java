package com.thinkwin.common.dto.promotion;

import java.io.Serializable;

public class SkuPricingConfig implements Serializable{

    private static final long serialVersionUID = -6693992590422324714L;
    private String sku;
    private String name;
    private SkuDiscountConfig config;
    private int discountOption;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSku(String sku) {
         this.sku = sku;
     }
     public String getSku() {
         return sku;
     }

    public void setConfig(SkuDiscountConfig config) {
         this.config = config;
     }
     public SkuDiscountConfig getConfig() {
         return config;
     }

    public void setDiscountOption(int discountOption) {
         this.discountOption = discountOption;
     }
     public int getDiscountOption() {
         return discountOption;
     }

}