package com.thinkwin.orders.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.orders.OrderInvoice;
import com.thinkwin.common.utils.StringUtil;
import com.thinkwin.orders.mapper.OrderInvoiceMapper;
import com.thinkwin.orders.service.OrderInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("orderInvoiceService")
public class OrderInvoiceServiceImpl implements OrderInvoiceService {

    @Autowired
    private OrderInvoiceMapper orderInvoiceMapper;

    @Override
    public List<OrderInvoice> selectOrderInvoiceList(OrderInvoice orderInvoice) {
        return this.orderInvoiceMapper.select(orderInvoice);
    }

    @Override
    public boolean insertOrderInvoice(OrderInvoice orderInvoice) {
        boolean success = false;
        if (orderInvoice != null) {
            Integer flag = this.orderInvoiceMapper.insert(orderInvoice);
            if (flag > 0) {
                success = true;
            }
        }
        return success;
    }

    @Override
    public boolean updateOrderInvoice(OrderInvoice orderInvoice) {
        boolean success = false;
        if (orderInvoice != null) {
            Integer flag = this.orderInvoiceMapper.updateByPrimaryKey(orderInvoice);
            if (flag > 0) {
                success = true;
            }
        }
        return success;
    }

    @Override
    public OrderInvoice selectOrderInvoiceById(String id) {
        return this.orderInvoiceMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo<OrderInvoice> selectSysLogListByPage(BasePageEntity page, OrderInvoice orderInvoice,List<String> status) {

        Map map = new HashMap();

        if (!StringUtil.isEmpty(orderInvoice.getOrderId())) {
            map.put("orderId", orderInvoice.getOrderId());
        }
        if (status != null) {
            map.put("status",status);
        }
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());

        List<OrderInvoice> orderInvoiceList = orderInvoiceMapper.selectOrderInvoiceByPage(map);
        return new PageInfo<>(orderInvoiceList);
    }

}
