package com.thinkwin.promotion.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.promotion.Coupon;
import com.thinkwin.common.model.promotion.CouponBatch;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.PingYinUtil;
import com.thinkwin.common.utils.TimeUtil;
import com.thinkwin.console.service.SaasUserService;
import com.thinkwin.promotion.mapper.CouponBatchMapper;
import com.thinkwin.promotion.mapper.CouponMapper;
import com.thinkwin.promotion.service.CouponBatchService;
import com.thinkwin.promotion.timer.CouponBatchStatusTask;
import com.thinkwin.schedule.service.TimerService;
import com.thinkwin.serialnumber.service.SerialNumberService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service("couponBatchService")
public class CouponBatchServiceImpl implements CouponBatchService {

    @Autowired
    CouponBatchMapper couponBatchMapper;
    @Autowired
    CouponMapper couponMapper;
    @Autowired
    SerialNumberService serialNumberService;
    @Autowired
    SaasUserService saasUserService;
    @Resource
    TimerService timerService;

    @Override
    public boolean addCouponBatch(CouponBatch couponBatch,Integer releaseState) {
        boolean result = false;
        int i = 0;
        Date today=new Date();
        if(null != couponBatch){
            String id = couponBatch.getId();
            String couponType = couponBatch.getCouponType();
            String couponCode = "T";

            if(StringUtils.isNotBlank(couponBatch.getCouponName())){
                couponBatch.setCouponNamePinyin(PingYinUtil.getPingYin(couponBatch.getCouponName()));
            }
            if(StringUtils.isNotBlank(couponType)){
                couponCode = couponType;
            }
            couponBatch.setStatus(releaseState);
            if(releaseState==1){
                couponBatch.setIssueMode(1);
                couponBatch.setIssuedQty(couponBatch.getTotalQty());
            }else{
                couponBatch.setIssueMode(0);
                couponBatch.setIssuedQty(0);
            }
            //生成批次号
            String batchNum = "000";
            if(releaseState==1){
                Example ex = new Example(CouponBatch.class);
                ex.createCriteria().andNotEqualTo("batchNum","-");
                ex.setOrderByClause("create_time desc,batch_num desc");
                List<CouponBatch> couponBatches = couponBatchMapper.selectByExample(ex);
                if(couponBatches!=null&&couponBatches.size()>0){
                    String num = couponBatches.get(0).getBatchNum();
                    String regex=".*[a-zA-Z]+.*";
                    Matcher matcher = Pattern.compile(regex).matcher(num);
                    if(matcher.matches()){
                        num = num.substring(1,num.length());
                    }
                    int n = Integer.valueOf(num);
                    n+=1;
                    if(n<10){
                        batchNum = "00"+n;
                    }else if(n<100){
                        batchNum = "0"+n;
                    }else {
                        batchNum = ""+n;
                    }
                }
                batchNum=couponCode+batchNum;
            }else{
                batchNum="-";
            }
            if(StringUtils.isNotBlank(id)){ //更新操作
                couponBatch.setUpdateTime(today);
                couponBatch.setCreateTime(today);
                if(StringUtils.isBlank(couponBatch.getBatchNum())){
                    couponBatch.setBatchNum(batchNum);
                }
                i = couponBatchMapper.updateByPrimaryKeySelective(couponBatch);
            }else { //新增
                couponBatch.setId(CreateUUIdUtil.Uuid());
                couponBatch.setCreateTime(today);
                couponBatch.setLimit(1);
                couponBatch.setBatchNum(batchNum);
                i = couponBatchMapper.insertSelective(couponBatch);
            }

            if(releaseState==1){
                //发布操作时级联创建优惠券
                Integer totalQty = couponBatch.getTotalQty();
                if(totalQty==null){
                    totalQty =1;
                }
                for (int j = 0; j < totalQty; j++) {
                    Coupon coupon = new Coupon();
                    coupon.setBatchId(couponBatch.getId());
                    coupon.setId(CreateUUIdUtil.Uuid());
                    coupon.setStatus(0);
                    couponCode = couponCode + TimeUtil.getStringRandom(11);
                    coupon.setCouponCode(couponCode);
                    coupon.setObtainedBy(TenantContext.getUserInfo().getUserName());
                    couponMapper.insertSelective(coupon);
                }
            }
            if(i>0){
                result = true;

                //定时检查优惠券状态
                this.settingCouponbatchTaskTimer(couponBatch);
            }


        }
        return result;
    }

