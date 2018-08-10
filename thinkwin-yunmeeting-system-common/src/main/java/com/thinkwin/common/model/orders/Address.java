package com.thinkwin.common.model.orders;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`address`")
public class Address implements Serializable {
    private static final long serialVersionUID = 5093880737728381165L;
    /**
     * id
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 租户Id
     */
    @Column(name = "`tenand_id`")
    private String tenandId;

    /**
     * 收件人姓名
     */
    @Column(name = "`name`")
    private String name;

    /**
     * 所在地区
     */
    @Column(name = "`location`")
    private String location;

    /**
     * 详细地址
     */
    @Column(name = "`detailed_address`")
    private String detailedAddress;

    /**
     * 邮政编码
     */
    @Column(name = "`postal_code`")
    private String postalCode;

    /**
     * 电话
     */
    @Column(name = "`phone_number`")
    private String phoneNumber;

    /**
     * 创建时间
     */
    @Column(name = "`creater_time`")
    private String createrTime;

    /**
     * 省
     */
    @Column(name = "`province`")
    private String province;

    /**
     * 城市
     */
    @Column(name = "`city`")
    private String city;

    /**
     * 县区
     */
    @Column(name = "`county`")
    private String county;

    /**
     * 街道
     */
    @Column(name = "`street`")
    private String street;

    @Column(name = "`status`")
    private Integer status;

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
     * 获取租户Id
     *
     * @return tenand_id - 租户Id
     */
    public String getTenandId() {
        return tenandId;
    }

    /**
     * 设置租户Id
     *
     * @param tenandId 租户Id
     */
    public void setTenandId(String tenandId) {
        this.tenandId = tenandId == null ? null : tenandId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取所在地区
     *
     * @return location - 所在地区
     */
    public String getLocation() {
        return location;
    }

    /**
     * 设置所在地区
     *
     * @param location 所在地区
     */
    public void setLocation(String location) {
        this.location = location == null ? null : location.trim();
    }

    /**
     * 获取详细地址
     *
     * @return detailed_address - 详细地址
     */
    public String getDetailedAddress() {
        return detailedAddress;
    }

    /**
     * 设置详细地址
     *
     * @param detailedAddress 详细地址
     */
    public void setDetailedAddress(String detailedAddress) {
        this.detailedAddress = detailedAddress == null ? null : detailedAddress.trim();
    }

    /**
     * 获取邮政编码
     *
     * @return postal_code - 邮政编码
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * 设置邮政编码
     *
     * @param postalCode 邮政编码
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode == null ? null : postalCode.trim();
    }

    /**
     * 获取电话
     *
     * @return phone_number - 电话
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * 设置电话
     *
     * @param phoneNumber 电话
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber == null ? null : phoneNumber.trim();
    }

    /**
     * 获取创建时间
     *
     * @return creater_time - 创建时间
     */
    public String getCreaterTime() {
        return createrTime;
    }

    /**
     * 设置创建时间
     *
     * @param createrTime 创建时间
     */
    public void setCreaterTime(String createrTime) {
        this.createrTime = createrTime == null ? null : createrTime.trim();
    }

    /**
     * 获取省
     *
     * @return province - 省
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置省
     *
     * @param province 省
     */
    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    /**
     * 获取城市
     *
     * @return city - 城市
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置城市
     *
     * @param city 城市
     */
    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    /**
     * 获取县区
     *
     * @return county - 县区
     */
    public String getCounty() {
        return county;
    }

    /**
     * 设置县区
     *
     * @param county 县区
     */
    public void setCounty(String county) {
        this.county = county == null ? null : county.trim();
    }

    /**
     * 获取街道
     *
     * @return street - 街道
     */
    public String getStreet() {
        return street;
    }

    /**
     * 设置街道
     *
     * @param street 街道
     */
    public void setStreet(String street) {
        this.street = street == null ? null : street.trim();
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
}