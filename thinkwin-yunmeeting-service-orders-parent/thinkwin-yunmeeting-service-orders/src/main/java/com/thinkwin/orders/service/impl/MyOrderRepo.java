package com.thinkwin.orders.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.goodscenter.dataview.ProductPackSkuView;
import com.thinkwin.goodscenter.dataview.ProductSkuView;
import com.thinkwin.goodscenter.service.ProductOrderService;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.orders.dto.OrderResponse;
import com.thinkwin.orders.mapper.OrderLineMapper;
import com.thinkwin.orders.mapper.OrderMapper;
import com.thinkwin.orders.mapper.TenantOrderInfoMapper;
import com.thinkwin.orders.model.Order;
import com.thinkwin.orders.model.OrderLine;
import com.thinkwin.orders.model.OrderStatus;
import com.thinkwin.orders.model.TenantOrderInfo;
import com.thinkwin.orders.service.OrderRepo;
import com.thinkwin.orders.util.OrderVoUtil;
import com.thinkwin.orders.vo.OrderLineVo;
import com.thinkwin.orders.vo.OrderVo;
import com.thinkwin.orders.vo.UomItem;
import com.thinkwin.pay.dto.PaymentVo;
import com.thinkwin.pay.model.PayChannelEnum;
import com.thinkwin.pay.model.PayStatus;
import com.thinkwin.pay.service.PayService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service(value = "orderRepo")
public class MyOrderRepo implements OrderRepo {
	private static Logger log = LoggerFactory.getLogger(MyOrderRepo.class);

	private static Set<Integer> closedStatus = new HashSet<Integer>();

	static {
		closedStatus.add(OrderStatus.YIFUKUAN.getCode());
		closedStatus.add(OrderStatus.QUXIAO.getCode());
		closedStatus.add(OrderStatus.KAIPIAOZHONG.getCode());
		closedStatus.add(OrderStatus.YIKAIPIAO.getCode());
	}

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private OrderLineMapper orderLineMapper;

	@Autowired
	private TenantOrderInfoMapper tenantOrderInfoMapper;

	@Autowired
	private ProductOrderService productOrderService;

	@Autowired
	private SaasTenantService saasTenantService;

	@Autowired
	private SysLogService logService;

	@Autowired
	private PayService payService;

	@Override
	public Order insertOrder(OrderResponse response) {
		OrderVo orderVo = response.getOrderVo();

		TenantOrderInfo orderInfo = tenantOrderInfoMapper.selectByPrimaryKey(orderVo.getTenantId());
		if(null == orderInfo){
			orderInfo = new TenantOrderInfo();
			orderInfo.setTenantId(orderVo.getTenantId());
			orderInfo.setCurrentOrder(orderVo.getOrderId());
			orderInfo.setUpdateTime(new Date());
			int rowInserted = tenantOrderInfoMapper.insertSelective(orderInfo);

			if(rowInserted != 1){
				return null;
			}
		}

		orderInfo = tenantOrderInfoMapper.getOrderInfoByIdLocked(orderVo.getTenantId());

		if(StringUtils.isNotBlank(orderInfo.getCurrentOrder())){
			logService.createLog(BusinessType.ordersOp.toString(), EventType.add_orders.toString(), "有未完成订单！", "", Loglevel.error.toString());
			response.setErrorMsg("有未完成订单!");
			return null;
		}

		Order order = OrderVoUtil.fromOrderVo(response.getOrderVo());
		orderMapper.insertSelective(order);

		for(OrderLine line : order.getOrderLines()){
			line.setOrderId(order.getOrderId());
		}
		orderLineMapper.InsertOrderLines(order.getOrderLines());

		if(null == order){
			logService.createLog(BusinessType.ordersOp.toString(), EventType.add_orders.toString(), "创建订单失败！", "", Loglevel.error.toString());
			response.setErrorMsg("创建订单失败!");
			return null;
		}

		TenantOrderInfo newOrderInfo = new TenantOrderInfo();
		newOrderInfo.setTenantId(orderVo.getTenantId());
		newOrderInfo.setCurrentOrder(orderVo.getOrderId());
		newOrderInfo.setUpdateTime(new Date());
		tenantOrderInfoMapper.updateByPrimaryKeySelective(newOrderInfo);

		boolean tenantInfoUpdated = false;
		try{
			tenantInfoUpdated = saasTenantService.updateTenantOrder(order.getTenantId(), order.getOrderId());
		} catch (Exception e){
			log.error("订单: 更新租户订单信息失败");
			logService.createLog(BusinessType.ordersOp.toString(), EventType.add_orders.toString(), "更新租户订单信息失败！", "", Loglevel.error.toString());
		}

		if(!tenantInfoUpdated){
			TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
			response.setErrorMsg("更新租户订单信息失败!");
			return null;
		}

		return order;
	}

