package com.thinkwin.common.model.orders;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`order_invoice`")
public class OrderInvoice implements Serializable {
    private static final long serialVersionUID = -4575932016329375709L;
    /**
     * id
     */
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`tenand_id`")
    private String tenandId;

    @Column(name = "`order_Id`")
    private String orderId;

    @Column(name = "`issue_type`")
    private Integer issueType;

    @Column(name = "`header`")
    private String header;

    @Column(name = "`invoice_type`")
    private String invoiceType;

    @Column(name = "`tax_number`")
    private String taxNumber;

    @Column(name = "`opening_bank`")
    private String openingBank;

    @Column(name = "`account_number`")
    private String accountNumber;

    @Column(name = "`address`")
    private String address;

    @Column(name = "`phone_number`")
    private String phoneNumber;

    @Column(name = "`name`")
    private String name;

    @Column(name = "`location`")
    private String location;

    @Column(name = "`detailed_address`")
    private String detailedAddress;

    @Column(name = "`postal_code`")
    private String postalCode;

    @Column(name = "`express_tracking_number`")
    private String expressTrackingNumber;

    @Column(name = "`express_company`")
    private String expressCompany;

    @Column(name = "`status`")
    private Integer status;

    @Column(name = "`create_time`")
    private String createTime;

    @Column(name = "`express_gate_url`")
    private String expressGateUrl;

    @Column(name = "`invoice_phone`")
    private String invoicePhone;

    /**
     * 获取id
     *
     * @return id - id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * @return tenand_id
     */
    public String getTenandId() {
        return tenandId;
    }

    /**
     * @param tenandId
     */
    public void setTenandId(String tenandId) {
        this.tenandId = tenandId == null ? null : tenandId.trim();
    }

    /**
     * @return order_Id
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * @param orderId
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    /**
     * 获取运输方式名称
     *
     * @return issue_type - 运输方式名称
     */
    public Integer getIssueType() {
        return issueType;
    }

    /**
     * 设置运输方式名称
     *
     * @param issueType 运输方式名称
     */
    public void setIssueType(Integer issueType) {
        this.issueType = issueType;
    }

    /**
     * 获取运输金额
     *
     * @return header - 运输金额
     */
    public String getHeader() {
        return header;
    }

    /**
     * 设置运输金额
     *
     * @param header 运输金额
     */
    public void setHeader(String header) {
        this.header = header == null ? null : header.trim();
    }

    /**
     * 获取运输物流号
     *
     * @return invoice_type - 运输物流号
     */
    public String getInvoiceType() {
        return invoiceType;
    }

    /**
     * 设置运输物流号
     *
     * @param invoiceType 运输物流号
     */
    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType == null ? null : invoiceType.trim();
    }

    /**
     * 获取创建人主键id
     *
     * @return tax_number - 创建人主键id
     */
    public String getTaxNumber() {
        return taxNumber;
    }

    /**
     * 设置创建人主键id
     *
     * @param taxNumber 创建人主键id
     */
    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber == null ? null : taxNumber.trim();
    }

    /**
     * 获取创建时间
     *
     * @return opening_bank - 创建时间
     */
    public String getOpeningBank() {
        return openingBank;
    }

    /**
     * 设置创建时间
     *
     * @param openingBank 创建时间
     */
    public void setOpeningBank(String openingBank) {
        this.openingBank = openingBank == null ? null : openingBank.trim();
    }

    /**
     * 获取修改人主键id
     *
     * @return account_number - 修改人主键id
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * 设置修改人主键id
     *
     * @param accountNumber 修改人主键id
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber == null ? null : accountNumber.trim();
    }

    /**
     * 获取修改时间
     *
     * @return address - 修改时间
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置修改时间
     *
     * @param address 修改时间
     */
    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    /**
     * @return phone_number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber == null ? null : phoneNumber.trim();
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location
     */
    public void setLocation(String location) {
        this.location = location == null ? null : location.trim();
    }

    /**
     * @return detailed_address
     */
    public String getDetailedAddress() {
        return detailedAddress;
    }

    /**
     * @param detailedAddress
     */
    public void setDetailedAddress(String detailedAddress) {
        this.detailedAddress = detailedAddress == null ? null : detailedAddress.trim();
    }

    /**
     * @return postal_code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode == null ? null : postalCode.trim();
    }

    /**
     * @return express_tracking_number
     */
    public String getExpressTrackingNumber() {
        return expressTrackingNumber;
    }

    /**
     * @param expressTrackingNumber
     */
    public void setExpressTrackingNumber(String expressTrackingNumber) {
        this.expressTrackingNumber = expressTrackingNumber == null ? null : expressTrackingNumber.trim();
    }

    /**
     * @return express_company
     */
    public String getExpressCompany() {
        return expressCompany;
    }

    /**
     * @param expressCompany
     */
    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany == null ? null : expressCompany.trim();
    }

    /**
     * @return status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return create_time
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    /**
     * @return express_gate_url
     */
    public String getExpressGateUrl() {
        return expressGateUrl;
    }

    /**
     * @param expressGateUrl
     */
    public void setExpressGateUrl(String expressGateUrl) {
        this.expressGateUrl = expressGateUrl == null ? null : expressGateUrl.trim();
    }

    public String getInvoicePhone() {
        return invoicePhone;
    }

    public void setInvoicePhone(String invoicePhone) {
        this.invoicePhone = invoicePhone;
    }
}