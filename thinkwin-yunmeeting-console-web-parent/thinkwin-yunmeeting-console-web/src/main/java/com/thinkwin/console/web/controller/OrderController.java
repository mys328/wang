package com.thinkwin.console.web.controller;

import com.github.pagehelper.PageInfo;
import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.order.CouponProduct;
import com.thinkwin.common.dto.order.ServiceTermInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.vo.ordersVo.OrderManagementVo;
import com.thinkwin.console.service.SaasUserService;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.orders.dto.AdminOrderQuery;
import com.thinkwin.orders.dto.ConfirmResult;
import com.thinkwin.orders.dto.OrderPaymentConfirm;
import com.thinkwin.orders.service.OrderService;
import com.thinkwin.orders.util.OrderUtil;
import com.thinkwin.orders.vo.OrderVo;
import com.thinkwin.pay.service.PayService;
import com.thinkwin.pay.util.CryptoUtil;
import com.thinkwin.pay.util.PayUtil;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;

/**
 * 订单管理controller层
 * User: yinchunlei
 * Date: 2017/8/31.
 * Company: thinkwin
 */
@Controller
public class OrderController {
    private static Logger log = LoggerFactory.getLogger(OrderController.class);

    @Resource
    private OrderService orderService;
    @Resource
    private SaasTenantService saasTenantService;

    @Resource
    private PayService payService;
    @Resource
    FileUploadService fileUploadService;
    @Resource
    private SaasUserService saasUserService;

