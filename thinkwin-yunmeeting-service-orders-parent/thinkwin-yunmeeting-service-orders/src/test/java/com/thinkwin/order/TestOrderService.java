package com.thinkwin.order;

import com.github.pagehelper.PageInfo;
import com.thinkwin.TenantUserVo;
import com.thinkwin.common.dto.order.OrderPricingResponse;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.mailsender.service.YunmeetingSendMailService;
import com.thinkwin.mailsender.vo.MailVo;
import com.thinkwin.orders.schedule.OrderCancelScheduleJob;
import com.thinkwin.orders.dto.OrderResponse;
import com.thinkwin.orders.service.OrderService;
import com.thinkwin.orders.vo.OrderLineVo;
import com.thinkwin.orders.vo.OrderVo;
import com.thinkwin.pay.dto.PayNotify;
import com.thinkwin.pay.service.PaymentHandler;
import com.thinkwin.pay.util.CryptoUtil;
import com.thinkwin.pay.util.PayUtil;
import com.thinkwin.schedule.service.TimerService;
import com.thinkwin.service.TenantContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations="classpath:spring-config.xml")
public class TestOrderService {
	private String orderId;

	@Autowired
	OrderService orderService;

	@Autowired
	PaymentHandler paymentHandler;

	@Autowired
	TimerService timerService;

	@Autowired
	YunmeetingSendMailService sendMailService;

	@Before
	public void beforeTest(){
		TenantContext.setTenantId("baadcf02ba44415b8715d2bf05f49c39");
		TenantContext.setUserInfo(new TenantUserVo());
		TenantContext.getUserInfo().setTenantId("baadcf02ba44415b8715d2bf05f49c39");
		orderId = "baadcf02ba44415b8715d2bf05f49c39";

	}

	@Test
	public void testNewOrder() throws IOException {
		OrderVo orderVo = new OrderVo();
//		{"payPrice":"0.17","orderLines":[{"productPackSku":"2001","qty":1}],"orderType":0}
		orderVo.setCreatedBy("a831e0567d6143d290ba7f8e2847c879");
		orderVo.setBuyerUserId("a831e0567d6143d290ba7f8e2847c879");
		orderVo.setBuyerUserName("userName");
		orderVo.setClientIp("127.0.0.1");
		orderVo.setTenantId("baadcf02ba44415b8715d2bf05f49c39");
		orderVo.setOrderSource(1);
		orderVo.setOrderType(0);
		orderVo.setServiceTerm(1);
		orderVo.setUom(0);//年
		orderVo.setCouponCode("TXB777W8BCOW");
		orderVo.setConfigId("52230ce24ef84e71beae85ce746e4b3b");

//		OrderLineVo orderLineVo = new OrderLineVo();
//		orderLineVo.setProductPackSku("2001");
//		orderLineVo.setQty(1);
//		orderVo.getOrderLines().add(orderLineVo);

		OrderLineVo acl = new OrderLineVo();
		acl.setProductSku("102");
		acl.setQty(10);
		orderVo.getOrderLines().add(acl);

		OrderLineVo productLine = new OrderLineVo();
		productLine.setProductSku("100");
		productLine.setQty(10);
		orderVo.getOrderLines().add(productLine);

		OrderLineVo storage = new OrderLineVo();
		storage.setProductSku("101");
		storage.setQty(20);
		orderVo.getOrderLines().add(storage);
		OrderResponse response = orderService.newOrder(orderVo);

		Assert.assertTrue(response.getOrderVo() != null);

		System.in.read();
	}

	@Test
	public void testCalcOrderPrice(){
		OrderVo orderVo = new OrderVo();
//		{"payPrice":"0.17","orderLines":[{"productPackSku":"2001","qty":1}],"orderType":0}
		orderVo.setCreatedBy("d5d577053378469cb477e9dc4e2f1740");
		orderVo.setBuyerUserId("d5d577053378469cb477e9dc4e2f1740");
		orderVo.setBuyerUserName("userName");
		orderVo.setClientIp("127.0.0.1");
		orderVo.setTenantId("4b399151c9934d3998a93598b43cfa29");
		orderVo.setOrderSource(1);
		orderVo.setOrderType(0);
		orderVo.setPayPrice("0.17");
		orderVo.setServiceTerm(1);
		orderVo.setUom(0);//年
		orderVo.setCouponCode("TL9W17U50LT6");

//		OrderLineVo orderLineVo = new OrderLineVo();
//		orderLineVo.setProductPackSku("2001");
//		orderLineVo.setQty(1);
//		orderVo.getOrderLines().add(orderLineVo);

		OrderLineVo productLine = new OrderLineVo();
		productLine.setProductSku("100");
		productLine.setQty(9);
		orderVo.getOrderLines().add(productLine);

		OrderLineVo acl = new OrderLineVo();
		acl.setProductSku("102");
		acl.setQty(150);
		orderVo.getOrderLines().add(acl);

		OrderLineVo storage = new OrderLineVo();
		storage.setProductSku("101");
		storage.setQty(9);
		orderVo.getOrderLines().add(storage);
		OrderPricingResponse response = orderService.calcOrderPrice(orderVo);

		Assert.assertTrue(response != null);
	}

