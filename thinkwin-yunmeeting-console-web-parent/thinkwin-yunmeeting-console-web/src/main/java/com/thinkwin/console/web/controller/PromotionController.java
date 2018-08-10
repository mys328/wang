package com.thinkwin.console.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.order.OrderPricingResponse;
import com.thinkwin.common.dto.promotion.PricingConfigDto;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.goodscenter.service.ProductPackSkuService;
import com.thinkwin.orders.service.OrderService;
import com.thinkwin.orders.vo.OrderVo;
import com.thinkwin.promotion.service.PricingConfigService;
import com.thinkwin.service.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/*
 * 类说明：商品定价API
 * @author lining 2018/1/26
 * @version 1.0
 *
 */
@Controller
@RequestMapping("/promotion")
public class PromotionController {

    private final Logger log = LoggerFactory.getLogger(PromotionController.class);


    @Resource
    private PricingConfigService pricingConfigService;
    @Resource
    private OrderService orderService;
    @Resource
    ProductPackSkuService productPackSkuService;

    /**
     * 跳转至定价配置
     * @return
     */
    @RequestMapping("/gotoPricesettingPage")
    public String gotoPricesettingPage(){
        return "console/pricesetting";
    }

    /**
     * 跳转至预览
     * @return
     */
    @RequestMapping("/setPricesettingProviewData")
    @ResponseBody
    public Object gotoPricesettingProviewPage(HttpServletRequest request,String data){
        HttpSession httpSession = request.getSession();
        String session=httpSession.getId();
        ResponseResult responseResult=new ResponseResult();
        RedisUtil.set("yunmeeting_pricesetting_"+session,data);
        responseResult.setIfSuc(1);
        return responseResult;
    }

    @RequestMapping("/gotoPricesettingProviewPage")
    public String gotoPricesettingProviewPage(){
        return "console/orderGroup";
    }

    /**
     * 跳转至预览-获取数据
     * @return
     */
    @RequestMapping("/getPricesettingProviewData")
    @ResponseBody
    public Object getPricesettingProviewData(HttpServletRequest request){
        HttpSession httpSession = request.getSession();
        String session=httpSession.getId();
        ResponseResult responseResult=new ResponseResult();

        String data=RedisUtil.get("yunmeeting_pricesetting_"+session);

        //RedisUtil.del("yunmeeting_pricesetting_"+session);

        if(null!=data){
            PricingConfigDto pricingConfigDto= JSON.parseObject(data, new TypeReference<PricingConfigDto>() {
            });
            responseResult.setIfSuc(1);
            responseResult.setData(pricingConfigDto);
        }else {
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.DataNull.getDescription());
        }
        return responseResult;
    }

    /**
     * 获取商品定价规则
     * @param
     * @return
     */
    @RequestMapping("/allRules")
    @ResponseBody
    public Object allRules(){

        ResponseResult responseResult=new ResponseResult();

        PricingConfigDto PricingConfigDto=this.pricingConfigService.getPricingConfig();

        if(null!=PricingConfigDto){
            responseResult.setIfSuc(1);
            responseResult.setData(PricingConfigDto);
        }else {
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.DataNull.getDescription());
        }
        return responseResult;
    }

    /**
     * 更新定价规则
     * @param
     * @param
     * @return
     */
    @RequestMapping("/rule/update")
    @ResponseBody
    public Object update(String data){

        PricingConfigDto pricingConfigDto= JSON.parseObject(data, new TypeReference<PricingConfigDto>() {
        });

        //校验pricingConfigVo

        //修改免费参数
        this.productPackSkuService.updateCommodityItem(pricingConfigDto.getFreeAccountConfig(),pricingConfigDto.getSkuDiscount().get(0).getConfig().getMax());


        //更新旧的定价，并生生成新的定价配置
        if(this.pricingConfigService.updateLastPricingConfig()) {
            boolean f = this.pricingConfigService.updatePricingConfig(pricingConfigDto);
            return f ? ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription()) :
                    ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());

        }

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }



    /**
     * 订单预览效果
     * @param
     * @param
     * @return
     */
    @RequestMapping("/calcPrice")
    @ResponseBody
    public Object calcPrice(@RequestBody OrderVo orderVo,PricingConfigDto pricingConfigDto){

        String clientIp = TenantContext.getUserInfo().getIp();

        orderVo.setClientIp(clientIp);
        orderVo.setTenantId(TenantContext.getTenantId());

        OrderPricingResponse response = orderService.calcOrderPrice(orderVo,pricingConfigDto);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", response);

    }




}
