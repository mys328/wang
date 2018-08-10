package com.thinkwin.common.model.publish;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`platform_programe_label`")
public class PlatformProgrameLabel implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 标签名称
     */
    @Column(name = "`label_name`")
    private String labelName;

    @Column(name = "`label_update_batch`")
    private String labelUpdateBatch;

    @Column(name = "`label_sort`")
    private Integer labelSort;

    /**
     * 标签状态:草稿状态:0;发布状态:1;内测状态:2
     */
    @Column(name = "`label_status`")
    private String labelStatus;

    /**
     * 记录状态:有效:1；删除:0
     */
    @Column(name = "`recorder_status`")
    private String recorderStatus;
    @Column(name = "`ver`")
    private String ver;

    /**
     * 创建人ID
     */
    @Column(name = "`creater`")
    private String creater;

    /**
     * 创建时间
     */
    @Column(name = "`creat_time`")
    private Date creatTime;

    /**
     * 修改人ID
     */
    @Column(name = "`modifier`")
    private String modifier;

    /**
     * 修改时间
     */
    @Column(name = "`modify_time`")
    private Date modifyTime;

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
     * 获取标签名称
     *
     * @return label_name - 标签名称
     */
    public String getLabelName() {
        return labelName;
    }

    /**
     * 设置标签名称
     *
     * @param labelName 标签名称
     */
    public void setLabelName(String labelName) {
        this.labelName = labelName == null ? null : labelName.trim();
    }

    /**
     * @return label_update_batch
     */
    public String getLabelUpdateBatch() {
        return labelUpdateBatch;
    }

    /**
     * @param labelUpdateBatch
     */
    public void setLabelUpdateBatch(String labelUpdateBatch) {
        this.labelUpdateBatch = labelUpdateBatch == null ? null : labelUpdateBatch.trim();
    }

    /**
     * @return label_sort
     */
    public Integer getLabelSort() {
        return labelSort;
    }

    /**
     * @param labelSort
     */
    public void setLabelSort(Integer labelSort) {
        this.labelSort = labelSort;
    }

    /**
     * 获取标签状态:草稿状态:0;发布状态:1;内测状态:2
     *
     * @return label_status - 标签状态:草稿状态:0;发布状态:1;内测状态:2
     */
    public String getLabelStatus() {
        return labelStatus;
    }

    /**
     * 设置标签状态:草稿状态:0;发布状态:1;内测状态:2
     *
     * @param labelStatus 标签状态:草稿状态:0;发布状态:1;内测状态:2
     */
    public void setLabelStatus(String labelStatus) {
        this.labelStatus = labelStatus == null ? null : labelStatus.trim();
    }

    /**
     * 获取记录状态:有效:1；删除:0
     *
     * @return recorder_status - 记录状态:有效:1；删除:0
     */
    public String getRecorderStatus() {
        return recorderStatus;
    }

    /**
     * 设置记录状态:有效:1；删除:0
     *
     * @param recorderStatus 记录状态:有效:1；删除:0
     */
    public void setRecorderStatus(String recorderStatus) {
        this.recorderStatus = recorderStatus == null ? null : recorderStatus.trim();
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    /**
     * 获取创建人ID
     *
     * @return creater - 创建人ID
     */
    public String getCreater() {
        return creater;
    }

    /**
     * 设置创建人ID
     *
     * @param creater 创建人ID
     */
    public void setCreater(String creater) {
        this.creater = creater == null ? null : creater.trim();
    }

    /**
     * 获取创建时间
     *
     * @return creat_time - 创建时间
     */
    public Date getCreatTime() {
        return creatTime;
    }

    /**
     * 设置创建时间
     *
     * @param creatTime 创建时间
     */
    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    /**
     * 获取修改人ID
     *
     * @return modifier - 修改人ID
     */
    public String getModifier() {
        return modifier;
    }

    /**
     * 设置修改人ID
     *
     * @param modifier 修改人ID
     */
    public void setModifier(String modifier) {
        this.modifier = modifier == null ? null : modifier.trim();
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