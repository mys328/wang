package com.thinkwin.pay.service.impl;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.orders.dto.PayResult;
import com.thinkwin.pay.dto.PaymentVo;
import com.thinkwin.pay.mapper.PaymentLogMapper;
import com.thinkwin.pay.mapper.PaymentMapper;
import com.thinkwin.pay.model.*;
import com.thinkwin.pay.service.EBank;
import com.thinkwin.pay.service.PaymentRepo;
import com.thinkwin.pay.util.PayUtil;
import com.thinkwin.serialnumber.service.SerialNumberService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WxPayService implements EBank {
	private static Logger log = LoggerFactory.getLogger(WxPayService.class);

	private WxPayConfig config;
	private WXPay wxpay;

	@Autowired
	SerialNumberService serialNumberService;

	@Autowired
	private SysLogService logService;

	@Autowired
	PaymentMapper paymentMapper;

	@Autowired
	PaymentLogMapper paymentLogMapper;

	@Autowired
	PaymentRepo paymentRepo;

	private static final String API_URLSUFFIX = "/pay/unifiedorder";

	public void setConfig(WxPayConfig config) {
		this.config = config;
	}

	public void init() {
		try {
			wxpay = new WXPay(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean payOrderAsync(Payment payment) {
		String tradeNo = serialNumberService.generateSerialNumber("order", null).toString();
		PaymentLog paymentLog = new PaymentLog();
		paymentLog.setOrderId(payment.getOrderId());
		paymentLog.setTradeNo(tradeNo);
		paymentLogMapper.insert(paymentLog);

		HashMap<String, String> data = new HashMap<String, String>();
		data.put("body", payment.getBody());
		data.put("out_trade_no", paymentLog.getTradeNo());

		String totalFee = BigDecimal.valueOf(payment.getTotalAmount()).scaleByPowerOfTen(2).toPlainString();
		data.put("total_fee", totalFee);
		data.put("notify_url", config.getNotifyUrl());
		//JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付
		data.put("trade_type", "NATIVE");
		data.put("spbill_create_ip", "123.12.12.123");

		try {
			Map<String, String> r = wxpay.unifiedOrder(data);
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean payOrder(PaymentVo payment) {
		return false;
	}

	@Override
	public PayResult queryOrder(String tradeNo) {
		PayResult payResult = new PayResult();
		payResult.setPayStatus(PayStatus.TRADE_FAILED.name());
		payResult.setResultCode("FAIL");
		PayStatus paymentStatus = null;

		HashMap<String, String> params = new HashMap<>();
		params.put("transaction_id", tradeNo);

		try {
			wxpay.fillRequestData(params);
			String response = wxpay.requestWithCert("/pay/orderquery", params, config.getHttpConnectTimeoutMs(), config.getHttpReadTimeoutMs());
			Map<String, String> map = WXPayUtil.xmlToMap(response);
			boolean isValid = wxpay.isResponseSignatureValid(map);
			if(isValid){
				if(map.containsKey("trade_state")){
					String tradeStatte = map.get("trade_state");
					if("SUCCESS".equals(tradeStatte)){
						payResult.setPayStatus(PayStatus.TRADE_SUCCESS.name());
						payResult.setTradeNo(map.get("out_trade_no"));
						payResult.setBuyerUserId(map.get("openid"));
						payResult.setOutTradeNo(map.get("transaction_id"));
						payResult.setResultCode(tradeStatte);
						Double totalAmount = Double.parseDouble(map.get("total_fee"));
						payResult.setTotalAmount(PayUtil.formatAmount(totalAmount));
					} else {
						payResult.setPayStatus(paymentStatus.name());
						log.error("支付: 查询微信支付订单状态失败, 订单交易失败, 服务器响应: {}", response);
						payResult.setErrorMsg(map.get("err_code_des"));
					}
				} else {
					log.error("支付: 查询微信支付订单状态失败, 查询失败, 服务器响应: {}", response);
				}
			} else {
				log.error("支付: 查询微信支付订单状态验证签名失败: {}", response);
			}
		} catch (Exception e) {
			log.error("支付: 查询微信支付订单状态失败, 请求参数: {}, 错误信息: {}", params, e);
			e.printStackTrace();
		}

		return payResult;
	}

	@Override
	public String prePay(PaymentVo payment) {
		Map<String, String> params = new HashMap<String, String>();
		try {
			WXPayUtil.generateSignature(params, config.getKey());
		} catch (Exception e) {
//			WXPayUtil.mapToXml(params);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean tradePagePay(PaymentVo request){
		request.setSellerId(config.getAppID());
		request.setPayChannel(PayChannelEnum.WEIXIN.getCode());
		Payment payment = paymentRepo.newPayment(request);
		if(null == payment || CollectionUtils.isEmpty(payment.getPaymentLogs())){
			return false;
		}

		String clientIp = request.getClientIp();
		Double totalAmount = request.getTotalAmount();

		String totalFee = BigDecimal.valueOf(totalAmount).setScale(2, BigDecimal.ROUND_HALF_EVEN)
				.scaleByPowerOfTen(2).toPlainString();
		String productId = request.getProductId();


		HashMap<String, String> params = new HashMap<>();
		params.put("body", request.getSubject());
		if(StringUtils.isNotBlank(request.getBody())){
			params.put("detail", request.getBody());
		}
		params.put("out_trade_no", request.getTradeNo());
		params.put("time_stamp", String.valueOf(WXPayUtil.getCurrentTimestamp()));
		params.put("total_fee", totalFee);
		params.put("spbill_create_ip", clientIp);
		params.put("notify_url", config.getNotifyUrl());
		params.put("trade_type", "NATIVE");
		params.put("product_id", productId);

		String code_url = "";
		try {
			wxpay.fillRequestData(params);
			String response = wxpay.requestWithCert(API_URLSUFFIX, params, config.getHttpConnectTimeoutMs(), config.getHttpReadTimeoutMs());
			Map<String, String> map = WXPayUtil.xmlToMap(response);
			boolean isValid = wxpay.isResponseSignatureValid(map);
			if(isValid){
				if(map.containsKey("code_url")){
					code_url = map.get("code_url");
					PaymentLog paymentLog = payment.getPaymentLogs().get(0);
					paymentLog.setPayInfo(code_url);
					paymentLog.setStatus(PayStatus.WAIT_BUYER_PAY.getValue());
					paymentLogMapper.updateByPrimaryKeySelective(paymentLog);
					return true;
				}
				else{
					log.error("支付: 获取微信支付二维码失败, 订单ID: {}, 微信支付返回的错误信息: {}", request.getOrderId(), response);
				}
			}
			else{
				log.error("支付: 获取微信支付二维码失败, 订单ID: {}, 微信支付返回的错误信息: {}", request.getOrderId(), response);
			}
		} catch (Exception e) {
			log.error("支付: 获取微信支付二维码失败, 订单ID: {}, 请求参数: {}", request.getOrderId(), params);
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean tradePagePay(String tradeNo) {
		return false;
	}

	public boolean refund(String tradeNo, double amount) {
		if(amount < 0.01){

		}
		return false;
	}

	@Override
	@Transactional
	public boolean verifyPayNotify(Map<String, String> params) {

		boolean isValid = false;
		try {
			isValid = wxpay.isResponseSignatureValid(params);

			if(!isValid){
				String logTxt = String.format("微信支付签名校验失败");
				log.error("支付: 微信支付通知签名校验失败!");
				logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), params.get("out_trade_no")+"支付失败", logTxt, Loglevel.error.toString());
				return false;
			}

			if(!"SUCCESS".equals(params.get("return_code"))){
				String logTxt = String.format("支付: 微信支付失败, return_msg: %s", params.get("return_msg"));
				log.error(logTxt);
				logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), params.get("out_trade_no")+"支付失败", logTxt, Loglevel.error.toString());
				return false;
			}


			if(!"SUCCESS".equals(params.get("result_code"))){
				String logTxt = String.format("支付: 微信支付失败, err_code: %s, err_code_des: %s", params.get("err_code"), params.get("err_code_des"));
				log.error(logTxt);
				logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), params.get("out_trade_no")+"支付失败",logTxt , Loglevel.error.toString());
				return false;
			}

			String mch_id = params.get("mch_id");
			String out_trade_no = params.get("out_trade_no");
			String transaction_id = params.get("transaction_id");
			String openid = params.get("openid");
			String total_fee = params.get("total_fee");

			if(!mch_id.equals(config.getMchID())){
				return false;
			}

			if(StringUtils.isBlank(transaction_id) || StringUtils.isBlank(out_trade_no)){
				return false;
			}

			PaymentLog paymentLog = paymentLogMapper.selectByPrimaryKey(out_trade_no);

			if(null == paymentLog){
				String logTxt = String.format("支付: 微信支付，没有找到支付请求记录, 内部支付流水号：%s, 第三方支付流水号: %s", out_trade_no, transaction_id);
				log.error(logTxt);
				logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(),out_trade_no+"支付失败", logTxt,  Loglevel.error.toString());
				return false;
			}

			if(!PayStatus.WAIT_BUYER_PAY.getValue().equals(paymentLog.getStatus())){
				String logTxt = String.format("支付: 微信支付，支付状态不是待付款, 内部支付流水号：%s, 第三方支付流水号: %s", out_trade_no, transaction_id);
				log.error(logTxt);
				logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
				return false;
			}

			Payment payment = paymentMapper.selectPaymentByIdLocked(paymentLog.getOrderId());

			if(PayStatus.TRADE_SUCCESS.getValue().equals(payment.getStatus())){
				String logTxt = String.format("支付: 微信支付，交易已结束， 订单ID: %s, 内部支付流水号：%s, 第三方支付流水号: %s", payment.getOrderId(), out_trade_no, transaction_id);
				log.error(logTxt);
				logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
				return true;
			}

			if(!PayStatus.WAIT_BUYER_PAY.getValue().equals(payment.getStatus())){
				String logTxt = String.format("支付: 微信支付，支付状态不是待付款, 内部支付流水号：{}, 第三方支付流水号: {}", out_trade_no, transaction_id);
				log.error(logTxt);
				logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
				return true;
			}

			if(payment == null){
				String logTxt = String.format("支付: 微信支付，没有找到支付信息, 内部支付流水号：%s, 第三方支付流水号: %s", out_trade_no, transaction_id);
				log.error(logTxt);
				logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
				return false;
			}

			BigDecimal x = new BigDecimal(total_fee);
			BigDecimal y = BigDecimal.valueOf(payment.getTotalAmount()).scaleByPowerOfTen(2);

			if(x.compareTo(y) != 0){
				String logTxt = String.format("支付: 微信支付，支付金额校验失败, 微信返回的交易金额: %s, 订单系统记录的交易金额: %s", x.toPlainString(), y.toPlainString());
				log.error(logTxt);
				logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
				return false;
			}

			if(payment.getStatus().equals(PayStatus.WAIT_BUYER_PAY.getValue())){
				Payment newPayment = new Payment();
				newPayment.setOrderId(payment.getOrderId());
				newPayment.setUpdateTime(new Date());
				newPayment.setTradeNo(out_trade_no);
				newPayment.setStatus(PayStatus.TRADE_SUCCESS.getValue());

				paymentLog.setTradeNo(out_trade_no);
				paymentLog.setOutTradeNo(transaction_id);
				paymentLog.setBuyerUserId(openid);
				paymentLog.setStatus(PayStatus.TRADE_SUCCESS.getValue());
				paymentRepo.updatePaymentStatus(newPayment, paymentLog);
				isValid = true;

				String logTxt = String.format("支付: 微信支付，支付成功, 订单ID: %s", payment.getOrderId());
				log.info(logTxt);
				logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付成功", "", Loglevel.info.toString());
			}
			else{
				String logTxt = String.format("支付: 微信支付，收到交易成功通知时订单不是待付款状态, 订单ID: %s", payment.getOrderId());
				log.error(logTxt);
				logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
			}

		} catch (Exception e) {
			String logTxt = String.format("支付: 微信支付， verifyPayNotify: %s", e);
			log.error(logTxt);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(),params.get("out_trade_no")+"支付失败", logTxt, Loglevel.error.toString());
		}
		return isValid;
	}
}
