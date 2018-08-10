package com.thinkwin.web.controller;

import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.PermissionService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.promotion.CapacityConfig;
import com.thinkwin.common.dto.promotion.PricingConfigDto;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.ProductSku;
import com.thinkwin.common.utils.DateUtil;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.vo.ProductSkuDto;
import com.thinkwin.common.vo.ProductVo;
import com.thinkwin.common.vo.ProductsVo;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.goodscenter.service.ProductOrderService;
import com.thinkwin.goodscenter.service.ProductPackSkuService;
import com.thinkwin.goodscenter.vo.ProductSpecVo;
import com.thinkwin.promotion.service.PricingConfigService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/*
  *
  *
  *  开发人员:daipengkai
  *  创建时间:2017/8/22
  *
  */
@RequestMapping("/commodity")
@Controller
public class ProductOrderController {
    @Autowired
    ProductOrderService productOrderService;
    @Autowired
    SaasTenantService saasTenantCoreService;
    @Autowired
    PricingConfigService pricingConfigService;
    @Autowired
    ProductPackSkuService productPackSkuService;

    /**
     * 获取商品显示信息
     *
     * @return
     */
    @RequestMapping(value = "/selectOrderInformation", method = RequestMethod.POST)
    @ResponseBody
    public Object selectOrderInformation() {
        //获取租户id
        String tenantId = TenantContext.getTenantId();
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
        Map<String, Object> map = new HashMap<String, Object>();
        map = this.productOrderService.selectOrderInformation(saasTenant);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
    }
    /**
     * 获取商品显示信息
     *
     * @return
     */
    @RequestMapping(value = "/selectOrderInformationModify", method = RequestMethod.POST)
    @ResponseBody
    public Object selectOrderInformationModify() {
        //获取租户id
        String tenantId = TenantContext.getTenantId();
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
        Map<String, Object> map = new HashMap<String, Object>();
        map = this.productOrderService.selectOrderInformationModify(saasTenant);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
    }
    /**
     * 获取商品列表和选择的每年价格
     *
     * @return
     */
    @RequestMapping(value = "/selectProductIdInfoAndList", method = RequestMethod.POST)
    @ResponseBody
    public Object selectProductIdInfoAndList(String productId) {
        //获取租户id
        String tenantId = TenantContext.getTenantId();
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
        Map<String, Object> map = new HashMap<String, Object>();
        map = this.productOrderService.selectProductIdInfoAndList(productId,saasTenant);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
    }

