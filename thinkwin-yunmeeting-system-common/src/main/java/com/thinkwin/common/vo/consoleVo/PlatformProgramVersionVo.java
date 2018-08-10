package com.thinkwin.common.vo.consoleVo;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * User: yinchunlei
 * Date: 2018/4/26
 * Company: thinkwin
 */
public class PlatformProgramVersionVo implements Serializable {
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
     * 锁标志
     */
    @Column(name = "`ver`")
    private String ver;

    /**
     * 发布说明
     */
    @Column(name = "`publish_note`")
    private String publishNote;

    /**
     * 版本批次号
     */
    private String versionUpdateBatch;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProgramVersionNum() {
        return programVersionNum;
    }

    public void setProgramVersionNum(String programVersionNum) {
        this.programVersionNum = programVersionNum;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getPublishNote() {
        return publishNote;
    }

    public void setPublishNote(String publishNote) {
        this.publishNote = publishNote;
    }

    public String getVersionUpdateBatch() {
        return versionUpdateBatch;
    }

    public void setVersionUpdateBatch(String versionUpdateBatch) {
        this.versionUpdateBatch = versionUpdateBatch;
    }
}
