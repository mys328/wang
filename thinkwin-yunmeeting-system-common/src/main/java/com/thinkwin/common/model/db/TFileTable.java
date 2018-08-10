package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`t_file_table`")
public class TFileTable implements Serializable {
    private static final long serialVersionUID = 5300907496956186777L;
    @Id
    @Column(name = "`Id`")
    private String id;

    /**
     * 文件所在的组
     */
    @Column(name = "`group`")
    private String group;

    /**
     * 文件路径
     */
    @Column(name = "`attachment_path`")
    private String attachmentPath;

    /**
     * 文件原文件名称
     */
    @Column(name = "`file_cn_name`")
    private String fileCnName;

    /**
     * 上传后的文件名称
     */
    @Column(name = "`file_name`")
    private String fileName;

    /**
     * 创建人ID
     */
    @Column(name = "`creater_id`")
    private String createrId;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`")
    private Date createTime;

    /**
     * 修改人ID
     */
    @Column(name = "`modifyer_id`")
    private String modifyerId;

    /**
     * 修改时间
     */
    @Column(name = "`modify_time`")
    private Date modifyTime;

    /**
     * 文件大小
     */
    @Column(name = "`sys_attachment`")
    private String sysAttachment;

    /**
     * 文件计量单位
     */
    @Column(name = "`unit`")
    private String unit;

    /**
     * @return Id
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
     * 获取文件所在的组
     *
     * @return group - 文件所在的组
     */
    public String getGroup() {
        return group;
    }

    /**
     * 设置文件所在的组
     *
     * @param group 文件所在的组
     */
    public void setGroup(String group) {
        this.group = group == null ? null : group.trim();
    }

    /**
     * 获取文件路径
     *
     * @return attachment_path - 文件路径
     */
    public String getAttachmentPath() {
        return attachmentPath;
    }

    /**
     * 设置文件路径
     *
     * @param attachmentPath 文件路径
     */
    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath == null ? null : attachmentPath.trim();
    }

    /**
     * 获取文件原文件名称
     *
     * @return file_cn_name - 文件原文件名称
     */
    public String getFileCnName() {
        return fileCnName;
    }

    /**
     * 设置文件原文件名称
     *
     * @param fileCnName 文件原文件名称
     */
    public void setFileCnName(String fileCnName) {
        this.fileCnName = fileCnName == null ? null : fileCnName.trim();
    }

    /**
     * 获取上传后的文件名称
     *
     * @return file_name - 上传后的文件名称
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置上传后的文件名称
     *
     * @param fileName 上传后的文件名称
     */
    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
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
     * 获取文件大小
     *
     * @return sys_attachment - 文件大小
     */
    public String getSysAttachment() {
        return sysAttachment;
    }

    /**
     * 设置文件大小
     *
     * @param sysAttachment 文件大小
     */
    public void setSysAttachment(String sysAttachment) {
        this.sysAttachment = sysAttachment == null ? null : sysAttachment.trim();
    }

    /**
     * 获取文件计量单位
     *
     * @return unit - 文件计量单位
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 设置文件计量单位
     *
     * @param unit 文件计量单位
     */
    public void setUnit(String unit) {
        this.unit = unit == null ? null : unit.trim();
    }
}