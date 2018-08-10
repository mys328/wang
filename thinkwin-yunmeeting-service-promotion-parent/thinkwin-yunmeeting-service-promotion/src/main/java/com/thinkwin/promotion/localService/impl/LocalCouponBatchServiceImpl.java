package com.thinkwin.promotion.localService.impl;

import com.thinkwin.common.model.promotion.CouponBatch;
import com.thinkwin.promotion.localService.LocalCouponBatchService;
import com.thinkwin.promotion.mapper.CouponBatchMapper;
import com.thinkwin.promotion.mapper.CouponMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("localCouponBatchService")
public class LocalCouponBatchServiceImpl implements LocalCouponBatchService {

    @Autowired
    CouponBatchMapper couponBatchMapper;
    @Autowired
    CouponMapper couponMapper;

    /**
     * 根据id获取对象
     *
     * @param id
     * @return
     */
    @Override
    public CouponBatch get(String id) {
        return this.couponBatchMapper.selectByPrimaryKey(id);
    }

    /**
     * 修改对象，根据id
     *
     * @param couponBatch
     * @return
     */
    @Override
    public boolean update(CouponBatch couponBatch) {

        int f=this.couponBatchMapper.updateByPrimaryKey(couponBatch);
        if(f>0){
            return true;
        }
        return false;
    }
}
