package com.thinkwin.orders.service.impl;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.dto.order.*;
import com.thinkwin.common.dto.promotion.*;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.promotion.Coupon;
import com.thinkwin.common.model.promotion.PricingConfig;
import com.thinkwin.common.timer.ServiceType;
import com.thinkwin.common.timer.TaskType;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.TimeUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.ProductSkuDto;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.goodscenter.dataview.ProductPackSkuView;
import com.thinkwin.goodscenter.dataview.ProductSkuView;
import com.thinkwin.goodscenter.service.ProductOrderService;
import com.thinkwin.goodscenter.service.ProductPackSkuService;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.orders.schedule.OrderCancelScheduleJob;
import com.thinkwin.orders.dto.AdminOrderQuery;
import com.thinkwin.orders.dto.OrderResponse;
import com.thinkwin.orders.mapper.OrderLineMapper;
import com.thinkwin.orders.mapper.OrderMapper;
import com.thinkwin.orders.mapper.TenantOrderInfoMapper;
import com.thinkwin.orders.model.Order;
import com.thinkwin.orders.model.OrderStatus;
import com.thinkwin.orders.model.OrderType;
import com.thinkwin.orders.model.TenantOrderInfo;
import com.thinkwin.orders.schedule.OrderPayRemindJob;
import com.thinkwin.orders.service.OrderRepo;
import com.thinkwin.orders.service.OrderService;
import com.thinkwin.orders.util.OrderUtil;
import com.thinkwin.orders.util.OrderVoUtil;
import com.thinkwin.orders.vo.OrderLineVo;
import com.thinkwin.orders.vo.OrderVo;
import com.thinkwin.orders.dto.OrderCancelRequest;
import com.thinkwin.pay.dto.PayNotify;
import com.thinkwin.pay.dto.PaymentVo;
import com.thinkwin.pay.model.PayChannelEnum;
import com.thinkwin.pay.model.PayStatus;
import com.thinkwin.pay.service.PayService;
import com.thinkwin.pay.service.PaymentHandler;
import com.thinkwin.pay.util.CryptoUtil;
import com.thinkwin.pay.util.PayUtil;
import com.thinkwin.promotion.service.CouponBatchService;
import com.thinkwin.promotion.service.CouponService;
import com.thinkwin.promotion.service.PricingConfigService;
import com.thinkwin.schedule.service.TimerService;
import com.thinkwin.serialnumber.service.SerialNumberService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.thinkwin.orders.constants.SKU.ACLSKU;

@Service(value = "orderService")
public class OrderServiceImpl implements OrderService {
	private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Value("${order.expire_in_hours}")
	private Integer orderExpireInHours = 24;

	@Value("${order.expire_in_minutes}")
	private Integer orderExpireInMinutes = 0;

	@Value("${order.expire_in_minutes}")
	private Integer orderExpireInDays = 7;

	@Autowired
	private PaymentHandler paymentHandler;

	private static BigDecimal fullYear = new BigDecimal(365);

	private static String ADMIN_CANCEL_ORDER_NONCESTR_CACHE_PREFIX = "ADMIN_CANCEL_ORDER_NONCESTR:";

	private static String ADMIN_GET_ORDER_NONCESTR_CACHE_PREFIX = "ADMIN_GET_ORDER_NONCESTR:";

	private static int CACHE_TTL = 10; // in seconds

	private List<Integer> WAIT_BUYER_PAY_STATUS = Arrays.asList(OrderStatus.WEIZHIFU.getCode(), OrderStatus.DAIHUIKUAN.getCode());

	private static List<Integer> STATUS_CAN_INVOICE = Arrays.asList(OrderStatus.YIFUKUAN.getCode(), OrderStatus.KAIPIAOZHONG.getCode());
	private static List<Integer> INVOICE_STATUS = Arrays.asList(OrderStatus.KAIPIAOZHONG.getCode(), OrderStatus.YIKAIPIAO.getCode());

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private OrderLineMapper orderLineMapper;

	@Autowired
	private TenantOrderInfoMapper tenantOrderInfoMapper;

	@Autowired
	private OrderRepo orderRepo;

	@Autowired
	private ProductOrderService productOrderService;

	@Autowired
	private TimerService timerService;

	@Autowired
	private SaasTenantService saasTenantService;

	@Autowired
	private SerialNumberService serialNumberService;

	@Autowired
	private SysLogService logService;

	@Autowired
	private PayService payService;

	@Autowired
	PricingConfigService pricingConfigService;

	@Autowired
	CouponService couponService;

	@Autowired
	CouponBatchService couponBatchService;

	@Autowired
	ProductPackSkuService productPackSkuService;

	@Resource
	private Map<String, String> trustStoreTradeSuccess;

	@Resource
	private Map<String, String> trustStoreSearchOrder;

	@Resource
	private Map<String, String> keyStoreTradeSuccess;

	protected static final ExecutorService executor = Executors.newWorkStealingPool();

	@Override
	public OrderResponse newOrder(OrderVo orderVo) {
		OrderResponse response = new OrderResponse();

		if(StringUtils.isBlank(orderVo.getConfigId())){
			logger.error("订单: 定价配置版本为空, 请求参数: {}", orderVo);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.add_orders.toString(), "创建订单失败", "定价配置版本为空", Loglevel.error.toString());
			response.setErrorMsg("定价配置版本为空");
			return response;
		}

