package com.thinkwin.orders.util;

import com.alibaba.fastjson.JSON;
import com.thinkwin.common.dto.order.OrderDiscount;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.orders.model.Order;
import com.thinkwin.orders.model.OrderLine;
import com.thinkwin.orders.model.OrderStatus;
import com.thinkwin.orders.vo.OrderLineVo;
import com.thinkwin.orders.vo.OrderVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderVoUtil {

	public static boolean isValidOrder(OrderVo vo){
		if(StringUtils.isBlank(vo.getClientIp())){
			return false;
		}

		if(StringUtils.isBlank(vo.getBuyerUserId())){
			return false;
		}

		if(StringUtils.isBlank(vo.getTenantId())){
			return false;
		}

		if(StringUtils.isBlank(vo.getCreatedBy())){
			return false;
		}

		if(CollectionUtils.isEmpty(vo.getOrderLines())){
			return false;
		}

		for(OrderLineVo item : vo.getOrderLines()){
			if(!isValidOrderLine(item)){
				return false;
			}
		}

		if(null == vo.getOrderType()){
			vo.setOrderType(0);
		}

		boolean upgradePlan = vo.getOrderType().equals(1);

		if(upgradePlan){
			if(CollectionUtils.isEmpty(vo.getOrderLines())){
				return false;
			}
		}

		return true;
	}

	public static boolean isValidOrderLine(OrderLineVo item){
		if(StringUtils.isBlank(item.getProductSku())){
			return false;
		}

		if(null == item.getQty() || item.getQty() < 0){
			return false;
		}

		return true;
	}

	public static Order fromOrderVo(OrderVo vo){
		Order order = new Order();
		if(StringUtils.isNotBlank(vo.getOrderId())){
			order.setOrderId(vo.getOrderId());
		}

		if(StringUtils.isNotBlank(vo.getOrderSn())){
			order.setOrderSn(vo.getOrderSn());
		}

		order.setTenantId(vo.getTenantId());
		order.setOrderType(vo.getOrderType());
		order.setOrderSubject(vo.getOrderSubject());
		order.setOrderSource(vo.getOrderSource());
		order.setTenantName(vo.getTenantName());
		order.setBuyerUserId(vo.getBuyerUserId());
		order.setBuyerUserName(vo.getBuyerUserName());
		order.setServiceTerm(vo.getServiceTerm());
		order.setDays(vo.getDays());
		order.setUom(vo.getUom());
		if(vo.getOrderDiscount() != null){
			order.setOrderDiscount(JSON.toJSONString(vo.getOrderDiscount()));
		}
		order.setPricingConfigVersion(vo.getPricingConfigVersion());
		order.setRemark(vo.getRemark());
		order.setCreatedBy(vo.getCreatedBy());
		if(StringUtils.isNotBlank(vo.getTotalPrice())){
			order.setTotalPrice(Double.parseDouble(vo.getTotalPrice()));
		}

		if(StringUtils.isNotBlank(vo.getPayPrice())){
			order.setPayPrice(Double.parseDouble(vo.getPayPrice()));
		}
		order.setClientIp(vo.getClientIp());
		if(vo.getRentStart() != null){
			order.setRentStart(vo.getRentStart());
		}
		if(vo.getRentEnd() != null){
			order.setRentEnd(vo.getRentEnd());
		}
		order.setCouponCode(vo.getCouponCode());
		order.setCreateTime(new Date());
		order.setStatus(0);

		if(null == order.getTotalPrice()){
			order.setTotalPrice(order.getPayPrice());
		}

		if(null == order.getPromotionCode()){
			order.setPromotionCode("");
		}

		if(null == order.getCouponCode()){
			order.setCouponCode("");
		}

		if(null == order.getOrderSource()){
			order.setOrderSource(0);
		}

		if(null == order.getOrderType()){
			order.setOrderType(0);
		}

		if(CollectionUtils.isNotEmpty(vo.getOrderLines())){
			for(OrderLineVo item : vo.getOrderLines()){
				OrderLine line = new OrderLine();
				line.setId(CreateUUIdUtil.Uuid());
				line.setProductPackSku(item.getProductPackSku());
				line.setProductSku(item.getProductSku());
				line.setUom(item.getUom());

				if(null == line.getProductPackSku()){
					line.setProductPackSku("");
				}

				if(null == line.getProductSku()){
					line.setProductSku("");
				}

				if(null == line.getItemDesc()){
					line.setItemDesc("");
				}

				line.setOrderId(vo.getOrderId());
				line.setQty(item.getQty());
				line.setFree(item.getFree());
				line.setGiveaway(item.getGiveaway());
				line.setCreateTime(new Date());
				order.getOrderLines().add(line);
			}
		}

		return order;
	}

	public static OrderVo fromOrder(Order order){
		OrderVo vo = new OrderVo();
		vo.setOrderId(order.getOrderId());
		vo.setTenantId(order.getTenantId());
		vo.setOrderSn(order.getOrderSn());
		vo.setOrderSource(order.getOrderSource());
		vo.setOrderSubject(order.getOrderSubject());
		vo.setTotalPrice(String.format("%.2f", order.getTotalPrice()));
		vo.setPayPrice(vo.getPayPrice());
		vo.setServiceTerm(order.getServiceTerm());
		vo.setUom(order.getUom());
		vo.setPromotionCode(order.getPromotionCode());
		vo.setCouponCode(order.getCouponCode());
		vo.setPaySuccessTime(order.getPaySuccessTime());
		vo.setClientIp(order.getClientIp());
		vo.setStatus(order.getStatus());
		vo.setStatusName(OrderStatus.getName(order.getStatus()));
		vo.setCreateTime(order.getCreateTime());
		vo.setTenantName(order.getTenantName());
		vo.setBuyerUserId(order.getBuyerUserId());
		vo.setDays(order.getDays());
		vo.setBuyerUserName(order.getBuyerUserName());
		vo.setCreateTime(order.getCreateTime());
		vo.setChannelName(order.getPayChannelName());
		vo.setOrderType(order.getOrderType());
		vo.setCreatedBy(order.getCreatedBy());
		vo.setUpdateTime(order.getUpdateTime());
		if(StringUtils.isNotBlank(order.getOrderDiscount())){
			vo.setOrderDiscount(JSON.parseObject(order.getOrderDiscount(), OrderDiscount.class));
		}

		vo.setRemark(order.getRemark());
		if(order.getRentStart() != null){
			vo.setRentStart(order.getRentStart());
		}

		if(order.getRentEnd() != null){
			vo.setRentEnd(DateUtils.addDays(order.getRentEnd(), -1));
		}

		if(null == vo.getPayPrice()){
			vo.setPayPrice(vo.getTotalPrice());
		}

		if(CollectionUtils.isNotEmpty(order.getOrderLines())){
			for(OrderLine item : order.getOrderLines()){
				if(item != null){
					vo.getOrderLines().add(fromOrderLine(item));
					if(StringUtils.isNotBlank(item.getProductPackSku())){
						vo.setProductId(item.getProductPackSku());
					}
				}
			}
		}

		return vo;
	}

	public static List<OrderVo> fromOrders(List<Order> orders){
		List<OrderVo> orderVos = new ArrayList<OrderVo>();
		for(Order order : orders){
			orderVos.add(fromOrder(order));
		}

		return orderVos;
	}

	public static OrderLineVo fromOrderLine(OrderLine orderLine){
		OrderLineVo vo = new OrderLineVo();
		vo.setProductPackSku(orderLine.getProductPackSku());
		vo.setItemDesc(orderLine.getItemDesc());
		vo.setProductSku(orderLine.getProductSku());
		vo.setQty(orderLine.getQty());
		vo.setFree(orderLine.getFree());
		vo.setGiveaway(orderLine.getGiveaway());
		return vo;
	}

	public static String formatAmount(Double amount){
		DecimalFormat dFormat = new DecimalFormat("#0.00");
		dFormat.setRoundingMode(RoundingMode.HALF_EVEN);
		return dFormat.format(amount);
	}
}
