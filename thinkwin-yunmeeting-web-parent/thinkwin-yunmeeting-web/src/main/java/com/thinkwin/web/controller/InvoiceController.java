package com.thinkwin.web.controller;

import com.thinkwin.SMSsender.service.SMSsenderService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.orders.Address;
import com.thinkwin.common.model.orders.Invoice;
import com.thinkwin.common.model.orders.OrderInvoice;
import com.thinkwin.common.utils.DateTypeFormat;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.console.service.SaasUserRoleService;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.mailsender.service.YunmeetingSendMailService;
import com.thinkwin.mailsender.vo.MailVo;
import com.thinkwin.orders.service.AddressService;
import com.thinkwin.orders.service.InvoiceService;
import com.thinkwin.orders.service.OrderInvoiceService;
import com.thinkwin.orders.service.OrderService;
import com.thinkwin.orders.vo.OrderVo;
import com.thinkwin.service.TenantContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 发票管理
 */
@Controller
@RequestMapping(value = "/invoice")
public class InvoiceController {

    private final Logger log = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private AddressService addressService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderInvoiceService orderInvoiceService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private SaasTenantService saasTenantService;

    @Autowired
    private SMSsenderService sMSsenderService;

    @Resource
    private SaasUserRoleService saasUserRoleService;

	@Resource
	YunmeetingSendMailService sendMailService;

    private static final ExecutorService executor = Executors.newWorkStealingPool(3);

	@Value("${invoice.financial_user_role_id}")
	private String financialUserRoleId = "3";

	@Value("${invoice.notify_sms_template_id}")
	private String invoiceNotifySMSTemplateId = "225941";

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

    /*
    * 索票
    * */
    @RequestMapping("/requestInvoice")
    @ResponseBody
    public Object requestInvoice(String orderId, String orderSN) {
        //修改订单状态 为开票中
        boolean success = this.orderService.updateOrderStatus(orderSN, 4);
        if (success) {
            //查询发票信息
            Invoice invoice = new Invoice();
            invoice.setTenandId(TenantContext.getTenantId());
            List<Invoice> invoiceList = this.invoiceService.selectInvoiceList(invoice);
            invoice = invoiceList.get(0);

            //查询地址信息
            Address address = new Address();
            address.setTenandId(TenantContext.getTenantId());
            List<Address> addressList = this.addressService.selectAddressList(address);
            address = addressList.get(0);

            //生成新的发票订单信息
            OrderInvoice orderInvoice = new OrderInvoice();
            orderInvoice.setId(UUID.randomUUID().toString());
            orderInvoice.setTenandId(TenantContext.getTenantId());
            orderInvoice.setOrderId(orderSN);
            orderInvoice.setIssueType(invoice.getIssueType());
            orderInvoice.setHeader(invoice.getHeader());
            orderInvoice.setInvoiceType(invoice.getInvoiceType());
            orderInvoice.setTaxNumber(invoice.getTaxNumber());
            orderInvoice.setOpeningBank(invoice.getOpeningBank());
            orderInvoice.setAccountNumber(invoice.getAccountNumber());
            orderInvoice.setAddress(invoice.getAddress());
            orderInvoice.setInvoicePhone(invoice.getPhoneNumber());

            orderInvoice.setName(address.getName());
            orderInvoice.setPhoneNumber(address.getPhoneNumber());
            orderInvoice.setLocation(address.getProvince()+address.getCity()+address.getCounty());
            orderInvoice.setDetailedAddress(address.getDetailedAddress());
            orderInvoice.setPostalCode(address.getPostalCode());
            orderInvoice.setStatus(0);
            orderInvoice.setCreateTime(DateTypeFormat.DateToStr(new Date()));

            boolean success1 = this.orderInvoiceService.insertOrderInvoice(orderInvoice);
            if (success1) {
                sysLogService.createLog(BusinessType.ordersOp.toString(), EventType.invoice_claim.toString(), orderSN +"索取发票成功", "", Loglevel.error.toString());

                executor.execute(()->{
                    OrderVo orderVo = orderService.getOrderById(orderId);
                    notifyFinancePersonnel(orderVo);
                });

                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
            }
            sysLogService.createLog(BusinessType.ordersOp.toString(), EventType.invoice_claim.toString(), orderSN +"索取发票失败", "", Loglevel.error.toString());

            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
        }

        sysLogService.createLog(BusinessType.ordersOp.toString(), EventType.invoice_claim.toString(), orderSN +"索取发票失败", "", Loglevel.error.toString());

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
    }

