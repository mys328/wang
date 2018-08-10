package com.thinkwin.orders.service.impl;

import com.thinkwin.SMSsender.service.SMSsenderService;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.console.service.SaasUserRoleService;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.mailsender.service.YunmeetingSendMailService;
import com.thinkwin.mailsender.vo.MailVo;
import com.thinkwin.orders.mapper.OrderMapper;
import com.thinkwin.orders.model.Order;
import com.thinkwin.orders.model.OrderStatus;
import com.thinkwin.orders.model.OrderType;
import com.thinkwin.orders.service.OrderRepo;
import com.thinkwin.orders.service.OrderService;
import com.thinkwin.orders.vo.OrderLineVo;
import com.thinkwin.orders.vo.OrderVo;
import com.thinkwin.orders.vo.UomItem;
import com.thinkwin.pay.dto.PayNotify;
import com.thinkwin.pay.dto.PaymentVo;
import com.thinkwin.pay.model.PayChannelEnum;
import com.thinkwin.pay.service.PayService;
import com.thinkwin.pay.util.CryptoUtil;
import com.thinkwin.pay.util.PayUtil;
import com.thinkwin.schedule.service.TimerService;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.thinkwin.pay.service.PaymentHandler;

@Service(value = "paymentHandler")
public class DefaultPaymentNotifyHandler implements PaymentHandler {
	private static Logger log = LoggerFactory.getLogger(DefaultPaymentNotifyHandler.class);

	@Value("${order.financial_user_role_id}")
	private String financialUserRoleId = "3";

	@Value("${order.new_notify_sms_template_id}")
	private String newOrderNotifySMSTemplateId = "225939";

	@Autowired
	private OrderRepo orderRepo;

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private OrderService orderService;

	@Autowired
	private TimerService timerService;

	@Autowired
	private SaasTenantService saasTenantService;

	@Autowired
	private PayService payService;

	@Resource
	Map<String, String> trustStoreTradeSuccess;

	@Resource
	YunmeetingSendMailService sendMailService;

	@Autowired
	private SMSsenderService sMSsenderService;

	@Resource
	private SaasUserRoleService saasUserRoleService;

	List<Integer> status = Arrays.asList(OrderStatus.WEIZHIFU.getCode(), OrderStatus.DAIHUIKUAN.getCode());

	private static String NOTIFY_NONCESTR_CACHE_PREFIX = "PAY_NOTIFY_NONCESTR:";

	private static int NOTIFY_CACHE_TTL = 10; // in seconds

	private static final ExecutorService executor = Executors.newWorkStealingPool(3);

	@Override
	public boolean process(PayNotify notify, String sign) {
		try{
			log.info("订单: 订单支付成功, 通知内容: {}", notify.toString());

			String content = PayUtil.getSignContent(notify);

			log.info("订单: 订单支付成功, 通知签名: {}", content);

			if(StringUtils.isBlank(content)){
				log.error("订单: 订单支付成功, 签名内容为空, 订单ID: {}", notify.getOrderId());
				return false;
			}

			boolean signValid = false;
			try{
				String key = trustStoreTradeSuccess.get(notify.getAppId());
//				log.info("订单: 订单支付成功, 验证签名使用的key长度: {}", key.length());
				signValid = CryptoUtil.rsaCheckContent(content, sign, key, "utf-8");
			}
			catch (Exception e){
				log.error("订单: 订单支付成功, 创建签名失败, 订单ID: {}, 异常信息: {}", notify.getOrderId(), e);
			}

			if(!signValid){
				log.error("订单: 订单支付成功, 验证签名失败, 订单ID: {}", notify.getOrderId());
				return false;
			}

			String cacheKey = NOTIFY_NONCESTR_CACHE_PREFIX + notify.getNonceStr();

			String value = RedisUtil.get(cacheKey);

			if(StringUtils.isNotBlank(value)){
				log.error("订单: 订单支付成功, 重复请求, 订单ID: {}", notify.getOrderId());
				return false;
			}

			if(PayUtil.nowTime().getTime() < Long.parseLong(notify.getTimestamp())){
				log.error("订单: 订单支付成功, 请求时间戳异常, 订单ID: {}", notify.getOrderId());
				return false;
			}

			if(PayUtil.nowTime().getTime() - Long.parseLong(notify.getTimestamp()) > TimeUnit.SECONDS.toMillis(NOTIFY_CACHE_TTL)){
				log.error("订单: 订单支付成功, 请求时间戳已过期, 订单ID: {}", notify.getOrderId());
				return false;
			}

			RedisUtil.set(cacheKey, "1", NOTIFY_CACHE_TTL);

			Order order = orderMapper.selectByPrimaryKey(notify.getOrderId());
			if(null == order){
				log.error("订单: 订单支付成功, 订单不存在, 订单ID: {}", notify.getOrderId());
				return true;
			}

			if(!status.contains(order.getStatus())){
				log.error("订单: 订单支付成功, 订单不是待付款状态, 订单ID: {}, 订单状态: {}", notify.getOrderId(), order.getStatus());
				return true;
			}

			OrderVo vo = orderService.getOrderById(notify.getOrderId());
			boolean success = freshTenantServiceInfo(vo);

			boolean orderUpdated = false;
			if(success) {
				orderUpdated = orderRepo.updateOrderAfterPaySuccess(notify.getOrderId(), PayChannelEnum.fromCode(notify.getPayChannel()).toString());
			} else {
				log.error("订单: 更新租户授权信息失败, 订单ID: {}", notify.getOrderId());
			}

			if(!orderUpdated){
				log.error("订单: 更新本地租户订单信息失败, 订单ID: {}", notify.getOrderId());
				return false;
			}

			PaymentVo paymentVo = payService.getSimplePaymentById(notify.getOrderId());
			try{
				timerService.cancelTask(paymentVo.getTimerTaskId());
			} catch (Exception e){
				log.error("订单: 删除订单超时定时任务失败, 订单ID: {}, msg: {}", notify.getOrderId(), e);
			}

			executor.execute(()->{
				sendOrderSuccessEmailToTenant(order);

				List<Integer> onlinePayCodes =  Arrays.asList(PayChannelEnum.WEIXIN.getCode(), PayChannelEnum.ZFB.getCode());
				if(onlinePayCodes.contains(vo.getPayChannel())){
					sendEmailToFinancePersonnel(order);
					sendSMSToFinancePersonnel(order);
				}
			});

			log.info("订单: 订单支付成功, 订单状态更新成功, 订单ID: {}", notify.getOrderId());
			return true;

		}
		catch (Exception ex){
			log.error("订单: 支付通知处理异常, 订单ID:{}, 异常信息:{}", notify.getOrderId(), ex);
		}
		return false;
	}

