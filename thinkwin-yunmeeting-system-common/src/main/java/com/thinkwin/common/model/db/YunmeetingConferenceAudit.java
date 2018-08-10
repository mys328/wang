package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`yunmeeting_conference_audit`")
public class YunmeetingConferenceAudit implements Serializable{
    private static final long serialVersionUID = 2376555368882503981L;
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 审核批注
     */
    @Column(name = "`audit_annotations`")
    private String auditAnnotations;

    /**
     * 实际审核人
     */
    @Column(name = "`act_auditor`")
    private String actAuditor;

    /**
     * 实际审核时间
     */
    @Column(name = "`act_audit_time`")
    private Date actAuditTime;

    /**
     * 会议公共表Id
     */
    @Column(name = "`base_confreren_id`")
    private String baseConfrerenId;

    /**
     * 提前发送通知时间
     */
    @Column(name = "`inform_time`")
    private Date informTime;

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
     * 修改人
     */
    @Column(name = "`modifyer_id`")
    private String modifyerId;

    /**
     * 修改时间
     */
    @Column(name = "`modify_time`")
    private Date modifyTime;

    /**
     * 参会回复状态:1审核通过；0:审核未通过
     */
    @Column(name = "`state`")
    private String state;

    /**
     * 记录状态:1:已删除；0:未删除
     */
    @Column(name = "`delete_state`")
    private String deleteState;

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
     * 获取审核批注
     *
     * @return audit_annotations - 审核批注
     */
    public String getAuditAnnotations() {
        return auditAnnotations;
    }

    /**
     * 设置审核批注
     *
     * @param auditAnnotations 审核批注
     */
    public void setAuditAnnotations(String auditAnnotations) {
        this.auditAnnotations = auditAnnotations == null ? null : auditAnnotations.trim();
    }

    /**
     * 获取实际审核人
     *
     * @return act_auditor - 实际审核人
     */
    public String getActAuditor() {
        return actAuditor;
    }

    /**
     * 设置实际审核人
     *
     * @param actAuditor 实际审核人
     */
    public void setActAuditor(String actAuditor) {
        this.actAuditor = actAuditor == null ? null : actAuditor.trim();
    }

    /**
     * 获取实际审核时间
     *
     * @return act_audit_time - 实际审核时间
     */
    public Date getActAuditTime() {
        return actAuditTime;
    }

    /**
     * 设置实际审核时间
     *
     * @param actAuditTime 实际审核时间
     */
    public void setActAuditTime(Date actAuditTime) {
        this.actAuditTime = actAuditTime;
    }

    /**
     * 获取会议公共表Id
     *
     * @return base_confreren_id - 会议公共表Id
     */
    public String getBaseConfrerenId() {
        return baseConfrerenId;
    }

    /**
     * 设置会议公共表Id
     *
     * @param baseConfrerenId 会议公共表Id
     */
    public void setBaseConfrerenId(String baseConfrerenId) {
        this.baseConfrerenId = baseConfrerenId == null ? null : baseConfrerenId.trim();
    }

    /**
     * 获取提前发送通知时间
     *
     * @return inform_time - 提前发送通知时间
     */
    public Date getInformTime() {
        return informTime;
    }

    /**
     * 设置提前发送通知时间
     *
     * @param informTime 提前发送通知时间
     */
    public void setInformTime(Date informTime) {
        this.informTime = informTime;
    }

    /**
     * 获取创建人
     *
     * @return createId - 创建人
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
     * 获取修改人
     *
     * @return modifyer_id - 修改人
     */
    public String getModifyerId() {
        return modifyerId;
    }

    /**
     * 设置修改人
     *
     * @param modifyerId 修改人
     */
    public void setModifyerId(String modifyerId) {
        this.modifyerId = modifyerId == null ? null : modifyerId.trim();
    }

    /**
     * 获取修改时间
     *
     * @return modify_time - 修改时间
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * 设置修改时间
     *
     * @param modifyTime 修改时间
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * 获取参会回复状态:1审核通过；0:审核未通过
     *
     * @return state - 参会回复状态:1审核通过；0:审核未通过
     */
    public String getState() {
        return state;
    }

    /**
     * 设置参会回复状态:1审核通过；0:审核未通过
     *
     * @param state 参会回复状态:1审核通过；0:审核未通过
     */
    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    /**
     * 获取记录状态:1:已删除；0:未删除
     *
     * @return delete_state - 记录状态:1:已删除；0:未删除
     */
    public String getDeleteState() {
        return deleteState;
    }

    /**
     * 设置记录状态:1:已删除；0:未删除
     *
     * @param deleteState 记录状态:1:已删除；0:未删除
     */
    public void setDeleteState(String deleteState) {
        this.deleteState = deleteState == null ? null : deleteState.trim();
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