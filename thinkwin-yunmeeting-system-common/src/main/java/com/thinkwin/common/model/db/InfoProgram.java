package com.thinkwin.common.model.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "`info_program`")
public class InfoProgram implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 节目名称
     */
    @Column(name = "`program_name`")
    private String programName;

    /**
     * 节目类型 0:平台节目；1:租户定制节目
     */
    @Column(name = "`program_type`")
    private String programType;

    @Column(name = "`program_version_num`")
    private String programVersionNum;

    @Column(name = "`sys_attachment_url`")
    private String sysAttachmentUrl;

    @Column(name = "`sys_attachment_id`")
    private String sysAttachmentId;

    /**
     * 效果图URL
     */
    @Column(name = "`photo_url`")
    private String photoUrl;

    /**
     * 节目批次,用来区分某个节目是否是最新版本，保存形式UUID
            如果同时有节目标签更新，则这个批次与标签批次相同
     */
    @Column(name = "`program_update_batch`")
    private String programUpdateBatch;

    /**
     * 节目状态   发布状态:1;内测状态:2 
            租户级没有草稿状态
     */
    @Column(name = "`program_status`")
    private String programStatus;

    /**
     * 记录状态 有效:1 ;删除:0
     */
    @Column(name = "`recorder_status`")
    private String recorderStatus;

    @Column(name = "`program_sort`")
    private Integer programSort;

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
     * 乐观锁标志
     */
    @Column(name = "`ver`")
    private Integer ver;

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
    private String imgUrlBig;//预览图大
    private String imgUrlSmall;//预览图小
    private String imgUrlInit;//预览图中
    private String imgUrlMiddle;//预览图原
    /**
     * 节目名称全拼
     */
    @Column(name = "`program_name_pinyin`")
    private String programNamePinyin;

    /**
     * 节目名称简拼
     */
    @Column(name = "`program_name_jianpin`")
    private String programNameJianpin;

    public String getImgUrlBig() {
        return imgUrlBig;
    }

    public void setImgUrlBig(String imgUrlBig) {
        this.imgUrlBig = imgUrlBig;
    }

    public String getImgUrlSmall() {
        return imgUrlSmall;
    }

    public void setImgUrlSmall(String imgUrlSmall) {
        this.imgUrlSmall = imgUrlSmall;
    }

    public String getImgUrlInit() {
        return imgUrlInit;
    }

    public void setImgUrlInit(String imgUrlInit) {
        this.imgUrlInit = imgUrlInit;
    }

    public String getImgUrlMiddle() {
        return imgUrlMiddle;
    }

    public void setImgUrlMiddle(String imgUrlMiddle) {
        this.imgUrlMiddle = imgUrlMiddle;
    }

    public String getProgramNamePinyin() {
        return programNamePinyin;
    }

    public void setProgramNamePinyin(String programNamePinyin) {
        this.programNamePinyin = programNamePinyin;
    }

    public String getProgramNameJianpin() {
        return programNameJianpin;
    }

    public void setProgramNameJianpin(String programNameJianpin) {
        this.programNameJianpin = programNameJianpin;
    }

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
     * 获取节目名称
     *
     * @return program_name - 节目名称
     */
    public String getProgramName() {
        return programName;
    }

    /**
     * 设置节目名称
     *
     * @param programName 节目名称
     */
    public void setProgramName(String programName) {
        this.programName = programName == null ? null : programName.trim();
    }


    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public String getProgramVersionNum() {
        return programVersionNum;
    }

    public void setProgramVersionNum(String programVersionNum) {
        this.programVersionNum = programVersionNum;
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
     * 获取效果图URL
     *
     * @return photo_url - 效果图URL
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * 设置效果图URL
     *
     * @param photoUrl 效果图URL
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl == null ? null : photoUrl.trim();
    }

    /**
     * 获取节目批次,用来区分某个节目是否是最新版本，保存形式UUID
            如果同时有节目标签更新，则这个批次与标签批次相同
     *
     * @return program_update_batch - 节目批次,用来区分某个节目是否是最新版本，保存形式UUID
            如果同时有节目标签更新，则这个批次与标签批次相同
     */
    public String getProgramUpdateBatch() {
        return programUpdateBatch;
    }

    /**
     * 设置节目批次,用来区分某个节目是否是最新版本，保存形式UUID
            如果同时有节目标签更新，则这个批次与标签批次相同
     *
     * @param programUpdateBatch 节目批次,用来区分某个节目是否是最新版本，保存形式UUID
            如果同时有节目标签更新，则这个批次与标签批次相同
     */
    public void setProgramUpdateBatch(String programUpdateBatch) {
        this.programUpdateBatch = programUpdateBatch == null ? null : programUpdateBatch.trim();
    }

    /**
     * 获取节目状态   发布状态:1;内测状态:2 
            租户级没有草稿状态
     *
     * @return program_status - 节目状态   发布状态:1;内测状态:2 
            租户级没有草稿状态
     */
    public String getProgramStatus() {
        return programStatus;
    }

    /**
     * 设置节目状态   发布状态:1;内测状态:2 
            租户级没有草稿状态
     *
     * @param programStatus 节目状态   发布状态:1;内测状态:2 
            租户级没有草稿状态
     */
    public void setProgramStatus(String programStatus) {
        this.programStatus = programStatus == null ? null : programStatus.trim();
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
     * @return program_sort
     */
    public Integer getProgramSort() {
        return programSort;
    }

    /**
     * @param programSort
     */
    public void setProgramSort(Integer programSort) {
        this.programSort = programSort;
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
     * 获取乐观锁标志
     *
     * @return ver - 乐观锁标志
     */
    public Integer getVer() {
        return ver;
    }

    /**
     * 设置乐观锁标志
     *
     * @param ver 乐观锁标志
     */
    public void setVer(Integer ver) {
        this.ver = ver;
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