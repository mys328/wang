package com.thinkwin.promotion.localService;

import com.thinkwin.common.model.promotion.CouponBatch;
import org.springframework.stereotype.Service;

@Service("localCouponBatchService")
public interface LocalCouponBatchService {


    /**
     * 根据id获取对象
     * @param id
     * @return
     */
    CouponBatch get(String id);

    /**
     * 修改对象，根据id
     * @param couponBatch
     * @return
     */
    boolean update(CouponBatch couponBatch);
}
