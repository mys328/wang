package com.thinkwin.console.web.controller;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.orders.OrderInvoice;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.orders.service.OrderInvoiceService;
import com.thinkwin.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 发票开票、寄送管理
 */
@Controller
@RequestMapping("/invoice")
public class InvoiceController {
    @Autowired
    private OrderInvoiceService orderInvoiceService;

    @Autowired
    private OrderService orderService;

    /**
     * 菜单跳转到发票开票页面
     */
    @RequestMapping("/gotoInvoicePage")
    public String gotoInvoicePage() {
        return "console/invoice";
    }

    /**
     * 菜单跳转到寄送管理页面
     */
    @RequestMapping("/gotoInvoiceSendPage")
    public String gotoInvoiceSendPage() {
        return "console/invoiceSend";
    }

    /*
   * 邮寄发票
   * */
    @RequestMapping("/mailInvoice")
    @ResponseBody
    public Object mailInvoice(String orderId, String expressTrackingNumber, String expressCompany, String expressGateUrl) {
        //修改订单状态 为已开票
        boolean success = this.orderService.updateOrderStatus(orderId, 5);
        if (success) {
            //查询订单发票信息
            OrderInvoice orderInvoice = new OrderInvoice();
            orderInvoice.setOrderId(orderId);

            List<OrderInvoice> orderInvoiceList = this.orderInvoiceService.selectOrderInvoiceList(orderInvoice);

            if (orderInvoiceList != null && orderInvoiceList.size() > 0) {
                orderInvoice = orderInvoiceList.get(0);
            }
            orderInvoice.setStatus(2);
            orderInvoice.setExpressTrackingNumber(expressTrackingNumber);
            orderInvoice.setExpressCompany(expressCompany);
            orderInvoice.setExpressGateUrl(expressGateUrl);
            boolean success1 = this.orderInvoiceService.updateOrderInvoice(orderInvoice);
            if (success1) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
    }

    /*
   * 开具发票
   * */
    @RequestMapping("/issueInvoice")
    @ResponseBody
    public Object issueInvoice(String orderId) {

            //查询订单发票信息
            OrderInvoice orderInvoice = new OrderInvoice();
            orderInvoice.setOrderId(orderId);

            List<OrderInvoice> orderInvoiceList = this.orderInvoiceService.selectOrderInvoiceList(orderInvoice);

            if (orderInvoiceList != null && orderInvoiceList.size() > 0) {
                orderInvoice = orderInvoiceList.get(0);
            }
            orderInvoice.setStatus(1);
            boolean success = this.orderInvoiceService.updateOrderInvoice(orderInvoice);
            if (success) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
    }

    /*
    * 分页查询所有订单发票信息
    *
    * */
    @RequestMapping("/selectOrderInvoiceByPage")
    @ResponseBody
    public Object selectOrderInvoiceByPage(BasePageEntity basePageEntity, String content, Integer status, Integer type) {
        OrderInvoice orderInvoice = new OrderInvoice();
        orderInvoice.setOrderId(content);
        List<String> statusList = new ArrayList<>();
        //开票查询
        if (type == 0) {
            if (status != null) {
                statusList.add(status.toString());

            } else {
                statusList.add("0");
                statusList.add("1");
            }
        }
        //寄送查询
        if (type == 1) {
            if (status != null) {
                statusList.add(status.toString());
            } else {
                statusList.add("1");
                statusList.add("2");
            }
        }
        PageInfo<OrderInvoice> orderInvoiceList = this.orderInvoiceService.selectSysLogListByPage(basePageEntity, orderInvoice, statusList);

        if (orderInvoiceList != null) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), orderInvoiceList);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.DataNull.getDescription(), orderInvoiceList, BusinessExceptionStatusEnum.Failure.getCode());
    }

    /*
   * 查询订单发票信息
   *
   * */
    @RequestMapping("/selectOrderInvoiceById")
    @ResponseBody
    public Object selectOrderInvoiceById(String id) {
        OrderInvoice orderInvoice = new OrderInvoice();
        orderInvoice.setOrderId(id);

        List<OrderInvoice> orderInvoiceList = this.orderInvoiceService.selectOrderInvoiceList(orderInvoice);

        if (orderInvoiceList != null && orderInvoiceList.size() > 0) {
            orderInvoice = orderInvoiceList.get(0);
        }
        if (orderInvoice != null) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), orderInvoice);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.DataNull.getDescription(), orderInvoice, BusinessExceptionStatusEnum.Failure.getCode());
    }
}