    @Override
    public PageInfo getCouponBatchList(String word, BasePageEntity basePageEntity) {
        Example example = new Example(CouponBatch.class);
        if(StringUtils.isNotBlank(word)){
            example.or().andLike("batchNum","%"+word+"%");
            example.or().andLike("couponName","%"+word+"%");
            example.or().andLike("couponNamePinyin","%"+word+"%");
            example.or().andLike("discountConfig","%"+word+"%");
        }
//        example.setOrderByClause("create_time desc");
        example.setOrderByClause("status asc,create_time desc");
        if(basePageEntity!=null){
            PageHelper.startPage(basePageEntity.getCurrentPage(),basePageEntity.getPageSize());
        }
        List<CouponBatch> couponBatches = couponBatchMapper.selectByExample(example);


        return new PageInfo<>(couponBatches);
    }

    @Override
    public CouponBatch getCouponBatchById(String batchId) {
        if(StringUtils.isNotBlank(batchId)){
            CouponBatch couponBatch= couponBatchMapper.selectByPrimaryKey(batchId);
            if(null!=couponBatch) {
                Coupon coupon = new Coupon();
                coupon.setBatchId(couponBatch.getId());
                List<Coupon> coupons = this.couponMapper.select(coupon);
                couponBatch.setItems(coupons);
                return couponBatch;
            }
        }
        return null;
    }

    @Override
    public boolean updateCouponBatch(CouponBatch couponBatch) {
        if(couponBatch!=null){
            couponBatch.setUpdateTime(new Date());
            int i = couponBatchMapper.updateByPrimaryKeySelective(couponBatch);
            if(i>0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteCouponBatch(String batchId) {
        if(StringUtils.isNotBlank(batchId)){
            int i = couponBatchMapper.deleteByPrimaryKey(batchId);
            if(i>0){
               /* Example ex = new Example(Coupon.class);
                ex.createCriteria().andEqualTo("batchId",batchId);
                int delete = couponMapper.deleteByExample(ex);
                if(delete>0){
                    return true;
                }*/
               return true;
            }
        }
        return false;
    }

    @Override
    public boolean setCouponBatchInvalid(String batchId) {
        if(StringUtils.isNotBlank(batchId)) {
            CouponBatch couponBatch = couponBatchMapper.selectByPrimaryKey(batchId);
            couponBatch.setStatus(2);
            couponBatch.setExpireTime(new Date());
            int i = couponBatchMapper.updateByPrimaryKeySelective(couponBatch);
            if(i>0){
                Coupon coupon = new Coupon();
                coupon.setBatchId(batchId);
                coupon.setStatus(0);
                List<Coupon> coupons = couponMapper.select(coupon);
                if(coupons!=null && coupons.size()>0){
                    for (Coupon c :coupons) {
                        c.setStatus(-1); //已失效
                        couponMapper.updateByPrimaryKeySelective(c);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Coupon> getCouponListByBatchId(String batchId) {
        if(StringUtils.isNotBlank(batchId)) {
            Coupon coupon = new Coupon();
            coupon.setBatchId(batchId);
            return couponMapper.select(coupon);
        }
        return null;
    }

    @Override
    public boolean verifyCouponBatchLimit(String tenantId, String couponCode) {
        if(StringUtils.isNotBlank(couponCode)){
            Coupon coupon = new Coupon();
            coupon.setCouponCode(couponCode);
            List<Coupon> coupons = couponMapper.select(coupon);
            if(coupons!=null&&coupons.size()>0){
                String batchId = coupons.get(0).getBatchId();
                CouponBatch couponBatch = couponBatchMapper.selectByPrimaryKey(batchId);
                Integer limit = couponBatch.getLimit();
                coupon.setTenantId(tenantId);
                List<Coupon> list = couponMapper.select(coupon);
                if(list==null||list.size()<limit){
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 设置使超出失效时间的优惠券过期
     */

    ExecutorService executor = Executors.newWorkStealingPool();
    public void settingCouponbatchTaskTimer(CouponBatch couponBatch){

        executor.execute(() -> {
            CouponBatchStatusTask couponBatchStatusTask=new CouponBatchStatusTask();
            couponBatchStatusTask.setCouponBatchId(couponBatch.getId());
            couponBatchStatusTask.setStatus(couponBatch.getStatus());

            String startTaskId = timerService.schedule(couponBatchStatusTask,couponBatch.getExpireTime());

            //任务Id存到Redis里面
           /* if(StringUtils.isNotBlank(startTaskId)){
                RedisUtil.set("QYH_CouponBatch_Timer_" + couponBatch.getId() , startTaskId);
            }*/


        });




    }


}
