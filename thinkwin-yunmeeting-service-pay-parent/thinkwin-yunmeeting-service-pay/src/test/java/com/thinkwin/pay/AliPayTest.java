package com.thinkwin.pay;

import com.github.wxpay.sdk.WXPayUtil;
import com.thinkwin.orders.dto.ConfirmResult;
import com.thinkwin.orders.dto.OrderPaymentConfirm;
import com.thinkwin.orders.dto.PayResult;
import com.thinkwin.orders.service.OrderService;
import com.thinkwin.pay.mapper.PayChannelMapper;
import com.thinkwin.pay.mapper.PayNotifyLogMapper;
import com.thinkwin.pay.mapper.PaymentMapper;
import com.thinkwin.pay.model.*;
import com.thinkwin.pay.service.EBank;
import com.thinkwin.pay.service.NotifyExecutor;
import com.thinkwin.pay.service.PayService;
import com.thinkwin.pay.service.PaymentRepo;
import com.thinkwin.pay.service.impl.DefaultNotifyExecutor;
import com.thinkwin.pay.util.CryptoUtil;
import com.thinkwin.pay.dto.PaymentVo;
import com.thinkwin.pay.util.PayUtil;
import com.thinkwin.serialnumber.service.SerialNumberService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import sun.security.krb5.Config;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations="classpath:spring-config.xml")
public class AliPayTest {

	public static final String ORDER_SN = "order";
	@Resource
	EBank zfbPayService;

	@Resource
	AlipayConfig aliPayConf;

	@Resource
	EBank wxPayService;

	@Resource
	WxPayConfig wxPayConf;

	@Autowired
	SerialNumberService serialNumberService;

	@Autowired
	PaymentMapper paymentMapper;

	@Autowired
	PayNotifyLogMapper payNotifyMapper;

	@Autowired
	PaymentRepo paymentRepo;

	@Autowired
	PayService payService;

	@Autowired
	NotifyExecutor notifyExecutor;

	@Autowired
	PayChannelMapper payChannelMapper;


	@Test
	public void testPayOrder(){
		PaymentVo vo = new PaymentVo();
		vo.setOrderId("ad70406e23834f4ba644eec229ede4cd");
		vo.setPayChannel(4);
		payService.payOrder(vo);
	}

	@Test
	public void testPaymentConfirmed(){
		OrderPaymentConfirm confirm = new OrderPaymentConfirm();
		confirm.setAppId("console");
		confirm.setOrderId("");
		confirm.setTimestamp("");
		confirm.setNonceStr("");

		String content = PayUtil.getSignContent(confirm);
		try {
			String sign = CryptoUtil.rsaSign(content, "", "utf-8");
			confirm.setSign(sign);
		} catch (Exception e) {
			e.printStackTrace();
		}

		payService.paymentReceived(confirm);
	}

