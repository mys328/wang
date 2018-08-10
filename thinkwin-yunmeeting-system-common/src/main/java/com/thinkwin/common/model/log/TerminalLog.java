package com.thinkwin.common.model.log;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`terminal_log`")
public class TerminalLog implements Serializable {
    private static final long serialVersionUID = 1576944319843625174L;
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 租户Id
     */
    @Column(name = "`tenant_id`")
    private String tenantId;

    /**
     * 租户名称
     */
    @Column(name = "`tenant_name`")
    private String tenantName;

    /**
     * 终端ID
     */
    @Column(name = "`terminal_id`")
    private String terminalId;

    /**
     * 终端名称
     */
    @Column(name = "`terminal_name`")
    private String terminalName;

    /**
     * 日志级别
     */
    @Column(name = "`loglevel`")
    private String loglevel;

    /**
     * 操作者
     */
    @Column(name = "`operator`")
    private String operator;

    /**
     * 操作人Id
     */
    @Column(name = "`operate_user_id`")
    private String operateUserId;

    /**
     * 操作时间
     */
    @Column(name = "`operatedate`")
    private String operatedate;

    /**
     * 业务id
     */
    @Column(name = "`businessid`")
    private String businessid;

    /**
     * 业务名称
     */
    @Column(name = "`businessname`")
    private String businessname;

    /**
     * 业务类型
     */
    @Column(name = "`businesstype`")
    private String businesstype;

    /**
     * 业务发生时间
     */
    @Column(name = "`businesstime`")
    private Date businesstime;

    /**
     * 事件类型
     */
    @Column(name = "`eventtype`")
    private String eventtype;

    /**
     * 操作名称
     */
    @Column(name = "`eventname`")
    private String eventname;

    /**
     * 指令代码
     */
    @Column(name = "`command`")
    private String command;

    /**
     * 执行结果(1成功 0失败)
     */
    @Column(name = "`status`")
    private Integer status;

    /**
     * 调用方法名
     */
    @Column(name = "`methodname`")
    private String methodname;

    /**
     * 调用类名
     */
    @Column(name = "`classname`")
    private String classname;

    /**
     * ip地址
     */
    @Column(name = "`ip`")
    private String ip;

    /**
     * 设备来源
     */
    @Column(name = "`source`")
    private String source;

    /**
     * 业务是否有效标识,0-有效，1-无效
     */
    @Column(name = "`state`")
    private Integer state;

    /**
     * 操作内容
     */
    @Column(name = "`content`")
    private String content;

    /**
     * 操作结果
     */
    @Column(name = "`result`")
    private String result;

    /**
     * 调用参数
     */
    @Column(name = "`methodarg`")
    private String methodarg;

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
     * 获取租户Id
     *
     * @return tenant_id - 租户Id
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * 设置租户Id
     *
     * @param tenantId 租户Id
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    /**
     * 获取租户名称
     *
     * @return tenant_name - 租户名称
     */
    public String getTenantName() {
        return tenantName;
    }