	private boolean freshTenantServiceInfo(OrderVo vo) {
		try{
			if(null == vo){
				return false;
			}

			SaasTenant tenant = saasTenantService.selectByIdSaasTenantInfo(vo.getTenantId());
			if(tenant == null){
				return false;
			}

			String tenantType = tenant.getTenantType();

			Integer totalAclAmount = 0;
			Integer totalMeetingRoom = 0;
			Integer totalStorage = 0;

			Map<String, OrderLineVo> skuMap = new HashMap<>();

			vo.getOrderLines().forEach(lineVo -> {
				if(StringUtils.isNotBlank(lineVo.getProductSku())){
					skuMap.put(lineVo.getProductSku(), lineVo);
				}
			});


			totalAclAmount = skuMap.get("102").getQty();
			totalMeetingRoom = skuMap.get("100").getQty();
			totalStorage = skuMap.get("101").getQty();

			if(null == tenant.getBuyRoomNumTotal()){
				tenant.setBuyRoomNumTotal(0);
			}

			if(null == tenant.getBuySpaceNumTotal()){
				tenant.setBuySpaceNumTotal(0);
			}

			if(OrderType.UPGRADING.getCode().equals(vo.getOrderType())){
				tenant.setExpectNumber(totalAclAmount);
				tenant.setBuyRoomNumTotal(totalMeetingRoom);
				tenant.setBuySpaceNumTotal(totalStorage);

				if("0".equals(tenant.getTenantType())){
					tenant.setTenantType("1");
					tenant.setBasePackageStart(new Date());
				}
			} else if(OrderType.EXPANDING.getCode().equals(vo.getOrderType())){
				tenant.setExpectNumber(tenant.getExpectNumber() + totalAclAmount);
				tenant.setBuyRoomNumTotal(tenant.getBuyRoomNumTotal() + totalMeetingRoom);
				tenant.setBuySpaceNumTotal(tenant.getBuySpaceNumTotal() + totalStorage);
			} else if(OrderType.RENEWAL.getCode().equals(vo.getOrderType())){
				Integer giveawayACL = skuMap.containsKey("102") ? skuMap.get("102").getGiveaway() : 0;
				Integer giveawayMeetingRoom = skuMap.containsKey("100") ? skuMap.get("100").getGiveaway() : 0;
				Integer giveawayStorage = skuMap.containsKey("101") ? skuMap.get("101").getGiveaway() : 0;

				if(giveawayACL != null && giveawayACL > 0){
					tenant.setExpectNumber(tenant.getExpectNumber() + giveawayACL);
				}
				if(giveawayMeetingRoom != null && giveawayMeetingRoom > 0){
					tenant.setBuyRoomNumTotal(tenant.getBuyRoomNumTotal() + giveawayMeetingRoom);
				}

				if(giveawayStorage != null && giveawayStorage > 0){
					tenant.setBuySpaceNumTotal(tenant.getBuySpaceNumTotal() + giveawayStorage);
				}

				if(vo.getRentEnd() != null){
					tenant.setBasePackageExpir(DateUtils.addDays(vo.getRentEnd(), 1));
				}
			}

			if(OrderType.UPGRADING.getCode().equals(vo.getOrderType()) || OrderType.RENEWAL.getCode().equals(vo.getOrderType())) {
				if(vo.getRentEnd() == null){
					log.info("订单: 更新租户授权信息失败, 续费期限为空, 订单ID:{}", vo.getOrderId());
				}
				tenant.setBasePackageExpir(DateUtils.addDays(vo.getRentEnd(), 1));
			}

			boolean success = saasTenantService.updateSaasTenantService(tenant, vo.getOrderId());
			if(success){
				//刷新付费用户菜单
				if("0".equals(tenantType)){
					refreshTenantMenu(vo.getOrderId(), tenant.getId());
				}
				log.info("订单: 更新租户授权成功, 订单ID:{}", vo.getOrderId());
			} else{
				log.error("订单: 更新租户授权失败, 订单ID:{}", vo.getOrderId());
			}
			return success;
		}
		catch (Exception e){
			log.error("订单: 更新租户授权失败, 订单ID: {}, 异常信息: {}", vo.getOrderId(), e);
		}

		log.error("订单: 更新租户授权失败, 订单ID:{}", vo.getOrderId());
		return false;
	}

