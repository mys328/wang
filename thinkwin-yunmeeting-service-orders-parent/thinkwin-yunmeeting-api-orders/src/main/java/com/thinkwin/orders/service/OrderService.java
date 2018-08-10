package com.thinkwin.orders.service;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.dto.order.CouponValidationResponse;
import com.thinkwin.common.dto.order.OrderPricingResponse;
import com.thinkwin.common.dto.promotion.PricingConfigDto;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.orders.dto.AdminOrderQuery;
import com.thinkwin.orders.dto.OrderCancelRequest;
import com.thinkwin.orders.dto.OrderPaymentConfirm;
import com.thinkwin.orders.dto.OrderResponse;
import com.thinkwin.orders.vo.OrderVo;

import java.util.Date;
import java.util.List;

public interface OrderService {
	OrderResponse newOrder(OrderVo orderVo);

	PageInfo<OrderVo> queryOrders(BasePageEntity page, String tenantId, List<Integer> status, List<Integer> orderTypes, Date startTime, Date endTime);

	PageInfo<OrderVo> queryOrders(AdminOrderQuery query);

	OrderVo getOrderById(String orderId);

	OrderVo getPayStatus(String orderId);

	boolean cancelOrder(String orderId);

	boolean cancelOrder(OrderCancelRequest request);

	OrderPricingResponse calcOrderPrice(OrderVo orderVo);

	OrderPricingResponse calcOrderPrice(OrderVo orderVo, PricingConfigDto pricingDto);

	String freshOrderTimerTask(String orderId, String timerTaskId, Date expireTime);

	/**
	 * 根据订单编号 修改订单状态
	 * @param orderSn
	 * @param status
	 * @return
	 */
	boolean updateOrderStatus(String orderSn,Integer status);

	boolean updateOrderRemark(String orderId, String remark);

	/**
	 * 验证优惠券信息
	 * @param couponCode
	 */
	CouponValidationResponse validateCoupon(String couponCode);
}
