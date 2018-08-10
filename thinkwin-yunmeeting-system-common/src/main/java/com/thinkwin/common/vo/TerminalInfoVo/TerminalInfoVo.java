package com.thinkwin.common.vo.TerminalInfoVo;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 类名: TerminalInfoVo </br>
 * 描述:</br>
 * 开发人员： weining </br>
 * 创建时间：  2018/5/3 </br>
 */
public class TerminalInfoVo implements Serializable{

    private static final long serialVersionUID = 7251464899645961077L;
    /**
     * 终端硬件标识
     */
    private String hardwareId;

    /*
    * 租户Id
    */
    private String tenantId;

    /**
     * 终端名称
     */
    private String terminalName;

    /**
     * 终端类型 ，数据来自于数据字典
     */
    private String terminalTypeId;

    /**
     * 设备初始OS, 数据来自于数据字典
     */
    private String terminalOs;

    /**
     * 终端初始OS版本 设备初始OS版本 数据来自数据字典
     */
    private String initOsVer;

    /**
     * 终端当前OS版本
     */
    private String currOsVer;

    /**
     * 初始信发客户端版本
     */
    private String initClientVer;

    /**
     * 当前信发客户端版本
     */
    private String currClientVer;

    /**
     * 分辨率
     */
    private String resolutionRatio;

    /**
     * 终端亮度
     */
    private Integer terminalBrightness;

    /**
     * 终端音量
     */
    private Integer terminalVolume;

    /**
     * 状态: 在线:1;离线:0
     */
    private String status;

    /*
    * 上级市名称
    * */
    private String city;
    //省
    private String province;
    //县
    private String county;
    //街道
    private String street;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHardwareId() {
        return hardwareId;
    }

    public void setHardwareId(String hardwareId) {
        this.hardwareId = hardwareId;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getTerminalTypeId() {
        return terminalTypeId;
    }

    public void setTerminalTypeId(String terminalTypeId) {
        this.terminalTypeId = terminalTypeId;
    }

    public String getTerminalOs() {
        return terminalOs;
    }

    public void setTerminalOs(String terminalOs) {
        this.terminalOs = terminalOs;
    }

    public String getInitOsVer() {
        return initOsVer;
    }

    public void setInitOsVer(String initOsVer) {
        this.initOsVer = initOsVer;
    }

    public String getCurrOsVer() {
        return currOsVer;
    }

    public void setCurrOsVer(String currOsVer) {
        this.currOsVer = currOsVer;
    }

    public String getInitClientVer() {
        return initClientVer;
    }

    public void setInitClientVer(String initClientVer) {
        this.initClientVer = initClientVer;
    }

    public String getCurrClientVer() {
        return currClientVer;
    }

    public void setCurrClientVer(String currClientVer) {
        this.currClientVer = currClientVer;
    }

    public String getResolutionRatio() {
        return resolutionRatio;
    }

    public void setResolutionRatio(String resolutionRatio) {
        this.resolutionRatio = resolutionRatio;
    }

    public Integer getTerminalBrightness() {
        return terminalBrightness;
    }

    public void setTerminalBrightness(Integer terminalBrightness) {
        this.terminalBrightness = terminalBrightness;
    }

    public Integer getTerminalVolume() {
        return terminalVolume;
    }

    public void setTerminalVolume(Integer terminalVolume) {
        this.terminalVolume = terminalVolume;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
