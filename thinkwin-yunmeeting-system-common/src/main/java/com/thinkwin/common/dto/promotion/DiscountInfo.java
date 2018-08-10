package com.thinkwin.common.dto.promotion;

import java.io.Serializable;
import java.util.List;

public class DiscountInfo implements Serializable {
    private static final long serialVersionUID = -3758742963588083067L;
    private Integer extraServiceTerm;
    private Integer discount;
    private List<SkuDiscount> giveaway;

    public Integer getExtraServiceTerm() {
        return extraServiceTerm;
    }

    public void setExtraServiceTerm(Integer extraServiceTerm) {
        this.extraServiceTerm = extraServiceTerm;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public List<SkuDiscount> getGiveaway() {
        return giveaway;
    }

    public void setGiveaway(List<SkuDiscount> giveaway) {
        this.giveaway = giveaway;
    }
}
