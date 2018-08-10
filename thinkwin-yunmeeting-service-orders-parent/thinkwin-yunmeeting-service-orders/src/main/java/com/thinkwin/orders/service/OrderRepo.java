package com.thinkwin.orders.service;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.orders.dto.OrderResponse;
import com.thinkwin.orders.model.Order;
import com.thinkwin.orders.vo.OrderVo;
import tk.mybatis.mapper.entity.Example;

public interface OrderRepo {
	Order insertOrder(OrderResponse vo);

	PageInfo<OrderVo> queryOrders(BasePageEntity page, Example example);

	OrderVo getOrderById(String orderId);

	boolean cancelOrder(String orderId);

	boolean updatePaymentStatus(String orderId);

	boolean updateOrderAfterPaySuccess(String orderId, String payChannelName);
}
