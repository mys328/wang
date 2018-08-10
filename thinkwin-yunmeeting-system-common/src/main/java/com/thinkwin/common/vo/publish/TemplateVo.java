package com.thinkwin.common.vo.publish;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2018/5/29 0029.
 */
public class TemplateVo implements Serializable {

    private static final long serialVersionUID = -7845269716286848489L;
    /**
     * 模板路径
     */
    private String attachmentUrl;
    /**
     * 播放开始时间
     */
    private Date start;
    /**
     * 播放结束时间
     */
    private Date end;


    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
