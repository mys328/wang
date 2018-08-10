package com.thinkwin.pay.service;

import com.thinkwin.pay.model.Payment;
import com.thinkwin.pay.model.PaymentLog;
import com.thinkwin.pay.dto.PaymentVo;

public interface PaymentRepo {
	Payment getPaymentByOrderId(String orderId);

	Payment getPaymentByIdLocked(String orderId);

	Payment newPayment(PaymentVo paymentVo);

	PaymentLog getPaymentLogById(String tradeNo);

	boolean updatePaymentStatus(Payment payment, PaymentLog paymentLog);
}