	@Override
	public PageInfo<OrderVo> queryOrders(BasePageEntity page, Example example) {
		Page<?> pageInfo = PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
		List<Order> orders = orderMapper.selectByExample(example);

		if (CollectionUtils.isEmpty(orders)) {
			return null;
		}

		Set<String> ids = orders.stream().map(o -> o.getOrderId())
				.distinct()
				.collect(Collectors.toSet());

		Example itemExample = new Example(OrderLine.class);
		itemExample.createCriteria()
				.andIn("orderId", ids);

		List<OrderLine> orderLines = orderLineMapper.selectByExample(itemExample);

		Map<String, List<OrderLine>> items = orderLines.stream()
				.collect(Collectors.groupingBy(OrderLine::getOrderId, toList()));

		for (Order order : orders) {
			order.setOrderLines(items.get(order.getOrderId()));
		}
		List<OrderVo> vos = OrderVoUtil.fromOrders(orders);


		Map<String, PaymentVo> orderPaymentMap = new HashMap<String, PaymentVo>();

		List<PaymentVo> payments = payService.getPaymentByIds(ids);
		if(CollectionUtils.isNotEmpty(payments)){
			payments.forEach(p -> {
				orderPaymentMap.put(p.getOrderId(), p);
			});
		}
		for (OrderVo orderVo : vos) {
			PaymentVo payment = orderPaymentMap.get(orderVo.getOrderId());
			if (payment != null) {
				if ((OrderStatus.WEIZHIFU.getCode().equals(orderVo.getStatus())
						|| OrderStatus.DAIHUIKUAN.getCode().equals(orderVo.getStatus()))
						&& PayStatus.WAIT_BUYER_PAY.getValue().equals(payment.getStatus())
						&& PayChannelEnum.HUIKUAN.getCode().equals(payment.getPayChannel())) {
					orderVo.setStatus(OrderStatus.DAIHUIKUAN.getCode());
					orderVo.setStatusName(OrderStatus.DAIHUIKUAN.toString());
				}
				orderVo.setPayChannel(payment.getPayChannel());
				orderVo.setCertImageUrl(payment.getCertImgUrl());
			}
		}

		PageInfo info = new PageInfo<OrderVo>(vos);
		info.setPageNum(pageInfo.getPageNum());
		info.setPages(pageInfo.getPages());
		info.setTotal(pageInfo.getTotal());
		return info;
	}

