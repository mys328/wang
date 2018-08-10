package com.thinkwin.pay.util;

import com.thinkwin.orders.dto.OrderPaymentConfirm;
import com.thinkwin.pay.dto.PayNotify;
import com.thinkwin.pay.model.*;
import com.thinkwin.pay.dto.PaymentVo;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class PayUtil {

	public static boolean isValidPayment(PaymentVo payment){
		if(payment.getTotalAmount() <= 0){
			return false;
		}

		BigDecimal fee = BigDecimal.valueOf(payment.getTotalAmount());
		if(fee.scale() > 2){
			return false;
		}

		if(null == payment.getPayChannel() || payment.getPayChannel() < 0){
			return false;
		}

		if(null == payment.getPayWay() || payment.getPayWay() < 0){
			return false;
		}

		if(StringUtils.isBlank(payment.getSubject())){
			return false;
		}

		if(payment.getSubject().length() > 128){
			return false;
		}

		if(StringUtils.isBlank(payment.getClientIp())){
			return false;
		}

		return true;
	}


	public static Date nowTime(){
		return new Date();
	}

	public static boolean isPaymentExpired(PaymentVo payment){
		return payment.getExpireTime().before(nowTime());
	}

	public boolean isPaymentClosed(PaymentVo pay){
		if(null == pay || null == pay.getStatus()){
			return false;
		}

		if(PayStatus.TERMINATED.contains(PayStatus.fromCode(pay.getStatus()))){
			return true;
		}

		return false;
	}

	public static String getSignContent(PayNotify notify){
		if(StringUtils.isBlank(notify.getOrderId())){
			return "";
		}

		if(StringUtils.isBlank(notify.getAppId())){
			return "";
		}

		if(StringUtils.isBlank(notify.getTenantId())){
			return "";
		}

		if(StringUtils.isBlank(notify.getPayChannel())){
			return "";
		}

		if(StringUtils.isBlank(notify.getTradeNo())){
			return "";
		}

		if(StringUtils.isBlank(notify.getTimestamp())){
			return "";
		}

		if(StringUtils.isBlank(notify.getNonceStr())){
			return "";
		}

		String content = "orderId=" + notify.getOrderId() + ";appId=" + notify.getAppId() + ";tenantId=" + notify.getTenantId() + ";tradeNo=" + notify.getTradeNo() + ";payChannel=" + notify.getPayChannel() + ";timestamp=" + notify.getTimestamp() + ";nonceStr=" + notify.getNonceStr();
		return content;
	}


	public static OrderPaymentConfirm signOrderPaymentConfirm(OrderPaymentConfirm confirm, String privateKey) throws Exception {
		String content = getSignContent(confirm);
		String sign = CryptoUtil.rsaSign(content, privateKey, "utf-8");
		confirm.setSign(sign);

		return confirm;
	}


	public static String getSignContent(OrderPaymentConfirm confirm){
		if(null == confirm){
			return "";
		}

		if(StringUtils.isBlank(confirm.getAppId())){
			return "";
		}

		if(StringUtils.isBlank(confirm.getAccountId())){
			confirm.setAccountId("");
		}

		if(StringUtils.isBlank(confirm.getPaymentNo())){
			confirm.setPaymentNo("");
		}

		if(StringUtils.isBlank(confirm.getOrderId())){
			return "";
		}

		if(StringUtils.isBlank(confirm.getTimestamp())){
			return "";
		}

		if(StringUtils.isBlank(confirm.getNonceStr())){
			return "";
		}

		if(null == confirm.getPaymentNo()){
			confirm.setPaymentNo("");
		}

		String content = "orderId=" + confirm.getOrderId() + ";appId=" + confirm.getAppId() + ";accountId=" + confirm.getAccountId() + ";outTradeNo=" + confirm.getPaymentNo() + ";timestamp=" + confirm.getTimestamp() + ";nonceStr=" + confirm.getNonceStr();
		return content;
	}


	public static String formatAmount(Double amount){
		DecimalFormat dFormat = new DecimalFormat("#0.00");
		dFormat.setRoundingMode(RoundingMode.HALF_EVEN);
		return dFormat.format(amount);
	}
}