		PricingConfig config = pricingConfigService.getPricingConfig(orderVo.getConfigId());
		if(null == config || null == config.getStatus() || !config.getStatus().equals(1)){
			logger.error("订单: 定价配置版本不匹配, 请求参数: {}", orderVo);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.add_orders.toString(), "创建订单失败", "定价配置版本不匹配", Loglevel.error.toString());
			response.setErrorMsg("定价配置版本不匹配");
			return response;
		}

		orderVo.setOrderId(CreateUUIdUtil.Uuid());
		try{
			String orderSN = serialNumberService.generateSerialNumber("order", null).toString();
			orderVo.setOrderSn(orderSN);
		}catch (Exception e){
			logger.error("订单: 无法生成新订单序列号, 请求参数: {}", orderVo);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.add_orders.toString(), "创建订单失败", "无法生成新订单序列号", Loglevel.error.toString());
			response.setErrorMsg("无法生成新订单序列号");
			return response;
		}

		prePareNewOrder(orderVo, response);

		if(null == response.getOrderVo()){
			if(StringUtils.isNotBlank(response.getTimerTaskId())){
				timerService.cancelTask(response.getTimerTaskId());
			}
			return response;
		}

		if(StringUtils.isNotBlank(orderVo.getCouponCode())){
			if(orderVo.isCouponValid()){
				Coupon coupon = couponService.getCoupon(orderVo.getCouponCode());
				if(null == coupon){
					response.setOrderVo(null);
					response.setErrorMsg("优惠券不可用.");
					return response;
				}
				boolean success = couponService.consumeCoupon(coupon.getId(), orderVo.getOrderId());
				if(!success){
					response.setOrderVo(null);
					response.setErrorMsg("优惠券不可用.");
					return response;
				}
			} else {
				response.setOrderVo(null);
				response.setErrorMsg(response.getCouponErrMsg());
				return response;
			}
		} else if(StringUtils.isBlank(orderVo.getPayPrice()) || Double.parseDouble(orderVo.getPayPrice()) < 0.01){
			response.setOrderVo(null);
			response.setErrorMsg("订单价格为0.");
			return response;
		}

		if(!orderVo.isCouponValid()){
			orderVo.setCouponCode("");
		}

		response.setOrderVo(orderVo);
		Order order = orderRepo.insertOrder(response);

		if(null == order){
			response.setOrderVo(null);
			if(StringUtils.isNotBlank(orderVo.getCouponCode()) && orderVo.isCouponValid()){
				Coupon coupon = couponService.getCoupon(orderVo.getCouponCode());
				executor.execute(()-> {
					couponService.returnCoupon(coupon.getId(), orderVo.getOrderId());
				});
			}
			return response;
		}

		executor.execute(()-> {
			this.afterNewOrder(order.getOrderId(), order.getPayPrice(), order.getTenantId(), response.getTimerTaskId());
		});

		OrderVo newOrder = OrderVoUtil.fromOrder(order);
		response.setOrderVo(newOrder);

		logger.info("订单: 新订单创建成功, 订单subject: {}, 租户名称: {}, 订单ID: {}", newOrder.getOrderSubject(), newOrder.getTenantName(), newOrder.getOrderId());
		logService.createLog(BusinessType.ordersOp.toString(), EventType.add_orders.toString(), order.getOrderSn()+"创建成功", "", Loglevel.info.toString());

		try{
			OrderPayRemindJob job = new OrderPayRemindJob();
			job.setOrderId(order.getOrderId());
			timerService.schedule(job, DateUtils.addHours(new Date(), 1));
		} catch (Exception e){
			logger.error("订单: 创建订单支付提醒定时任务失败， 订单信息: {}, 异常信息: {}", order.toString(), e);
		}
		return response;
	}

	private void prePareNewOrder(OrderVo orderVo, OrderResponse response){
		if(!OrderVoUtil.isValidOrder(orderVo)){
			logger.error("订单: 参数格式错误, 请求参数: {}", orderVo);
			logService.createLog(BusinessType.ordersOp.toString(), EventType.add_orders.toString(), "创建订单失败", "参数格式错误", Loglevel.error.toString());
			response.setErrorMsg("参数格式错误!");
			return;
		}

		TenantOrderInfo orderInfo = tenantOrderInfoMapper.selectByPrimaryKey(orderVo.getTenantId());
		if(null == orderInfo){
			orderInfo = new TenantOrderInfo();
			orderInfo.setTenantId(orderVo.getTenantId());
			orderInfo.setCurrentOrder("");
			orderInfo.setCreateTime(new Date());
			tenantOrderInfoMapper.insert(orderInfo);
		} else if(StringUtils.isNotBlank(orderInfo.getCurrentOrder())){
			logService.createLog(BusinessType.ordersOp.toString(), EventType.add_orders.toString(), "创建订单失败", "有未完成订单", Loglevel.error.toString());
			response.setErrorMsg("有未完成订单!");
			return;
		}

		SaasTenant saasTenant = null;

		try{
			saasTenant = saasTenantService.selectByIdSaasTenantInfo(orderVo.getTenantId());
		} catch (Exception e){
		}

		if(null == saasTenant){
			logger.error("订单: 创建订单, 租户服务不可用, 租户ID: {}", orderVo.getTenantId());
			response.setErrorMsg("租户服务不可用!");
			return;
		}

		if("0".equals(saasTenant.getTenantType()) && OrderType.EXPANDING.getCode().equals(orderVo.getOrderType())){
			logger.error("订单: 创建订单, 免费账号无法增容, 租户ID: {}", orderVo.getTenantId());
			response.setErrorMsg("免费账号无法增容!");
			return;
		}


		orderVo.setTenantName(saasTenant.getTenantName());

		// 订单超时自动取消
		OrderCancelScheduleJob job = new OrderCancelScheduleJob();
		job.setOrderId(orderVo.getOrderId());
		job.setUserId(orderVo.getCreatedBy());

		String timerTaskId = "";
		try{
			long expire = orderExpireInMinutes + orderExpireInHours * 60;
			timerTaskId = timerService.schedule(ServiceType.ORDER.getCode()
					, TaskType.ORDER_EXPIRE.getCode()
					, orderVo.getOrderId(), job, expire, TimeUnit.MINUTES);

		} catch (Exception e){
			logger.error("订单: 创建定时任务失败, 异常信息: {}", e);
		}

		if(StringUtils.isBlank(timerTaskId)){
			logService.createLog(BusinessType.ordersOp.toString(), EventType.add_orders.toString(), "创建订单失败", "定时服务不可用", Loglevel.error.toString());
			logger.error("订单: 创建订单, 定时服务不可用, 租户ID: {}", orderVo.getTenantId());
			response.setErrorMsg("定时服务不可用!");
			return;
		}

		response.setTimerTaskId(timerTaskId);

		boolean payServiceAvailable = true;
		try{
			payService.getSimplePaymentById("0");
		} catch (Exception e){
			payServiceAvailable = false;
		}

		if(!payServiceAvailable){
			logService.createLog(BusinessType.ordersOp.toString(), EventType.add_orders.toString(), "创建订单失败", "支付服务不可用", Loglevel.error.toString());
			logger.error("订单: 创建订单, 支付服务不可用, 租户ID: {}", orderVo.getTenantId());
			response.setErrorMsg("支付服务不可用!");
			return;
		}

		String orderSubject = "";

		boolean freeVersion = false;

		if("0".equals(saasTenant.getTenantType())){
			freeVersion = true;
		}

		// 设置授权有效期
		orderVo.setRentStart(new Date());

		if(freeVersion || OrderType.RENEWAL.getCode().equals(orderVo.getOrderType())){
			if(freeVersion){
				orderSubject = "专业版";
			} else{
				orderSubject = "专业版续费";
			}
		}
		else{
			orderSubject = "专业版增容";
		}

		// 计算订单价格
		OrderPricingResponse pricingResponse = this.calcOrderPrice(orderVo);
		orderVo.setRentEnd(new Date(pricingResponse.getExpireTime()));

		if(StringUtils.isNotBlank(pricingResponse.getMsg())){
			response.setCouponErrMsg(pricingResponse.getMsg());
		}

		orderVo.setCouponValid(pricingResponse.isCouponValid());
		BigDecimal orderPrice = new BigDecimal(pricingResponse.getPayPrice()).setScale(6, RoundingMode.HALF_EVEN);;

		// 升级授权，只有免费版更新授权有效期
		if(orderVo.getOrderType().equals(OrderType.UPGRADING.getCode())){
			List<String> skus = productOrderService.getFutureProductPack(orderVo.getTenantId());

			if(CollectionUtils.sizeIsEmpty(skus)){
				logService.createLog(BusinessType.ordersOp.toString(), EventType.add_orders.toString(), "创建订单失败", "授权不可升级", Loglevel.error.toString());
				logger.error("订单: 创建订单, 授权不可升级, 租户ID: {}", orderVo.getTenantId());
				response.setErrorMsg("授权不可升级!");
				return;
			}


			orderPrice = orderPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		}

		// 扩容
		if(orderVo.getOrderType().equals(OrderType.EXPANDING.getCode())){
			//免费版不支持扩容
			if(freeVersion){
				logger.error("订单: 创建订单, 免费版不支持扩容, 租户ID: {}", orderVo.getTenantId());
				response.setErrorMsg("免费版不支持扩容！");
				return;
			}

			if(null == saasTenant.getBasePackageExpir()){
				logger.error("订单: 创建增容订单, 服务有效期为空, 租户ID: {}", orderVo.getTenantId());
				response.setErrorMsg("服务有效期为空！");
				return;
			}

			if(saasTenant.getBasePackageExpir().before(new Date())){
				logger.error("订单: 创建订单, 服务已到期, 租户ID: {}", orderVo.getTenantId());
				response.setErrorMsg("服务已到期！");
				return;
			}

			orderPrice = orderPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);

			orderSubject = "专业版增容";
		}

		// 续费，不能修改授权，费用就是所选SKU的价格
		if(orderVo.getOrderType().equals(OrderType.RENEWAL.getCode())){
			if(saasTenant.getBasePackageExpir() == null){
				String logTxt = String.format("租户授权截止日志异常, 租户ID: %s", saasTenant.getId());
				logger.error("订单: 创建订单, 租户授权截止日志异常, 租户ID: {}", orderVo.getTenantId());
				logService.createLog(BusinessType.ordersOp.toString(), EventType.add_orders.toString(), "创建订单失败", logTxt, Loglevel.error.toString());
				response.setErrorMsg(logTxt);
				return;
			}

			orderPrice = orderPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);

		}