	@Override
	public OrderVo getOrderById(String orderId) {
		if(StringUtils.isEmpty(orderId)){
			return null;
		}

		Order order = orderMapper.selectByPrimaryKey(orderId);
		if(null == order){
			return null;
		}

		OrderLine line = new OrderLine();
		line.setOrderId(order.getOrderId());
		List<OrderLine> items =  orderLineMapper.select(line);
		order.setOrderLines(items);

		OrderVo orderVo = OrderVoUtil.fromOrder(order);

		List<OrderLineVo> servicePack = orderVo.getOrderLines().stream()
				.filter(vo-> StringUtils.isNotBlank(vo.getProductPackSku()))
				.collect(toList());

		List<String> productSkus = orderVo.getOrderLines().stream()
				.filter(vo-> StringUtils.isNotBlank(vo.getProductSku()))
				.map(l -> l.getProductSku())
				.collect(toList());


		SortedMap<String, UomItem> itemsSeq = new TreeMap<String, UomItem>();

		//套餐
		if(CollectionUtils.isNotEmpty(servicePack)){
			String packSku = servicePack.get(0).getProductPackSku();
			ProductPackSkuView skuView = productOrderService.getProductPackByCode(Arrays.asList(packSku)).get(0);


			/** skuInfo
			 * 1acd608a865d11e79978000c29eb4caa|会议室|间|quantity|3
			 * #1ad010a3865d11e79978000c29eb4caa|存储空间|GB|storage|5
			 * #f69554f0865c11e79978000c29eb4caa|客户访问授权|人|person|100
			 */
			for(String item : StringUtils.split(skuView.getSkuInfo(), "#")){
				String[] info = StringUtils.split(item, "|");
				if(!info[3].equals("person")){
					if(!itemsSeq.containsKey(info[0])){
						itemsSeq.put(info[0], new UomItem());
					}

					UomItem uom = itemsSeq.get(info[0]);
					uom.setProductId(info[0]);
					uom.setProductName(info[1]);
					uom.setBase(Integer.parseInt(info[4]));
				} else {
					orderVo.setLicenseQty(Integer.parseInt(info[4]));
				}
			}

			orderVo.setDiscount(skuView.getDiscount());
			orderVo.setDiscountTip(skuView.getDiscountTip());
			orderVo.setSpecName(skuView.getSpeName());
			orderVo.setSpecValue(skuView.getSpecValue());
			orderVo.setUomName(skuView.getUomName());
			orderVo.setUomClass(skuView.getUomClass());
		}

//		orderVo.setRentStart(orderVo.getCreateTime());
//
//		if(CollectionUtils.isEmpty(servicePack)){
//			SaasTenant tenant = saasTenantService.selectByIdSaasTenantInfo(orderVo.getTenantId());
//			orderVo.setRentEnd(tenant.getBasePackageExpir());
//		}
//		else{
//			orderVo.setRentEnd(DateUtils.addYears(orderVo.getRentStart(), Integer.parseInt(orderVo.getSpecValue())));
//		}

		if(CollectionUtils.isNotEmpty(productSkus)){
			List<ProductSkuView> produtSkuViews = productOrderService.getProductSkuByCode(productSkus);

			Map<String, ProductSkuView> lineSku = new HashMap<String, ProductSkuView>();
			produtSkuViews.forEach(v -> lineSku.put(v.getSku(), v));

			for(OrderLineVo lineVo : orderVo.getOrderLines()){
				if(StringUtils.isNotBlank(lineVo.getProductSku())){
					ProductSkuView view = lineSku.get(lineVo.getProductSku());
					if(null == view){
						continue;
					}
					lineVo.setItemDesc(view.getSkuUom());

					String[] info = StringUtils.split(view.getSkuUom(), "|");
					if(!itemsSeq.containsKey(view.getProductId())){
						itemsSeq.put(view.getProductId(), new UomItem());
					}

					UomItem uom = itemsSeq.get(view.getProductId());
					uom.setProductId(view.getProductId());
					uom.setProductName(view.getSkuDesc());
					uom.setDisplayName(view.getDisplayName());
					uom.setExtra(lineVo.getQty() * Integer.parseInt(info[0]));
					uom.setTotal(uom.getExtra());
					uom.setUom(info[1]);
					uom.setFree(lineVo.getFree());
					uom.setGiveaway(lineVo.getGiveaway());
				}
			}
		}

		PaymentVo paymentVo = payService.getPaymentById(orderId);
		if(paymentVo != null){

			if((OrderStatus.WEIZHIFU.getCode().equals(orderVo.getStatus())
					|| OrderStatus.DAIHUIKUAN.getCode().equals(orderVo.getStatus()))
					&& PayStatus.WAIT_BUYER_PAY.getValue().equals(paymentVo.getStatus())
					&& PayChannelEnum.HUIKUAN.getCode().equals(paymentVo.getPayChannel())){
				orderVo.setStatus(OrderStatus.DAIHUIKUAN.getCode());
				orderVo.setStatusName(OrderStatus.DAIHUIKUAN.toString());
			}
			orderVo.setExpireTime(paymentVo.getExpireTime());
			orderVo.setPayChannel(paymentVo.getPayChannel());
			orderVo.setCertImageUrl(paymentVo.getCertImgUrl());
		}

		for(UomItem item : itemsSeq.values()){
			orderVo.getItems().add(item);
		}


		return orderVo;
	}

	@Override
	public boolean cancelOrder(String orderId) {
		Order o = new Order();
		o.setOrderId(orderId);
		o.setStatus(OrderStatus.QUXIAO.getCode());
		o.setUpdateTime(new Date());
		int result = orderMapper.updateByPrimaryKeySelective(o);
		return result == 1;
	}

	@Override
	public boolean updatePaymentStatus(String orderId) {
		if(StringUtils.isBlank(orderId)){
			return false;
		}

		Order order = orderMapper.selectOrderByIdLocked(orderId);

		List<Integer> status = Arrays.asList(OrderStatus.DAIHUIKUAN.getCode());
		if(status.contains(order.getStatus())){
			try{
				order.setStatus(OrderStatus.YIFUKUAN.getCode());
				int affected = orderMapper.updateByPrimaryKeySelective(order);
				return affected == 1;
			} catch (Exception e){
				log.error("确认订单已收到银行汇款失败, 订单ID: {}: msg: {}", orderId, e);
			}
		}

		return false;
	}

	@Override
	public boolean updateOrderAfterPaySuccess(String orderId, String payChannelName) {
		Order order = orderMapper.selectOrderByIdLocked(orderId);

		if(null == order){
			return false;
		}

		if(closedStatus.contains(order.getStatus())){
			return true;
		}

		List<Integer> status = Arrays.asList(OrderStatus.WEIZHIFU.getCode(), OrderStatus.DAIHUIKUAN.getCode());

		if(status.contains(order.getStatus())){
			TenantOrderInfo info = new TenantOrderInfo();
			info.setTenantId(order.getTenantId());
			info.setCurrentOrder("");
			info.setUpdateTime(new Date());
			int infoAffected = tenantOrderInfoMapper.updateByPrimaryKeySelective(info);

			if(infoAffected != 1){
				return false;
			}

			order.setStatus(OrderStatus.YIFUKUAN.getCode());
			order.setUpdateTime(new Date());
			order.setPaySuccessTime(new Date());
			order.setPayChannelName(payChannelName);
			int affectedRow = orderMapper.updateByPrimaryKeySelective(order);
			return affectedRow == 1;
		}

		return false;
	}
}
