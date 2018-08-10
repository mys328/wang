package com.thinkwin.common.model.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "`yuncm_room_reserve_conf`")
public class YuncmRoomReserveConf implements Serializable{
    private static final long serialVersionUID = 5017916585183550515L;
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 可预订时间开始
     */
    @Column(name = "`reserve_time_start`")
    private Date reserveTimeStart;

    /**
     * 可预订时间结束
     */
    @Column(name = "`reserve_time_end`")
    private Date reserveTimeEnd;

    /**
     * 单次会议最大时长
     */
    @Column(name = "`meeting_maximum`")
    private Integer meetingMaximum;

    /**
     * 单次会议最小时长
     */
    @Column(name = "`meeting_minimum`")
    private Integer meetingMinimum;

    /**
     * 会议室可预订周期
     */
    @Column(name = "`reserve_cycle`")
    private Integer reserveCycle;

    /**
     * 会议可提前开始时间
     */
    @Column(name = "`may_start_early`")
    private Integer mayStartEarly;

    /**
     * 会议释放设置
     */
    @Column(name = "`release_settings`")
    private String releaseSettings;

    /**
     * 二维码有效时长
     */
    @Column(name = "`qr_duration`")
    private Integer qrDuration;

    /**
     * 二维码有效时长
     */
    @Column(name = "`sign_set`")
    private String signSet;

    /**
     * 创建人ID
     */
    @Column(name = "`creater_id`")
    private String createrId;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`")
    private Date createTime;

    /**
     * 修改人
     */
    @Column(name = "`modifyer_id`")
    private String modifyerId;

    /**
     * 修改时间
     */
    @Column(name = "`modify_time`")
    private Date modifyTime;

    /**
     * 记录状态:1:已删除；0:未删除
     */
    @Column(name = "`delete_state`")
    private String deleteState;

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
     * 获取可预订时间开始
     *
     * @return reserve_time_start - 可预订时间开始
     */
    public Date getReserveTimeStart() {
        return reserveTimeStart;
    }

    /**
     * 设置可预订时间开始
     *
     * @param reserveTimeStart 可预订时间开始
     */
    public void setReserveTimeStart(Date reserveTimeStart) {
        this.reserveTimeStart = reserveTimeStart;
    }

    /**
     * 获取可预订时间结束
     *
     * @return reserve_time_end - 可预订时间结束
     */
    public Date getReserveTimeEnd() {
        return reserveTimeEnd;
    }

    /**
     * 设置可预订时间结束
     *
     * @param reserveTimeEnd 可预订时间结束
     */
    public void setReserveTimeEnd(Date reserveTimeEnd) {
        this.reserveTimeEnd = reserveTimeEnd;
    }

    /**
     * 获取单次会议最大时长
     *
     * @return meeting_maximum - 单次会议最大时长
     */
    public Integer getMeetingMaximum() {
        return meetingMaximum;
    }

    /**
     * 设置单次会议最大时长
     *
     * @param meetingMaximum 单次会议最大时长
     */
    public void setMeetingMaximum(Integer meetingMaximum) {
        this.meetingMaximum = meetingMaximum;
    }

    /**
     * 获取单次会议最小时长
     *
     * @return meeting_minimum - 单次会议最小时长
     */
    public Integer getMeetingMinimum() {
        return meetingMinimum;
    }

    /**
     * 设置单次会议最小时长
     *
     * @param meetingMinimum 单次会议最小时长
     */
    public void setMeetingMinimum(Integer meetingMinimum) {
        this.meetingMinimum = meetingMinimum;
    }

    /**
     * 获取会议室可预订周期
     *
     * @return reserve_cycle - 会议室可预订周期
     */
    public Integer getReserveCycle() {
        return reserveCycle;
    }

    /**
     * 设置会议室可预订周期
     *
     * @param reserveCycle 会议室可预订周期
     */
    public void setReserveCycle(Integer reserveCycle) {
        this.reserveCycle = reserveCycle;
    }

    /**
     * 获取会议可提前开始时间
     *
     * @return may_start_early - 会议可提前开始时间
     */
    public Integer getMayStartEarly() {
        return mayStartEarly;
    }

    /**
     * 设置会议可提前开始时间
     *
     * @param mayStartEarly 会议可提前开始时间
     */
    public void setMayStartEarly(Integer mayStartEarly) {
        this.mayStartEarly = mayStartEarly;
    }

    /**
     * 获取会议释放设置
     *
     * @return release_settings - 会议释放设置
     */
    public String getReleaseSettings() {
        return releaseSettings;
    }

    /**
     * 设置会议释放设置
     *
     * @param releaseSettings 会议释放设置
     */
    public void setReleaseSettings(String releaseSettings) {
        this.releaseSettings = releaseSettings == null ? null : releaseSettings.trim();
    }

    public Integer getQrDuration() {
        return qrDuration;
    }

    public void setQrDuration(Integer qrDuration) {
        this.qrDuration = qrDuration;
    }

    public String getSignSet() {
        return signSet;
    }

    public void setSignSet(String signSet) {
        this.signSet = signSet;
    }

    /**
     * 获取创建人ID
     *
     * @return creater_id - 创建人ID
     */
    public String getCreaterId() {
        return createrId;
    }

    /**
     * 设置创建人ID
     *
     * @param createrId 创建人ID
     */
    public void setCreaterId(String createrId) {
        this.createrId = createrId == null ? null : createrId.trim();
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取修改人
     *
     * @return modifyer_id - 修改人
     */
    public String getModifyerId() {
        return modifyerId;
    }

    /**
     * 设置修改人
     *
     * @param modifyerId 修改人
     */
    public void setModifyerId(String modifyerId) {
        this.modifyerId = modifyerId == null ? null : modifyerId.trim();
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
     * 获取记录状态:1:已删除；0:未删除
     *
     * @return delete_state - 记录状态:1:已删除；0:未删除
     */
    public String getDeleteState() {
        return deleteState;
    }

    /**
     * 设置记录状态:1:已删除；0:未删除
     *
     * @param deleteState 记录状态:1:已删除；0:未删除
     */
    public void setDeleteState(String deleteState) {
        this.deleteState = deleteState == null ? null : deleteState.trim();
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
}