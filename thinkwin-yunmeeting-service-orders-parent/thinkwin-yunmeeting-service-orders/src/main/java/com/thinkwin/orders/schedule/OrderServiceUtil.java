package com.thinkwin.orders.schedule;

import com.thinkwin.common.ContextHolder;
import com.thinkwin.orders.mapper.OrderMapper;
import com.thinkwin.orders.model.Order;
import com.thinkwin.orders.model.OrderStatus;
import com.thinkwin.orders.service.OrderService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderServiceUtil {
	private static Logger LOGGER = LoggerFactory.getLogger(OrderServiceUtil.class);

	public void cancelOrder(String orderId, String userId){
		if(StringUtils.isBlank(userId)){
			LOGGER.error("订单: 取消订单失败, userId为空");
			return;
		}

		if(StringUtils.isBlank(orderId)){
			LOGGER.error("订单: 取消订单失败, orderId为空");
			return;
		}

		OrderMapper orderMapper = (OrderMapper) ContextHolder.getApplicationContext().getBean("orderMapper");

		if(null == orderMapper){
			LOGGER.error("订单: 取消订单失败, orderMapper为空, 订单ID: {}", orderId);
			return;
		}

		Order order = orderMapper.selectByPrimaryKey(orderId);

		if(null == order){
			LOGGER.error("订单: 取消订单失败, 订单不存在, 订单ID: {}", orderId);
			return;
		}

		if(StringUtils.isBlank(order.getTenantId())){
			LOGGER.error("订单: 取消订单失败, 订单租户ID为空, 订单ID: {}", orderId);
			return;
		}

		TenantContext.setTenantId(order.getTenantId());

		if(OrderStatus.QUXIAO.equals(order.getStatus())){
			LOGGER.info("订单: 取消订单失败, 订单已取消, 订单ID: {}, 订单状态: {}", order.getOrderId(), order.getStatus());
		}

		if(OrderStatus.WEIZHIFU.getCode().equals(order.getStatus())){
			OrderService orderService = (OrderService) ContextHolder.getApplicationContext().getBean("orderService");
			if(null == orderService){
				LOGGER.error("订单: 取消订单失败, orderService为空, 订单ID: {}", orderId);
				return;
			}

			boolean success = orderService.cancelOrder(orderId);

			if(success){
				LOGGER.info("订单: 取消订单成功, 订单ID: {}", order.getOrderId());
			}
		}
		else{
			String statusName = OrderStatus.fromCode(order.getStatus()).toString();
			LOGGER.info("订单: 取消订单失败, 订单ID: {}, 订单状态: {}({})", order.getOrderId(), order.getStatus(), statusName);
		}
	}
}
