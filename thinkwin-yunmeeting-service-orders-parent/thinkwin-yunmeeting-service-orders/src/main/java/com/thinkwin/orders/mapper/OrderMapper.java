package com.thinkwin.orders.mapper;

import com.thinkwin.orders.model.Order;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface OrderMapper extends Mapper<Order> {
	Order selectOrderByIdLocked(@Param("orderId")String orderId);
}
