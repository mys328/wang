package com.thinkwin.console.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.PageInfo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.promotion.CouponSubmitDto;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.common.model.promotion.Coupon;
import com.thinkwin.common.model.promotion.CouponBatch;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.console.service.SaasUserService;
import com.thinkwin.promotion.service.CouponBatchService;
import com.thinkwin.promotion.service.CouponService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * 类说明：优惠券API
 * @author lining 2018/1/26
 * @version 1.0
 *
 */
@Controller
@RequestMapping("/promotion/coupon")
public class CouponController {

    private final Logger log = LoggerFactory.getLogger(CouponController.class);


    @Resource
    private CouponService couponService;

    @Resource
    private CouponBatchService couponBatchService;

    @Resource
    private SaasUserService saasUserService;






    /**
     * 跳转至优惠券列表
     * @return
     */
    @RequestMapping("/gotoCouponsPage")
    public String gotoOrderPage(){
        return "console/coupons";
    }

    /**
     * 创建优惠券
     * @param
     * @param
     * @return
     */
    @RequestMapping("/new")
    @ResponseBody
    public Object save(String data){
        CouponSubmitDto couponSubmitDto = JSON.parseObject(data, new TypeReference<CouponSubmitDto>() {
        });

        //校验数据
        int flag=checkVal(couponSubmitDto);
         if(flag==1){
             return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "时间格式不对：失效时间小于当前时间");
         }else if(flag==2){
             return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "时间格式不对：失效时间小于生效时间");
         }


        //保存数据
        CouponBatch couponBatch=new CouponBatch();
        if(StringUtils.isNotBlank(couponSubmitDto.getId())){
            couponBatch.setId(couponSubmitDto.getId());

            //处理并发情况
            CouponBatch temp=this.couponBatchService.getCouponBatchById(couponSubmitDto.getId());
            if(null!=temp && temp.getStatus()==1){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "此优惠劵已发布","-1");
            }
        }
        couponBatch.setCouponName(couponSubmitDto.getCouponName());
        couponBatch.setCouponType(couponSubmitDto.getCouponType());
        couponBatch.setDiscountConfig(JSONObject.toJSONString(couponSubmitDto.getDiscountConfig(), SerializerFeature.WriteNullStringAsEmpty));
        couponBatch.setEffectiveTime(new Date(couponSubmitDto.getEffectiveTime()));
        couponBatch.setExpireTime(new Date(couponSubmitDto.getExpireTime()));
        couponBatch.setTotalQty(couponSubmitDto.getTotalQty());
        couponBatch.setUsedQty(0);
        couponBatch.setCreatedBy(TenantContext.getUserInfo().getUserId());
        couponBatch.setStatus(couponSubmitDto.getIsRelease());


        boolean f=this.couponBatchService.addCouponBatch(couponBatch,couponSubmitDto.getIsRelease());

        return f ? ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription()) :
                ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());

    }

    /**
     * 优惠券列表，带查询功能
     * @param word 搜索关键字
     * @param currentPage,pageSize 分页组件
     * @return
     */
    @RequestMapping("/query")
    @ResponseBody
    public Object query(String word,Integer currentPage,Integer pageSize){


            BasePageEntity  page=new BasePageEntity();
            if(null!=currentPage && currentPage>0){
                page.setCurrentPage(currentPage);
            }
            if(null!=pageSize && pageSize>0){
                page.setPageSize(pageSize);
            }

        ResponseResult responseResult=new ResponseResult();

        PageInfo pageInfo=this.couponBatchService.getCouponBatchList(word,page);
        List<CouponBatch> couponBatches=pageInfo.getList();
        List<CouponBatch> list=new ArrayList<>();

        for(CouponBatch c:couponBatches){
            Coupon coupon=new Coupon();
            coupon=this.couponService.findByCouponBatchId(c.getId());
            if(null!=coupon &&coupon.getStatus()==0){
                c.setCouponCode(coupon.getCouponCode());
            }
            c.setUsedQty(this.couponService.getConsumeCouponCount(c.getId()));
            SaasUser saasUser=this.saasUserService.selectSaasUserByUserId(c.getCreatedBy());
            c.setCreateName((null!=saasUser)?saasUser.getUserName():null);
            list.add(c);
        }
        pageInfo.setList(list);

        responseResult.setIfSuc(1);
        responseResult.setData(pageInfo);
        return responseResult;
    }


    /**
     * 删除优惠券
     * @param id 优惠券IDs
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(String id){
        ResponseResult responseResult=new ResponseResult();

        boolean f=false;
        if(StringUtils.isNotBlank(id)){
            String ids[]=id.split(",");

            for(int i=0;i<ids.length;i++){
            //处理并发情况
            CouponBatch couponBatch = this.couponBatchService.getCouponBatchById(ids[i]);
            if (null == couponBatch) {
                responseResult.setIfSuc(0);
                responseResult.setMsg("此优惠劵已删除");
                responseResult.setCode("-1");
                return responseResult;
            }
            f = this.couponBatchService.deleteCouponBatch(ids[i]);
        }
        }
        if(f){
            responseResult.setIfSuc(1);
            responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());
        }else {
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.Failure.getDescription());
        }
        return responseResult;
    }

    /**
     * 使优惠券作废
     * @param id 优惠券ID
     * @return
     */
    @RequestMapping("/invalidate")
    @ResponseBody
    public Object invalidate(String id){

        ResponseResult responseResult=new ResponseResult();
        boolean f=false;
        if(StringUtils.isNotBlank(id)){
            String ids[]=id.split(",");

            for(int i=0;i<ids.length;i++){

            //处理并发情况
            CouponBatch couponBatch=this.couponBatchService.getCouponBatchById(ids[i]);
            int n=this.couponService.getConsumeCouponCount(couponBatch.getId());
            boolean flag=false;
            if(n==couponBatch.getTotalQty()){
                flag=true;
            }
            if(couponBatch.getStatus()==2 || flag){
                responseResult.setIfSuc(0);
                responseResult.setMsg("此优惠劵已失效");
                responseResult.setCode("-1");
                return responseResult;
            }

            f=this.couponBatchService.setCouponBatchInvalid(ids[i]);
        }
        }
        if(f){
            responseResult.setIfSuc(1);
            responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());
        }else{
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.Failure.getDescription());
        }


        return responseResult;
    }

    /**
     * 获取优惠券详情
     * @param id 优惠券ID
     * @return
     */
    @RequestMapping("/get")
    @ResponseBody
    public Object get(String id){

        ResponseResult responseResult=new ResponseResult();

        CouponBatch couponBatch=this.couponBatchService.getCouponBatchById(id);
        if(null!=couponBatch){
            couponBatch.setUsedQty(this.couponService.getConsumeCouponCount(couponBatch.getId()));
            String JsonString=couponBatch.getDiscountConfig();
            responseResult.setIfSuc(1);
            responseResult.setData(couponBatch);
        }else {
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.Failure.getDescription());
        }
        return responseResult;
    }


    public int checkVal(CouponSubmitDto couponSubmitDto){

        int i=0; //0:通过，1:校验时间
        Date today=new Date();
        if(today.getTime()>couponSubmitDto.getExpireTime()){
            i=1;
        }else if(couponSubmitDto.getExpireTime()<couponSubmitDto.getEffectiveTime()){
            i=2;
        }

        return i;
    }


}