    /**
     * 设置租户名称
     *
     * @param tenantName 租户名称
     */
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName == null ? null : tenantName.trim();
    }

    /**
     * 获取终端ID
     *
     * @return terminal_id - 终端ID
     */
    public String getTerminalId() {
        return terminalId;
    }

    /**
     * 设置终端ID
     *
     * @param terminalId 终端ID
     */
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId == null ? null : terminalId.trim();
    }

    /**
     * 获取终端名称
     *
     * @return terminal_name - 终端名称
     */
    public String getTerminalName() {
        return terminalName;
    }

    /**
     * 设置终端名称
     *
     * @param terminalName 终端名称
     */
    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName == null ? null : terminalName.trim();
    }

    /**
     * 获取日志级别
     *
     * @return loglevel - 日志级别
     */
    public String getLoglevel() {
        return loglevel;
    }

    /**
     * 设置日志级别
     *
     * @param loglevel 日志级别
     */
    public void setLoglevel(String loglevel) {
        this.loglevel = loglevel == null ? null : loglevel.trim();
    }

    /**
     * 获取操作者
     *
     * @return operator - 操作者
     */
    public String getOperator() {
        return operator;
    }

    /**
     * 设置操作者
     *
     * @param operator 操作者
     */
    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    /**
     * 获取操作人Id
     *
     * @return operate_user_id - 操作人Id
     */
    public String getOperateUserId() {
        return operateUserId;
    }

    /**
     * 设置操作人Id
     *
     * @param operateUserId 操作人Id
     */
    public void setOperateUserId(String operateUserId) {
        this.operateUserId = operateUserId == null ? null : operateUserId.trim();
    }

    /**
     * 获取操作时间
     *
     * @return operatedate - 操作时间
     */
    public String getOperatedate() {
        return operatedate;
    }

    /**
     * 设置操作时间
     *
     * @param operatedate 操作时间
     */
    public void setOperatedate(String operatedate) {
        this.operatedate = operatedate == null ? null : operatedate.trim();
    }

    /**
     * 获取业务id
     *
     * @return businessid - 业务id
     */
    public String getBusinessid() {
        return businessid;
    }

    /**
     * 设置业务id
     *
     * @param businessid 业务id
     */
    public void setBusinessid(String businessid) {
        this.businessid = businessid == null ? null : businessid.trim();
    }

    /**
     * 获取业务名称
     *
     * @return businessname - 业务名称
     */
    public String getBusinessname() {
        return businessname;
    }

    /**
     * 设置业务名称
     *
     * @param businessname 业务名称
     */
    public void setBusinessname(String businessname) {
        this.businessname = businessname == null ? null : businessname.trim();
    }

    /**
     * 获取业务类型
     *
     * @return businesstype - 业务类型
     */
    public String getBusinesstype() {
        return businesstype;
    }

    /**
     * 设置业务类型
     *
     * @param businesstype 业务类型
     */
    public void setBusinesstype(String businesstype) {
        this.businesstype = businesstype == null ? null : businesstype.trim();
    }

    /**
     * 获取业务发生时间
     *
     * @return businesstime - 业务发生时间
     */
    public Date getBusinesstime() {
        return businesstime;
    }

    /**
     * 设置业务发生时间
     *
     * @param businesstime 业务发生时间
     */
    public void setBusinesstime(Date businesstime) {
        this.businesstime = businesstime;
    }

    /**
     * 获取事件类型
     *
     * @return eventtype - 事件类型
     */
    public String getEventtype() {
        return eventtype;
    }

    /**
     * 设置事件类型
     *
     * @param eventtype 事件类型
     */
    public void setEventtype(String eventtype) {
        this.eventtype = eventtype == null ? null : eventtype.trim();
    }

    /**
     * 获取操作名称
     *
     * @return eventname - 操作名称
     */
    public String getEventname() {
        return eventname;
    }

    /**
     * 设置操作名称
     *
     * @param eventname 操作名称
     */
    public void setEventname(String eventname) {
        this.eventname = eventname == null ? null : eventname.trim();
    }

    /**
     * 获取指令代码
     *
     * @return command - 指令代码
     */
    public String getCommand() {
        return command;
    }

    /**
     * 设置指令代码
     *
     * @param command 指令代码
     */
    public void setCommand(String command) {
        this.command = command == null ? null : command.trim();
    }

    /**
     * 获取执行结果(1成功 0失败)
     *
     * @return status - 执行结果(1成功 0失败)
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置执行结果(1成功 0失败)
     *
     * @param status 执行结果(1成功 0失败)
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取调用方法名
     *
     * @return methodname - 调用方法名
     */
    public String getMethodname() {
        return methodname;
    }

    /**
     * 设置调用方法名
     *
     * @param methodname 调用方法名
     */
    public void setMethodname(String methodname) {
        this.methodname = methodname == null ? null : methodname.trim();
    }

    /**
     * 获取调用类名
     *
     * @return classname - 调用类名
     */
    public String getClassname() {
        return classname;
    }

    /**
     * 设置调用类名
     *
     * @param classname 调用类名
     */
    public void setClassname(String classname) {
        this.classname = classname == null ? null : classname.trim();
    }

    /**
     * 获取ip地址
     *
     * @return ip - ip地址
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置ip地址
     *
     * @param ip ip地址
     */
    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    /**
     * 获取设备来源
     *
     * @return source - 设备来源
     */
    public String getSource() {
        return source;
    }

    /**
     * 设置设备来源
     *
     * @param source 设备来源
     */
    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
    }

    /**
     * 获取业务是否有效标识,0-有效，1-无效
     *
     * @return state - 业务是否有效标识,0-有效，1-无效
     */
    public Integer getState() {
        return state;
    }

    /**
     * 设置业务是否有效标识,0-有效，1-无效
     *
     * @param state 业务是否有效标识,0-有效，1-无效
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 获取操作内容
     *
     * @return content - 操作内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置操作内容
     *
     * @param content 操作内容
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    /**
     * 获取操作结果
     *
     * @return result - 操作结果
     */
    public String getResult() {
        return result;
    }

    /**
     * 设置操作结果
     *
     * @param result 操作结果
     */
    public void setResult(String result) {
        this.result = result == null ? null : result.trim();
    }

    /**
     * 获取调用参数
     *
     * @return methodarg - 调用参数
     */
    public String getMethodarg() {
        return methodarg;
    }

    /**
     * 设置调用参数
     *
     * @param methodarg 调用参数
     */
    public void setMethodarg(String methodarg) {
        this.methodarg = methodarg == null ? null : methodarg.trim();
    }
}