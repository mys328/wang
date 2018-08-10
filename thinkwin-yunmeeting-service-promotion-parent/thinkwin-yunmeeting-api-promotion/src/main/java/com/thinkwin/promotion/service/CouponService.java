package com.thinkwin.promotion.service;
import com.thinkwin.common.dto.promotion.CouponDto;
import com.thinkwin.common.model.promotion.Coupon;

public interface CouponService {

    /**
     * 获取优惠券信息
     * @param code
     * @return
     */
    Coupon getCoupon(String code);

    /**
     * 获取优惠券信息
     * @param code 优惠券码
     * @return
     */
    CouponDto getCouponByCode(String code);

    /**
     * 消费优惠券
     * @param couponId 优惠券Id
     * @param orderId 订单Id
     * @return
     */
    boolean consumeCoupon(String couponId, String orderId);

    /**
     * 返还优惠券
     * @param couponId 优惠券Id
     * @param orderId 订单Id
     * @return
     */
    boolean returnCoupon(String couponId, String orderId);

    /**
     * 获取特权优惠券
     * @param couponBatchId  批次Id
     * @return
     */
    Coupon findByCouponBatchId(String couponBatchId);

    boolean update(Coupon coupon);

    /**
     * 获取某个批次下优惠券的使用数量
     * @param couponBatchId
     * @return
     */
    Integer getConsumeCouponCount(String couponBatchId);
}
