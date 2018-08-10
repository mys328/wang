package com.thinkwin.common.dto.promotion;

import java.util.List;

/**
 * 创建特权优惠DTO
 */
public class CouponSubmitDto {

    private String id;   //创建变空，修改传ID
    private String couponName;
    private String couponType; //特权券为T，赠送券为G，时长券为S，折扣券为Z
    private DiscountConfig discountConfig;
    private Long effectiveTime; //生效时间
    private Long expireTime; //失效时间
    private Integer totalQty; //数量
    private Integer isRelease; //是否发布

    //优惠值
    public static class DiscountConfig{
        private List<Giveaway> giveaway;
        private Integer discount; //折扣值
        private Integer extraServiceTerm; //增送时长

        public List<Giveaway> getGiveaway() {
            return giveaway;
        }

        public void setGiveaway(List<Giveaway> giveaway) {
            this.giveaway = giveaway;
        }

        public Integer getDiscount() {
            return discount;
        }

        public void setDiscount(Integer discount) {
            this.discount = discount;
        }

        public Integer getExtraServiceTerm() {
            return extraServiceTerm;
        }

        public void setExtraServiceTerm(Integer extraServiceTerm) {
            this.extraServiceTerm = extraServiceTerm;
        }
    }

    //优惠值-产品项
    public static class Giveaway{
        private String sku;
        private String name;
        private Integer qty;
        private String uom;

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public Integer getQty() {
            return qty;
        }

        public void setQty(Integer qty) {
            this.qty = qty;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUom() {
            return uom;
        }

        public void setUom(String uom) {
            this.uom = uom;
        }
    }


    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public DiscountConfig getDiscountConfig() {
        return discountConfig;
    }

    public void setDiscountConfig(DiscountConfig discountConfig) {
        this.discountConfig = discountConfig;
    }

    public Long getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Long effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Integer totalQty) {
        this.totalQty = totalQty;
    }

    public Integer getIsRelease() {
        return isRelease;
    }

    public void setIsRelease(Integer isRelease) {
        this.isRelease = isRelease;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
