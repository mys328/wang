package com.thinkwin.promotion.service;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.promotion.Coupon;
import com.thinkwin.common.model.promotion.CouponBatch;

import java.util.List;

public interface CouponBatchService {

    /**
     * 创建(保存或发布)优惠券
     * @param couponBatch
     * @param releaseState 保存0  发布1
     * @return
     */
    boolean addCouponBatch(CouponBatch couponBatch,Integer releaseState);

    /**
     * 获取优惠券列表
     * @param word 搜索关键字
     * @param basePageEntity
     * @return
     */
    PageInfo getCouponBatchList(String word, BasePageEntity basePageEntity);

    /**
     * 根据优惠券批次Id获取单个批次信息
     * @param batchId 优惠券批次Id
     * @return
     */
    CouponBatch getCouponBatchById(String batchId);

    /**
     * 更新优惠券批次信息
     * @param couponBatch
     * @return
     */
    boolean updateCouponBatch(CouponBatch couponBatch);

    /**
     * 删除优惠券批次信息
     * @param batchId
     * @return
     */
    boolean deleteCouponBatch(String batchId);

    /**
     * 使优惠券批次失效（设置失效时间为当前时间）
     * @param batchId
     * @return
     */
    boolean setCouponBatchInvalid(String batchId);

    /**
     * 根据优惠券批次Id获取优惠券列表
     * @param batchId 优惠券批次Id
     * @return
     */
    List<Coupon> getCouponListByBatchId(String batchId);

    /**
     * 用户是否符合使用限制
     * @param tenantId 租户Id
     * @param couponCode 优惠券码
     * @return 符合true 不符合false
     */
    boolean verifyCouponBatchLimit(String tenantId,String couponCode);
}