//		if(orderPrice.compareTo(payPrice) != 0){
//			if(orderPrice.subtract(payPrice).abs().doubleValue() > 0.01D){
//				logService.createLog(BusinessType.ordersOp.toString(), EventType.add_orders.toString(), "创建订单失败", "价格校验失败", Loglevel.error.toString());
//				logger.error("订单: 创建订单, 价格校验失败, 传入的价格: {}, 计算的价格:{}, 租户ID: {}", orderPrice.toPlainString(), payPrice.toPlainString(), orderVo.getTenantId());
//				response.setErrorMsg("价格校验失败!");
//				return response;
//			}
//		}

		orderVo.setOrderSubject(orderSubject);
		orderVo.setPayPrice(String.format("%.2f", orderPrice.doubleValue()));

		if(pricingResponse.getCouponInfo() != null && CollectionUtils.isNotEmpty(pricingResponse.getCouponInfo().getProducts())){
			Map<String, CouponProduct> productMap = new HashMap<>();
			for(CouponProduct product : pricingResponse.getCouponInfo().getProducts()){
				productMap.put(product.getSku(), product);
			}

			for(OrderLineVo orderLineVo : orderVo.getOrderLines()){
				if(productMap.containsKey(orderLineVo.getProductSku())){
					Integer qty = productMap.get(orderLineVo.getProductSku()).getQty();
					orderLineVo.setQty(orderLineVo.getQty() + qty);
				}
			}
		}
		response.setOrderVo(orderVo);
		return;
	}

	private void afterNewOrder(String orderId, Double payPrice, String tenantId, String timerTaskId){
		String tradeNo = "";
		// 预先创建支付信息，减少支付时延迟
		try{
			PaymentVo paymentVo = new PaymentVo();
			paymentVo.setTenantId(tenantId);
			paymentVo.setOrderId(orderId);
			if(payPrice < 0.01){
				paymentVo.setTotalAmount(0D);
				paymentVo.setStatus(PayStatus.TRADE_SUCCESS.getValue());
			}
			paymentVo.setTimerTaskId(timerTaskId);
			paymentVo.setPayChannel(PayChannelEnum.HUIKUAN.getCode());
			tradeNo = payService.preCreatePayment(paymentVo);
		} catch (Exception e){
			logger.error("订单: 创建支付信息失败, 订单ID: {}, 租户ID: {}, 异常信息: {}", orderId, tenantId, e);
		}

		if(payPrice < 0.01){
			PayNotify notify = new PayNotify();
			notify.setOrderId(orderId);
			notify.setAppId("zeroPrice");
			notify.setNonceStr(CryptoUtil.genRandomStr());
			notify.setTenantId(tenantId);
			notify.setTradeNo(tradeNo);
			notify.setPayChannel(PayChannelEnum.HUIKUAN.getCode().toString());
			notify.setTimestamp(Long.toString(PayUtil.nowTime().getTime()));

			String content = PayUtil.getSignContent(notify);
			String privateKey = keyStoreTradeSuccess.get("zeroPrice");
			String sign = "";
			try{
				sign = CryptoUtil.rsaSign(content, privateKey, "utf-8");
			} catch (Exception ex){
				logger.error("订单: 支付结果通知订单系统时异常, 签名校验失败, tradeNo: {}", tradeNo);
			}

			paymentHandler.process(notify, sign);
		}

	}

	@Override
	public PageInfo<OrderVo> queryOrders(BasePageEntity page, String tenantId, List<Integer> status, List<Integer> orderTypes, Date startTime, Date endTime) {
		Example example = new Example(Order.class);
		example.setOrderByClause("create_time desc");
		Example.Criteria criteria = example.createCriteria();

		if(StringUtils.isBlank(tenantId)){
			tenantId = TenantContext.getTenantId();
		}

		if(StringUtils.isBlank(tenantId)){
			return null;
		}

		criteria.andEqualTo("tenantId", tenantId);

		if(CollectionUtils.isNotEmpty(status)){
			criteria.andIn("status", status);
		}

		if(CollectionUtils.isNotEmpty(orderTypes)){
			criteria.andIn("orderType", orderTypes);
		}

		if(startTime != null){
			criteria.andGreaterThanOrEqualTo("createTime", startTime);
		}

		if(endTime != null){
			criteria.andLessThanOrEqualTo("createTime", endTime);
		}

		return orderRepo.queryOrders(page, example);
	}

	@Override
	public PageInfo<OrderVo> queryOrders(AdminOrderQuery query) {
		String content = OrderUtil.getSignContent(query);

		if(StringUtils.isBlank(content)){
			return null;
		}

		if(!trustStoreSearchOrder.containsKey(query.getAppId())){
			return null;
		}

		String key = trustStoreSearchOrder.get(query.getAppId());

		boolean signValid = false;
		try{
			signValid = CryptoUtil.rsaCheckContent(content, query.getSign(), key, "utf-8");
		}
		catch (Exception e){
			logger.error("console查询订单验证签名异常:", e);
		}

		if(!signValid){
			return null;
		}

		String cacheKey = ADMIN_GET_ORDER_NONCESTR_CACHE_PREFIX + query.getNonceStr();

		String value = RedisUtil.get(cacheKey);

		if(StringUtils.isNotBlank(value)){
			logger.error("console查询订单异常: 重复的请求");
			return null;
		}

		if(PayUtil.nowTime().getTime() < Long.parseLong(query.getTimeStamp())){
			logger.error("console查询订单异常: 无效的时间戳");
			return null;
		}

		if(PayUtil.nowTime().getTime() - Long.parseLong(query.getTimeStamp()) > TimeUnit.SECONDS.toMillis(CACHE_TTL)){
			logger.error("console查询订单异常: 请求已过期");
			return null;
		}

		RedisUtil.set(cacheKey, "1", CACHE_TTL);

		List<Integer> status = query.getStatus();
		List<Integer> orderTypes = query.getOrderTypes();
		Date startTime = query.getStartTime();
		Date endTime = query.getEndTime();
		BasePageEntity page = query.getPage();

		Example example = new Example(Order.class);
		example.setOrderByClause("create_time desc");
		Example.Criteria criteria = example.createCriteria();
		if(CollectionUtils.isNotEmpty(status)){
			criteria.andIn("status", status);
		}

		if(CollectionUtils.isNotEmpty(orderTypes)){
			criteria.andIn("orderType", orderTypes);
		}

		if(startTime != null){
			criteria.andGreaterThanOrEqualTo("createTime", startTime);
		}

		if(endTime != null){
			criteria.andLessThanOrEqualTo("createTime", endTime);
		}

		return orderRepo.queryOrders(page, example);
	}

	@Override
	public OrderVo getOrderById(String orderId) {
		return orderRepo.getOrderById(orderId);
	}

	@Override
	public OrderVo getPayStatus(String orderId) {
		if(StringUtils.isBlank(orderId)){
			return null;
		}

		Example example = new Example(Order.class);
		example.selectProperties("status");
		example.selectProperties("orderType");
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("orderId", orderId);

		List<Order> orders = orderMapper.selectByExample(example);

		if(CollectionUtils.isEmpty(orders)){
			return null;
		}

		OrderVo vo = OrderVoUtil.fromOrder(orders.get(0));
		return vo;
	}

	@Override
	public boolean cancelOrder(String orderId) {
		String tenantId = TenantContext.getTenantId();

		if(StringUtils.isBlank(tenantId)){
			return false;
		}

		if(StringUtils.isBlank(orderId)){
			return false;
		}

		Order order = orderMapper.selectByPrimaryKey(orderId);
		if(null == order){
			return false;
		}

		if(!tenantId.equals(order.getTenantId())){
			return false;
		}

		boolean success = false;
		try{
			success = cancelOrderCommon(orderId);
		} catch (Exception e){
			logger.error("订单: 取消订单失败，异常信息: {}", e);
		}

		return success;
	}

	private boolean cancelOrderCommon(String orderId){
		Order order = orderMapper.selectOrderByIdLocked(orderId);

		if(null == order){
			return false;
		}

		if(WAIT_BUYER_PAY_STATUS.contains(order.getStatus())){
			if(StringUtils.isNotBlank(order.getCouponCode())){
				Coupon coupon = couponService.getCoupon(order.getCouponCode());
				if(null == coupon){
					logger.error("订单: 取消订单异常，订单优惠券信息不存在.");
					return false;
				}
				boolean success = couponService.returnCoupon(coupon.getId(), order.getOrderId());
				if(!success){
					logger.error("订单: 取消订单异常，返还优惠券失败.");
					return false;
				}

				logger.info("订单: 返还优惠券成功，订单ID: {}, 优惠券编码: {}", order.getOrderId(), order.getCouponCode());
			}

			boolean success = orderRepo.cancelOrder(order.getOrderId());

			if(!success){
				logService.createLog(BusinessType.ordersOp.toString(), EventType.cancel_order.toString(), order.getOrderSn()+"取消失败", "取消订单失败", Loglevel.error.toString());
				logger.error("订单: 取消订单失败，将订单状态修改为已取消状态失败, 租户名称: {}, 订单ID: {}", order.getTenantName(), orderId);
				return false;
			}

			TenantOrderInfo info = new TenantOrderInfo();
			info.setTenantId(order.getTenantId());
			info.setCurrentOrder("");
			info.setUpdateTime(new Date());
			int affected = tenantOrderInfoMapper.updateByPrimaryKeySelective(info);

			PaymentVo paymentVo = payService.getSimplePaymentById(orderId);
			if(null == paymentVo || StringUtils.isBlank(paymentVo.getTimerTaskId())){
				logger.error("订单: 订单数据异常，缺少支付及定时相关信息, 租户名称: {}, 订单ID: {}", order.getTenantName(), orderId);
				executor.execute(()->{
					logService.createLog(BusinessType.ordersOp.toString(), EventType.cancel_order.toString(), order.getOrderSn()+"取消失败", "订单数据异常，缺少支付及定时相关信息", Loglevel.error.toString());
				});
			} else {
				timerService.cancelTask(paymentVo.getTimerTaskId());
			}

			if(affected == 1){
				logger.info("订单: 取消订单成功, 租户名称: {}, 订单ID: {}", order.getTenantName(), orderId);
				executor.execute(()->{
					logService.createLog(BusinessType.ordersOp.toString(), EventType.cancel_order.toString(), order.getOrderSn()+"被取消", "", Loglevel.info.toString());
				});
				return true;
			}

			logger.error("订单: 取消订单失败, 租户名称: {}, 订单ID: {}", order.getTenantName(), orderId);
			return false;
		}

		return false;
	}

	@Override
	public boolean cancelOrder(OrderCancelRequest request) {
		String content = OrderUtil.getSignContent(request);

		if(StringUtils.isBlank(content)){
			logger.error("订单: 取消订单失败, 签名内容为空, 请求信息: {}", request);
			return false;
		}

		if(!trustStoreTradeSuccess.containsKey(request.getAppId())){
			logger.error("订单: 取消订单失败, appId异常, 请求信息: {}", request);
			return false;
		}

		String key = trustStoreTradeSuccess.get(request.getAppId());

		boolean signValid = false;
		try{
			signValid = CryptoUtil.rsaCheckContent(content, request.getSign(), key, "utf-8");
		}
		catch (Exception e){
			logger.error("订单: 取消订单失败, 验证签名异常, 请求信息: {}, 异常信息:{}", request, e);
		}

		if(!signValid){
			logger.error("订单: 取消订单失败, 签名不匹配, 请求信息: {}", request);
			return false;
		}

		String cacheKey = ADMIN_CANCEL_ORDER_NONCESTR_CACHE_PREFIX + request.getNonceStr();

		String value = RedisUtil.get(cacheKey);

		if(StringUtils.isNotBlank(value)){
			logger.error("订单: 取消订单失败, 重复请求, 请求信息: {}", request);
			return false;
		}

		if(PayUtil.nowTime().getTime() < Long.parseLong(request.getTimestamp())){
			logger.error("订单: 取消订单失败, 时间戳异常, 请求信息: {}", request);
			return false;
		}

		if(PayUtil.nowTime().getTime() - Long.parseLong(request.getTimestamp()) > TimeUnit.SECONDS.toMillis(CACHE_TTL)){
			logger.error("订单: 取消订单失败, 请求已过期, 请求信息: {}", request);
			return false;
		}

		RedisUtil.set(cacheKey, "1", CACHE_TTL);

		return cancelOrderCommon(request.getOrderId());
	}

	private void getTenantProductInfo(OrderVo orderVo, SaasTenant saasTenant){
		if(saasTenant != null) {
			List<ProductSkuDto> dtos = productPackSkuService.getAllSku();

			OrderLineVo lineVo = new OrderLineVo();
			orderVo.getOrderLines().add(lineVo);
			lineVo.setProductSku("102");
			lineVo.setQty((saasTenant.getExpectNumber()));

			lineVo = new OrderLineVo();
			orderVo.getOrderLines().add(lineVo);
			lineVo.setProductSku("100");
			lineVo.setQty((saasTenant.getBuyRoomNumTotal()));

			lineVo = new OrderLineVo();
			orderVo.getOrderLines().add(lineVo);
			lineVo.setProductSku("101");
			lineVo.setQty((saasTenant.getBuySpaceNumTotal()));
		}
	}

	@Override
	public OrderPricingResponse calcOrderPrice(OrderVo orderVo) {
		final String cheng = "×";

		Map<String, Integer> tenantSkuQty = new HashMap<>();

		SaasTenant saasTenant = saasTenantService.selectByIdSaasTenantInfo(orderVo.getTenantId());
		tenantSkuQty.put("102", saasTenant.getExpectNumber());
		tenantSkuQty.put("100", saasTenant.getBuyRoomNumTotal());
		tenantSkuQty.put("101", saasTenant.getBuySpaceNumTotal());

		OrderPricingResponse response = new OrderPricingResponse();
		if(OrderType.EXPANDING.getCode().equals(orderVo.getOrderType()) && "0".equals(saasTenant.getTenantType())){
			response.setMsg("免费帐号不支持增容");
		}

		List<ProductSkuDto> allProductSku = productPackSkuService.getAllSku();
		Map<String, ProductSkuDto> allProdutMap = new HashMap<>();
		for(ProductSkuDto dto: allProductSku){
			allProdutMap.put(dto.getSku(), dto);
		}

		orderVo.getOrderLines().sort((a,b)->{
			return Integer.compare(allProdutMap.get(a.getProductSku()).getSortOrder(), allProdutMap.get(b.getProductSku()).getSortOrder());
		});

		if(OrderType.RENEWAL.getCode().equals(orderVo.getOrderType())){
			orderVo.setOrderLines(new ArrayList<OrderLineVo>());
			getTenantProductInfo(orderVo, saasTenant);
		} else if(OrderType.EXPANDING.getCode().equals(orderVo.getOrderType())){
			orderVo.setCouponCode("");
		}

		List<String> productSkus = new ArrayList<String>();

		Map<String, OrderLineVo> skuMap = new HashMap<>();

		for(OrderLineVo lineVo : orderVo.getOrderLines()){
			//初始化
			lineVo.setGiveaway(0);
			lineVo.setFree(0);

			if(StringUtils.isNotBlank(lineVo.getProductSku())){
				productSkus.add(lineVo.getProductSku());
				skuMap.put(lineVo.getProductSku(), lineVo);
			}
		}

		Double totalPrice = 0D;
		Double unitPrice = 0D;

		Integer serviceTerm = orderVo.getServiceTerm();

		List<String> exp1 = new ArrayList<>();
		List<String> exp2 = new ArrayList<>();

		OrderDiscount discountInfo = new OrderDiscount();
		PricingConfigDto pricingDto = null;
		List<SkuDiscountInfo> skuDiscount = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(productSkus)){
			try{
				pricingDto = pricingConfigService.getPricingConfig();
			} catch (Exception e){
			}

			if(null == pricingDto){
				throw new RuntimeException("无法获取定价配置");
			}

			orderVo.setPricingConfigVersion(pricingDto.getId());

			Integer aclQty = 0;
			if(OrderType.UPGRADING.getCode().equals(orderVo.getOrderType())){
				aclQty = skuMap.get(ACLSKU).getQty();
			} else if(OrderType.EXPANDING.getCode().equals(orderVo.getOrderType())) {
				if(skuMap.containsKey(ACLSKU)){
					aclQty = tenantSkuQty.get(ACLSKU) + skuMap.get(ACLSKU).getQty();
				} else{
					aclQty = tenantSkuQty.get(ACLSKU);
				}
			} else if(OrderType.RENEWAL.getCode().equals(orderVo.getOrderType())){
				aclQty = tenantSkuQty.get(ACLSKU);
			} else {
				throw new RuntimeException("无法获取员工人数折扣");
			}

			RuleInfo rule = getDiscount(pricingDto, ACLSKU, aclQty);
			Double aclSkuDiscount = rule.getDiscount();
			Integer unit = rule.getUnit();

			if(aclSkuDiscount < 1){
				SkuDiscountInfo info = new SkuDiscountInfo();
				info.setSku("102");
				info.setName("员工人数");
				info.setUnit(unit);
				info.setUom("人");
				info.setDiscount(Double.valueOf(aclSkuDiscount * 100).intValue());
				skuDiscount.add(info);
			}

			List<ProductSkuView> productViews = productOrderService.getProductSkuByCode(productSkus);
			Map<String, ProductSkuView> productMap = new HashMap<>(5);
			for(ProductSkuView view : productViews){
				productMap.put(view.getSku(), view);
			}

//			orderVo.getOrderLines().sort((a, b) -> Integer.compare(skuMap.get(a.getProductSku()).getso));

			//计算订单价格
			for(OrderLineVo lineVo : orderVo.getOrderLines()){
				String sku = lineVo.getProductSku();
				ProductSkuView view = productMap.get(sku);

				Integer freeQty = getFreeQty(pricingDto, sku);
				if(freeQty < 0){
					freeQty = 0;
				}

				lineVo.setFree(freeQty);

				String skuUom = "";
				if(StringUtils.isNotBlank(view.getSkuUom())){
					String[] uomInfo = StringUtils.split(view.getSkuUom(), "|");
					if(uomInfo.length > 1){
						skuUom = uomInfo[1];
					}
				}

				lineVo.setUom(skuUom);
				Integer feeQty = lineVo.getQty() - freeQty;
				Integer visualQty = lineVo.getQty();

				if(OrderType.RENEWAL.getCode().equals(orderVo.getOrderType())){
					feeQty = tenantSkuQty.get(sku)- freeQty;
					visualQty = tenantSkuQty.get(sku);
				} else if(OrderType.EXPANDING.getCode().equals(orderVo.getOrderType())){
					feeQty = lineVo.getQty();
					visualQty = tenantSkuQty.get(sku) + lineVo.getQty();
				}

				if(feeQty <= 0){
					continue;
				}

				RuleInfo ruleInfo = null;
				if(ACLSKU.equals(sku)){
					ruleInfo = rule;
				} else {
					ruleInfo = getDiscount(pricingDto, sku, visualQty);
				}

				Double price = ruleInfo.getUnitPrice() * feeQty;

				Double skuUnitPrice = rule.getUnitPrice();
				Double itemDiscount = 1D;
				if(ACLSKU.equals(sku)){
					price = price * aclSkuDiscount;
					itemDiscount = aclSkuDiscount;
				} else {
					skuUnitPrice = ruleInfo.getUnitPrice();
					Double discount = ruleInfo.getDiscount();
					unit = ruleInfo.getUnit();

					if(discount < 0){
						discount = aclSkuDiscount;
					}

					if(discount < 1){
						SkuDiscountInfo info = new SkuDiscountInfo();
						info.setSku(sku);
						info.setName(view.getDisplayName());
						info.setUnit(unit);
						info.setUom(skuUom);
						info.setDiscount(Double.valueOf(discount * 100).intValue());
						skuDiscount.add(info);
					}

					itemDiscount = discount;
					price = price * discount;
				}

				String uom = StringUtils.split(view.getSkuUom(), "|")[1];
				exp1.add(OrderVoUtil.formatAmount(skuUnitPrice) + "元/年/" + uom + cheng + feeQty + uom + (itemDiscount < 1 ? cheng + itemDiscount : ""));

				totalPrice += price;
				unitPrice += price;
			}
		}

		if(CollectionUtils.isNotEmpty(skuDiscount)){
			discountInfo.setSkuDiscount(skuDiscount);
		}

		String unitExp = OrderVoUtil.formatAmount(unitPrice) + "元/年";

		final Date nowTime = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

		//升级或续费订单的购买时长开始时间
		final Date startTime = OrderType.RENEWAL.getCode().equals(orderVo.getOrderType()) ? DateUtils.truncate(saasTenant.getBasePackageExpir(), Calendar.DAY_OF_MONTH) : nowTime;

		String timeExp = "";
		int years = 0;
		long daysLeft = 0;
		if(OrderType.RENEWAL.getCode().equals(orderVo.getOrderType()) || OrderType.UPGRADING.getCode().equals(orderVo.getOrderType())){
			if(orderVo.getUom().equals(0)){
				timeExp = orderVo.getServiceTerm() + "年";
				response.setExpireTime(DateUtils.addYears(startTime, orderVo.getServiceTerm()).getTime());
				daysLeft = TimeUtil.getDateDiff(startTime, DateUtils.addYears(startTime, orderVo.getServiceTerm()), TimeUnit.DAYS);
			} else{
				timeExp = "(" + TimeUtil.getDateDiff(startTime, DateUtils.addMonths(startTime, orderVo.getServiceTerm()), TimeUnit.DAYS) + "/365)年";
				response.setExpireTime(DateUtils.addMonths(startTime, orderVo.getServiceTerm()).getTime());
				daysLeft = TimeUtil.getDateDiff(startTime, DateUtils.addMonths(startTime, orderVo.getServiceTerm()), TimeUnit.DAYS);
			}
		} else if(OrderType.EXPANDING.getCode().equals(orderVo.getOrderType())) {
			response.setExpireTime(DateUtils.truncate(saasTenant.getBasePackageExpir(), Calendar.DAY_OF_MONTH).getTime());

			Date expireDate = DateUtils.truncate(saasTenant.getBasePackageExpir(), Calendar.DAY_OF_MONTH);
			Date afterYears = nowTime;
			while(true){
				if(afterYears.before(expireDate)){
					if(DateUtils.addYears(afterYears, 1).equals(expireDate) || DateUtils.addYears(afterYears, 1).before(expireDate)){
						years++;
						afterYears = DateUtils.addYears(afterYears, 1);
					} else {
						break;
					}
				} else {
					break;
				}
			}
			daysLeft = TimeUtil.getDateDiff(afterYears, expireDate, TimeUnit.DAYS);

			if(years > 0 && daysLeft > 0){
				timeExp = "(" + years + "+" + daysLeft + "/365)年";
			} else if(years > 0){
				timeExp = years + "年";
			} else {
				timeExp = "(" + daysLeft + "/365)年";
			}

			orderVo.setServiceTerm(years);
			orderVo.setUom(0);
			orderVo.setDays(Long.valueOf(daysLeft).intValue());
		}

		if(OrderType.EXPANDING.getCode().equals(orderVo.getOrderType())){
			totalPrice = totalPrice * years + totalPrice / 365 * daysLeft;
		} else {
			if(orderVo.getUom().equals(1)){
				totalPrice = totalPrice / 365 * daysLeft;
			} else{
				totalPrice = totalPrice * serviceTerm;
			}
		}

		boolean couponValid = false;
		CouponInfo couponInfo = null;
		if(StringUtils.isNotBlank(orderVo.getCouponCode())){
			CouponDto couponDto = couponService.getCouponByCode(orderVo.getCouponCode());

			couponInfo = getCouponDiscountInfo(couponDto);

			if(couponInfo == null || couponInfo.getStatus().equals(-9)){
				response.setMsg("优惠码不正确，请填写真实的优惠码");
			} else if(couponInfo.getStatus().equals(1) || couponInfo.getStatus().equals(-1)){
				response.setMsg("优惠码已经失效");
			} else if(new Date(couponDto.getExpireTime()).before(new Date())) {
				response.setMsg("优惠码已经过期");
			} else if(checkCouponLimit(orderVo.getTenantId(), orderVo.getCouponCode())) {
				response.setMsg("您已经参加过这个优惠活动");
			} else if(0 == couponInfo.getStatus()){
				couponValid = true;
			}
		}

		exp2.add(unitExp);


		exp2.add(cheng + timeExp);

		response.setCouponValid(couponValid);

		Double orderDiscount = 1D;
		Double serviceTermDiscount = 1D;
		if(!OrderType.EXPANDING.getCode().equals(orderVo.getOrderType())){
			SortedMap<Integer, Integer> tiers = sortServiceTermDiscount(pricingDto.getServiceTermDiscount());
			Integer termUnit = orderVo.getServiceTerm();
			if(orderVo.getUom().equals(0)){
				termUnit = termUnit * 12;
			}

			RuleInfo rule = getMatchedRule(tiers, termUnit);
			serviceTermDiscount = rule.getDiscount();
			totalPrice = totalPrice * serviceTermDiscount;
		}

		if(couponValid){
			response.setCouponInfo(couponInfo);

			CouponDiscount couponDiscount = new CouponDiscount();
			couponDiscount.setProducts(couponInfo.getProducts());
			couponDiscount.setServiceTermInfo(couponInfo.getServiceTerm());
			discountInfo.setCouponDiscount(couponDiscount);

			if(CollectionUtils.isNotEmpty(couponInfo.getProducts())){
				for(CouponProduct product : couponInfo.getProducts()){
					if(skuMap.containsKey(product.getSku())){
						OrderLineVo lineVo = skuMap.get(product.getSku());
						lineVo.setGiveaway(product.getQty());
					}
				}
			}

			//计算服务截至日期
			if(couponInfo.getServiceTerm() != null && couponInfo.getServiceTerm().getQty() != null && couponInfo.getServiceTerm().getQty() > 0){
				if(orderVo.getUom().equals(1)){
					orderVo.setServiceTerm(couponInfo.getServiceTerm().getQty() * 12 + orderVo.getServiceTerm());
				} else if(orderVo.getUom().equals(0)){
					orderVo.setServiceTerm(orderVo.getServiceTerm() + couponInfo.getServiceTerm().getQty());
				}
				response.setExpireTime(DateUtils.addYears(new Date(response.getExpireTime()), couponInfo.getServiceTerm().getQty()).getTime());
			}
			if(couponInfo.getDiscount() != null){
				totalPrice = totalPrice * couponInfo.getDiscount() / 100;
				orderDiscount = couponInfo.getDiscount() / 100D;
				couponDiscount.setDiscount(couponInfo.getDiscount());
			}
		}

		//优惠券没有时长折扣时定价配置的时长折扣才能生效
		if(orderDiscount.equals(1D) && serviceTermDiscount < 1D){
			orderDiscount = serviceTermDiscount;
			if(orderDiscount < 1){
				discountInfo.setDiscount(Double.valueOf(orderDiscount * 100).intValue());
			}
		}

		Double listPrice = 0D;

		for(OrderLineVo orderLineVo : orderVo.getOrderLines()){
			if(orderLineVo.getQty() + orderLineVo.getGiveaway() < orderLineVo.getFree()){
				orderLineVo.setFree(0);
			}

			Integer itemQty = orderLineVo.getQty();
			if(OrderType.RENEWAL.getCode().equals(orderVo.getOrderType())){
				itemQty = tenantSkuQty.get(orderLineVo.getProductSku());
			}

			Integer giveaway = 0;
			if(orderLineVo.getGiveaway() != null && orderLineVo.getGiveaway() > 0){
				giveaway = orderLineVo.getGiveaway();
			}
			Integer total = itemQty + giveaway;
			Double price = getSkuUnitPrice(pricingDto, orderLineVo.getProductSku());
			listPrice += total * price;
		}

		//统一转换为M年N天的形式计算原价
		//增容时剩余时长包含M年N天，无需转换
		if(OrderType.EXPANDING.getCode().equals(orderVo.getOrderType())){
			listPrice = listPrice * orderVo.getServiceTerm() + listPrice / 365 * orderVo.getDays();
		} else{//升级或扩容时购买时长为M年N月
			if(orderVo.getUom().equals(1)){
				Integer year = orderVo.getServiceTerm() / 12;
				listPrice = listPrice * year + listPrice / 365 * (TimeUtil.getDateDiff(DateUtils.addYears(startTime, year), new Date(response.getExpireTime()), TimeUnit.DAYS));
			} else if(orderVo.getUom().equals(0)){
				listPrice = listPrice * orderVo.getServiceTerm();
			}
		}

