package com.thinkwin.promotion.localService.impl;

import com.thinkwin.common.model.promotion.Coupon;
import com.thinkwin.promotion.localService.LocalCouponService;
import com.thinkwin.promotion.mapper.CouponMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("localCouponService")
public class LocalCouponServiceImpl implements LocalCouponService {
    @Autowired
    CouponMapper couponMapper;

    /**
     * 根据id获取对象
     *
     * @param id
     * @return
     */
    @Override
    public Coupon get(String id) {

        return this.couponMapper.selectByPrimaryKey(id);
    }

    /**
     * 修改对象，根据id
     *
     * @param coupon
     * @return
     */
    @Override
    public boolean update(Coupon coupon) {

        int n=this.couponMapper.updateByPrimaryKey(coupon);
        if(n>0){
            return true;
        }
        return false;
    }

    /**
     * 根据couponBatchId获取优惠券
     *
     * @param couponBatchId
     * @return
     */
    @Override
    public List<Coupon> findByCouponBatchId(String couponBatchId) {
        Coupon coupon=new Coupon();
        coupon.setBatchId(couponBatchId);
        return this.couponMapper.select(coupon);
    }
}