    /*
    * 更新发票
   * */
    @RequestMapping("/updateInvoice")
    @ResponseBody
    public Object updateInvoice(Invoice invoice) {
        invoice.setTenandId(TenantContext.getTenantId());
        boolean success = this.invoiceService.updateInvoice(invoice);

        if (success) {
            sysLogService.createLog(BusinessType.ordersOp.toString(), EventType.invoice_claim.toString(), "发票信息保存成功", "", Loglevel.error.toString());

            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        sysLogService.createLog(BusinessType.ordersOp.toString(), EventType.invoice_claim.toString(), "发票信息保存失败", "", Loglevel.error.toString());

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
    }

    /*
   * 更新地址
   * */
    @RequestMapping("/updateAddress")
    @ResponseBody
    public Object updateAddress(Address address) {
        address.setTenandId(TenantContext.getTenantId());
        boolean success = this.addressService.updateAddress(address);

        if (success) {
            sysLogService.createLog(BusinessType.ordersOp.toString(), EventType.invoice_claim.toString(), "发票寄送地址保存成功", "", Loglevel.error.toString());

            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        sysLogService.createLog(BusinessType.ordersOp.toString(), EventType.invoice_claim.toString(), "发票寄送地址保存失败", "", Loglevel.error.toString());

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
    }

    /*
    * 插入发票
    * */
    @RequestMapping("/insertInvoice")
    @ResponseBody
    public Object insertInvoice(Invoice invoice) {
        invoice.setId(UUID.randomUUID().toString());
        invoice.setTenandId(TenantContext.getTenantId());
        invoice.setCreateTime(DateTypeFormat.DateToStr(new Date()));
        invoice.setStatus(0);
        boolean success = this.invoiceService.insertInvoice(invoice);

        if (success) {
            sysLogService.createLog(BusinessType.ordersOp.toString(), EventType.invoice_claim.toString(), "发票信息保存成功", "", Loglevel.error.toString());

            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        sysLogService.createLog(BusinessType.ordersOp.toString(), EventType.invoice_claim.toString(), "发票信息保存失败", "", Loglevel.error.toString());

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
    }

    /*
   * 插入地址
   * */
    @RequestMapping("/insertAddress")
    @ResponseBody
    public Object insertAddress(Address address) {
        address.setId(UUID.randomUUID().toString());
        address.setTenandId(TenantContext.getTenantId());
        address.setCreaterTime(DateTypeFormat.DateToStr(new Date()));
        address.setStatus(0);
        boolean success = this.addressService.insertAddress(address);

        if (success) {
            sysLogService.createLog(BusinessType.ordersOp.toString(), EventType.invoice_claim.toString(), "发票寄送地址保存成功", "", Loglevel.error.toString());

            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        sysLogService.createLog(BusinessType.ordersOp.toString(), EventType.invoice_claim.toString(), "发票寄送地址保存失败", "", Loglevel.error.toString());

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
    }

    /*
    * 查询发票
     * */
    @RequestMapping("/selectInvoiceList")
    @ResponseBody
    public Object selectInvoiceList() {
        Invoice invoice = new Invoice();
        invoice.setTenandId(TenantContext.getTenantId());
        List<Invoice> invoiceList = this.invoiceService.selectInvoiceList(invoice);

        if (invoiceList != null && invoiceList.size() > 0) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), invoiceList.get(0));
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.DataNull.getDescription(), invoiceList, BusinessExceptionStatusEnum.DataNull.getCode());
    }

    /*
    * 查询地址
    * */
    @RequestMapping("/selectAddressList")
    @ResponseBody
    public Object selectAddressList() {
        Address address = new Address();
        address.setTenandId(TenantContext.getTenantId());
        List<Address> addressList = this.addressService.selectAddressList(address);

        if (addressList != null && addressList.size() > 0) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), addressList.get(0));
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.DataNull.getDescription(), addressList, BusinessExceptionStatusEnum.DataNull.getCode());
    }

    private void notifyFinancePersonnel(OrderVo order){
	    List<SaasUser> users = saasUserRoleService.findSaasUsersByRoleId(financialUserRoleId);
	    if (CollectionUtils.isEmpty(users)) {
		    log.error("财务角色人员为空");
		    return;
	    }

	    try {
            for (SaasUser user : users) {
                if (StringUtils.isBlank(user.getPhoneNumber())) {
                    log.error("财务人员手机号为空");
                    continue;
                }
                sMSsenderService.SMSsender(user.getPhoneNumber(), Integer.parseInt(invoiceNotifySMSTemplateId));
            }
        } catch (Exception e) {
            log.error("订单: 发送订单发票提醒短信失败, 订单信息: {}, 异常信息: {}", order, e);
        }

        try {
            MailVo mailVo = new MailVo();
            Map<String, String> recipientsMaps = new HashMap<>();
            for(SaasUser user : users){
                if(StringUtils.isNotBlank(user.getEmail())){
                    recipientsMaps.put(user.getEmail(), user.getUserName());
                }
            }

            SaasTenant saasTenant = saasTenantService.selectByIdSaasTenantInfo(order.getTenantId());

            Map<String, String> templateParamMaps = new HashMap<>();
            templateParamMaps.put("corporateName", saasTenant.getTenantName());
            templateParamMaps.put("serviceName", order.getOrderSubject());
            templateParamMaps.put("orderAmount", order.getTotalPrice());

            mailVo.setRecipientsMap(recipientsMaps);
            mailVo.setTemplateName("invoicing.remind.ftl");
            mailVo.setTemplateParamMap(templateParamMaps);
            mailVo.setSubject("开具订单发票提醒");
            sendMailService.sendMail(mailVo);

        } catch (Exception e){
            log.error("订单: 发送订单发票提醒邮件失败, 订单信息: {}, 异常信息: {}", order, e);
        }

    }
}
