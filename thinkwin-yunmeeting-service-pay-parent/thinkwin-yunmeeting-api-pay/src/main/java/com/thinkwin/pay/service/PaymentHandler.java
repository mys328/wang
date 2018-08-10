package com.thinkwin.pay.service;

import com.thinkwin.pay.dto.PayNotify;

public interface PaymentHandler {
	boolean process(PayNotify notify, String sign);
}
