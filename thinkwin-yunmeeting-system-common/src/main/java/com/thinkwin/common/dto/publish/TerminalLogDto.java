package com.thinkwin.common.dto.publish;

import java.io.Serializable;
import java.util.Date;

/**
 * User:wangxilei
 * Date:2018/6/3
 * Company:thinkwin
 */
public class TerminalLogDto implements Serializable {
    private static final long serialVersionUID = 1397835504452759916L;
    private String id;
    private String tenantName;
    private String terminalName;
    private String operatedate;
    private String eventname;
    private String businessname;
    private Date businesstime;
    private String content;
    private String errorLogContent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getOperatedate() {
        return operatedate;
    }

    public void setOperatedate(String operatedate) {
        this.operatedate = operatedate;
    }

    public String getEventname() {
        return eventname;
    }

    public void setEventname(String eventname) {
        this.eventname = eventname;
    }

    public String getBusinessname() {
        return businessname;
    }

    public void setBusinessname(String businessname) {
        this.businessname = businessname;
    }

    public Date getBusinesstime() {
        return businesstime;
    }

    public void setBusinesstime(Date businesstime) {
        this.businesstime = businesstime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getErrorLogContent() {
        return errorLogContent;
    }

    public void setErrorLogContent(String errorLogContent) {
        this.errorLogContent = errorLogContent;
    }
}
