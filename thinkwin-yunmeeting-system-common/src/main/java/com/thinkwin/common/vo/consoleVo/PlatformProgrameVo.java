package com.thinkwin.common.vo.consoleVo;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 标签添加和关联辅助类
 * User: yinchunlei
 * Date: 2018/4/24
 * Company: thinkwin
 */
public class PlatformProgrameVo implements Serializable {
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
     * 效果图URL
     */
    @Column(name = "`photo_url`")
    private String photoUrlBig;


    @Column(name = "`program_sort`")
    private Integer programSort;

    /**
     * 节目批次,用来区分某个节目是否是最新版本，保存形式UUID
     */
    @Column(name = "`program_update_batch`")
    private String programUpdateBatch;

    /**
     * 节目状态 草稿状态:0;发布状态:1;内测状态:2
     草稿状态用于上传节目但未发布，也未同步到租户
     */
    @Column(name = "`program_status`")
    private String programStatus;

    @Column(name = "`program_version_num`")
    private String programVersionNum;

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
     * 乐观锁标志
     */
    @Column(name = "`ver`")
    private String ver;

    private String programType;

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public String getPhotoUrlBig() {
        return photoUrlBig;
    }

    public void setPhotoUrlBig(String photoUrlBig) {
        this.photoUrlBig = photoUrlBig;
    }

    /**
     * 该节目所拥有的标签集合
     */
    private List<LabelVo> labelIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getSysAttachmentUrl() {
        return sysAttachmentUrl;
    }

    public void setSysAttachmentUrl(String sysAttachmentUrl) {
        this.sysAttachmentUrl = sysAttachmentUrl;
    }

    public String getSysAttachmentId() {
        return sysAttachmentId;
    }

    public void setSysAttachmentId(String sysAttachmentId) {
        this.sysAttachmentId = sysAttachmentId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Integer getProgramSort() {
        return programSort;
    }

    public void setProgramSort(Integer programSort) {
        this.programSort = programSort;
    }

    public String getProgramUpdateBatch() {
        return programUpdateBatch;
    }

    public void setProgramUpdateBatch(String programUpdateBatch) {
        this.programUpdateBatch = programUpdateBatch;
    }

    public String getProgramStatus() {
        return programStatus;
    }

    public void setProgramStatus(String programStatus) {
        this.programStatus = programStatus;
    }

    public String getProgramVersionNum() {
        return programVersionNum;
    }

    public void setProgramVersionNum(String programVersionNum) {
        this.programVersionNum = programVersionNum;
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

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public List<LabelVo> getLabelIds() {
        return labelIds;
    }

    public void setLabelIds(List<LabelVo> labelIds) {
        this.labelIds = labelIds;
    }
}
