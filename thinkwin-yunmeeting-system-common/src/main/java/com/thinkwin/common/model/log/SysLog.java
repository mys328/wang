package com.thinkwin.common.model.log;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Table(name = "`sys_log`")
public class SysLog implements Serializable {

    private static final long serialVersionUID = 1925058811633879656L;
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 租户Id
     */
    @Column(name = "`tenant_id`")
    private String tenantId;

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
     * 操作时间
     */
    @Column(name = "`operatedate`")
    private String operatedate;

    /**
     * 业务类型
     */
    @Column(name = "`businesstype`")
    private String businesstype;

    /**
     * 业务id
     */
    @Column(name = "`businessid`")
    private String businessid;

    /**
     * 事件类型
     */
    @Column(name = "`eventtype`")
    private String eventtype;

    /**
     * 业务是否有效标识,1-有效，0-无效
     */
    @Column(name = "`state`")
    private Integer state;

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

    @Column(name = "`operate_user_id`")
    private String operateUserId;

    @Column(name = "`ip`")
    private String ip;

    /**
     * 业务名称
     */
    @Column(name = "`businessname`")
    private String businessname;

    /**
     * 操作名称
     */
    @Column(name = "`eventname`")
    private String eventname;

    /**
     * 设备来源
     */
    @Column(name = "`source`")
    private String source;

    /**
     * 预留字段
     */
    @Column(name = "`reserve_1`")
    private String reserve1;

    @Column(name = "`reserve_2`")
    private String reserve2;

    @Column(name = "`reserve_3`")
    private String reserve3;

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

    @Transient
    private List<String> businesstypes;

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
        this.operatedate = operatedate;
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
     * 获取业务是否有效标识,1-有效，0-无效
     *
     * @return state - 业务是否有效标识,1-有效，0-无效
     */
    public Integer getState() {
        return state;
    }

    /**
     * 设置业务是否有效标识,1-有效，0-无效
     *
     * @param state 业务是否有效标识,1-有效，0-无效
     */
    public void setState(Integer state) {
        this.state = state;
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
     * @return operate_user_id
     */
    public String getOperateUserId() {
        return operateUserId;
    }

    /**
     * @param operateUserId
     */
    public void setOperateUserId(String operateUserId) {
        this.operateUserId = operateUserId == null ? null : operateUserId.trim();
    }

    /**
     * @return ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
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
     * 获取预留字段
     *
     * @return reserve_1 - 预留字段
     */
    public String getReserve1() {
        return reserve1;
    }

    /**
     * 设置预留字段
     *
     * @param reserve1 预留字段
     */
    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1 == null ? null : reserve1.trim();
    }

    /**
     * @return reserve_2
     */
    public String getReserve2() {
        return reserve2;
    }

    /**
     * @param reserve2
     */
    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2 == null ? null : reserve2.trim();
    }

    /**
     * @return reserve_3
     */
    public String getReserve3() {
        return reserve3;
    }

    /**
     * @param reserve3
     */
    public void setReserve3(String reserve3) {
        this.reserve3 = reserve3 == null ? null : reserve3.trim();
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

    public List<String> getBusinesstypes() {
        return businesstypes;
    }

    public void setBusinesstypes(List<String> businesstypes) {
        this.businesstypes = businesstypes;
    }
}