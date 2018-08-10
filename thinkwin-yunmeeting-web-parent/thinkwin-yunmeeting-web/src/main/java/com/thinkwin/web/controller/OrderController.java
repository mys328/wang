package com.thinkwin.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.thinkwin.SMSsender.service.SMSsenderService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.order.OrderPricingResponse;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.SysAttachment;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.FileManager;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.console.service.SaasUserRoleService;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.console.service.SaasUserService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.mailsender.service.YunmeetingSendMailService;
import com.thinkwin.mailsender.vo.MailVo;
import com.thinkwin.orders.dto.OrderResponse;
import com.thinkwin.orders.model.OrderStatus;
import com.thinkwin.orders.service.OrderService;
import com.thinkwin.orders.vo.OrderVo;
import com.thinkwin.pay.dto.PaymentVo;
import com.thinkwin.pay.model.PayChannelEnum;
import com.thinkwin.pay.service.PayService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/order")
public class OrderController {
	private static Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Resource
	private OrderService orderService;
	@Resource
	private FileUploadService fileUploadService;
	@Resource
	private PayService payService;

	@Resource
	YunmeetingSendMailService mailSenderService;
    @Resource
    private SaasUserService consoleUserService;

	@Autowired
	private SaasTenantService saasTenantService;

	@Autowired
	private SMSsenderService sMSsenderService;

	@Resource
	private SaasUserRoleService saasUserRoleService;

	@Value("${invoice.financial_user_role_id}")
	private String financialUserRoleId = "3";

	@Value("${invoice.cancel_notify_sms_template_id}")
	private String cancelOrderNotifySMSTemplateId = "225940";

	@Value("${invoice.new_notify_sms_template_id}")
	private String newOrderNotifySMSTemplateId = "225939";

	private static final List<Integer> TENANT_ORDER_STATUS = new ArrayList<Integer>();

	static {
		TENANT_ORDER_STATUS.add(OrderStatus.WEIZHIFU.getCode());
		TENANT_ORDER_STATUS.add(OrderStatus.DAIHUIKUAN.getCode());
		TENANT_ORDER_STATUS.add(OrderStatus.YIFUKUAN.getCode());
		TENANT_ORDER_STATUS.add(OrderStatus.KAIPIAOZHONG.getCode());
		TENANT_ORDER_STATUS.add(OrderStatus.YIKAIPIAO.getCode());
	}
	@Resource
    private UserService userService;

	private static final ExecutorService executor = Executors.newWorkStealingPool(3);

