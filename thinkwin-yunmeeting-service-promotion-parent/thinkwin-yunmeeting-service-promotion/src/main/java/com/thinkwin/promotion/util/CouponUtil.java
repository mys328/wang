package com.thinkwin.promotion.util;

import com.thinkwin.promotion.model.Coupon;
import com.thinkwin.promotion.vo.CouponVo;

public class CouponUtil {
	public static CouponVo fromCoupon(Coupon coupon){
		CouponVo vo = new CouponVo();
		vo.setCouponCode(coupon.getCouponCode());
		vo.setName(coupon.getName());
		vo.setPromotionCode(coupon.getCouponCode());
		vo.setOrderId(coupon.getOrderId());
		vo.setCreateTime(coupon.getCreateTime());
		vo.setEffectiveTime(coupon.getEffectiveTime());
		vo.setExpireTime(coupon.getExpireTime());
		vo.setTenantId(coupon.getTenantId());
		return vo;
	}
}
