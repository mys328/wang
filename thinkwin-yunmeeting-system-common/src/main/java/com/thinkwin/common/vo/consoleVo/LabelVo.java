package com.thinkwin.common.vo.consoleVo;

import java.io.Serializable;

/**
 * User: yinchunlei
 * Date: 2018/4/24
 * Company: thinkwin
 */
public class LabelVo implements Serializable {
    private String labelId;//标签主键id
    private String labelName;//标签名称

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
