package com.thinkwin.pay.util;

import com.thinkwin.pay.dto.PaymentVo;
import com.thinkwin.pay.model.PayChannelEnum;
import com.thinkwin.pay.model.PayStatus;
import com.thinkwin.pay.model.Payment;
import com.thinkwin.pay.model.PaymentLog;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;

public class VoUtil {

	public static PaymentVo fromPayment(Payment payment){
		if(null == payment){
			return null;
		}

		PaymentVo dto = new PaymentVo();
		dto.setTenantId(payment.getTenantId());
		dto.setOrderId(payment.getOrderId());
		dto.setSellerId(payment.getSellerId());
		dto.setSubject(payment.getSubject());
		dto.setBody(payment.getBody());
		dto.setTotalAmount(payment.getTotalAmount());
		dto.setFeeType(payment.getFeeType());
		dto.setExpireTime(payment.getExpireTime());
		dto.setTimerTaskId(payment.getTimerTaskId());
		dto.setCertImgUrl(payment.getCertImgUrl());
		dto.setStatus(payment.getStatus());

		PayStatus status = PayStatus.fromCode(payment.getStatus());
		if(status != null){
			dto.setStatusName(status.toString());
		}

		if(CollectionUtils.isNotEmpty(payment.getPaymentLogs())){
			PaymentLog log = Collections.max(payment.getPaymentLogs(), Comparator.comparing(PaymentLog::getCreateTime));
			dto.setTradeNo(log.getTradeNo());
			dto.setOutTradeNo(log.getOutTradeNo());
			dto.setStatus(log.getStatus());
			dto.setBuyerUserId(log.getBuyerUserId());
			dto.setBuyerUserName(log.getBuyerUserName());
			dto.setClientIp(log.getClientIp());
			dto.setPayInfo(log.getPayInfo());
			dto.setPayChannel(log.getPayChannel());
			dto.setChannelName(PayChannelEnum.fromCode(log.getPayChannel()).toString());
		}
		return dto;
	}
}
