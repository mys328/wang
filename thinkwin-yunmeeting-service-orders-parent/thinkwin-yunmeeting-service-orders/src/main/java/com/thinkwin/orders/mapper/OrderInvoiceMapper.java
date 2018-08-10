package com.thinkwin.orders.mapper;

import com.thinkwin.common.model.orders.OrderInvoice;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface OrderInvoiceMapper extends Mapper<OrderInvoice> {

    List<OrderInvoice> selectOrderInvoiceByPage(Map map);

}