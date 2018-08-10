package com.thinkwin.pay.service;

import com.thinkwin.orders.dto.PayResult;
import com.thinkwin.pay.dto.PaymentVo;

import java.util.Map;

public interface EBank {

	boolean payOrder(PaymentVo payment);

	String prePay(PaymentVo payment);

	boolean tradePagePay(PaymentVo payment);

	boolean tradePagePay(String tradeNo);

	PayResult queryOrder(String tradeNo);

	boolean verifyPayNotify(Map<String, String> params);

}
