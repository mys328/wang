package com.thinkwin.promotion.service.impl;

import com.alibaba.fastjson.JSON;
import com.thinkwin.common.dto.promotion.CouponDto;
import com.thinkwin.common.dto.promotion.DiscountInfo;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.promotion.Coupon;
import com.thinkwin.common.model.promotion.CouponBatch;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.promotion.mapper.CouponMapper;
import com.thinkwin.promotion.service.CouponBatchService;
import com.thinkwin.promotion.service.CouponService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("couponService")
public class CouponServiceImpl implements CouponService {
    @Autowired
    CouponMapper couponMapper;
    @Autowired
    CouponBatchService batchService;
    @Autowired
    SaasTenantService saasTenantService;

    @Override
    public Coupon getCoupon(String code) {
        if(StringUtils.isNotBlank(code)){
            Coupon coupon = new Coupon();
            coupon.setCouponCode(code);
            List<Coupon> coupons = couponMapper.select(coupon);
            if(coupons!=null &&coupons.size()>0){
                return coupons.get(0);
            }
        }
        return null;
    }

    @Override
    public CouponDto getCouponByCode(String code) {
        if(StringUtils.isNotBlank(code)) {
            Coupon coupon = this.getCoupon(code);
            if(coupon!=null){
                CouponDto dto = new CouponDto();
                String batchId = coupon.getBatchId();
                dto.setId(coupon.getId());
                dto.setCouponCode(coupon.getCouponCode());
                dto.setBatchId(batchId);
                dto.setStatus(coupon.getStatus());
                dto.setObtainedBy(coupon.getObtainedBy());
                dto.setTenantId(coupon.getTenantId());
                dto.setSalesAgentId(coupon.getSalesAgentId());
                dto.setUsedBy(coupon.getUsedBy());
                CouponBatch batch = batchService.getCouponBatchById(batchId);
                if(batch!=null){
                    dto.setBatchNum(batch.getBatchNum());
                    dto.setCouponName(batch.getCouponName());
                    dto.setEffectiveTime(batch.getEffectiveTime().getTime());
                    dto.setExpireTime(batch.getExpireTime().getTime());
                    dto.setCouponType(batch.getCouponType());
                    dto.setCreatedBy(batch.getCreatedBy());
                    dto.setDiscountInfo(JSON.parseObject(batch.getDiscountConfig(), DiscountInfo.class));
                }
                return dto;
            }
        }
        return null;
    }

    @Override
    public boolean consumeCoupon(String couponId, String orderId) {
        if(StringUtils.isNotBlank(couponId)&&StringUtils.isNotBlank(orderId)){
            Coupon coupon = couponMapper.selectByPrimaryKey(couponId);
            if(null!=coupon){
                Integer status = coupon.getStatus();
                if(status==0){
                    coupon.setOrderId(orderId);
                    coupon.setTenantId(TenantContext.getUserInfo().getTenantId());
                    SaasTenant saasTenant = saasTenantService.selectByIdSaasTenantInfo(TenantContext.getUserInfo().getTenantId());
                    coupon.setUsedBy(saasTenant.getTenantName());
                    coupon.setUsedAt(new Date());
                    coupon.setStatus(1);
                    int i = couponMapper.updateByPrimaryKeySelective(coupon);
                    if(i>0){
                        CouponBatch batch = batchService.getCouponBatchById(coupon.getBatchId());
                        Integer usedQty = batch.getUsedQty();
                        usedQty+=1; //批次中使用数量同步+1
                        batch.setUsedQty(usedQty);
                        boolean b = batchService.updateCouponBatch(batch);
                        if(b){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean returnCoupon(String couponId, String orderId) {
        if(StringUtils.isNotBlank(couponId)){
            Coupon coupon = couponMapper.selectByPrimaryKey(couponId);
            if(null!=coupon){
                coupon.setOrderId("");
                coupon.setStatus(0);
                coupon.setUsedBy("");
                coupon.setTenantId("");
                coupon.setUsedAt(null);
                int i = couponMapper.updateByPrimaryKey(coupon);
                if(i>0){
                    CouponBatch batch = batchService.getCouponBatchById(coupon.getBatchId());
                    Integer usedQty = batch.getUsedQty();
                    if(usedQty>0){
                        usedQty-=1; //批次中使用数量同步-1
                    }
                    batch.setUsedQty(usedQty);
                    boolean b = batchService.updateCouponBatch(batch);
                    if(b){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取特权优惠券
     *
     * @param couponBatchId 批次Id
     * @return
     */
    @Override
    public Coupon findByCouponBatchId(String couponBatchId) {
        if(StringUtils.isNotBlank(couponBatchId)){
            Coupon coupon = new Coupon();
            coupon.setBatchId(couponBatchId);
            List<Coupon> coupons = couponMapper.select(coupon);
            if(coupons!=null &&coupons.size()>0){
                return coupons.get(0);
            }
        }
        return null;
    }

    @Override
    public boolean update(Coupon coupon) {
        int i=this.couponMapper.updateByPrimaryKey(coupon);
        if(i>0){
            return true;
        }
        return false;
    }

    @Override
    public Integer getConsumeCouponCount(String couponBatchId) {
        if(StringUtils.isNotBlank(couponBatchId)){
            Coupon coupon = new Coupon();
            coupon.setBatchId(couponBatchId);
            coupon.setStatus(1); //已使用
            List<Coupon> select = couponMapper.select(coupon);
            if(select!=null){
                return select.size();
            }
        }
        return 0;
    }
}
