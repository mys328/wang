package com.thinkwin.common.model.publish;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "`platform_info_client_version_lib`")
public class PlatformInfoClientVersionLib implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`tenant_id`")
    private String tenantId;

    /**
     * 版本文件名称
     */
    @Column(name = "`version_title`")
    private String versionTitle;

    @Column(name = "`sys_attachment_url`")
    private String sysAttachmentUrl;

    @Column(name = "`sys_attachment_id`")
    private String sysAttachmentId;

    /**
     * 版本编号
     */
    @Column(name = "`ver_num`")
    private String verNum;

    /**
     * 版本code 识别版本高低
     */
    @Column(name = "`code`")
    private Integer code;

    /**
     * 版本大小
     */
    @Column(name = "`size`")
    private String size;

    /**
     * 终端类型
     */
    @Column(name = "`terminal_type`")
    private String terminalType;

    /**
     * 更新数量
     */
    @Column(name = "`change_num`")
    private Integer changeNum;

    /**
     * 发布状态:已发布:1；未发布:0
     */
    @Column(name = "`reasle_status`")
    private String reasleStatus;

    /**
     * 版本状态 当前版本:1;内测试版本:2;普通版本:0
     */
    @Column(name = "`ver_status`")
    private String verStatus;

    /**
     * 记录状态 有效:1 ;删除:0
     */
    @Column(name = "`recorder_status`")
    private String recorderStatus;

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
     * 更新记录
     */
    @Column(name = "`change_recode`")
    private String changeRecode;

    /**
     * 创建人
     */
    @Column(name = "`creater`")
    private String creater;

    /**
     * 创建时间
     */
    @Column(name = "`creat_time`")
    private Date creatTime;

    /**
     * 修改人
     */
    @Column(name = "`modifier`")
    private String modifier;

    /**
     * 修改时间
     */
    @Column(name = "`modify_time`")
    private Date modifyTime;


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
     * @return tenant_id
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    /**
     * 获取版本文件名称
     *
     * @return version_title - 版本文件名称
     */
    public String getVersionTitle() {
        return versionTitle;
    }

    /**
     * 设置版本文件名称
     *
     * @param versionTitle 版本文件名称
     */
    public void setVersionTitle(String versionTitle) {
        this.versionTitle = versionTitle == null ? null : versionTitle.trim();
    }

    /**
     * @return sys_attachment_url
     */
    public String getSysAttachmentUrl() {
        return sysAttachmentUrl;
    }

    /**
     * @param sysAttachmentUrl
     */
    public void setSysAttachmentUrl(String sysAttachmentUrl) {
        this.sysAttachmentUrl = sysAttachmentUrl == null ? null : sysAttachmentUrl.trim();
    }

    /**
     * @return sys_attachment_id
     */
    public String getSysAttachmentId() {
        return sysAttachmentId;
    }

    /**
     * @param sysAttachmentId
     */
    public void setSysAttachmentId(String sysAttachmentId) {
        this.sysAttachmentId = sysAttachmentId == null ? null : sysAttachmentId.trim();
    }

    /**
     * 获取版本编号
     *
     * @return ver_num - 版本编号
     */
    public String getVerNum() {
        return verNum;
    }

    /**
     * 设置版本编号
     *
     * @param verNum 版本编号
     */
    public void setVerNum(String verNum) {
        this.verNum = verNum == null ? null : verNum.trim();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    /**
     * 获取终端类型
     *
     * @return terminal_type - 终端类型
     */
    public String getTerminalType() {
        return terminalType;
    }

    /**
     * 设置终端类型
     *
     * @param terminalType 终端类型
     */
    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType == null ? null : terminalType.trim();
    }

    /**
     * 获取更新数量
     *
     * @return change_num - 更新数量
     */
    public Integer getChangeNum() {
        return (changeNum==null)?0:changeNum;
    }

    /**
     * 设置更新数量
     *
     * @param changeNum 更新数量
     */
    public void setChangeNum(Integer changeNum) {
        this.changeNum = changeNum;
    }

    /**
     * 获取发布状态:已发布:1；未发布:0
     *
     * @return reasle_status - 发布状态:已发布:1；未发布:0
     */
    public String getReasleStatus() {
        return reasleStatus;
    }

    /**
     * 设置发布状态:已发布:1；未发布:0
     *
     * @param reasleStatus 发布状态:已发布:1；未发布:0
     */
    public void setReasleStatus(String reasleStatus) {
        this.reasleStatus = reasleStatus == null ? null : reasleStatus.trim();
    }

    /**
     * 获取版本状态 当前版本:1;内测试版本:2;普通版本:0
     *
     * @return ver_status - 版本状态 当前版本:1;内测试版本:2;普通版本:0
     */
    public String getVerStatus() {
        return verStatus;
    }

    /**
     * 设置版本状态 当前版本:1;内测试版本:2;普通版本:0
     *
     * @param verStatus 版本状态 当前版本:1;内测试版本:2;普通版本:0
     */
    public void setVerStatus(String verStatus) {
        this.verStatus = verStatus == null ? null : verStatus.trim();
    }

    /**
     * 获取记录状态 有效:1 ;删除:0
     *
     * @return recorder_status - 记录状态 有效:1 ;删除:0
     */
    public String getRecorderStatus() {
        return recorderStatus;
    }

    /**
     * 设置记录状态 有效:1 ;删除:0
     *
     * @param recorderStatus 记录状态 有效:1 ;删除:0
     */
    public void setRecorderStatus(String recorderStatus) {
        this.recorderStatus = recorderStatus == null ? null : recorderStatus.trim();
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

    /**
     * 获取更新记录
     *
     * @return change_recode - 更新记录
     */
    public String getChangeRecode() {
        return changeRecode;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * 设置更新记录
     *
     * @param changeRecode 更新记录
     */
    public void setChangeRecode(String changeRecode) {
        this.changeRecode = changeRecode == null ? null : changeRecode.trim();
    }
}