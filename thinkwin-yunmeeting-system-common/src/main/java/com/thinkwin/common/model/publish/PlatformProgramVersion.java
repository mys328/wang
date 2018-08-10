package com.thinkwin.common.model.publish;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`platform_program_version`")
public class PlatformProgramVersion implements Serializable {
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`program_version_num`")
    private String programVersionNum;

    /**
     * 发布时间
     */
    @Column(name = "`publish_time`")
    private Date publishTime;

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
     * 锁标志
     */
    @Column(name = "`ver`")
    private String ver;

    @Column(name = "`custom_tenant_Id`")
    private String customTenantId;

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
     * 发布说明
     */
    @Column(name = "`publish_note`")
    private String publishNote;
    /**
     * 版本批次号
     */
    private String versionUpdateBatch;

    public String getVersionUpdateBatch() {
        return versionUpdateBatch;
    }

    public void setVersionUpdateBatch(String versionUpdateBatch) {
        this.versionUpdateBatch = versionUpdateBatch;
    }

    public String getCustomTenantId() {
        return customTenantId;
    }

    public void setCustomTenantId(String customTenantId) {
        this.customTenantId = customTenantId;
    }

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * @return program_version_num
     */
    public String getProgramVersionNum() {
        return programVersionNum;
    }

    /**
     * @param programVersionNum
     */
    public void setProgramVersionNum(String programVersionNum) {
        this.programVersionNum = programVersionNum == null ? null : programVersionNum.trim();
    }

    /**
     * 获取发布时间
     *
     * @return publish_time - 发布时间
     */
    public Date getPublishTime() {
        return publishTime;
    }

    /**
     * 设置发布时间
     *
     * @param publishTime 发布时间
     */
    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
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
     * 获取锁标志
     *
     * @return ver - 锁标志
     */
    public String getVer() {
        return ver;
    }

    /**
     * 设置锁标志
     *
     * @param ver 锁标志
     */
    public void setVer(String ver) {
        this.ver = ver == null ? null : ver.trim();
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
     * 获取发布说明
     *
     * @return publish_note - 发布说明
     */
    public String getPublishNote() {
        return publishNote;
    }

    /**
     * 设置发布说明
     *
     * @param publishNote 发布说明
     */
    public void setPublishNote(String publishNote) {
        this.publishNote = publishNote == null ? null : publishNote.trim();
    }
}