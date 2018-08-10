package com.thinkwin.pay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.AlipayUtils;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.orders.dto.PayResult;
import com.thinkwin.pay.mapper.PaymentLogMapper;
import com.thinkwin.pay.mapper.PaymentMapper;
import com.thinkwin.pay.model.*;
import com.thinkwin.pay.service.EBank;
import com.thinkwin.pay.service.PaymentRepo;
import com.thinkwin.pay.util.JSONUtils;
import com.thinkwin.pay.util.PayUtil;
import com.thinkwin.pay.dto.PaymentVo;
import com.thinkwin.serialnumber.service.SerialNumberService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.params.Params;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZfbPayService implements EBank {

	private static Logger log = LoggerFactory.getLogger(ZfbPayService.class);

	private AlipayConfig config;
	private AlipayClient alipayClient;

	@Autowired
	private SerialNumberService serialNumberService;

	@Autowired
	private SysLogService logService;

	@Autowired
	private PaymentMapper paymentMapper;

	@Autowired
	private PaymentLogMapper paymentLogMapper;

	@Autowired
	private PaymentRepo paymentRepo;

	public void setConfig(AlipayConfig config){
		this.config = config;
		alipayClient = new DefaultAlipayClient(config.getServerUrl(), config.getAppId(), config.getPrivateKey(), config.getFormat(), config.getCharset(), config.getZfbPublicKey(), config.getSignType());
	}

	public AlipayClient getAlipayClient(){
		return this.alipayClient;
	}

	public boolean payOrderAsync(Payment payment) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			AlipaySignature.rsaCheckV1(map, config.getZfbPublicKey(), config.getCharset());
		} catch (AlipayApiException e) {
		}
		return true;
	}

	@Override
	public boolean payOrder(PaymentVo payment) {
		boolean success = false;
		Map<String, String> params = new HashMap<String, String>();
		log.info("payOrder: params={}", params);

		String sign = "";
		try {
			sign = AlipaySignature.rsaSign(params, config.getPrivateKey(),config.getCharset());
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}

		try {
			success = AlipaySignature.rsaCheckContent(JSONUtils.toJson(params), sign, config.getZfbPublicKey(), config.getCharset());
			return success;
		} catch (AlipayApiException e) {
			log.error("payOrder: {}", e);
		}
		return success;
	}

	@Override
	public PayResult queryOrder(String tradeNo) {
		PayResult payResult = new PayResult();
		payResult.setPayStatus(PayStatus.TRADE_FAILED.name());
		payResult.setResultCode("FAIL");

		if(StringUtils.isBlank(tradeNo)){
			return payResult;
		}

		//https://doc.open.alipay.com/doc2/detail.htm?treeId=26&articleId=757&docType=4
		Map<String, String> params = new HashMap<String, String>();
		params.put("trade_no", tradeNo);

		try {
			AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
			request.setBizContent(JSONUtils.toJson(AlipayUtils.cleanupMap(params)));
			AlipayTradeQueryResponse response = alipayClient.execute(request);

			//https://docs.open.alipay.com/203/105286/
			if(response.isSuccess()){
				log.info("支付: 查询支付宝订单支付状态成功, 支付宝订单号: {}", tradeNo);

				payResult.setPayStatus(response.getTradeStatus());
				payResult.setTradeNo(response.getOutTradeNo());
				payResult.setBuyerUserId(response.getBuyerUserId());
				payResult.setOutTradeNo(response.getTradeNo());
				payResult.setTotalAmount(response.getTotalAmount());
				if("TRADE_SUCCESS".equals(response.getTradeStatus())
						|| "TRADE_FINISHED".equals(response.getTradeStatus())){
					payResult.setResultCode("SUCCESS");
				}
			} else {
				log.error("支付: 查询支付宝订单支付状态失败, 支付宝订单号: {}，错误信息", tradeNo, response.getMsg());

				payResult.setErrorMsg(response.getMsg());
			}
		} catch (AlipayApiException e) {
			log.error("支付: 查询支付宝订单支付状态失败, 请求参数: {}, 错误信息: {}", params, e);
		}

		return payResult;
	}

	@Override
	public String prePay(PaymentVo dto) {
		try {
			AlipayTradePrecreateRequest requestInfo = new AlipayTradePrecreateRequest();
			requestInfo.setNotifyUrl(config.getNotifyUrl());

			dto.setSellerId(config.getAppId());
			dto.setPayChannel(PayChannelEnum.ZFB.getCode());
			Payment payment = paymentRepo.newPayment(dto);

			if(null == payment || CollectionUtils.isEmpty(payment.getPaymentLogs())){
				return "";
			}

			PaymentLog paymentLog = payment.getPaymentLogs().get(0);

			String out_trade_no = paymentLog.getTradeNo();

			Map<String, String> params = new HashMap<String, String>();
			params.put("out_trade_no", out_trade_no);
			params.put("total_amount", BigDecimal.valueOf(dto.getTotalAmount()).toPlainString());
			params.put("subject", dto.getSubject());
			if(StringUtils.isNotBlank(dto.getBody())){
				params.put("body", dto.getBody());
			}
//			String sign = AlipaySignature.rsaSign(params, config.getPrivateKey(),config.getCharset());
//			params.put("sign", sign);
			requestInfo.setBizContent(JSONUtils.toJson(params));

			AlipayTradePrecreateResponse execute = alipayClient.execute(requestInfo);
			String qrCode = execute.getQrCode();
//			String qrCodeUrl = LIANTU_URL + URLEncoder.encode(qrCode, "UTF-8");
			String body = execute.getBody();
//			log.info("qrCode:{}, qrCodeUrl:{}, Body:{}", qrCode, qrCodeUrl, body);
			return qrCode;

		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public boolean tradePagePay(PaymentVo dto) {
		AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
		alipayRequest.setReturnUrl(config.getReturnUrl());
		alipayRequest.setNotifyUrl(config.getNotifyUrl());

		dto.setPayChannel(PayChannelEnum.ZFB.getCode());
		dto.setSellerId(config.getAppId());
		Payment payment = paymentRepo.newPayment(dto);

		if(null == payment || CollectionUtils.isEmpty(payment.getPaymentLogs())){
			return false;
		}

		PaymentLog paymentLog = payment.getPaymentLogs().get(0);

		String out_trade_no = paymentLog.getTradeNo();
		String total_amount = BigDecimal.valueOf(dto.getTotalAmount())
				.setScale(2, BigDecimal.ROUND_HALF_EVEN).toPlainString();
		String subject = dto.getSubject();

		String body = "";
		if(StringUtils.isNotBlank(dto.getBody())){
			body = dto.getBody();
		}

		String content = "{\"out_trade_no\":\""+ out_trade_no +"\","
				+ "\"total_amount\":\""+ total_amount +"\","
				+ "\"subject\":\""+ subject +"\","
				+ "\"body\":\""+ body +"\","
				+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}";

		alipayRequest.setBizContent(content);

		try {
			AlipayResponse response = alipayClient.pageExecute(alipayRequest);

			if(response.isSuccess()){
				paymentLog.setPayInfo(response.getBody());
				paymentLog.setStatus(PayStatus.WAIT_BUYER_PAY.getValue());
				paymentLogMapper.updateByPrimaryKeySelective(paymentLog);
				return true;
			}
			else{
				String errMsg = "";
				if(response != null){
					errMsg = response.getMsg();
				}
				log.error("支付: 获取支付宝支付二维码失败, 订单ID: {}, 支付宝返回的错误信息: {}", dto.getOrderId(), errMsg);
			}
		} catch (AlipayApiException e) {
			log.error("支付: 获取支付宝支付二维码失败, 订单ID: {}, 请求参数: {}", dto.getOrderId(), content);
		}
		return false;
	}

	@Override
	public boolean tradePagePay(String tradeNo) {
		return false;
	}

	public boolean refund(String tradeNo, double amount) {
		Map<String, String> params = new HashMap<String, String>();

		AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
		request.setBizContent(JSONUtils.toJson(AlipayUtils.cleanupMap(params)));
		AlipayTradeRefundResponse response = null;

		Map<String, Object> data = new HashMap<String, Object>();
		try {
			response = alipayClient.execute(request);
			log.info("refund: {}", response.getBody());
			return "Y".equals(response.getFundChange());
		} catch (Exception e) {
			log.error("refund", e);
		}

		return false;
	}

	@Override
	@Transactional
	public boolean verifyPayNotify(Map<String, String> params) {
		boolean isValid = false;

		try {
			isValid = AlipaySignature.rsaCheckV1(params, config.getZfbPublicKey(), config.getCharset(), config.getSignType());
		} catch (AlipayApiException e) {
			logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), params.get("out_trade_no")+"支付失败", e.getErrMsg(), Loglevel.error.toString());
			log.error("verifyPayNotify: {}", e);
		}

		if(!isValid){
			return false;
		}

		String seller_id = params.get("seller_id");
		String out_trade_no = params.get("out_trade_no");
		String trade_no = params.get("trade_no");
		String trade_status = params.get("trade_status");
		String buyer_id = params.get("buyer_id");
		String total_amount = params.get("total_amount");

		if("TRADE_FINISHED".equals(trade_status)){
			return true;
			//判断该笔订单是否在商户网站中已经做过处理
			//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
			//如果有做过处理，不执行商户的业务程序

			//注意：
			//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
		}

		if (!"TRADE_SUCCESS".equals(trade_status)) {
			String logTxt = String.format("支付: 支付宝支付，支付失败, 内部支付流水号：%s, 支付状态: %s", out_trade_no, trade_status);
			log.error(logTxt);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
			return false;
		}

		if(!config.getPartner().equals(seller_id)){
			String logTxt = String.format("支付: 支付宝，商户ID不匹配, 交易通知返回的商户ID：%s, 系统配置的商户ID: %s", seller_id, config.getPartner());
			log.error(logTxt);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
			return false;
		}

		if(StringUtils.isBlank(trade_no) || StringUtils.isBlank(out_trade_no)){
			String logTxt = String.format("支付: 支付宝，系统内没有找到支付请求信息, 内部支付流水号：%s, 第三方支付流水号: %s", out_trade_no, trade_no);
			log.error(logTxt);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
			return false;
		}

		PaymentLog paymentLog = paymentRepo.getPaymentLogById(out_trade_no);

		if(null == paymentLog || StringUtils.isBlank(paymentLog.getOrderId())){
			String logTxt = String.format("支付: 支付宝，没有找到支付请求信息,内部支付流水号：%s, 第三方支付流水号: %s", out_trade_no, trade_no);
			log.error(logTxt);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
			return false;
		}

		if(!PayStatus.WAIT_BUYER_PAY.getValue().equals(paymentLog.getStatus())){
			String logTxt = String.format("支付: 支付宝，支付状态不是待付款, 内部支付流水号：%s, 第三方支付流水号: %s", out_trade_no, trade_no);
			log.error(logTxt);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
			return false;
		}

		Payment payment = paymentMapper.selectPaymentByIdLocked(paymentLog.getOrderId());
		if(null == payment){
			String logTxt = String.format("支付: 支付宝，没有找到支付信息,内部支付流水号：%s, 第三方支付流水号: %s", out_trade_no, trade_no);
			log.error(logTxt);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(),out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
			return false;
		}


		if(PayStatus.TRADE_SUCCESS.getValue().equals(payment.getStatus())){
			String logTxt = String.format("支付: 支付宝支付，支付状态不是待付款, 内部支付流水号：%s, 第三方支付流水号: %s", out_trade_no, trade_no);
			log.error(logTxt);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
			return true;
		}

		if(!PayStatus.WAIT_BUYER_PAY.getValue().equals(payment.getStatus())){
			String logTxt = String.format("支付: 支付宝支付，支付状态不是待付款, 内部支付流水号：%s, 第三方支付流水号: %s", out_trade_no, trade_no);
			log.error(logTxt);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
			return true;
		}

		BigDecimal x = new BigDecimal(total_amount);
		BigDecimal y = BigDecimal.valueOf(payment.getTotalAmount());

		if(x.compareTo(y) != 0){
			String logTxt = String.format("支付: 支付宝，支付金额校验失败, 支付宝返回的交易金额: %s, 订单系统记录的交易金额: %s", x.toPlainString(), y.toPlainString());
			log.error(logTxt);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
			return false;
		}

		if(PayStatus.WAIT_BUYER_PAY.getValue().equals(payment.getStatus())){
			Payment newPayment = new Payment();
			newPayment.setOrderId(payment.getOrderId());
			newPayment.setTradeNo(out_trade_no);
			newPayment.setUpdateTime(new Date());
			newPayment.setStatus(PayStatus.TRADE_SUCCESS.getValue());

			paymentLog.setTradeNo(out_trade_no);
			paymentLog.setOutTradeNo(trade_no);
			paymentLog.setBuyerUserId(buyer_id);
			paymentLog.setStatus(PayStatus.TRADE_SUCCESS.getValue());

			paymentRepo.updatePaymentStatus(newPayment, paymentLog);
			String logTxt = String.format("支付: 支付宝，支付成功, 订单ID: %s", payment.getOrderId());
			log.info(logTxt);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(), out_trade_no+"支付成功", logTxt, Loglevel.info.toString());

			return true;
		}
		else{
			String logTxt = String.format("支付: 支付宝，交易失败, 订单ID: %s，交易状态: %s", payment.getOrderId(), trade_status);
			log.error(logTxt);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.orders_pay.toString(),out_trade_no+"支付失败", logTxt, Loglevel.error.toString());
		}

		return false;
	}
}