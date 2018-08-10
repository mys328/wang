package com.thinkwin.orders.mapper;

import com.thinkwin.orders.model.OrderLine;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface OrderLineMapper extends Mapper<OrderLine> {
	Integer InsertOrderLines(@Param("list")List<OrderLine> orderLines);
}