	@Test
	public void testNewOrderPrice(){
		OrderVo orderVo = new OrderVo();
//		{"payPrice":"0.17","orderLines":[{"productPackSku":"2001","qty":1}],"orderType":0}
		orderVo.setCreatedBy("15899023e41b490ca6cf012aaeeb1eb2");
		orderVo.setBuyerUserId("15899023e41b490ca6cf012aaeeb1eb2");
		orderVo.setBuyerUserName("userName");
		orderVo.setClientIp("127.0.0.1");
		orderVo.setTenantId("b394fe91f4a241b996e24a4d0cdca8ce");
		orderVo.setOrderSource(1);
		orderVo.setOrderType(1);
		orderVo.setPayPrice("299.32");

		OrderLineVo orderLineVo = new OrderLineVo();
//		orderLineVo.setProductPackSku("101");
		orderLineVo.setProductSku("101");
		orderLineVo.setQty(1);
		orderVo.getOrderLines().add(orderLineVo);

//		OrderLineVo productLine = new OrderLineVo();
//		productLine.setProductSku("101");
//		productLine.setQty(1);
//		orderVo.getOrderLines().add(productLine);
		OrderResponse response = orderService.newOrder(orderVo);

		Assert.assertTrue(response.getOrderVo() != null);
	}

	@Test
	public void testCancelOrder(){
		orderService.cancelOrder(orderId);
	}

	@Test
	public void testCancelOrderJob(){
		String orderId = "18576809f6584fafa817b090597690b2";
		OrderCancelScheduleJob job = new OrderCancelScheduleJob();
		job.setOrderId(orderId);

		job.run();
	}

	@Test
	public void testGetOrderById(){
		OrderVo vo = orderService.getOrderById(orderId);
		Assert.assertNotNull(vo);
	}

	@Test
	public void testSearchOrder(){

		BasePageEntity page = new BasePageEntity();
		page.setPageSize(10);
		page.setCurrentPage(1);
		PageInfo<OrderVo> orders = orderService.queryOrders(page, "c7cebdf9018c41fa9db8abbe4fef5b09", null, null, null, null);
		Assert.assertNotNull(orders);
	}

