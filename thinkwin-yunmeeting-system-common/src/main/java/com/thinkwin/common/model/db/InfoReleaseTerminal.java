package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`info_release_terminal`")
public class InfoReleaseTerminal implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 租户ID
     */
    @Column(name = "`tenant_id`")
    private String tenantId;

    /**
     * 会议室ID
     */
    @Column(name = "`meeting_room_id`")
    private String meetingRoomId;

    /**
     * 终端硬件标识
     */
    @Column(name = "`hardware_id`")
    private String hardwareId;

    /**
     * 终端名称
     */
    @Column(name = "`terminal_name`")
    private String terminalName;

    /**
     * 终端类型 ，数据来自于数据字典
     */
    @Column(name = "`terminal_type_id`")
    private String terminalTypeId;

    /**
     * 设备初始OS, 数据来自于数据字典
     */
    @Column(name = "`terminal_os`")
    private String terminalOs;

    /**
     * 终端初始OS版本 设备初始OS版本 数据来自数据字典
     */
    @Column(name = "`init_os_ver`")
    private String initOsVer;

    /**
     * 终端当前OS版本
     */
    @Column(name = "`curr_os_ver`")
    private String currOsVer;

    /**
     * 初始信发客户端版本
     */
    @Column(name = "`init_client_ver`")
    private String initClientVer;

    /**
     * 当前信发客户端版本
     */
    @Column(name = "`curr_client_ver`")
    private String currClientVer;

    /**
     * 分辨率
     */
    @Column(name = "`resolution_ratio`")
    private String resolutionRatio;

    /**
     * 背景图片url
     */
    @Column(name = "`background_url`")
    private String backgroundUrl;

    /**
     * 背景图片ID
     */
    @Column(name = "`background_id`")
    private String backgroundId;

    /**
     * 安全key公钥
     */
    @Column(name = "`public_key`")
    private String publicKey;

    /**
     * 安全key私钥
     */
    @Column(name = "`private_key`")
    private String privateKey;

    /**
     * 终端亮度
     */
    @Column(name = "`terminal_brightness`")
    private Integer terminalBrightness;

    /**
     * 终端音量
     */
    @Column(name = "`terminal_volume`")
    private Integer terminalVolume;

    /**
     * 状态: 在线:1;离线:0
     */
    @Column(name = "`status`")
    private String status;

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
     * 修改人ID
     */
    @Column(name = "`modifier`")
    private String modifier;

    /**
     * 修改时间
     */
    @Column(name = "`modify_time`")
    private Date modifyTime;

    /**
     * 乐观锁标志 
     */
    @Column(name = "`ver`")
    private Integer ver;

    /**
     * 预留字段1
     */
    @Column(name = "`reserve_1`")
    private String reserve1;

    /**
     * 预留字段3
     */
    @Column(name = "`reserve_3`")
    private String reserve3;

    /**
     * 预留字段2
     */
    @Column(name = "`reserve_2`")
    private String reserve2;

    /**
     * 省
     */
    @Column(name = "`province`")
    private String province;

    /**
     * 市
     */
    @Column(name = "`city`")
    private String city;

    /**
     * 县区
     */
    @Column(name = "`county`")
    private String county;

    /**
     * 街道
     */
    @Column(name = "`street`")
    private String street;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取租户ID
     *
     * @return tenant_id - 租户ID
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * 设置租户ID
     *
     * @param tenantId 租户ID
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    /**
     * 获取会议室ID
     *
     * @return meeting_room_id - 会议室ID
     */
    public String getMeetingRoomId() {
        return meetingRoomId;
    }

    /**
     * 设置会议室ID
     *
     * @param meetingRoomId 会议室ID
     */
    public void setMeetingRoomId(String meetingRoomId) {
        this.meetingRoomId = meetingRoomId == null ? null : meetingRoomId.trim();
    }

    /**
     * 获取终端硬件标识
     *
     * @return hardware_id - 终端硬件标识
     */
    public String getHardwareId() {
        return hardwareId;
    }

    /**
     * 设置终端硬件标识
     *
     * @param hardwareId 终端硬件标识
     */
    public void setHardwareId(String hardwareId) {
        this.hardwareId = hardwareId == null ? null : hardwareId.trim();
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
     * 获取终端类型 ，数据来自于数据字典
     *
     * @return terminal_type_id - 终端类型 ，数据来自于数据字典
     */
    public String getTerminalTypeId() {
        return terminalTypeId;
    }

    /**
     * 设置终端类型 ，数据来自于数据字典
     *
     * @param terminalTypeId 终端类型 ，数据来自于数据字典
     */
    public void setTerminalTypeId(String terminalTypeId) {
        this.terminalTypeId = terminalTypeId == null ? null : terminalTypeId.trim();
    }

    /**
     * 获取设备初始OS, 数据来自于数据字典
     *
     * @return terminal_os - 设备初始OS, 数据来自于数据字典
     */
    public String getTerminalOs() {
        return terminalOs;
    }

    /**
     * 设置设备初始OS, 数据来自于数据字典
     *
     * @param terminalOs 设备初始OS, 数据来自于数据字典
     */
    public void setTerminalOs(String terminalOs) {
        this.terminalOs = terminalOs == null ? null : terminalOs.trim();
    }

    /**
     * 获取终端初始OS版本 设备初始OS版本 数据来自数据字典
     *
     * @return init_os_ver - 终端初始OS版本 设备初始OS版本 数据来自数据字典
     */
    public String getInitOsVer() {
        return initOsVer;
    }

    /**
     * 设置终端初始OS版本 设备初始OS版本 数据来自数据字典
     *
     * @param initOsVer 终端初始OS版本 设备初始OS版本 数据来自数据字典
     */
    public void setInitOsVer(String initOsVer) {
        this.initOsVer = initOsVer == null ? null : initOsVer.trim();
    }

    /**
     * 获取终端当前OS版本
     *
     * @return curr_os_ver - 终端当前OS版本
     */
    public String getCurrOsVer() {
        return currOsVer;
    }

    /**
     * 设置终端当前OS版本
     *
     * @param currOsVer 终端当前OS版本
     */
    public void setCurrOsVer(String currOsVer) {
        this.currOsVer = currOsVer == null ? null : currOsVer.trim();
    }

    /**
     * 获取初始信发客户端版本
     *
     * @return init_client_ver - 初始信发客户端版本
     */
    public String getInitClientVer() {
        return initClientVer;
    }

    /**
     * 设置初始信发客户端版本
     *
     * @param initClientVer 初始信发客户端版本
     */
    public void setInitClientVer(String initClientVer) {
        this.initClientVer = initClientVer == null ? null : initClientVer.trim();
    }

    /**
     * 获取当前信发客户端版本
     *
     * @return curr_client_ver - 当前信发客户端版本
     */
    public String getCurrClientVer() {
        return currClientVer;
    }

    /**
     * 设置当前信发客户端版本
     *
     * @param currClientVer 当前信发客户端版本
     */
    public void setCurrClientVer(String currClientVer) {
        this.currClientVer = currClientVer == null ? null : currClientVer.trim();
    }

    /**
     * 获取分辨率
     *
     * @return resolution_ratio - 分辨率
     */
    public String getResolutionRatio() {
        return resolutionRatio;
    }

    /**
     * 设置分辨率
     *
     * @param resolutionRatio 分辨率
     */
    public void setResolutionRatio(String resolutionRatio) {
        this.resolutionRatio = resolutionRatio == null ? null : resolutionRatio.trim();
    }

    /**
     * 获取背景图片url
     *
     * @return background_url - 背景图片url
     */
    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    /**
     * 设置背景图片url
     *
     * @param backgroundUrl 背景图片url
     */
    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl == null ? null : backgroundUrl.trim();
    }

    /**
     * 获取背景图片ID
     *
     * @return background_id - 背景图片ID
     */
    public String getBackgroundId() {
        return backgroundId;
    }

    /**
     * 设置背景图片ID
     *
     * @param backgroundId 背景图片ID
     */
    public void setBackgroundId(String backgroundId) {
        this.backgroundId = backgroundId == null ? null : backgroundId.trim();
    }

    /**
     * 获取安全key公钥
     *
     * @return public_key - 安全key公钥
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * 设置安全key公钥
     *
     * @param publicKey 安全key公钥
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey == null ? null : publicKey.trim();
    }

    /**
     * 获取安全key私钥
     *
     * @return private_key - 安全key私钥
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * 设置安全key私钥
     *
     * @param privateKey 安全key私钥
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey == null ? null : privateKey.trim();
    }

    /**
     * 获取终端亮度
     *
     * @return terminal_brightness - 终端亮度
     */
    public Integer getTerminalBrightness() {
        return terminalBrightness;
    }

    /**
     * 设置终端亮度
     *
     * @param terminalBrightness 终端亮度
     */
    public void setTerminalBrightness(Integer terminalBrightness) {
        this.terminalBrightness = terminalBrightness;
    }

    /**
     * 获取终端音量
     *
     * @return terminal_volume - 终端音量
     */
    public Integer getTerminalVolume() {
        return terminalVolume;
    }

    /**
     * 设置终端音量
     *
     * @param terminalVolume 终端音量
     */
    public void setTerminalVolume(Integer terminalVolume) {
        this.terminalVolume = terminalVolume;
    }

    /**
     * 获取状态: 在线:1;离线:0
     *
     * @return status - 状态: 在线:1;离线:0
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态: 在线:1;离线:0
     *
     * @param status 状态: 在线:1;离线:0
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * 获取创建人ID
     *
     * @return creater - 创建人ID
     */
    public String getCreater() {
        return creater;
    }

    /**
     * 设置创建人ID
     *
     * @param creater 创建人ID
     */
    public void setCreater(String creater) {
        this.creater = creater == null ? null : creater.trim();
    }

    /**
     * 获取创建时间
     *
     * @return creat_time - 创建时间
     */
    public Date getCreatTime() {
        return creatTime;
    }

    /**
     * 设置创建时间
     *
     * @param creatTime 创建时间
     */
    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    /**
     * 获取修改人ID
     *
     * @return modifier - 修改人ID
     */
    public String getModifier() {
        return modifier;
    }

    /**
     * 设置修改人ID
     *
     * @param modifier 修改人ID
     */
    public void setModifier(String modifier) {
        this.modifier = modifier == null ? null : modifier.trim();
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
     * 获取乐观锁标志 
     *
     * @return ver - 乐观锁标志 
     */
    public Integer getVer() {
        return ver;
    }

    /**
     * 设置乐观锁标志 
     *
     * @param ver 乐观锁标志 
     */
    public void setVer(Integer ver) {
        this.ver = ver;
    }

    /**
     * 获取预留字段1
     *
     * @return reserve_1 - 预留字段1
     */
    public String getReserve1() {
        return reserve1;
    }

    /**
     * 设置预留字段1
     *
     * @param reserve1 预留字段1
     */
    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1 == null ? null : reserve1.trim();
    }

    /**
     * 获取预留字段3
     *
     * @return reserve_3 - 预留字段3
     */
    public String getReserve3() {
        return reserve3;
    }

    /**
     * 设置预留字段3
     *
     * @param reserve3 预留字段3
     */
    public void setReserve3(String reserve3) {
        this.reserve3 = reserve3 == null ? null : reserve3.trim();
    }

    /**
     * 获取预留字段2
     *
     * @return reserve_2 - 预留字段2
     */
    public String getReserve2() {
        return reserve2;
    }

    /**
     * 设置预留字段2
     *
     * @param reserve2 预留字段2
     */
    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2 == null ? null : reserve2.trim();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
}