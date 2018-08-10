package com.thinkwin.orders.schedule;

import com.thinkwin.common.ContextHolder;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.mailsender.service.YunmeetingSendMailService;
import com.thinkwin.mailsender.vo.MailVo;
import com.thinkwin.orders.mapper.OrderMapper;
import com.thinkwin.orders.model.Order;
import com.thinkwin.orders.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderPayRemindJob implements Runnable {
	private static Logger LOGGER = LoggerFactory.getLogger(OrderPayRemindJob.class);

	private String orderId;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Override
	public void run() {
		List<Integer> WAIT_BUYER_PAY_STATUS = Arrays.asList(OrderStatus.WEIZHIFU.getCode(), OrderStatus.DAIHUIKUAN.getCode());

		MailVo mailVo = new MailVo();
		try{
			OrderMapper orderMapper = (OrderMapper) ContextHolder.getApplicationContext().getBean("orderMapper");
			Order order = orderMapper.selectByPrimaryKey(orderId);

			if(WAIT_BUYER_PAY_STATUS.contains(order.getStatus())){
				SaasTenantService saasTenantService = (SaasTenantService) ContextHolder.getApplicationContext().getBean("saasTenantService");
				YunmeetingSendMailService sendMailService = (YunmeetingSendMailService) ContextHolder.getApplicationContext().getBean("sendMailService");

				SaasTenant saasTenant = saasTenantService.selectByIdSaasTenantInfo(order.getTenantId());
				Map<String, String> recipientsMaps = new HashMap<>();
				recipientsMaps.put(saasTenant.getContactsEmail(), saasTenant.getContacts());
				Map<String, String> templateParamMaps = new HashMap<>();
				templateParamMaps.put("orderNumber", order.getOrderSn());
				templateParamMaps.put("productName", order.getOrderSubject());

				mailVo.setRecipientsMap(recipientsMaps);
				mailVo.setTemplateName("order.payment.reminder.ftl");
				mailVo.setTemplateParamMap(templateParamMaps);
				mailVo.setSubject("订单支付提醒通知");
				sendMailService.sendMail(mailVo);
			}

		} catch (Exception e){
			LOGGER.error("订单: 发送订单支付提醒邮件失败, 订单ID: {}, 邮件信息: , 异常信息: {}", orderId, mailVo, e);
		}
	}
}