//		listPrice = listPrice / 365 * TimeUtil.getDateDiff(nowTime, new Date(response.getExpireTime()), TimeUnit.DAYS);

		orderVo.setOrderDiscount(discountInfo);
		if(orderDiscount < 1){
			exp2.add(cheng + orderDiscount);
		}

		exp2.add(" = " + OrderVoUtil.formatAmount(totalPrice) + "元");

//		if(response.getExpireTime() != null){
//			response.setExpireTime(DateUtils.addDays(new Date(response.getExpireTime()), -1).getTime());
//		}

		response.setListPrcie(OrderVoUtil.formatAmount(listPrice));
		response.setPayPrice(OrderVoUtil.formatAmount(totalPrice));
		response.setExpression1(unitPrice > 0 ? (StringUtils.join(exp1, " + ") + " = " + unitExp) : "");
		response.setExpression2(totalPrice > 0 ? StringUtils.join(exp2, "") : "");

		return response;
	}

	@Override
	public OrderPricingResponse calcOrderPrice(OrderVo orderVo, PricingConfigDto pricingDto) {
		final String cheng = "×";

		List<String> productSkus = new ArrayList<String>();

		Map<String, OrderLineVo> skuMap = new HashMap<>();
		orderVo.getOrderLines().forEach(lineVo -> {
			if(StringUtils.isNotBlank(lineVo.getProductSku())){
				productSkus.add(lineVo.getProductSku());
				skuMap.put(lineVo.getProductSku(), lineVo);
			}
		});

		Double totalPrice = 0D;
		Double listPrice = 0D;
//		Double unitPrice = 0D;

		List<String> exp1 = new ArrayList<>();
		List<String> exp2 = new ArrayList<>();

		Integer serviceTerm = orderVo.getServiceTerm();

		OrderDiscount discountInfo = new OrderDiscount();
		if(CollectionUtils.isNotEmpty(productSkus)){
			List<SkuDiscountInfo> skuDiscount = new ArrayList<>();
			discountInfo.setSkuDiscount(skuDiscount);
			RuleInfo rule = getDiscount(pricingDto, ACLSKU, skuMap.get(ACLSKU).getQty());
			Double aclSkuDiscount = rule.getDiscount();
			Integer unit = rule.getUnit();

			if(aclSkuDiscount < 1){
				SkuDiscountInfo info = new SkuDiscountInfo();
				info.setSku("102");
				info.setUnit(unit);
				info.setUom("人");
				info.setDiscount(Double.valueOf(aclSkuDiscount * 100).intValue());
				skuDiscount.add(info);
			}

			List<ProductSkuView> productViews = productOrderService.getProductSkuByCode(productSkus);
			Map<String, ProductSkuView> productMap = new HashMap<>(5);
			for(ProductSkuView view : productViews){
				productMap.put(view.getSku(), view);
			}

			//计算订单价格
			for(OrderLineVo lineVo : orderVo.getOrderLines()){
				String sku = lineVo.getProductSku();
				ProductSkuView view = productMap.get(sku);

				Integer freeQty = getFreeQty(pricingDto, sku);
				if(freeQty < 0){
					freeQty = 0;
				}

				Integer feeQty = lineVo.getQty() - freeQty;

				if(feeQty <= 0){
					continue;
				}


				String skuUom = "";
				if(StringUtils.isNotBlank(view.getSkuUom())){
					String[] uomInfo = StringUtils.split(view.getSkuUom(), "|");
					if(uomInfo.length > 1){
						skuUom = uomInfo[1];
					}
				}

				Double price = getSkuUnitPrice(pricingDto, sku) * feeQty;
				listPrice += price;

				if(ACLSKU.equals(sku)){
					price = price * aclSkuDiscount;
				} else {
					RuleInfo ruleInfo = getDiscount(pricingDto, sku, feeQty);
					Double discount = ruleInfo.getDiscount();
					unit = ruleInfo.getUnit();

					if(discount < 1){
						SkuDiscountInfo info = new SkuDiscountInfo();
						info.setSku(sku);
						info.setUnit(unit);
						info.setUom(skuUom);
						info.setDiscount(Double.valueOf(discount * 100).intValue());
						skuDiscount.add(info);
					}

					if(discount < 0){
						discount = aclSkuDiscount;
					}
					price = price * discount;
				}

				totalPrice += price;
			}
		}

		String unitExp = totalPrice > 0 ? OrderVoUtil.formatAmount(totalPrice) + "元/年" : "";

		if(orderVo.getUom().equals(1)){
			totalPrice = totalPrice / 12 * serviceTerm;
		} else{
			totalPrice = totalPrice * serviceTerm;
		}

		OrderPricingResponse response = new OrderPricingResponse();

		String timeExp = "";
		if(orderVo.getUom().equals(0)){
			timeExp = orderVo.getServiceTerm() + "年";
		} else{
			timeExp = orderVo.getUom() + "个月";
		}

		exp2.add(cheng + timeExp);

		SortedMap<Integer, Integer> tiers = sortServiceTermDiscount(pricingDto.getServiceTermDiscount());
		Integer termUnit = orderVo.getServiceTerm();
		if(orderVo.getUom().equals(0)){
			termUnit = termUnit * 12;
		}

		RuleInfo rule = getMatchedRule(tiers, termUnit);
		Double serviceTermDiscount = rule.getDiscount();
		totalPrice = totalPrice * serviceTermDiscount;

		orderVo.setOrderDiscount(discountInfo);
		response.setListPrcie(OrderVoUtil.formatAmount(listPrice));
		response.setPayPrice(OrderVoUtil.formatAmount(totalPrice));
		response.setExpression1("".equals(unitExp) ? "" : (StringUtils.join(exp1, " + ") + " = " + unitExp));
		response.setExpression2(totalPrice > 0 ? StringUtils.join(exp2, "") : "");

		return response;
	}

	private boolean checkCouponLimit(String tenantId, String couponCode){
		boolean exceedLimit = true;
		try{
			exceedLimit = !couponBatchService.verifyCouponBatchLimit(tenantId, couponCode);
		} catch (Exception e){

		}

		return exceedLimit;
	}

	private Integer getFreeQty(PricingConfigDto dto, String sku){
		for(SkuPricingConfig skuPricingConfig : dto.getSkuDiscount()){
			if(sku.equals(skuPricingConfig.getSku())){
				return skuPricingConfig.getConfig().getFree();
			}
		}

		return 0;
	}

	private Double getSkuUnitPrice(PricingConfigDto dto, String sku){
		for(SkuPricingConfig skuPricingConfig : dto.getSkuDiscount()) {
			if (sku.equals(skuPricingConfig.getSku())) {
				return skuPricingConfig.getConfig().getUnitPrice();
			}
		}

		return null;
	}

	private static RuleInfo getDiscount(PricingConfigDto dto, String sku, Integer qty){
		RuleInfo rule = new RuleInfo();
		rule.setDiscount(1D);
		rule.setUnit(-1);
		if(null == dto || CollectionUtils.isEmpty(dto.getSkuDiscount())){
			return rule;
		}

		for(SkuPricingConfig skuPricingConfig : dto.getSkuDiscount()){
			if(sku.equals(skuPricingConfig.getSku())){
				rule.setUnitPrice(skuPricingConfig.getConfig().getUnitPrice());
				if(0 == skuPricingConfig.getDiscountOption()){
					rule.setDiscount(-1D);
					return rule;
				} else if(2 == skuPricingConfig.getDiscountOption()){
					return rule;
				}

				if(null == skuPricingConfig.getConfig() || CollectionUtils.isEmpty(skuPricingConfig.getConfig().getTiers())) {
					return rule;
				}

				SortedMap<Integer, Integer> tiers = sortPricingConfig(skuPricingConfig);

				RuleInfo ruleInfo = getMatchedRule(tiers, qty);
				ruleInfo.setUnitPrice(rule.getUnitPrice());
				return ruleInfo;
			}
		}

		return rule;
	}

	private static RuleInfo getMatchedRule(SortedMap<Integer, Integer> tiers, Integer qty){
		RuleInfo rule = new RuleInfo();
		rule.setUnit(-1);
		rule.setDiscount(1D);

		for(Map.Entry<Integer,Integer> entry : tiers.entrySet()) {
			Integer unit = entry.getKey();
			Integer discount = entry.getValue();
			if(qty >= unit){
				rule.setUnit(unit);
				rule.setDiscount(discount/100D);
				return rule;
			}
		}

		return rule;
	}

	private CouponInfo getCouponDiscountInfo(CouponDto couponDto){
		if(null == couponDto){
			return null;
		}

		CouponInfo info = new CouponInfo();
		info.setStatus(couponDto.getStatus());
		info.setCouponType(couponDto.getCouponType());
		ServiceTermInfo serviceTermInfo = new ServiceTermInfo();
		if(null == couponDto.getDiscountInfo().getExtraServiceTerm()){
			serviceTermInfo.setQty(0);
		} else {
			serviceTermInfo.setQty(couponDto.getDiscountInfo().getExtraServiceTerm());
		}
		serviceTermInfo.setUom(0);
		info.setServiceTerm(serviceTermInfo);
		info.setDiscount(couponDto.getDiscountInfo().getDiscount());

		List<ProductSkuDto> skuDtos = productPackSkuService.getAllSku();

		Map<String, ProductSkuDto> skuMap = new HashMap<>();
		for(ProductSkuDto dto : skuDtos){
			skuMap.put(dto.getSku(), dto);
		}

		if(CollectionUtils.isNotEmpty(couponDto.getDiscountInfo().getGiveaway())){
			List<CouponProduct> products = new ArrayList<>();
			for(SkuDiscount discount : couponDto.getDiscountInfo().getGiveaway()){
				CouponProduct product = new CouponProduct();
				ProductSkuDto dto = skuMap.get(discount.getSku());
				if(dto != null && discount.getQty() > 0){
					product.setName(dto.getDisplayName());
					product.setSku(discount.getSku());
					product.setQty(discount.getQty());
					product.setUom(dto.getUom());
					products.add(product);
				}
			}
			info.setProducts(products);
		}
		return info;
	}

	private static SortedMap<Integer, Integer> sortPricingConfig(SkuPricingConfig config){
		SortedMap<Integer, Integer> tiers = new TreeMap<>(Collections.reverseOrder());

		if(null == config || null == config.getConfig() || CollectionUtils.isEmpty(config.getConfig().getTiers())){
			return tiers;
		}

		for(PricingTier tier : config.getConfig().getTiers()){
			if(tiers.containsKey(tier.getUnit())){
				tiers.replace(tier.getUnit(), tier.getDiscount());
			} else {
				tiers.put(tier.getUnit(), tier.getDiscount());
			}
		}

		return tiers;
	}

	private static SortedMap<Integer, Integer> sortServiceTermDiscount(List<ServiceTermDiscountItem> discountConfig){
		SortedMap<Integer, Integer> tiers = new TreeMap<>(Collections.reverseOrder());

		if(CollectionUtils.isEmpty(discountConfig)){
			return tiers;
		}

		for(ServiceTermDiscountItem item : discountConfig){
			Integer unit = GetServiceTermInMonth(item);
			if(tiers.containsKey(unit)){
				tiers.replace(unit, item.getDiscount());
			} else {
				tiers.put(unit, item.getDiscount());
			}
		}

		return tiers;
	}

	private static Integer GetServiceTermInMonth(ServiceTermDiscountItem item){
		if(0 == item.getUom()){
			return item.getUnit() * 12;
		}

		return item.getUnit();
	}

	@Override
	public String freshOrderTimerTask(String orderId, String timerTaskId, Date expireTime) {
		if(StringUtils.isBlank(orderId)){
			logger.error("订单: 刷新订单定时任务失败, 订单Id为空");
			return null;
		}

		if(StringUtils.isBlank(timerTaskId)){
			logger.error("订单: 刷新订单定时任务失败, 任务Id为空, 订单ID: {}", orderId);
			return null;
		}

		if(expireTime == null){
			logger.error("订单: 刷新订单定时任务失败, 时间为空, 订单ID: {}", orderId);
			return null;
		}

		if(TenantContext.getUserInfo() == null
				|| StringUtils.isBlank(TenantContext.getUserInfo().getUserId())){
			logger.error("订单: 刷新订单定时任务失败, 操作者为空, 订单ID: {}", orderId);
			return null;
		}

		timerService.cancelTask(timerTaskId);

		OrderCancelScheduleJob scheduleJob = new OrderCancelScheduleJob();
		scheduleJob.setOrderId(orderId);
		scheduleJob.setUserId(TenantContext.getUserInfo().getUserId());

		String newTaskId = timerService.schedule(ServiceType.ORDER.getCode()
				, TaskType.ORDER_EXPIRE.getCode()
				, orderId, scheduleJob, expireTime);

		return newTaskId;
	}

	@Override
	public boolean updateOrderStatus(String orderSn, Integer status) {
		Order order1 = new Order();
		order1.setOrderSn(orderSn);
		List<Order> orderList = this.orderMapper.select(order1);
		Order order = orderList.get(0);

		if(null == order){
			return false;
		}

		if(!STATUS_CAN_INVOICE.contains(order.getStatus())){
			return false;
		}

		if(!INVOICE_STATUS.contains(status)){
			return false;
		}

		order.setStatus(status);
		Integer result =  this.orderMapper.updateByPrimaryKeySelective(order);
		return result == 1;
	}

	@Override
	public boolean updateOrderRemark(String orderId, String remark) {
		if(StringUtils.isBlank(orderId)){
			return false;
		}
		if(remark != null && remark.length() > 500){
			return false;
		}

		Order order = new Order();
		order.setOrderId(orderId);

		order.setRemark(remark);
		Integer result =  this.orderMapper.updateByPrimaryKeySelective(order);
		return result == 1;

	}

	@Override
	public CouponValidationResponse validateCoupon(String couponCode) {
		return null;
	}
}