	@Test
	public void testNotify(){
		String tradeNo = "17091310580";
		notifyExecutor.notify(tradeNo);

		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPaymentLogMapper(){
		Payment payment = paymentMapper.getPaymentById("1000");
		Assert.assertNotNull(payment);
		Assert.assertNotNull(payment.getPaymentLogs());
	}

	@Test
	public void testQueryOrders(){
//		String tradeNo = "2017091421001004390233124834";
//		PayResult result = zfbPayService.queryOrder(tradeNo);
//
//		Assert.assertTrue(result.getOutTradeNo().equals(tradeNo));

		String tradeNo = "4003132001201709152185502628";
		PayResult result = wxPayService.queryOrder(tradeNo);

		Assert.assertTrue(result.getOutTradeNo().equals(tradeNo));

	}


	@Test
	public void testConfirmMannully() throws Exception {
//		String tradeNo = "2017091421001004390233124834";
//		PayResult result = zfbPayService.queryOrder(tradeNo);
//
//		Assert.assertTrue(result.getOutTradeNo().equals(tradeNo));

		OrderPaymentConfirm confirm = new OrderPaymentConfirm();
		confirm.setAppId("console");
		confirm.setAccountId("");
		confirm.setPaymentNo("2017091521001004390235109196");
		confirm.setOrderId("6360161899b0452fb2749581565f19b8");
		confirm.setTimestamp(Long.toString((new Date()).getTime()));
		confirm.setNonceStr(WXPayUtil.generateNonceStr());
		String key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJhxS8DUL1ZuS/9Ocni+cSOU1GEzU1O++/hDt9exrzultf3RdwLR/DrzxvqC+EdFw4j1H+aZkWMboR7rU5qVwjDve1Z4PHKT3cs2DPWz53YKiAFlElQFmwpXs3fFkNnQsLRAJ3NHJ8E/HxFzVuLmcUciDXjI3vQpi0tDEIN3op51AgMBAAECgYAItD9dDUB+sOGJ/FKw7j7Lh03xFtslJvyAGm2+1RhqUXXovjMLaC38t7qexyeh+ivGACkII7CXdhZnC4zEgNSLnDGu6QQhgcUzwsdunJMs6Jy6r60yGdtim2SAI6X1tFwBNgc2gkvR2f9hQNuVtJMB1bio8fo5BbqafENDQnBPKQJBAMZwI+UBA5w5BuUWnNTzfCkkMBI/I/0pxxoCd0v7PXE21eBgngp8LfPaq2ffJfADNAsOxKhtM/YDuB4VwE40cPsCQQDEqZDMw6z0uMaBui+MmUvab8T7Q89cww1qW4SjTzCrbxa+bkUxkJxWRAoU/PemVqZDkOyXigmK/IuyVlTDqXNPAkEApzSaqj3+gsZyjtpM8AryVeBOOTHG620mfT4Ss41RqnHgKZ5/zKnyNoXM5EtdNi4czix1fM33eJaNr5v4XG1HvQJAYu7OmkQB2SavLgnE690ebKf3l1OYmJjDQfMCOcmXuVZsQaZlhrax/y/BRqevye0jPTBF5UIa5OGBQVju0XOIPwJBAKBn86rVzP8GdiqwC1Qy769BRpi5X6jeMgEi9JXT6d3GJr0TpUmi9Ylv1uiyLoXzIgjKQR+J/kxLOm/4LTIFh2g=";
		String content = PayUtil.getSignContent(confirm);
		String sign = CryptoUtil.rsaSign(content, key, "utf-8");
		confirm.setSign(sign);

		ConfirmResult result = payService.paymentReceived(confirm);
		Assert.assertTrue(result.getOutTradeNo().equals(confirm.getPaymentNo()));

	}

	@Test
	public void testMapper(){
		String clientIp = "210.12.69.98";
		PayChannel payChannel = payChannelMapper.selectByPrimaryKey(2);

		String orderNum = serialNumberService.generateSerialNumber(ORDER_SN, null).toString();
		Payment payment = new Payment();
		payment.setOrderId(orderNum);
		payment.setSubject("订单基础版-" + orderNum);
		payment.setBody("订单描述");
		payment.setTotalAmount(0.01D);
		payment.setCreateTime(new Date());
		payment.setExpireTime(DateUtils.addHours(payment.getCreateTime(), 2));

		paymentMapper.insert(payment);
		Payment p = paymentRepo.getPaymentByOrderId(payment.getOrderId());
		Assert.assertEquals(payment.getSubject(), p.getSubject());
	}

//	@Test
	public void testUpdateSelective(){
		String orderNum = "17080410117";
		Payment payment = new Payment();
		payment.setOrderId(orderNum);
		payment.setStatus(PayStatus.TRADE_SUCCESS.getValue());

		int success = paymentMapper.updateByPrimaryKeySelective(payment);
		Assert.assertTrue(success != 0);
	}

//	@Test
	public void testOrderNo(){
		Set<String> orderIds = new HashSet<String>();
		for(int i=0;i<20;i++){
			String orderNum = serialNumberService.generateSerialNumber(ORDER_SN, null).toString();
			System.out.println(orderNum);
			Assert.assertNotNull(orderNum);
			Assert.assertFalse(orderIds.contains(orderNum));
		}
	}

//	@Test
	public void testCrypto() throws Exception {
		String charSet = "UTF-8";
		String content = "hello world";
		String sign = CryptoUtil.rsaSign(content, aliPayConf.getPrivateKey(), charSet);
		boolean valid = CryptoUtil.rsaCheckContent(content, sign, aliPayConf.getPublicKey(), charSet);
		Assert.assertTrue(valid);
	}

	public void testPrePay(){
		String orderNum = serialNumberService.generateSerialNumber(ORDER_SN, null).toString();
		PaymentVo payment = new PaymentVo();
		payment.setSubject("订单基础版-" + orderNum);
		payment.setBody("订单描述");
		payment.setTotalAmount(0.01D);
		String result = zfbPayService.prePay(payment);
		System.out.println(result);
	}

	public void testTradePagePay(){
		String orderNum = serialNumberService.generateSerialNumber(ORDER_SN, null).toString();
		PaymentVo payment = new PaymentVo();
		payment.setSubject("订单基础版-" + orderNum);
		payment.setBody("订单描述");
		payment.setTotalAmount(0.01D);
		zfbPayService.tradePagePay(payment);
	}

	public void testWxPay(){
		String orderNum = serialNumberService.generateSerialNumber(ORDER_SN, null).toString();
		PaymentVo payment = new PaymentVo();
		payment.setSubject("订单基础版-1");
		payment.setBody("订单描述");
		payment.setTotalAmount(150d);

		String clientIp = "210.12.69.98";
		PayChannel payChannel = payChannelMapper.selectByPrimaryKey(2);

		String tradeNo = serialNumberService.generateSerialNumber(ORDER_SN, null).toString();
		boolean success = wxPayService.tradePagePay(payment);
		if(success){
			PaymentLog paymentLog = paymentRepo.getPaymentLogById(payment.getTradeNo());
			System.out.print(paymentLog.getPayInfo());
		}
	}

//	@Test
	public void testPayService(){
		String clientIp = "210.12.69.98";
		PayChannel payChannel = payChannelMapper.selectByPrimaryKey(2);

		PaymentVo payment = new PaymentVo();
//		payment.setPayInfo("17080810018");
		payment.setOrderId("65678");
		payment.setSubject("企业版会议系统-100");
		payment.setBody("订单描述");
		payment.setTotalAmount(150d);

		payService.payOrder(payment);
	}
}
