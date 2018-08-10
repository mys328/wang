package com.thinkwin.common.dto.promotion;
import java.io.Serializable;
import java.util.List;

public class PricingConfigDto implements Serializable{

    private static final long serialVersionUID = 6085803682032844398L;
    private String id;
    private List<SkuPricingConfig> skuDiscount;
    private List<CapacityConfig> freeAccountConfig;
    private List<ServiceTermDiscountItem> serviceTermDiscount;
    public void setSkuDiscount(List<SkuPricingConfig> skuDiscount) {
         this.skuDiscount = skuDiscount;
     }
     public List<SkuPricingConfig> getSkuDiscount() {
         return skuDiscount;
     }

    public void setFreeAccountConfig(List<CapacityConfig> freeAccountConfig) {
         this.freeAccountConfig = freeAccountConfig;
     }
     public List<CapacityConfig> getFreeAccountConfig() {
         return freeAccountConfig;
     }

    public void setServiceTermDiscount(List<ServiceTermDiscountItem> serviceTermDiscount) {
         this.serviceTermDiscount = serviceTermDiscount;
     }
     public List<ServiceTermDiscountItem> getServiceTermDiscount() {
         return serviceTermDiscount;
     }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}