package com.thinkwin.orders.service;

import com.thinkwin.common.model.orders.Invoice;

import java.util.List;

/**
 * 发票管理
 */
public interface InvoiceService {

    /*
    * 根据条件查询
    * */
    List<Invoice> selectInvoiceList(Invoice invoice);

    /*
    * 添加地址
    * */
    boolean insertInvoice(Invoice invoice);

    /*
    * 修改地址
    * */
    boolean updateInvoice(Invoice invoice);

    /*
    * 根据id查询
    * */
    Invoice selectInvoiceById(String id);
}
