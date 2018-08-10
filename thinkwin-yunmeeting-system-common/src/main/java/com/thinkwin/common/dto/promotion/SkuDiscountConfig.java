package com.thinkwin.common.dto.promotion;
import java.io.Serializable;
import java.util.List;

public class SkuDiscountConfig implements Serializable{

    private static final long serialVersionUID = 8966542398046888219L;
    private double unitPrice;
    private int step;
    private int min;
    private int max;
    private int free;
    private List<PricingTier> tiers;
    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    public void setStep(int step) {
         this.step = step;
     }
     public int getStep() {
         return step;
     }

    public void setMin(int min) {
         this.min = min;
     }
     public int getMin() {
         return min;
     }

    public void setMax(int max) {
         this.max = max;
     }
     public int getMax() {
         return max;
     }

    public void setFree(int free) {
         this.free = free;
     }
     public int getFree() {
         return free;
     }

    public void setTiers(List<PricingTier> tiers) {
         this.tiers = tiers;
     }
     public List<PricingTier> getTiers() {
         return tiers;
     }

}