    /**
     * 跳转至订单管理页
     * @return
     */
    @RequestMapping("/gotoOrderPage")
    public String gotoOrderPage(){
        return "console/order";
    }
    /**
     * 获取订单管理列表功能接口
     * @return
     */
    @RequestMapping("/getAllOrderListInfo")
    @ResponseBody
    public ResponseResult getAllOrderListInfo(BasePageEntity basePageEntity){
        Properties properties = getProperties();
        if(null == properties){
            log.error("获取配置文件失败");
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "获取平台私钥出错！");
        }
        String queryOrderListKey = properties.getProperty("queryOrderListKey");
        AdminOrderQuery adminOrderQuery = new AdminOrderQuery();
        adminOrderQuery.setPage(basePageEntity);
        adminOrderQuery.setAppId("console");
        adminOrderQuery.setTimeStamp(Long.toString(new Date().getTime()));
        adminOrderQuery.setNonceStr(CreateUUIdUtil.Uuid());
        String content = OrderUtil.getSignContent(adminOrderQuery);
        String sign = getSign(content, queryOrderListKey);
        if(StringUtils.isBlank(sign)){
            log.error("获取签名失败");
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "获取签名失败！");
        }
        adminOrderQuery.setSign(sign);
        PageInfo<OrderVo> orderVoPageInfo = orderService.queryOrders(adminOrderQuery);
        PageInfo<OrderManagementVo> orderList = new PageInfo<>();
        if(null != orderVoPageInfo){
            orderList.setEndRow(orderVoPageInfo.getEndRow());
            List<OrderVo> list = orderVoPageInfo.getList();
            List<OrderManagementVo> ll = new ArrayList();
            if(null != list && list.size() > 0){
                for (OrderVo orderVo:list) {
                    OrderManagementVo orderManagementVo = new OrderManagementVo();
                    String orderId = orderVo.getOrderId();
                    orderManagementVo.setOrderId(orderId);
                    String orderSn = orderVo.getOrderSn();
                    orderManagementVo.setOrderNum(orderSn);
                    orderManagementVo.setRemark(orderVo.getRemark());
                    if(orderVo.getOrderDiscount() != null && orderVo.getOrderDiscount().getCouponDiscount() != null){
                        List<CouponProduct> l = orderVo.getOrderDiscount().getCouponDiscount().getProducts();
                        if(l!=null&&l.size()<1){
                            l=null;
                        }
                        orderVo.getOrderDiscount().getCouponDiscount().setProducts(l);
                        ServiceTermInfo s =orderVo.getOrderDiscount().getCouponDiscount().getServiceTermInfo();
                        if(s!=null){
                            if(s.getQty()==0){
                                s=null;
                            }
                        }
                        orderVo.getOrderDiscount().getCouponDiscount().setServiceTermInfo(s);
                   }
                    orderManagementVo.setOrderDiscount(orderVo.getOrderDiscount());
                    orderManagementVo.setCouponCode(orderVo.getCouponCode());

                 //   String tenantId = orderVo.getTenantId();
              /*      if(StringUtils.isNotBlank(tenantId)){
                        SaasTenant saasTenant = saasTenantService.selectByIdSaasTenantInfo(tenantId);
                        if(null != saasTenant){
                            String tenantName = saasTenant.getTenantName();
                            if(StringUtils.isNotBlank(tenantName)){
                                orderManagementVo.setTenantName(tenantName);
                            }
                        }
                    }*/
                    orderManagementVo.setTenantName(orderVo.getTenantName());
                    orderManagementVo.setSkuName(orderVo.getOrderSubject());
                    orderManagementVo.setPayStatus(orderVo.getStatusName());
                    orderManagementVo.setPayTime(orderVo.getPaySuccessTime());
                    String status = orderVo.getStatus().toString();
                    orderManagementVo.setPayPrice(Double.valueOf(orderVo.getPayPrice()).doubleValue());
                    String certImageUrl = orderVo.getCertImageUrl();
                    if(StringUtils.isNotBlank(certImageUrl)) {
                        String tenantId = TenantContext.getTenantId();
                        String userName = TenantContext.getUserInfo().getUserName();
                        Map<String, String> photos = saasUserService.getUploadInfo(certImageUrl);
                        if(null != photos) {
                            certImageUrl = photos.get("primary");
                        }
                    }
                    orderManagementVo.setCertificatePath(certImageUrl);
                    orderManagementVo.setDiscount(orderVo.getDiscountTip());
                    orderManagementVo.setStatus(status);
                    Integer payChannel = orderVo.getPayChannel();
                    if(null != payChannel) {
                        if("99".equals(status)){
                            orderManagementVo.setChannel(null);
                        }else {
                            orderManagementVo.setChannel(orderVo.getPayChannel().toString());
                        }
                    }
                    String channelName = orderVo.getChannelName();
                    if(StringUtils.isBlank(channelName)) {
                        if(null != payChannel){
                            if(payChannel == 1){
                                channelName="微信";
                            }else if(payChannel == 2){
                                channelName="支付宝";
                            }else if(payChannel == 3){
                                channelName="网银支付";
                            }else if(payChannel == 4){
                                channelName="银行汇款";
                            }
                        }
                        if ((StringUtils.isNotBlank(status) && "99".equals(status))) {
                            orderManagementVo.setPayType(null);
                        } else {
                            orderManagementVo.setPayType(channelName);
                        }
                    }else{
                        orderManagementVo.setPayType(channelName);
                    }
                    ll.add(orderManagementVo);
                }
            }
            orderList.setList(ll);
            orderList.setPageNum(orderVoPageInfo.getPageNum());
            orderList.setSize(orderVoPageInfo.getSize());
            orderList.setTotal(orderVoPageInfo.getTotal());
        } else {
            log.info("订单数据为空");
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), orderList, BusinessExceptionStatusEnum.Success.getCode());
    }


    /**
     * 获取签名
     * @return
     */
    public String getSign(String content,String key){
        String sign = null;
        try {
            sign = CryptoUtil.rsaSign(content,key, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sign;
    }
    /**
     * 确认到账功能接口
     * @return
     */
    @RequestMapping("/confirmationOfAccount")
    @ResponseBody
    public ResponseResult confirmationOfAccount(String orderNum, String remark, String orderId,String payMentNo){
        Properties properties = getProperties();
        if(null == properties){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "获取平台私钥出错！");
        }
        String privateKey = properties.getProperty("privateKey");
        if(StringUtils.isNotBlank(orderNum) && StringUtils.isNotBlank(orderId)){
            OrderPaymentConfirm confirm = new OrderPaymentConfirm();
            confirm.setAppId("console");
            confirm.setPaymentNo(payMentNo);
            confirm.setOrderId(orderId);
            confirm.setOrderRemark(remark);
            confirm.setTimestamp(Long.toString(new Date().getTime()));
            confirm.setNonceStr(CreateUUIdUtil.Uuid());
            String content = PayUtil.getSignContent(confirm);
            String sign = getSign(content, privateKey);
            if(StringUtils.isBlank(sign)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "获取签名失败！");
            }
            confirm.setSign(sign);
            ConfirmResult result = payService.paymentReceived(confirm);
            if(result.isSuccess()){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, result.getErrorMsg(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }


    /**
     * 获取properties文件
     * @return
     */
    public Properties getProperties(){
        Properties properties = new Properties();
        try {
            InputStream is = OrderController.class.getClassLoader().getResourceAsStream("service.properties");
            properties.load(is);
        } catch (Exception e) {
            e.getStackTrace();
            return null;
       }
       return properties;
    }

    /**
     * 编辑备注功能接口
     * @return
     */
    @RequestMapping(value = "/editRemarks",method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult editRemarks(String orderId,String remark) {
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if (null != userInfo) {
            String userId = userInfo.getUserId();
            if (StringUtils.isNotBlank(userId)) {
                if (StringUtils.isNotBlank(orderId)) {
                   boolean b = orderService.updateOrderRemark(orderId,remark);
                    if (b) {
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
                    }
                }
            }
        }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.PageErr.getCode());
        }

}
