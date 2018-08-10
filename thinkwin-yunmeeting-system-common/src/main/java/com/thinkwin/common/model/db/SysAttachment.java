package com.thinkwin.common.model.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "`sys_attachment`")
public class SysAttachment implements Serializable {
    private static final long serialVersionUID = -4397083374255941356L;
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 文件所在组
     */
    @Column(name = "`group`")
    private String group;

    /**
     * 附件路径 不包括文件名
     */
    @Column(name = "`attachment_path`")
    private String attachmentPath;

    /**
     * 上传后的uuid文件名
     */
    @Column(name = "`file_name`")
    private String fileName;

    /**
     * 文件上传前的文件名
     */
    @Column(name = "`file_cn_name`")
    private String fileCnName;

    /**
     * 创建人ID
     */
    @Column(name = "`creater_id`")
    private String createrId;

    @Column(name = "`create_time`")
    private Date createTime;

    /**
     * 修改人ID
     */
    @Column(name = "`modifyer_id`")
    private String modifyerId;

    @Column(name = "`modify_time`")
    private Date modifyTime;

    @Column(name = "`attachment_size`")
    private String attachmentSize;

    @Column(name = "`unit`")
    private String unit;

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
     * 获取文件所在组
     *
     * @return group - 文件所在组
     */
    public String getGroup() {
        return group;
    }

    /**
     * 设置文件所在组
     *
     * @param group 文件所在组
     */
    public void setGroup(String group) {
        this.group = group == null ? null : group.trim();
    }

    /**
     * 获取附件路径 不包括文件名
     *
     * @return attachment_path - 附件路径 不包括文件名
     */
    public String getAttachmentPath() {
        return attachmentPath;
    }

    /**
     * 设置附件路径 不包括文件名
     *
     * @param attachmentPath 附件路径 不包括文件名
     */
    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath == null ? null : attachmentPath.trim();
    }

    /**
     * 获取上传后的uuid文件名
     *
     * @return file_name - 上传后的uuid文件名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置上传后的uuid文件名
     *
     * @param fileName 上传后的uuid文件名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    /**
     * 获取文件上传前的文件名
     *
     * @return file_cn_name - 文件上传前的文件名
     */
    public String getFileCnName() {
        return fileCnName;
    }

    /**
     * 设置文件上传前的文件名
     *
     * @param fileCnName 文件上传前的文件名
     */
    public void setFileCnName(String fileCnName) {
        this.fileCnName = fileCnName == null ? null : fileCnName.trim();
    }

    /**
     * 获取创建人ID
     *
     * @return creater_id - 创建人ID
     */
    public String getCreaterId() {
        return createrId;
    }

    /**
     * 设置创建人ID
     *
     * @param createrId 创建人ID
     */
    public void setCreaterId(String createrId) {
        this.createrId = createrId == null ? null : createrId.trim();
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取修改人ID
     *
     * @return modifyer_id - 修改人ID
     */
    public String getModifyerId() {
        return modifyerId;
    }

    /**
     * 设置修改人ID
     *
     * @param modifyerId 修改人ID
     */
    public void setModifyerId(String modifyerId) {
        this.modifyerId = modifyerId == null ? null : modifyerId.trim();
    }

    /**
     * @return modify_time
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * @param modifyTime
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * @return attachment_size
     */
    public String getAttachmentSize() {
        return attachmentSize;
    }

    /**
     * @param attachmentSize
     */
    public void setAttachmentSize(String attachmentSize) {
        this.attachmentSize = attachmentSize;
    }

    /**
     * @return unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit == null ? null : unit.trim();
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