	@RequestMapping("get")
	public Object getOrderById(@RequestParam("orderId")String orderId){
		BusinessExceptionStatusEnum errMsg = BusinessExceptionStatusEnum.Success;
		Integer resultCode = 1;
		Object result = null;

		if(StringUtils.isEmpty(orderId)){
			errMsg = BusinessExceptionStatusEnum.MissingOrderId;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), result);
		}

		OrderVo orderVo = orderService.getOrderById(orderId);
		if(null == orderVo){
			resultCode = 0;
			errMsg = BusinessExceptionStatusEnum.OrderNotExists;
		}
		else{
			String imageUrl = orderVo.getCertImageUrl();
			if(StringUtils.isNotBlank(imageUrl)){
                Map<String, String> photos = consoleUserService.getUploadInfo(imageUrl);
                if(null != photos) {
                    orderVo.setCertImageUrl(photos.get("primary"));
                }
            }

			result = orderVo;
		}
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), result);
	}

	@RequestMapping(value = "new", method = RequestMethod.POST)
	public Object newOrder(@RequestBody OrderVo orderVo){
		BusinessExceptionStatusEnum errMsg = BusinessExceptionStatusEnum.Success;
		Integer resultCode = 1;

		Map<String, String> modelInfo = new HashMap<String, String>();

		String userId = TenantContext.getUserInfo().getUserId();
		String userName = TenantContext.getUserInfo().getUserName();
		String clientIp = TenantContext.getUserInfo().getIp();

//		if(clientIp.contains("0:0")){
//			resultCode = 0;
//			errMsg = BusinessExceptionStatusEnum.InvalidClientIp;
//			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), modelInfo);
//		}

		if(null == TenantContext.getUserInfo()
				|| StringUtils.isBlank(TenantContext.getUserInfo().getUserId())){
			resultCode = 0;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, "用户信息为空!", modelInfo);
		}

		orderVo.setBuyerUserId(userId);
		orderVo.setBuyerUserName(userName);
		orderVo.setClientIp(clientIp);
		orderVo.setTenantId(TenantContext.getTenantId());
		orderVo.setOrderSource(1);
		orderVo.setCreatedBy(TenantContext.getUserInfo().getUserId());

		OrderVo newOrder = null;
		OrderResponse response = null;
		try{
			response = orderService.newOrder(orderVo);

			if(null == response || null == response.getOrderVo()){
				resultCode = 0;
			} else{
				newOrder = response.getOrderVo();
			}
		}
		catch (Exception ex){
			logger.error("创建新订单失败：", ex);
		}

		if(newOrder != null){
			modelInfo.put("orderId", newOrder.getOrderId());
		}

		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, response.getErrorMsg(), modelInfo);
	}

	@RequestMapping(value = "myOrders", method = RequestMethod.POST)
	public Object tenantQueryOrders(@RequestBody String data){
		BusinessExceptionStatusEnum errMsg = BusinessExceptionStatusEnum.Success;
		Integer resultCode = 1;
		Object result = null;

		JSONObject json = JSONObject.parseObject(data);
		String tenantId = TenantContext.getTenantId();

		if(StringUtils.isBlank(tenantId)){
			resultCode = 0;
			errMsg = BusinessExceptionStatusEnum.ParamErr;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), result);
		}

		BasePageEntity page = new BasePageEntity();
		page.setCurrentPage(1);
		page.setPageSize(10);

		if(json.containsKey("pageSize")){
			page.setPageSize(json.getInteger("pageSize"));
		}

		if(json.containsKey("pageNum")){
			page.setCurrentPage(json.getInteger("pageNum"));
		}

		result = orderService.queryOrders(page, tenantId, TENANT_ORDER_STATUS, null, null, null);
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), result);
	}


	@RequestMapping(value = "query", method = RequestMethod.POST)
	public Object queryOrders(@RequestBody String data){
		BusinessExceptionStatusEnum errMsg = BusinessExceptionStatusEnum.Success;
		Integer resultCode = 1;
		Object result = null;

		JSONObject json = JSONObject.parseObject(data);
		String tenantId = TenantContext.getTenantId();

		if(StringUtils.isBlank(tenantId)){
			resultCode = 0;
			errMsg = BusinessExceptionStatusEnum.ParamErr;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), result);
		}

		List<Integer> status = null;
		if(json.containsKey("status")){
			status = json.getJSONArray("status").toJavaList(Integer.class);
		}

		List<Integer> orderTypes = null;
		if(json.containsKey("orderType")){
			orderTypes = json.getJSONArray("orderType").toJavaList(Integer.class);
		}

		Date startTime = null;
		if(json.containsKey("startTime")){
			startTime = json.getDate("startTime");
		}

		Date endTime = null;
		if(json.containsKey("endTime")){
			endTime = json.getDate("endTime");
		}

		BasePageEntity page = new BasePageEntity();
		page.setCurrentPage(1);
		page.setPageSize(10);

		if(json.containsKey("pageSize")){
			page.setPageSize(json.getInteger("pageSize"));
		}

		if(json.containsKey("pageNum")){
			page.setCurrentPage(json.getInteger("pageNum"));
		}

		result = orderService.queryOrders(page, tenantId, status, orderTypes, startTime, endTime);
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), result);
	}

	@RequestMapping("/payStatus")
	public Object payStatus(@RequestParam("orderId")String orderId){
		OrderVo vo = orderService.getPayStatus(orderId);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("statusCode", vo.getStatus());
		result.put("statusName", vo.getStatusName());
		return result;
	}

	@RequestMapping("/cancel")
	public Object cancelOrder(@RequestParam("orderId")String orderId){
		BusinessExceptionStatusEnum errMsg = BusinessExceptionStatusEnum.Success;
		Integer resultCode = 1;
		Object result = null;

		if(StringUtils.isEmpty(orderId)){
			errMsg = BusinessExceptionStatusEnum.MissingOrderId;
			resultCode = 0;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), result);
		}

		boolean sucessed = orderService.cancelOrder(orderId);

		if(sucessed){
			executor.execute(()->{
				OrderVo orderVo = orderService.getOrderById(orderId);
				notifyOrderCancel(orderVo);
			});
		} else {
			resultCode = 0;
			errMsg = BusinessExceptionStatusEnum.OrderCanNotCancel;
            OrderVo orderVo = orderService.getOrderById(orderId);
            if (null != orderVo) {
                SysAttachment sys = this.fileUploadService.selectByidFile(orderVo.getCertImageUrl());
                if(sys != null) {
                    boolean b = FileManager.deleteFileUpload(sys.getGroup(), sys.getAttachmentPath() + sys.getFileName());
                    if(b) {
                        consoleUserService.deleteImageUrl(orderVo.getCertImageUrl());
                    }
                }
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), result);
	}

	/**
	 * 支付订单
	 * @param payment
	 * @return
	 */
	@RequestMapping("pay")
	public Object pay(@ModelAttribute("payment") PaymentVo payment){
		BusinessExceptionStatusEnum errMsg = BusinessExceptionStatusEnum.Success;
		Integer resultCode = 1;
		Object result = null;

		if(null == payment || StringUtils.isBlank(payment.getOrderId())){
			resultCode = 0;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, "参数错误!", result);
		}

		PayChannelEnum payChannel = PayChannelEnum.fromCode(payment.getPayChannel());

		if(null == payChannel){
			resultCode = 0;
			errMsg = BusinessExceptionStatusEnum.MissingPayChannel;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), result);
		}

		String clientIp = TenantContext.getUserInfo().getIp();
