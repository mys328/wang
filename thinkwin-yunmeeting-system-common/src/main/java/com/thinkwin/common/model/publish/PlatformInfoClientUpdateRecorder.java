package com.thinkwin.common.model.publish;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`platform_info_client_update_recorder`")
public class PlatformInfoClientUpdateRecorder implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 终端ID
     */
    @Column(name = "`terminal_id`")
    private String terminalId;

    /**
     * 原客户端版本
     */
    @Column(name = "`old_client_ver`")
    private String oldClientVer;

    /**
     * 最新客户端版本
     */
    @Column(name = "`curr_client_ver`")
    private String currClientVer;

    /**
     * 更新时间
     */
    @Column(name = "`update_time`")
    private Date updateTime;

    /**
     * 更新状态:更新成功:1;更新失败:0,信发端上报更新状态
     */
    @Column(name = "`update_status`")
    private String updateStatus;

    /**
     * 预留字段1
     */
    @Column(name = "`reserve_1`")
    private String reserve1;

    /**
     * 预留字段3
     */
    @Column(name = "`reserve_3`")
    private String reserve3;

    /**
     * 预留字段2
     */
    @Column(name = "`reserve_2`")
    private String reserve2;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取终端ID
     *
     * @return terminal_id - 终端ID
     */
    public String getTerminalId() {
        return terminalId;
    }

    /**
     * 设置终端ID
     *
     * @param terminalId 终端ID
     */
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId == null ? null : terminalId.trim();
    }

    /**
     * 获取原客户端版本
     *
     * @return old_client_ver - 原客户端版本
     */
    public String getOldClientVer() {
        return oldClientVer;
    }

    /**
     * 设置原客户端版本
     *
     * @param oldClientVer 原客户端版本
     */
    public void setOldClientVer(String oldClientVer) {
        this.oldClientVer = oldClientVer == null ? null : oldClientVer.trim();
    }

    /**
     * 获取最新客户端版本
     *
     * @return curr_client_ver - 最新客户端版本
     */
    public String getCurrClientVer() {
        return currClientVer;
    }

    /**
     * 设置最新客户端版本
     *
     * @param currClientVer 最新客户端版本
     */
    public void setCurrClientVer(String currClientVer) {
        this.currClientVer = currClientVer == null ? null : currClientVer.trim();
    }

    /**
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取更新状态:更新成功:1;更新失败:0,信发端上报更新状态
     *
     * @return update_status - 更新状态:更新成功:1;更新失败:0,信发端上报更新状态
     */
    public String getUpdateStatus() {
        return updateStatus;
    }

    /**
     * 设置更新状态:更新成功:1;更新失败:0,信发端上报更新状态
     *
     * @param updateStatus 更新状态:更新成功:1;更新失败:0,信发端上报更新状态
     */
    public void setUpdateStatus(String updateStatus) {
        this.updateStatus = updateStatus == null ? null : updateStatus.trim();
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
}