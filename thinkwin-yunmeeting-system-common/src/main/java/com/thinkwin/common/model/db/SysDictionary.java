package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`sys_dictionary`")
public class SysDictionary implements Serializable{
    private static final long serialVersionUID = 3698931086694190482L;
    @Id
    @Column(name = "`dict_id`")
    private String dictId;

    /**
     * 上一级ID
     */
    @Column(name = "`parent_id`")
    private String parentId;

    /**
     * 字典分类
     */
    @Column(name = "`dict_sort`")
    private String dictSort;

    /**
     * 是否为默认选项，1：默认选项，0：非默认选项
     */
    @Column(name = "`is_default`")
    private Integer isDefault;

    /**
     * 租户Id
     */
    @Column(name = "`tenant_id`")
    private String tenantId;

    /**
     * 字典编码
     */
    @Column(name = "`dict_code`")
    private String dictCode;

    /**
     * 字典名称
     */
    @Column(name = "`dict_name`")
    private String dictName;

    /**
     * 字典值
     */
    @Column(name = "`dict_value`")
    private String dictValue;

    /**
     * 备注
     */
    @Column(name = "`descript`")
    private String descript;

    /**
     * 有效:1 ；无效:0
     */
    @Column(name = "`status`")
    private Integer status;

    /**
     * 创建人
     */
    @Column(name = "`create_id`")
    private String createId;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`")
    private Date createTime;

    /**
     * 最后修改人
     */
    @Column(name = "`update_id`")
    private String updateId;

    /**
     * 数据最后修改时间
     */
    @Column(name = "`update_time`")
    private Date updateTime;

    /**
     * 排序
     */
    @Column(name = "`order_num`")
    private Integer orderNum;

    /**
     * 平台id
     */
    @Column(name = "`platform_id`")
    private Integer platformId;

    /**
     * 预留字段1
     */
    @Column(name = "`reserve_1`")
    private String reserve1;

    /**
     * 预留字段2
     */
    @Column(name = "`reserve_2`")
    private String reserve2;

    /**
     * 预留字段3
     */
    @Column(name = "`reserve_3`")
    private String reserve3;

    /**
     * @return dict_id
     */
    public String getDictId() {
        return dictId;
    }

    /**
     * @param dictId
     */
    public void setDictId(String dictId) {
        this.dictId = dictId == null ? null : dictId.trim();
    }

    /**
     * 获取上一级ID
     *
     * @return parent_id - 上一级ID
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * 设置上一级ID
     *
     * @param parentId 上一级ID
     */
    public void setParentId(String parentId) {
        this.parentId = parentId == null ? null : parentId.trim();
    }

    /**
     * 获取字典分类
     *
     * @return dict_sort - 字典分类
     */
    public String getDictSort() {
        return dictSort;
    }

    /**
     * 设置字典分类
     *
     * @param dictSort 字典分类
     */
    public void setDictSort(String dictSort) {
        this.dictSort = dictSort == null ? null : dictSort.trim();
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * 获取租户Id
     *
     * @return tenant_id - 租户Id
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * 设置租户Id
     *
     * @param tenantId 租户Id
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    /**
     * 获取字典编码
     *
     * @return dict_code - 字典编码
     */
    public String getDictCode() {
        return dictCode;
    }

    /**
     * 设置字典编码
     *
     * @param dictCode 字典编码
     */
    public void setDictCode(String dictCode) {
        this.dictCode = dictCode == null ? null : dictCode.trim();
    }

    /**
     * 获取字典名称
     *
     * @return dict_name - 字典名称
     */
    public String getDictName() {
        return dictName;
    }

    /**
     * 设置字典名称
     *
     * @param dictName 字典名称
     */
    public void setDictName(String dictName) {
        this.dictName = dictName == null ? null : dictName.trim();
    }

    /**
     * 获取字典值
     *
     * @return dict_value - 字典值
     */
    public String getDictValue() {
        return dictValue;
    }

    /**
     * 设置字典值
     *
     * @param dictValue 字典值
     */
    public void setDictValue(String dictValue) {
        this.dictValue = dictValue == null ? null : dictValue.trim();
    }

    /**
     * 获取备注
     *
     * @return descript - 备注
     */
    public String getDescript() {
        return descript;
    }

    /**
     * 设置备注
     *
     * @param descript 备注
     */
    public void setDescript(String descript) {
        this.descript = descript == null ? null : descript.trim();
    }

    /**
     * 获取有效:1 ；无效:0
     *
     * @return status - 有效:1 ；无效:0
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置有效:1 ；无效:0
     *
     * @param status 有效:1 ；无效:0
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取创建人
     *
     * @return create_id - 创建人
     */
    public String getCreateId() {
        return createId;
    }

    /**
     * 设置创建人
     *
     * @param createId 创建人
     */
    public void setCreateId(String createId) {
        this.createId = createId == null ? null : createId.trim();
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取最后修改人
     *
     * @return update_id - 最后修改人
     */
    public String getUpdateId() {
        return updateId;
    }

    /**
     * 设置最后修改人
     *
     * @param updateId 最后修改人
     */
    public void setUpdateId(String updateId) {
        this.updateId = updateId == null ? null : updateId.trim();
    }

    /**
     * 获取数据最后修改时间
     *
     * @return update_time - 数据最后修改时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置数据最后修改时间
     *
     * @param updateTime 数据最后修改时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取排序
     *
     * @return order_num - 排序
     */
    public Integer getOrderNum() {
        return orderNum;
    }

    /**
     * 设置排序
     *
     * @param orderNum 排序
     */
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    /**
     * 获取平台id
     *
     * @return platform_id - 平台id
     */
    public Integer getPlatformId() {
        return platformId;
    }

    /**
     * 设置平台id
     *
     * @param platformId 平台id
     */
    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    /**
     * 获取预留字段1
     *
     * @return reserve_1 - 预留字段1
     */
    public String getReserve1() {
        return reserve1;
    }

    /**
     * 设置预留字段1
     *
     * @param reserve1 预留字段1
     */
    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1 == null ? null : reserve1.trim();
    }

    /**
     * 获取预留字段2
     *
     * @return reserve_2 - 预留字段2
     */
    public String getReserve2() {
        return reserve2;
    }

    /**
     * 设置预留字段2
     *
     * @param reserve2 预留字段2
     */
    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2 == null ? null : reserve2.trim();
    }

    /**
     * 获取预留字段3
     *
     * @return reserve_3 - 预留字段3
     */
    public String getReserve3() {
        return reserve3;
    }

    /**
     * 设置预留字段3
     *
     * @param reserve3 预留字段3
     */
    public void setReserve3(String reserve3) {
        this.reserve3 = reserve3 == null ? null : reserve3.trim();
    }
}