package com.thinkwin.pay.service.impl;

import com.thinkwin.orders.service.OrderService;
import com.thinkwin.pay.dto.PaymentVo;
import com.thinkwin.pay.mapper.PaymentLogMapper;
import com.thinkwin.pay.mapper.PaymentMapper;
import com.thinkwin.pay.model.PayChannelEnum;
import com.thinkwin.pay.model.PayStatus;
import com.thinkwin.pay.model.Payment;
import com.thinkwin.pay.model.PaymentLog;
import com.thinkwin.pay.service.PayService;
import com.thinkwin.pay.service.PaymentRepo;
import com.thinkwin.pay.util.PayUtil;
import com.thinkwin.serialnumber.service.SerialNumberService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service(value = "paymentRepo")
public class MyPaymentRepo implements PaymentRepo {
	@Autowired
	private PaymentMapper paymentMapper;

	@Autowired
	private PaymentLogMapper paymentLogMapper;

	@Autowired
	private SerialNumberService serialNumberService;

	@Autowired
	private OrderService orderService;

	@Override
	public Payment getPaymentByOrderId(String orderId) {
		return paymentMapper.selectByPrimaryKey(orderId);
	}

	public Payment getPaymentByIdLocked(String tradeNo){
		return paymentMapper.selectPaymentByIdLocked(tradeNo);
	}

	@Override
	public Payment newPayment(PaymentVo dto) {
		if (null == dto.getFeeType()) {
			dto.setFeeType("CNY");
		}

		if (null == dto.getBody()) {
			dto.setBody("");
		}

		if(StringUtils.isBlank(dto.getSellerId())){
			return null;
		}

		if(StringUtils.isBlank(dto.getTradeNo())){
			return null;
		}

		if(dto.getPayChannel() == null){
			return null;
		}

		if(PayChannelEnum.fromCode(dto.getPayChannel()) == null){
			return null;
		}

		Integer payChannel = dto.getPayChannel();
		String tradeNo = dto.getTradeNo();
		String sellerId = dto.getSellerId();

		Payment payment = null;
		payment = paymentMapper.selectPaymentByIdLocked(dto.getOrderId());

		if (null == payment) {
			payment = new Payment();
			payment.setTradeNo(tradeNo);
			payment.setTenantId(dto.getTenantId());
			payment.setTimerTaskId(dto.getTimerTaskId());
			payment.setSellerId(sellerId);
			payment.setOrderId(dto.getOrderId());
			payment.setSubject(dto.getSubject());
			payment.setBody(dto.getBody());
			payment.setTotalAmount(dto.getTotalAmount());
			payment.setFeeType(dto.getFeeType());
			payment.setCreateTime(PayUtil.nowTime());
			payment.setExpireTime(dto.getExpireTime());
			if(PayStatus.TRADE_SUCCESS.getValue().equals(dto.getStatus()) && dto.getTotalAmount() != null && dto.getTotalAmount().equals(0D)){
				payment.setStatus(PayStatus.TRADE_SUCCESS.getValue());
			} else{
				payment.setStatus(PayStatus.WAIT_BUYER_PAY.getValue());
			}
			payment.setCertImgUrl("");
			paymentMapper.insertSelective(payment);
		} else{
			Payment newPayment = new Payment();
			newPayment.setOrderId(dto.getOrderId());
			payment.setStatus(PayStatus.WAIT_BUYER_PAY.getValue());
			if(StringUtils.isNotBlank(dto.getTimerTaskId())){
				newPayment.setSellerId(sellerId);
			}
			newPayment.setTimerTaskId(dto.getTimerTaskId());
			newPayment.setTradeNo(tradeNo);
			newPayment.setUpdateTime(new Date());
			newPayment.setExpireTime(dto.getExpireTime());
			paymentMapper.updateByPrimaryKeySelective(newPayment);
		}

		//预创建支付信息时无需创建log
		if(!PayService.FAKE_SELLER_ID.equals(dto.getSellerId())){
			PaymentLog paymentLog = new PaymentLog();
			paymentLog.setOrderId(payment.getOrderId());
			paymentLog.setTradeNo(tradeNo);
			paymentLog.setOutTradeNo("");
			paymentLog.setPayChannel(payChannel);
			paymentLog.setCreateTime(PayUtil.nowTime());
			paymentLog.setExpireTime(DateUtils.addHours(PayUtil.nowTime(), 2));
			paymentLog.setClientIp(dto.getClientIp());
			payment.addPaymentLog(paymentLog);
			paymentLog.setStatus(payment.getStatus());

			if(null == paymentLog.getBuyerUserName()){
				paymentLog.setBuyerUserName("");
			}

			if(null == paymentLog.getClientIp()){
				paymentLog.setClientIp("");
			}

			paymentLogMapper.insertSelective(paymentLog);
		}


		return payment;
	}

	@Override
	public PaymentLog getPaymentLogById(String tradeNo) {
		PaymentLog paymentLog = paymentLogMapper.selectByPrimaryKey(tradeNo);
		return paymentLog;
	}

	@Override
	public boolean updatePaymentStatus(Payment payment, PaymentLog paymentLog) {
		paymentMapper.updateByPrimaryKeySelective(payment);
		paymentLogMapper.updateByPrimaryKeySelective(paymentLog);

		return true;
	}
}
