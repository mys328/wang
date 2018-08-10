package com.thinkwin.common.model.orders;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`invoice`")
public class Invoice implements Serializable {
    private static final long serialVersionUID = -470154080614644904L;
    /**
     * id
     */
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`tenand_id`")
    private String tenandId;

    /**
     * 开具类型
     */
    @Column(name = "`issue_type`")
    private Integer issueType;

    /**
     * 发票抬头
     */
    @Column(name = "`header`")
    private String header;

    /**
     * 发票类型
     */
    @Column(name = "`invoice_type`")
    private String invoiceType;

    /**
     * 税务证号
     */
    @Column(name = "`tax_number`")
    private String taxNumber;

    /**
     * 开户银行
     */
    @Column(name = "`opening_bank`")
    private String openingBank;

    /**
     * 开户账号
     */
    @Column(name = "`account_number`")
    private String accountNumber;

    /**
     * 注册地址
     */
    @Column(name = "`address`")
    private String address;

    /**
     * 联系电话
     */
    @Column(name = "`phone_number`")
    private String phoneNumber;

    @Column(name = "`status`")
    private Integer status;

    @Column(name = "`create_time`")
    private String createTime;

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
     * 获取开具类型
     *
     * @return issue_type - 开具类型
     */
    public Integer getIssueType() {
        return issueType;
    }

    /**
     * 设置开具类型
     *
     * @param issueType 开具类型
     */
    public void setIssueType(Integer issueType) {
        this.issueType = issueType;
    }

    /**
     * 获取发票抬头
     *
     * @return header - 发票抬头
     */
    public String getHeader() {
        return header;
    }

    /**
     * 设置发票抬头
     *
     * @param header 发票抬头
     */
    public void setHeader(String header) {
        this.header = header == null ? null : header.trim();
    }

    /**
     * 获取发票类型
     *
     * @return invoice_type - 发票类型
     */
    public String getInvoiceType() {
        return invoiceType;
    }

    /**
     * 设置发票类型
     *
     * @param invoiceType 发票类型
     */
    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType == null ? null : invoiceType.trim();
    }

    /**
     * 获取税务证号
     *
     * @return tax_number - 税务证号
     */
    public String getTaxNumber() {
        return taxNumber;
    }

    /**
     * 设置税务证号
     *
     * @param taxNumber 税务证号
     */
    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber == null ? null : taxNumber.trim();
    }

    /**
     * 获取开户银行
     *
     * @return opening_bank - 开户银行
     */
    public String getOpeningBank() {
        return openingBank;
    }

    /**
     * 设置开户银行
     *
     * @param openingBank 开户银行
     */
    public void setOpeningBank(String openingBank) {
        this.openingBank = openingBank == null ? null : openingBank.trim();
    }

    /**
     * 获取开户账号
     *
     * @return account_number - 开户账号
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * 设置开户账号
     *
     * @param accountNumber 开户账号
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber == null ? null : accountNumber.trim();
    }

    /**
     * 获取注册地址
     *
     * @return address - 注册地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置注册地址
     *
     * @param address 注册地址
     */
    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    /**
     * 获取联系电话
     *
     * @return phone_number - 联系电话
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * 设置联系电话
     *
     * @param phoneNumber 联系电话
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber == null ? null : phoneNumber.trim();
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
}