    /**
     * 获取商品列表和选择的每年价格
     *
     * @return
     */
    @RequestMapping(value = "/selectProductPriceInfo", method = RequestMethod.POST)
    @ResponseBody
    public Object selectProductPriceInfo(String productId) {
        //获取租户id
        String tenantId = TenantContext.getTenantId();
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
        List<ProductSpecVo> specVos = this.productOrderService.selectProductByIdInfo(productId,saasTenant);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), specVos);
    }
    @Resource
    PermissionService permissionService;
    /*
    * 菜单跳转页面
    * */
    @RequestMapping("/orderManger")
    public String orderManger(HttpServletRequest request) throws Exception {
        ///////////////////////////添加资源拦截功能/////////////////////////////////
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null != userInfo) {
            String userId = userInfo.getUserId();
            if(StringUtils.isNotBlank(userId)) {
                //在此处需要获取用户请求的路径
                String url = request.getRequestURI();
                boolean userJurisdiction = permissionService.getUserJurisdiction(userId,url);
                if(userJurisdiction){
                    return "orders_manage/orderManger";
                }
            }
        }
        return "redirect:/logout";
        ////////////////////////////添加资源拦截功能///////////////////////////
       // return "orders_manage/orderManger";
    }


    /*
    * 跳转商品展示
    *
    * */
    @RequestMapping("/orderExhibition")
    public String orderExhibition
    () throws Exception {
        return "yuncm_pay/upgrade";
    }

    /*
    * 跳转增容
    *
    * */
    @RequestMapping("/getDidClickExpansion")
    public ModelAndView getDidClickExpansion(){
        Map<String, Object> map = new HashMap<String, Object>();
        List<ProductSku> skus = this.productOrderService.selectProductSkuInfo();
        for(ProductSku sku : skus){
            if("会议室".equals(sku.getSkuDesc())){
                map.put("room",sku.getSku());
            }
            if("存储空间".equals(sku.getSkuDesc())){
                map.put("space",sku.getSku());
            }
        }
        return new ModelAndView("order/extension",map);
    }

    /**
     * 跳转到升级订单页面
     * @return
     */
    @RequestMapping(value = "/immediatelyUpgrade", method = RequestMethod.GET)
    public ModelAndView immediatelyUpgrade(String productId,String type) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("productId",productId);
        map.put("type",type);
        return new ModelAndView("yuncm_pay/submitOrder",map);
    }

    /**
     * 跳转到升级订单页面
     * @return
     */
    @RequestMapping(value = "/getOrderDetail", method = RequestMethod.GET)
    public ModelAndView getOrderDetail(String type) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type",type);
        return new ModelAndView("order/orderGroup",map);
    }


    /**
     * 获取商品列表和选择的每年价格
     *
     * @return
     */
    @RequestMapping(value = "/getAll", method = RequestMethod.POST)
    @ResponseBody
    public Object getAll() {
        Map<String, Object> map = null;
        try {
            //获取租户id
            String tenantId = TenantContext.getTenantId();
            //获取定价配置
            PricingConfigDto pricingConfig = pricingConfigService.getPricingConfig();
           /* String param = pricingConfig.getContent();
            map = new HashMap<String, Object>();
            JSONObject object = JSON.parseObject(param);//JSON.parseArray(param);
            Object product =  object.get("skuDiscount");
            Object serviceTermDiscount =  object.get("serviceTermDiscount");*/
            map = new HashMap<String, Object>();
            List<Object> list = new ArrayList<Object>();
            //获取租户信息
            SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
            ProductsVo productsVo = new ProductsVo();
            String configId = "";
            if(saasTenant != null) {
                //获取定价配置
                PricingConfigDto configDto = pricingConfigService.getPricingConfig();
                configId = configDto.getId();
                if("0".equals(saasTenant.getTenantType())) {
                    //获取免费价格
                    List<CapacityConfig> configs = configDto.getFreeAccountConfig();
                    for (CapacityConfig config : configs) {
                        //会议室数量
                        if ("100".equals(config.getSku())) {
                            saasTenant.setBuyRoomNumTotal(config.getQty());
                            saasTenant.setBasePackageRoomNum(0);
                        }
                        //储存空间
                        if ("101".equals(config.getSku())) {
                            saasTenant.setBuySpaceNumTotal(config.getQty());
                        }
                        //员工人数
                        if ("102".equals(config.getSku())) {
                            saasTenant.setExpectNumber(config.getQty());
                        }
                    }
                }
               List<ProductSkuDto> dtos = productPackSkuService.getAllSku();
               List<ProductVo> vos = new ArrayList<ProductVo>();
               if(dtos.size()>=3 && "1".equals(saasTenant.getTenantType())) {
                   ProductVo voa = new ProductVo();
                   voa.setDisplayName(dtos.get(0).getDisplayName() + "：");
                   voa.setQty(saasTenant.getExpectNumber() + "");
                   voa.setUom(dtos.get(0).getUom());
                   vos.add(voa);
                   ProductVo vob = new ProductVo();
                   vob.setDisplayName(dtos.get(1).getDisplayName() + "：");
                   vob.setQty(saasTenant.getBuyRoomNumTotal()+"");
                   vob.setUom(dtos.get(1).getUom());
                   vos.add(vob);
                   ProductVo voc = new ProductVo();
                   voc.setDisplayName(dtos.get(2).getDisplayName() + "：");
                   voc.setQty(saasTenant.getBuySpaceNumTotal() + "");
                   voc.setUom(dtos.get(2).getUom());
                   vos.add(voc);
                   productsVo.setVos(vos);
                   productsVo.setExpireTime(DateUtils.addDays(DateUtils.truncate(saasTenant.getBasePackageExpir(), Calendar.DAY_OF_MONTH), -1));
                   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                   productsVo.setSurplusTime(DateUtil.getTimeDiff(sdf.format(new Date()),sdf.format(saasTenant.getBasePackageExpir()),"add"));

               }
            }
            map.put("tenantInfo",productsVo);
            map.put("product",pricingConfig.getSkuDiscount());
            map.put("serviceTermDiscount",pricingConfig.getServiceTermDiscount());
            map.put("configId",configId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
    }







}
