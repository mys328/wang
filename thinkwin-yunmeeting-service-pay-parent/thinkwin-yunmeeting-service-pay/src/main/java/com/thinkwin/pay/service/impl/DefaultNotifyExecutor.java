package com.thinkwin.pay.service.impl;

import com.thinkwin.pay.dto.PayNotify;
import com.thinkwin.pay.dto.PaymentVo;
import com.thinkwin.pay.mapper.PaymentLogMapper;
import com.thinkwin.pay.mapper.PaymentMapper;
import com.thinkwin.pay.model.Payment;
import com.thinkwin.pay.model.PaymentLog;
import com.thinkwin.pay.service.NotifyExecutor;
import com.thinkwin.pay.service.PaymentHandler;
import com.thinkwin.pay.util.CryptoUtil;
import com.thinkwin.pay.util.PayUtil;
import com.thinkwin.pay.util.VoUtil;
import com.thinkwin.schedule.service.TimerService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service(value = "notifyExecutor")
public class DefaultNotifyExecutor implements NotifyExecutor {

	private static final String NEW_LINE = System.getProperty("line.separator");

	private static Logger log = LoggerFactory.getLogger(DefaultNotifyExecutor.class);

	@Autowired
	private PaymentHandler handler;

	@Autowired
	PaymentMapper paymentMapper;

	@Autowired
	PaymentLogMapper paymentLogMapper;

	@Autowired
	TimerService timerService;

	@Resource
	Map<String, String> keyStoreTradeSuccess;

	private static ExecutorService executor = Executors.newWorkStealingPool();

	private static Integer[] retryPeriod = new Integer[]{15, 15, 30, 60, 300, 300, 600, 600, 1800, 1800, 3600};
//	private static Integer[] retryPeriod = new Integer[]{60, 60, 60, 60, 60, 60, 60, 60, 60};

	@Override
	public void notify(String tradeNo){
		executor.execute(new Runnable() {
			@Override
			public void run() {
				if(StringUtils.isBlank(tradeNo)){
					log.error("订单: 支付结果通知订单系统时异常, tradeNo为空");
					return;
				}

				PaymentLog paymentLog = paymentLogMapper.selectByPrimaryKey(tradeNo);
				if(paymentLog == null){
					log.error("订单: 支付结果通知订单系统时异常, PaymentLog为空, tradeNo: {}", tradeNo);
					return;
				}

				Payment payment = paymentMapper.selectByPrimaryKey(paymentLog.getOrderId());
				payment.addPaymentLog(paymentLog);
				PaymentVo vo = VoUtil.fromPayment(payment);
				vo.setPayChannel(paymentLog.getPayChannel());

				boolean result = false;
				try{
					Integer retryTimes = payment.getRetryTimes();
					if(null == retryTimes){
						retryTimes = 0;
					}

					payment.setRetryTimes(retryTimes);

					if(retryTimes >= retryPeriod.length){
						log.error("订单: 支付结果通知订单系统时异常, 超过最大重试次数, tradeNo: {}", tradeNo);
						return;
					}

					PayNotify notify = new PayNotify();
					notify.setOrderId(vo.getOrderId());
					notify.setAppId("daemon");
					notify.setNonceStr(CryptoUtil.genRandomStr());
					notify.setTenantId(vo.getTenantId());
					notify.setTradeNo(vo.getTradeNo());
					notify.setPayChannel(vo.getPayChannel().toString());
					notify.setTimestamp(Long.toString(PayUtil.nowTime().getTime()));

					String content = PayUtil.getSignContent(notify);
					String privateKey = keyStoreTradeSuccess.get(notify.getAppId());
					String sign = "";
					try{
						sign = CryptoUtil.rsaSign(content, privateKey, "utf-8");
					} catch (Exception ex){
						log.error("订单: 支付结果通知订单系统时异常, 签名校验失败, tradeNo: {}", tradeNo);
					}

					result = handler.process(notify, sign);
				}
				catch (Exception e){
					log.error("订单: 支付结果通知订单系统时异常, tradeNo: {}, 异常信息: {}", tradeNo, e.getMessage());
				}
				finally {

					try{
						payment.setRetryTimes(payment.getRetryTimes() + 1);
						paymentMapper.updateByPrimaryKeySelective(payment);
					} catch (Exception ex){
						log.error("订单: 支付结果通知订单系统时异常, 更新重试次数失败, tradeNo: {}", tradeNo);
					}
				}

				if(payment.getRetryTimes() >= retryPeriod.length){
					return;
				}

				if(!result){
					log.error("订单: 支付结果通知订单系统时异常, 第{}次重试失败, tradeNo: {}", payment.getRetryTimes(), tradeNo);

					Date scheduleTime = DateUtils.addSeconds(new Date(), retryPeriod[payment.getRetryTimes()]);
					NotifyRetryWorker worker = new NotifyRetryWorker();
					worker.setTradeNo(tradeNo);

					timerService.schedule(worker, scheduleTime);
				}
			}
		});
	}
}