	@Test
	public void testPayNotifyHandler(){
		String orderId = "24d81b85c792423596d677f35e2d92ab";
		PayNotify notify = new PayNotify();
		notify.setOrderId(orderId);
		notify.setNonceStr(CryptoUtil.genRandomStr());
		notify.setTenantId("3gfjgjghj");
		notify.setTradeNo("55667788");
		notify.setTimestamp(Long.toString(PayUtil.nowTime().getTime()));

		String content = PayUtil.getSignContent(notify);
		String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCJ2W2XGwc+PV5IaUbY35W4l3QrlVcOdV86QpioEfzk/wzg+7RGz1ETJoNgHqoHdplFGNCCBVsAP974vg+L/LyoFZhYUBeBy2CeVVxWHNu4zwdGGA78SrgvvcFDKzE6GORK+uxVhvbjznpPhpftWsPjc2LWdY0h1FSI1zLEoND6ksANNcqB8OVx8FpILwWCoWSMc6jid+mZoZLKkx3iTd9tqolTI+6ZC4yj9sYjdQytXWByR1fA8h8KxZBhugSVTLPdpQx2b+7/5/YxSdcFIA9q4CPpYNVcNSmnp/uHoJqMeJi2m0q81yz90AVNhHzjrHUPbwbrceLe+gsf23qxrGwfAgMBAAECggEAX/b1zBQM3s9qK47eu2wrlu4Y+Z5llLMxMCXckGS1YdYM6TNRNVpuXp6ZJvkx9TAjU28K+PkxeOL+Hrxxpkq0K2+dNx3Kv5kOEivqtCgy7l1x+uIUzJ5XyySWvmP7SS66/1ff30qt5bmAfIx+h8aASH+zdwkuzzY6A2mM9kXeNhGcyWKeoSgX6cx18mHQDktL7I46LMAIa8yhHB6AJ4pQrDu3unGiLk+Nb8hY5GzNDP9STi+cSvyQ0Z3F0HWYGMqyHIiCVIINeDWfqXAppja9BExvnrIU7otQ1KDDJtTCu9LOBJPH2OGWz5D4fIQFXWZoIuoVJVRMOmcR0uRWxenMwQKBgQDO8LmAoMLuIe9YEhflnACKtAJE6XJeL5qPkaxJ0dS+N2PCweAkkeIEXU4TJ+a0jOghSSUKmZE6Vs7TLVQnk4N1P9DU8yLxEPXf3GYIHr/3H7HjNubZQJITS68BQTqmyKkyYtlagoCZFolE7WqQmsIa9G3MdYWP/hM3cr5LR0LdYwKBgQCqh4xb0Fp9VuRqfn9Aq0RHMfBXvw7aN5bO7DvpzSvUjOMOQ9MAv2AkCxqq67ko5NJCipOWINHm0qzyAO+2l2JjbfaEpfmfYps6eOQ9YUdA2yFkgDEQtVWc7466OJ4wXH2DMa1PRqEvNjEdZhdPE6gFNrBLNRiHw8Ab75q6vMehFQKBgHumHME9zZuqTS/rh7nbb/twuZZhgsPCX/D/Etn89cg+om5Bl+NiA5GmCrupcNFbDGyvHuHphnw8WFKYWBtZqgfJ/MbxDGTMRI3hTFn3va40kv731BHl4iKDa2sthZUYevivVb4eEibm71hvH+iNgAPJHz42znqOTv8noYqrZsmPAoGBAKY9yA4FmwGPgNN0h/dyPvis7WGNJxyCjqkgUPQHCNCHnB3e7sdOyZm5nYC8leAQ2gz2bRQw4I+GGAU/9nBpOgg2PdlouGWxyiQBkHdp6v2F4hLIiYyBSnnYa6qM4WHB+LW56vtanm2ZBd5kFRsIzYZvnwNiOoy3vwM83PloabBVAoGBAKW0xiF3FtTNPVhoD2D4bl73sJPKeWM5kpDQ/+pc3yybO6nM9T3NBppaV+/fl1rUQZtIRu0cuGO+OHsEPegj0RjYH93eycWa1S35bb7A7P94X5Ym0Q2BH+LKeRDUEN29yrfOkS2E2WY+4nUCSCIjRaXDBFO5xNkhoyi5ttnsnKaU";
		String sign = "";
		try{
			sign = CryptoUtil.rsaSign(content, privateKey, "utf-8");
		} catch (Exception ex){}

		paymentHandler.process(notify, sign);
	}

	@Test
	public void testTimerService() throws IOException {
		SomeTestRunnablexx7 a = new SomeTestRunnablexx7();
		a.setMeetingId("xxxx");

		TenantContext.setTenantId("567");
		String taskId = timerService.schedule(1, 0, "780bbc21764e614850315ecb49d74b5c", a, 10, TimeUnit.SECONDS);

		System.out.println("taskId: " + taskId);
		System.in.read();
	}

	@Test
	public void  testCancelTask(){
		timerService.cancelTask("780bbc21764e614850315ecb49d74b5c");
	}

	@Test
	public void testSendMail(){
		MailVo mailVo = new MailVo();
		Map<String, String> recipientsMaps = new HashMap<>();
		recipientsMaps.put("yangyiqian@thinkwin.com.cn", "yang");
		recipientsMaps.put("abc$392347kshfsdf@thinkwin.com.cn", "unknown");
		recipientsMaps.put("zhangyongwei@thinkwin.com.cn", "zhang");
		Map<String, String> templateParamMaps = new HashMap<>();
		templateParamMaps.put("orderNumber", "123456");
		templateParamMaps.put("productName", "测试邮件");

		mailVo.setRecipientsMap(recipientsMaps);
		mailVo.setTemplateName("order.payment.reminder.ftl");
		mailVo.setTemplateParamMap(templateParamMaps);
		mailVo.setSubject("===测试邮件===");
		sendMailService.sendMail(mailVo);

	}

}
