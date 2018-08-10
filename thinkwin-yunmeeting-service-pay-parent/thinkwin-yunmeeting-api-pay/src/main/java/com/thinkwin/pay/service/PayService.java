package com.thinkwin.pay.service;

import com.thinkwin.orders.dto.ConfirmResult;
import com.thinkwin.orders.dto.OrderPaymentConfirm;
import com.thinkwin.pay.dto.PaymentVo;

import java.util.Collection;
import java.util.List;

public interface PayService {
	String FAKE_SELLER_ID = "yunmeeting";

	String payOrder(PaymentVo payment);

	String preCreatePayment(PaymentVo payment);

	PaymentVo queryPayment(String tradeNo);

	PaymentVo querySimplePayment(String tradeNo);

	PaymentVo getPaymentById(String orderId);

	/**
	 * 不包含paymentlog信息
	 * @param orderId
	 * @return
	 */
	PaymentVo getSimplePaymentById(String orderId);

	/**
	 * 查询订单的支付信息
	 * @param ids
	 * @return
	 */
	List<PaymentVo> getPaymentByIds(Collection<String> ids);

	boolean updatePaymentCertImg(String orderId, String imgUrl);

	/**
	 * 确认已收到银行汇款后更新订单状态
	 * @param request
	 * @return
	 */
	ConfirmResult paymentReceived(OrderPaymentConfirm request);

	/**
	 * 查询第三方支付状态
	 * @param request
	 * @return
	 */
	ConfirmResult queryPayStatus(OrderPaymentConfirm request);

}
