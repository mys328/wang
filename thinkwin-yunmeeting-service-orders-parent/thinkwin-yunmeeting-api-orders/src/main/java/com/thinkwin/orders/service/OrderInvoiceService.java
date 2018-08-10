package com.thinkwin.orders.service;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.orders.OrderInvoice;

import java.util.List;

/**
 * 订单发票
 */
public interface OrderInvoiceService {
    /*
    * 根据条件查询
    * */
    List<OrderInvoice> selectOrderInvoiceList(OrderInvoice orderInvoice);

    /*
    * 添加订单发票
    * */
    boolean insertOrderInvoice(OrderInvoice orderInvoice);

    /*
    * 修改订单发票
    * */
    boolean updateOrderInvoice(OrderInvoice orderInvoice);

    /*
    * 根据id查询
    * */
    OrderInvoice selectOrderInvoiceById(String id);

    /*
   * 带参数 分页查询
   * */
    PageInfo<OrderInvoice> selectSysLogListByPage(BasePageEntity page, OrderInvoice orderInvoice,List<String> status);
}