	private void refreshTenantMenu(String orderId, String tenantId){
		if(StringUtils.isBlank(tenantId)){
			return;
		}

		try{
			RedisUtil.delRedisKeys(tenantId + "_Menus_*");
		} catch (Exception e){
			log.error("订单: 删除用户菜单缓存数据失败, 订单ID: {}, 租户ID: {}, 异常信息: {}", orderId, tenantId, e);
		}
	}


	private void sendOrderSuccessEmailToTenant(Order order){
		MailVo mailVo = new MailVo();
		try{
			SaasTenant saasTenant = saasTenantService.selectByIdSaasTenantInfo(order.getTenantId());
			Map<String, String> recipientsMaps = new HashMap<>();
			recipientsMaps.put(saasTenant.getContactsEmail(), saasTenant.getContacts());

			Map<String, String> templateParamMaps = new HashMap<>();
			templateParamMaps.put("corporateName", saasTenant.getTenantName());
			templateParamMaps.put("productName", order.getOrderSubject());

			mailVo.setRecipientsMap(recipientsMaps);
			mailVo.setTemplateName("complete.payment.notice.ftl");
			mailVo.setTemplateParamMap(templateParamMaps);
			mailVo.setSubject("订单支付成功通知");
			sendMailService.sendMail(mailVo);
		} catch (Exception e){
			log.error("订单: 发送支付成功邮件失败, 订单ID: {}, 邮件信息：, 异常信息: {}", order.getOrderId(), mailVo, e);
		}
	}

	private void sendSMSToFinancePersonnel(Order order){
		try{
			List<SaasUser> users = saasUserRoleService.findSaasUsersByRoleId(financialUserRoleId);
			if(CollectionUtils.isEmpty(users)){
				log.error("订单通知角色人员为空");
				return;
			}

			for(SaasUser user : users){
				if(StringUtils.isBlank(user.getPhoneNumber())){
					log.error("财务人员手机号为空");
					continue;
				}
				sMSsenderService.SMSsender(user.getPhoneNumber(), Integer.parseInt(newOrderNotifySMSTemplateId));
			}
		} catch (Exception e){
			log.error("订单: 发送新订单提醒短信失败, 订单信息: {}, 异常信息: {}", order, e);
		}
	}

	private void sendEmailToFinancePersonnel(Order order){

		MailVo mailVo = new MailVo();
		try{
			List<SaasUser> users = saasUserRoleService.findSaasUsersByRoleId(financialUserRoleId);
			if(CollectionUtils.isEmpty(users)){
				log.error("订单通知角色人员为空");
				return;
			}

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
			NumberFormat formatter = new DecimalFormat("#0.00");
			templateParamMaps.put("orderAmount", formatter.format(order.getTotalPrice()));

			mailVo.setRecipientsMap(recipientsMaps);
			mailVo.setTemplateName("newOrder.remind.ftl");
			mailVo.setTemplateParamMap(templateParamMaps);
			mailVo.setSubject("新订单提醒");
			sendMailService.sendMail(mailVo);
		} catch (Exception e){
			log.error("订单: 发送新订单提醒邮件失败, 订单信息: {}, 邮件信息：, 异常信息: {}", order, mailVo, e);
		}
	}
}
