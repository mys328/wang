package com.thinkwin.promotion.timer;

import com.thinkwin.common.model.promotion.Coupon;
import com.thinkwin.common.model.promotion.CouponBatch;
import com.thinkwin.promotion.localService.LocalCouponBatchService;
import com.thinkwin.promotion.localService.LocalCouponService;
import com.thinkwin.promotion.timer.Utils.ContextHolder;

import java.util.List;

/*
 * 类说明：
 * @author lining 2018/2/5
 * @version 1.0
 *
 */
public class CouponBatchStatusTask implements Runnable {

    final Integer couponBatchStatus=1; //发布状态

    private String couponBatchId;
    private Integer status;


    @Override
    public void run() {
        LocalCouponBatchService localCouponBatchService= (LocalCouponBatchService) ContextHolder.getApplicationContext().getBean("localCouponBatchService");
        CouponBatch couponBatch=localCouponBatchService.get(couponBatchId);
        if(null!=couponBatch){

            LocalCouponService localCouponService= (LocalCouponService)ContextHolder.getApplicationContext().getBean("localCouponService");
            List<Coupon> coupons=localCouponService.findByCouponBatchId(couponBatch.getId());
            Coupon coupon=(null!=coupons?coupons.get(0):null);
            int s=couponBatch.getStatus();
            if(couponBatchStatus==s){
                couponBatch.setStatus(2);
                localCouponBatchService.update(couponBatch);
            }

            if(null!=coupon && coupon.getStatus()==0){
                coupon.setStatus(-1);
                localCouponService.update(coupon);
            }

            //RedisUtil.remove("QYH_CouponBatch_Timer_" + couponBatchId );

        }

    }

    public String getCouponBatchId() {
        return couponBatchId;
    }

    public void setCouponBatchId(String couponBatchId) {
        this.couponBatchId = couponBatchId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
