package com.thinkwin.promotion.localService;

import com.thinkwin.common.model.promotion.Coupon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("localCouponService")
public interface LocalCouponService {

    /**
     * 根据id获取对象
     * @param id
     * @return
     */
    Coupon get(String id);

    /**
     * 修改对象，根据id
     * @param coupon
     * @return
     */
    boolean update(Coupon coupon);


    /**
     * 根据couponBatchId获取优惠券
     * @param couponBatchId
     * @return
     */
    List<Coupon> findByCouponBatchId(String couponBatchId);


}