//		if(clientIp.contains("0:0")){
//			resultCode = 0;
//			errMsg = BusinessExceptionStatusEnum.InvalidClientIp;
//			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), result);
//		}

		OrderVo vo = orderService.getPayStatus(payment.getOrderId());

		List<Integer> status = Arrays.asList(OrderStatus.QUXIAO.getCode(), OrderStatus.YIFUKUAN.getCode());
		if(null == vo){
			resultCode = 0;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, "订单ID错误！", result);
		}

		if(OrderStatus.QUXIAO.getCode().equals(vo.getStatus())){
			resultCode = 0;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, "订单已取消！", result);
		}

		if(OrderStatus.YIFUKUAN.getCode().equals(vo.getStatus())){
			resultCode = 0;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, "订单已完成支付！", result);
		}

		payment.setClientIp(clientIp);

		List<Integer> channels = Arrays.asList(PayChannelEnum.ZFB.getCode(), PayChannelEnum.WEIXIN.getCode());

		payment.setTenantId(TenantContext.getTenantId());
		String tradeNo = payService.payOrder(payment);

		if(StringUtils.isNotBlank(tradeNo)){
			if(payChannel.equals(PayChannelEnum.ZFB)){
				String token = payment.getToken();
				RedisUtil.set("PAY_RETURN_TOKEN_" + tradeNo, token, 600);
			}

			// 租户选择银行汇款支付订单时发送邮件和短信通知财务人员
			if(payChannel.equals(PayChannelEnum.HUIKUAN)){
				OrderVo _vo = orderService.getOrderById(payment.getOrderId());
				notifyTenantReadyToHuikuan(_vo);
			}

			Map<String, String> map = new HashMap<String, String>();
			map.put("tradeNo", tradeNo);
			result = map;
		}

		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), result);
	}

	/**
	 * 查询可开发票订单
	 * @param page
	 * @return
	 */
	@RequestMapping("invoiceRequest")
	public Object getOrderWaitingForInvoicing(BasePageEntity page){
		BusinessExceptionStatusEnum errMsg = BusinessExceptionStatusEnum.Success;
		Integer resultCode = 1;
		Object result = null;
		String tenantId = TenantContext.getTenantId();

		if(StringUtils.isBlank(tenantId)){
			resultCode = 0;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, "缺少租户ID!", result);
		}

		List<Integer> status = Arrays.asList(OrderStatus.YIFUKUAN.getCode(), OrderStatus.KAIPIAOZHONG.getCode(), OrderStatus.YIKAIPIAO.getCode());
		result = orderService.queryOrders(page, tenantId, status, null, null, null);
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), result);
	}

	/**
	 * 查询已支付订单
	 * @param page
	 * @return
	 */
	@RequestMapping("paid")
	public Object getPaidOrders(BasePageEntity page){
		BusinessExceptionStatusEnum errMsg = BusinessExceptionStatusEnum.Success;
		Integer resultCode = 1;
		Object result = null;

		String tenantId = TenantContext.getTenantId();
		List<Integer> status = Arrays.asList(1);
		result = orderService.queryOrders(page, tenantId, status, null, null, null);
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), result);
	}

	/**
	 * 订单详情
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("info")
	public ModelAndView orderDetail(String orderId) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId",orderId);
		PaymentVo vo = payService.getPaymentById(orderId);

		if(vo != null){
			map.put("cert_img_url", vo.getCertImgUrl());
		}

		return new ModelAndView("order/orderDetail", "model", map);
	}

	/**
	 * 计算订单价格
	 * @param orderVo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "calcPrice", method = RequestMethod.POST)
	public Object calcPrice(@RequestBody OrderVo orderVo) {
		String clientIp = TenantContext.getUserInfo().getIp();

		orderVo.setClientIp(clientIp);
		orderVo.setTenantId(TenantContext.getTenantId());

		OrderPricingResponse response = orderService.calcOrderPrice(orderVo);
		Date expireDate = DateUtils.addDays(new Date(response.getExpireTime()), -1);
		response.setExpireTime(expireDate.getTime());
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", response);
	}

	/**
	 * 支付页面
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("prePay")
	public ModelAndView prePay(String orderId) throws Exception {
		OrderVo vo = orderService.getPayStatus(orderId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId",orderId);
		if(vo != null){
			map.put("status", vo.getStatus());
			map.put("statusName", vo.getStatusName());
		}
		map.put("orderType", vo.getOrderType());
		return new ModelAndView("pay/payOrder", "model", map);
	}



	private void notifyTenantReadyToHuikuan(OrderVo order){
		List<SaasUser> users = saasUserRoleService.findSaasUsersByRoleId(financialUserRoleId);
		if (CollectionUtils.isEmpty(users)) {
			logger.error("财务角色人员为空");
			return;
		}

		try {
			for (SaasUser user : users) {
				if (StringUtils.isNotBlank(user.getPhoneNumber())) {
					sMSsenderService.SMSsender(user.getPhoneNumber(), Integer.parseInt(newOrderNotifySMSTemplateId));
				}
			}
		} catch (Exception e) {
			logger.error("订单: 发送银行汇款订单提醒短信失败, 订单信息: {}, 异常信息: {}", order, e);
		}

		try {
			MailVo mailVo = new MailVo();
			Map<String, String> recipientsMaps = new HashMap<>();
			for(SaasUser user : users){
				if(StringUtils.isNotBlank(user.getEmail())){
					recipientsMaps.put(user.getEmail(), user.getUserName());
				}
			}

			SaasTenant saasTenant = saasTenantService.selectByIdSaasTenantInfo(order.getTenantId());

			Map<String, String> templateParamMaps = new HashMap<>();
			templateParamMaps.put("corporateName", saasTenant.getTenantName());
			templateParamMaps.put("serviceName", order.getOrderSubject());
			templateParamMaps.put("orderAmount", order.getTotalPrice());

			mailVo.setRecipientsMap(recipientsMaps);
			mailVo.setTemplateName("newOrder.remind.ftl");
			mailVo.setTemplateParamMap(templateParamMaps);
			mailVo.setSubject("银行汇款订单提醒");
			mailSenderService.sendMail(mailVo);

		} catch (Exception e){
			logger.error("订单: 发送银行汇款订单提醒邮件失败, 订单信息: {}, 异常信息: {}", order, e);
		}
	}

	private void notifyOrderCancel(OrderVo order){
		if(!PayChannelEnum.HUIKUAN.getCode().equals(order.getPayChannel())){
			return;
		}

		List<SaasUser> users = saasUserRoleService.findSaasUsersByRoleId(financialUserRoleId);
		if (CollectionUtils.isEmpty(users)) {
			logger.error("财务角色人员为空");
			return;
		}

		try {
			for (SaasUser user : users) {
				if (StringUtils.isNotBlank(user.getPhoneNumber())) {
					sMSsenderService.SMSsender(user.getPhoneNumber(), Integer.parseInt(cancelOrderNotifySMSTemplateId));
				}
			}
		} catch (Exception e) {
			logger.error("订单: 发送订单取消提醒短信失败, 订单信息: {}, 异常信息: {}", order, e);
		}

		try {
			MailVo mailVo = new MailVo();
			Map<String, String> recipientsMaps = new HashMap<>();
			for(SaasUser user : users){
				if(StringUtils.isNotBlank(user.getEmail())){
					recipientsMaps.put(user.getEmail(), user.getUserName());
				}
			}

			SaasTenant saasTenant = saasTenantService.selectByIdSaasTenantInfo(order.getTenantId());

			Map<String, String> templateParamMaps = new HashMap<>();
			templateParamMaps.put("corporateName", saasTenant.getTenantName());
			templateParamMaps.put("serviceName", order.getOrderSubject());
			templateParamMaps.put("orderAmount", order.getTotalPrice());

			mailVo.setRecipientsMap(recipientsMaps);
			mailVo.setTemplateName("bankRemittance.order.cancel.ftl");
			mailVo.setTemplateParamMap(templateParamMaps);
			mailVo.setSubject("银行汇款订单取消提醒");
			mailSenderService.sendMail(mailVo);

		} catch (Exception e){
			logger.error("订单: 发送取消银行汇款订单提醒邮件失败, 订单信息: {}, 异常信息: {}", order, e);
		}
	}
}
