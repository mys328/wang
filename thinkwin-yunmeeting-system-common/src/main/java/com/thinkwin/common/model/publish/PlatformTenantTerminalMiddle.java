package com.thinkwin.common.model.publish;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`platform_tenant_terminal_middle`")
public class PlatformTenantTerminalMiddle implements Serializable{
    private static final long serialVersionUID = -758976012372076439L;
    /**
     * 主键Id
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 租户Id
     */
    @Column(name = "`tenant_id`")
    private String tenantId;

    /**
     * 终端Id
     */
    @Column(name = "`terminal_id`")
    private String terminalId;

    /**
     * 终端名称
     */
    @Column(name = "`terminal_name`")
    private String terminalName;

    /**
     * 终端唯一标识
     */
    @Column(name = "`hardware_id`")
    private String hardwareId;

    /**
     * 1 注册  0 其他
     */
    @Column(name = "`status`")
    private String status;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "`update_time`")
    private Date updateTime;

    /**
     * 预留字段
     */
    @Column(name = "`reserve_1`")
    private String reserve1;

    /**
     * 获取主键Id
     *
     * @return id - 主键Id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键Id
     *
     * @param id 主键Id
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
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
     * 获取终端Id
     *
     * @return terminal_id - 终端Id
     */
    public String getTerminalId() {
        return terminalId;
    }

    /**
     * 设置终端Id
     *
     * @param terminalId 终端Id
     */
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId == null ? null : terminalId.trim();
    }

    /**
     * 获取终端名称
     *
     * @return terminal_name - 终端名称
     */
    public String getTerminalName() {
        return terminalName;
    }

    /**
     * 设置终端名称
     *
     * @param terminalName 终端名称
     */
    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName == null ? null : terminalName.trim();
    }

    /**
     * 获取终端唯一标识
     *
     * @return hardware_id - 终端唯一标识
     */
    public String getHardwareId() {
        return hardwareId;
    }

    /**
     * 设置终端唯一标识
     *
     * @param hardwareId 终端唯一标识
     */
    public void setHardwareId(String hardwareId) {
        this.hardwareId = hardwareId == null ? null : hardwareId.trim();
    }

    /**
     * 获取1 注册  0 其他
     *
     * @return status - 1 注册  0 其他
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置1 注册  0 其他
     *
     * @param status 1 注册  0 其他
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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
     * 获取修改时间
     *
     * @return update_time - 修改时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置修改时间
     *
     * @param updateTime 修改时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取预留字段
     *
     * @return reserve_1 - 预留字段
     */
    public String getReserve1() {
        return reserve1;
    }

    /**
     * 设置预留字段
     *
     * @param reserve1 预留字段
     */
    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1 == null ? null : reserve1.trim();
    }
}