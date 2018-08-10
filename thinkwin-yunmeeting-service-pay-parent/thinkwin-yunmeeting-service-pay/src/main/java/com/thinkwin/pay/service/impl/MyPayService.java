package com.thinkwin.pay.service.impl;

import com.thinkwin.common.utils.TimeUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.orders.dto.ConfirmResult;
import com.thinkwin.orders.dto.OrderPaymentConfirm;
import com.thinkwin.orders.dto.PayResult;
import com.thinkwin.orders.model.OrderStatus;
import com.thinkwin.orders.service.OrderService;
import com.thinkwin.orders.vo.OrderVo;
import com.thinkwin.pay.mapper.PayChannelMapper;
import com.thinkwin.pay.mapper.PaymentLogMapper;
import com.thinkwin.pay.mapper.PaymentMapper;
import com.thinkwin.pay.model.*;
import com.thinkwin.pay.service.EBank;
import com.thinkwin.pay.service.NotifyExecutor;
import com.thinkwin.pay.service.PayService;
import com.thinkwin.pay.service.PaymentRepo;
import com.thinkwin.pay.util.CryptoUtil;
import com.thinkwin.pay.util.PayUtil;
import com.thinkwin.pay.dto.PaymentVo;
import com.thinkwin.pay.util.VoUtil;
import com.thinkwin.serialnumber.service.SerialNumberService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MyPayService implements PayService {
	private static Logger LOGGER = LoggerFactory.getLogger(ZfbPayService.class);

	private static List<Integer> onlinePay = Arrays.asList(PayChannelEnum.WEIXIN.getCode(), PayChannelEnum.ZFB.getCode());

	private static String ADMIN_ORDER_PAYMENT_CONFIRM_NONCESTR_CACHE_PREFIX = "ADMIN_CANCEL_ORDER_NONCESTR:";

	private static int CACHE_TTL = 10; // in seconds

	private Integer orderExpireInHours = 24;

	private Integer orderExpireInMinutes = 0;

	private Integer orderExpireInDays = 7;

	private static final ExecutorService executor = Executors.newWorkStealingPool(3);

	public void setOrderExpireInHours(Integer orderExpireInHours) {
		this.orderExpireInHours = orderExpireInHours;
	}

	public void setOrderExpireInMinutes(Integer orderExpireInMinutes) {
		this.orderExpireInMinutes = orderExpireInMinutes;
	}

	public void setOrderExpireInDays(Integer orderExpireInDays) {
		this.orderExpireInDays = orderExpireInDays;
	}

	@Resource(name = "serviceMap")
	private Map<Integer, EBank> serviceMap;

	@Autowired
	private SerialNumberService serialNumberService;

	@Autowired
	private PaymentMapper paymentMapper;

	@Autowired
	private PaymentRepo paymentRepo;

	@Autowired
	private PaymentLogMapper paymentLogMapper;

	@Autowired
	private PayChannelMapper payChannelMapper;

	@Autowired
	private OrderService orderService;

	@Autowired
	private NotifyExecutor notifyExecutor;

	@Resource
	private Map<String, String> trustStoreTradeSuccess;

	@Override
	public String payOrder(PaymentVo request) {
		if(StringUtils.isBlank(request.getOrderId())
				|| PayChannelEnum.fromCode(request.getPayChannel()) == null){
			return "";
		}

		OrderVo orderVo = orderService.getOrderById(request.getOrderId());
		if(null == orderVo){
			return "";
		}

		Date nowTime = new Date();
		Date expireTime = DateUtils.addMinutes(nowTime, orderExpireInHours * 60 + orderExpireInMinutes);
		if(!PayService.FAKE_SELLER_ID.equals(request.getSellerId()) &&
				PayChannelEnum.HUIKUAN.getCode().equals(request.getPayChannel())){
			expireTime = DateUtils.addDays(PayUtil.nowTime(), orderExpireInDays);
		}

		request.setExpireTime(expireTime);
		request.setCreateTime(nowTime);

		request.setSubject(orderVo.getOrderSubject());
		request.setTotalAmount(Double.parseDouble(orderVo.getPayPrice()));
		request.setProductId(orderVo.getOrderId());

		Payment payment = paymentMapper.selectByPrimaryKey(request.getOrderId());
		if(payment != null){
			if(!PayStatus.WAIT_BUYER_PAY.getValue().equals(payment.getStatus())){
				return "";
			}

			if(StringUtils.isNotBlank(payment.getTradeNo())){
				PaymentLog log = paymentLogMapper.selectByPrimaryKey(payment.getTradeNo());

				// 选择银行汇款后不允许修改支付方式
				if(log != null && PayChannelEnum.HUIKUAN.getCode().equals(log.getPayChannel())){
					return log.getTradeNo();
				}

				//如果支付方式变更则更新定时任务
				if(log == null || isPayWayChanged(request.getPayChannel(), log.getPayChannel())){
					request.setExpireTime(expireTime);
					String timerTaskId = orderService.freshOrderTimerTask(payment.getOrderId(), payment.getTimerTaskId(), expireTime);
					request.setTimerTaskId(timerTaskId);
				} else if(PayChannelEnum.HUIKUAN.getCode().equals(request.getPayChannel())){
					return log.getTradeNo();
				} else if(request.getPayChannel().equals(log.getPayChannel())){
					long timeLeft = TimeUtil.getDateDiff(PayUtil.nowTime(), log.getExpireTime(), TimeUnit.MINUTES);
					if(StringUtils.isNotBlank(log.getPayInfo()) && timeLeft > 5){
						return log.getTradeNo();
					}
				}
			}
		}


		String tradeNo = serialNumberService.generateSerialNumber("order", null).toString();
		request.setTradeNo(tradeNo);
		request.setProductId(orderVo.getOrderId());

		if(PayChannelEnum.HUIKUAN.getCode().equals(request.getPayChannel())){
			request.setSellerId("thinkwin");
			Payment newPayment = paymentRepo.newPayment(request);
			if(newPayment != null){
				return tradeNo;
			}
		}

		EBank bank = serviceMap.get(request.getPayChannel());
		if(null == bank){
			String[] a = new String[0];
			return "";
		}

		boolean success = bank.tradePagePay(request);
		if(success){
			return tradeNo;
		}

		return "";
	}

	private boolean isPayWayChanged(Integer oldChannel, Integer newChannel){
		if(oldChannel.equals(newChannel)){
			return false;
		}

		if(onlinePay.contains(oldChannel) && onlinePay.contains(newChannel)){
			return false;
		}

		return true;
	}


	@Override
	public String preCreatePayment(PaymentVo request) {
		if(StringUtils.isBlank(request.getOrderId())
				|| PayChannelEnum.fromCode(request.getPayChannel()) == null){
			return "";
		}

		OrderVo orderVo = orderService.getOrderById(request.getOrderId());
		if(null == orderVo){
			return "";
		}

		if(PayChannelEnum.HUIKUAN.getCode().equals(request.getPayChannel())){
			request.setPayChannel(request.getPayChannel());
			request.setSellerId("thinkwin");
		}

		Date nowTime = new Date();
		Date expireTime = DateUtils.addMinutes(nowTime, orderExpireInHours * 60 + orderExpireInMinutes);
		request.setExpireTime(expireTime);
		request.setCreateTime(nowTime);

		request.setSubject(orderVo.getOrderSubject());
		request.setTotalAmount(Double.parseDouble(orderVo.getPayPrice()));
		request.setProductId(orderVo.getOrderId());

		Payment payment = paymentMapper.selectByPrimaryKey(request.getOrderId());

		if(payment != null){
			return payment.getTradeNo();
		}

		String tradeNo = serialNumberService.generateSerialNumber("order", null).toString();
		request.setTradeNo(tradeNo);
		request.setProductId(orderVo.getOrderId());

		request.setSellerId(FAKE_SELLER_ID);
		Payment newPayment = paymentRepo.newPayment(request);

		if(newPayment != null){
			return tradeNo;
		}

		return "";
	}

	@Override
	public PaymentVo queryPayment(String tradeNo) {
		PaymentLog log = paymentLogMapper.selectByPrimaryKey(tradeNo);
		PaymentVo vo = new PaymentVo();
		vo.setOrderId(log.getOrderId());
		vo.setTradeNo(log.getTradeNo());
		vo.setStatus(log.getStatus());
		vo.setPayInfo(log.getPayInfo());
		vo.setPayChannel(log.getPayChannel());

		return vo;
	}

	@Override
	public PaymentVo querySimplePayment(String tradeNo) {
		PaymentLog log = paymentLogMapper.selectByPrimaryKey(tradeNo);
		Payment payment = paymentMapper.selectByPrimaryKey(log.getOrderId());
		payment.addPaymentLog(log);

		return VoUtil.fromPayment(payment);
	}

	@Override
	public PaymentVo getPaymentById(String orderId) {
		Payment payment = paymentMapper.selectByPrimaryKey(orderId);

		if(null == payment){
			return null;
		}

		PaymentLog paymentLog = paymentLogMapper.selectByPrimaryKey(payment.getTradeNo());

		payment.addPaymentLog(paymentLog);
		return VoUtil.fromPayment(payment);
	}

	@Override
	public PaymentVo getSimplePaymentById(String orderId) {
		Payment payment = paymentMapper.selectByPrimaryKey(orderId);

		if(null == payment){
			return null;
		}

		return VoUtil.fromPayment(payment);
	}

	@Override
	public List<PaymentVo> getPaymentByIds(Collection<String> ids) {
		Example example = new Example(Payment.class);
		example.createCriteria()
				.andIn("orderId", ids);
		List<Payment> payments = paymentMapper.selectByExample(example);

		List<String> paymentIds = payments.stream()
				.map(p->p.getOrderId())
				.collect(Collectors.toList());

		Map<String, List<PaymentLog>> payLogMap = new HashMap<String, List<PaymentLog>>();
		if(CollectionUtils.isNotEmpty(paymentIds)){
			Example logExample = new Example(PaymentLog.class);
			logExample.createCriteria()
					.andIn("orderId", paymentIds);

			if(CollectionUtils.isNotEmpty(paymentIds)){
				List<PaymentLog> logs = paymentLogMapper.selectByExample(logExample);

				payLogMap = logs.stream()
						.collect(Collectors.groupingBy(l->l.getOrderId(), Collectors.toList()));
			}
		}

		List<PaymentVo> vos = new ArrayList<PaymentVo>();
		for(Payment payment : payments){
			payment.setPaymentLogs(payLogMap.get(payment.getOrderId()));
			vos.add(VoUtil.fromPayment(payment));
		}

		return vos;
	}

	@Override
	public boolean updatePaymentCertImg(String orderId, String imgUrl) {
		if(StringUtils.isBlank(orderId) || StringUtils.isBlank(imgUrl)){
			return false;
		}

		Payment payment = paymentMapper.selectByPrimaryKey(orderId);

		if(null == payment){
			return false;
		}

		Payment newPayment = new Payment();
		newPayment.setOrderId(payment.getOrderId());
		newPayment.setCertImgUrl(imgUrl);
		newPayment.setUpdateTime(new Date());

		paymentMapper.updateByPrimaryKeySelective(newPayment);
		return true;
	}

	@Override
	@Transactional
	public ConfirmResult paymentReceived(OrderPaymentConfirm confirm) {
		ConfirmResult result = new ConfirmResult();
		result.setSuccess(false);
		String content = PayUtil.getSignContent(confirm);

		if(StringUtils.isBlank(content)){
			return result;
		}

		if(!trustStoreTradeSuccess.containsKey(confirm.getAppId())){
			return result;
		}

		String key = trustStoreTradeSuccess.get(confirm.getAppId());

		boolean signValid = false;
		try{
			signValid = CryptoUtil.rsaCheckContent(content, confirm.getSign(), key, "utf-8");
		}
		catch (Exception e){}

		if(!signValid){
			String logTxt = String.format("支付: 人工确认支付到账失败，签名验证失败, 确认信息: %s", confirm);
			LOGGER.error(logTxt);
			result.setErrorMsg("签名验证失败!");
			return result;
		}

		if(confirm.getOrderRemark() != null && confirm.getOrderRemark().length() > 500){
			String logTxt = String.format("支付: 人工确认支付到账失败，订单备注超长");
			LOGGER.error(logTxt);
			result.setErrorMsg("订单备注超长!");
			return result;
		}

		String cacheKey = ADMIN_ORDER_PAYMENT_CONFIRM_NONCESTR_CACHE_PREFIX + confirm.getNonceStr();

		String value = RedisUtil.get(cacheKey);

		if(StringUtils.isNotBlank(value)){
			String logTxt = String.format("支付: 人工确认支付到账失败，重复的确认信息, 确认信息: %s", confirm);
			LOGGER.error(logTxt);
			result.setErrorMsg("重复的确认信息!");
			return result;
		}

		if(PayUtil.nowTime().getTime() < Long.parseLong(confirm.getTimestamp())){
			String logTxt = String.format("支付: 人工确认支付到账失败，无效的确认信息, 确认信息: %s", confirm);
			LOGGER.error(logTxt);
			result.setErrorMsg("无效的确认信息!");
			return result;
		}

		if(PayUtil.nowTime().getTime() - Long.parseLong(confirm.getTimestamp()) > TimeUnit.SECONDS.toMillis(CACHE_TTL)){
			String logTxt = String.format("支付: 人工确认支付到账失败，确认信息已过期, 确认信息: %s", confirm);
			LOGGER.error(logTxt);
			result.setErrorMsg("确认信息已过期!");
			return result;
		}
		RedisUtil.set(cacheKey, "1", CACHE_TTL);

		Payment payment = paymentMapper.selectPaymentByIdLocked(confirm.getOrderId());

		if(null == payment){
			String logTxt = String.format("支付: 人工确认支付到账失败，找不到订单信息, 确认信息: %s", confirm);
			LOGGER.error(logTxt);
			result.setErrorMsg("找不到订单信息!");
			return result;
		}

		if(!PayStatus.WAIT_BUYER_PAY.getValue().equals(payment.getStatus())){
			String logTxt = String.format("支付: 人工确认支付到账失败，订单不是待付款状态, 确认信息: %s", confirm);
			LOGGER.error(logTxt);
			result.setErrorMsg("订单不是待付款状态");
			return result;
		}

		PaymentLog log = paymentLogMapper.selectByPrimaryKey(payment.getTradeNo());
		if(null == log){
			String logTxt = String.format("支付: 人工确认支付到账失败，找不到支付记录, 确认信息: %s", confirm);
			LOGGER.error(logTxt);
			result.setErrorMsg("找不到支付记录!");
			return result;
		}

		OrderVo orderVo = orderService.getOrderById(log.getOrderId());
		if(orderVo.getStatus().equals(OrderStatus.QUXIAO.getCode())){
			String logTxt = String.format("支付: 人工确认支付到账失败，订单已取消。");
			LOGGER.error(logTxt);
			result.setErrorMsg("订单已取消!");
			return result;
		}

		Payment newPayment = new Payment();
		newPayment.setOrderId(confirm.getOrderId());
		PaymentLog paymentLog = new PaymentLog();
		paymentLog.setTradeNo(payment.getTradeNo());

		if(PayChannelEnum.ZFB.getCode().equals(log.getPayChannel())
				|| PayChannelEnum.WEIXIN.getCode().equals(log.getPayChannel())){

			if(StringUtils.isBlank(confirm.getPaymentNo())){
				result.setErrorMsg("缺少第三方支付交易单号!");
				LOGGER.error("支付: 人工确认支付到账失败，缺少第三方支付交易单号!");
				return result;
			}


			EBank bank = serviceMap.get(log.getPayChannel());
			PayResult payResult = bank.queryOrder(confirm.getPaymentNo());

			boolean success = "SUCCESS".equals(payResult.getResultCode());
			if(!success){
				String logTxt = String.format("支付: 人工确认支付到账失败，查询第三方交易平台失败, 确认信息: %s，错误信息: %s", confirm, payResult.getErrorMsg());
				LOGGER.error(logTxt);
				result.setErrorMsg("查询第三方交易平台失败!");
				return result;
			}

			if(!payment.getTradeNo().equals(payResult.getTradeNo())){
				result.setErrorMsg("不是当前订单的支付信息!");
				LOGGER.error("支付: 人工确认支付到账失败，不是当前订单的支付信息, 确认信息: {}", confirm);
				return result;
			}

			Example paymentLogExample = new Example(PaymentLog.class);
			paymentLogExample.createCriteria()
					.andEqualTo("outTradeNo", confirm.getPaymentNo());

			List<PaymentLog> logs = paymentLogMapper.selectByExample(paymentLogExample);
			if(CollectionUtils.isNotEmpty(logs)){
				result.setErrorMsg("重复的第三方支付交易单号!");
				LOGGER.error("支付: 人工确认支付到账失败，重复的第三方支付交易单号, 确认信息: {}", confirm);
				return result;
			}

			result.setSuccess("SUCCESS".equals(payResult.getResultCode()));
			result.setOutTradeNo(payResult.getOutTradeNo());
			result.setPayStatus(payResult.getPayStatus());

			paymentLog.setOutTradeNo(result.getOutTradeNo());
			paymentLog.setBuyerUserId(payResult.getBuyerUserId());
		} else {
			paymentLog.setOutTradeNo(confirm.getPaymentNo());
			paymentLog.setBuyerUserId(confirm.getAccountId());
		}

		Date updateTime = PayUtil.nowTime();
		paymentLog.setUpdateTime(updateTime);
		paymentLog.setStatus(PayStatus.TRADE_SUCCESS.getValue());

		newPayment.setUpdateTime(updateTime);
		newPayment.setStatus(PayStatus.TRADE_SUCCESS.getValue());

		boolean success = paymentRepo.updatePaymentStatus(newPayment, paymentLog);
		if(success){
			LOGGER.info("支付: 人工确认支付到账成功, 确认信息: {}", confirm);
			notifyExecutor.notify(payment.getTradeNo());
			executor.execute(()->{
				orderService.updateOrderRemark(confirm.getOrderId(), confirm.getOrderRemark());
			});
		} else {
			LOGGER.info("支付: 人工确认支付到账失败, 确认信息: {}", confirm);
		}

		result.setSuccess(success);
		return result;
	}

	@Override
	public ConfirmResult queryPayStatus(OrderPaymentConfirm request) {
		return null;
	}
}
