package com.thinkwin.orders.service.impl;

import com.thinkwin.common.model.orders.Invoice;
import com.thinkwin.orders.mapper.InvoiceMapper;
import com.thinkwin.orders.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("invoiceService")
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Override
    public List<Invoice> selectInvoiceList(Invoice invoice) {
        return this.invoiceMapper.select(invoice);
    }

    @Override
    public boolean insertInvoice(Invoice invoice) {
        boolean success = false;
        if(invoice != null){
            Integer flag =  this.invoiceMapper.insert(invoice);
            if(flag > 0){
                success=true;
            }
        }
        return success;
    }

    @Override
    public boolean updateInvoice(Invoice invoice) {
        boolean success = false;
        if(invoice != null){
            Integer flag =  this.invoiceMapper.updateByPrimaryKey(invoice);
            if(flag > 0){
                success=true;
            }
        }
        return success;
    }

    @Override
    public Invoice selectInvoiceById(String id) {
        return this.invoiceMapper.selectByPrimaryKey(id);
    }
}
