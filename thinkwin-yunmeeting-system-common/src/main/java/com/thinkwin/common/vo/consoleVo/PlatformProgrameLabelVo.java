package com.thinkwin.common.vo.consoleVo;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * User: yinchunlei
 * Date: 2018/4/25
 * Company: thinkwin
 */
public class PlatformProgrameLabelVo implements Serializable {

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
    @Column(name = "`ver`")
    private String ver;

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    /**
     * 标签状态:草稿状态:0;发布状态:1;内测状态:2
     */
    @Column(name = "`label_status`")
    private String labelStatus;

    private Integer platformProgrameNum;//该标签下的节目数量

    public Integer getPlatformProgrameNum() {
        return platformProgrameNum;
    }

    public void setPlatformProgrameNum(Integer platformProgrameNum) {
        this.platformProgrameNum = platformProgrameNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getLabelUpdateBatch() {
        return labelUpdateBatch;
    }

    public void setLabelUpdateBatch(String labelUpdateBatch) {
        this.labelUpdateBatch = labelUpdateBatch;
    }

    public Integer getLabelSort() {
        return labelSort;
    }

    public void setLabelSort(Integer labelSort) {
        this.labelSort = labelSort;
    }

    public String getLabelStatus() {
        return labelStatus;
    }

    public void setLabelStatus(String labelStatus) {
        this.labelStatus = labelStatus;
    }
}
