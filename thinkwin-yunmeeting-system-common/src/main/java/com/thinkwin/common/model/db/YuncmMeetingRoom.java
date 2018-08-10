package com.thinkwin.common.model.db;

import com.thinkwin.common.vo.meetingVo.MeetingRoomVo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Table(name = "`yuncm_meeting_room`")
public class YuncmMeetingRoom implements Serializable{
    private static final long serialVersionUID = -6967106274101460301L;
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 会议室名称
     */
    @Column(name = "`name`")
    private String name;

    /**
     * 可容纳人数
     */
    @Column(name = "`persion_number`")
    private Integer persionNumber;

    /**
     * 会议室位置
     */
    @Column(name = "`location`")
    private String location;

    /**
     * 会议室图片
     */
    @Column(name = "`image_url`")
    private String imageUrl;

    /**
     * 会议室二维码URL
     */
    @Column(name = "`qr_code_url`")
    private String qrCodeUrl;

    /**
     * 区域ID
     */
    @Column(name = "`area_id`")
    private String areaId;

    /**
     * 排序
     */
    @Column(name = "`sort`")
    private Integer sort;

    /**
     * 管理员ID
     */
    @Column(name = "`admin_id`")
    private String adminId;

    /**
     * 0:无须审核 ；1:需要审核
     */
    @Column(name = "`is_audit`")
    private String isAudit;

    /**
     * 会议开始日期
     */
    @Column(name = "`start_time`")
    private Date startTime;

    /**
     * 会议结束日期
     */
    @Column(name = "`end_time`")
    private Date endTime;

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
     * 操作原因
     */
    @Column(name = "`oper_reason`")
    private String operReason;

    /**
     * 会议室状态:不能为空1－永久关闭;2－正常开启;;3－临时关闭
     */
    @Column(name = "`state`")
    private String state;

    /**
     * 记录状态:1:已删除；0:未删除
     */
    @Column(name = "`delete_state`")
    private String deleteState;

    /**
     * 全局排序用于全部区域排序
     */
    @Column(name = "`global_sort`")
    private Integer globalSort;

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
     * 返回会议室设备
     */
    @Transient
    private String deviceService;

    /**
     * 返回会议室设备
     */
    @Transient
    private List<String> deviceServices;

    /**
     * 返回会议室预定权限
     */
    @Transient
    private String reservationStatus;

    /**
     * 当前会议室是否被占用
     */
    @Transient
    private String isOccupy;
    /**
     * 占用信息
     */
    @Transient
    private List<MeetingRoomVo> roomVos;

    /**
     * 会议室图片大图
     */
    @Transient
    private String bigPicture;
    /**
     *会议室图片中图
     */
    @Transient
    private String inPicture;
    /**
     * 会议室图片小图
     */
    @Transient
    private String smallPicture;


    public String getBigPicture() {
        return bigPicture;
    }

    public void setBigPicture(String bigPicture) {
        this.bigPicture = bigPicture;
    }

    public String getInPicture() {
        return inPicture;
    }

    public void setInPicture(String inPicture) {
        this.inPicture = inPicture;
    }

    public String getSmallPicture() {
        return smallPicture;
    }

    public void setSmallPicture(String smallPicture) {
        this.smallPicture = smallPicture;
    }

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
     * 获取会议室名称
     *
     * @return name - 会议室名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置会议室名称
     *
     * @param name 会议室名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取可容纳人数
     *
     * @return persion_number - 可容纳人数
     */
    public Integer getPersionNumber() {
        return persionNumber;
    }

    /**
     * 设置可容纳人数
     *
     * @param persionNumber 可容纳人数
     */
    public void setPersionNumber(Integer persionNumber) {
        this.persionNumber = persionNumber;
    }

    /**
     * 获取会议室位置
     *
     * @return location - 会议室位置
     */
    public String getLocation() {
        return location;
    }

    /**
     * 设置会议室位置
     *
     * @param location 会议室位置
     */
    public void setLocation(String location) {
        this.location = location == null ? null : location.trim();
    }

    /**
     * 获取会议室图片
     *
     * @return image_url - 会议室图片
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 设置会议室图片
     *
     * @param imageUrl 会议室图片
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl == null ? null : imageUrl.trim();
    }

    /**
     * 获取会议室二维码URL
     *
     * @return qr_code_url - 会议室二维码URL
     */
    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    /**
     * 设置会议室二维码URL
     *
     * @param qrCodeUrl 会议室二维码URL
     */
    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl == null ? null : qrCodeUrl.trim();
    }

    /**
     * 获取区域ID
     *
     * @return area_id - 区域ID
     */
    public String getAreaId() {
        return areaId;
    }

    /**
     * 设置区域ID
     *
     * @param areaId 区域ID
     */
    public void setAreaId(String areaId) {
        this.areaId = areaId == null ? null : areaId.trim();
    }

    /**
     * 获取排序
     *
     * @return sort - 排序
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 设置排序
     *
     * @param sort 排序
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 获取管理员ID
     *
     * @return admin_id - 管理员ID
     */
    public String getAdminId() {
        return adminId;
    }

    /**
     * 设置管理员ID
     *
     * @param adminId 管理员ID
     */
    public void setAdminId(String adminId) {
        this.adminId = adminId == null ? null : adminId.trim();
    }

    /**
     * 获取0:无须审核 ；1:需要审核
     *
     * @return is_audit - 0:无须审核 ；1:需要审核
     */
    public String getIsAudit() {
        return isAudit;
    }

    /**
     * 设置0:无须审核 ；1:需要审核
     *
     * @param isAudit 0:无须审核 ；1:需要审核
     */
    public void setIsAudit(String isAudit) {
        this.isAudit = isAudit == null ? null : isAudit.trim();
    }

    /**
     * 获取会议开始日期
     *
     * @return start_time - 会议开始日期
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * 设置会议开始日期
     *
     * @param startTime 会议开始日期
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取会议结束日期
     *
     * @return end_time - 会议结束日期
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 设置会议结束日期
     *
     * @param endTime 会议结束日期
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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
     * 获取操作原因
     *
     * @return oper_reason - 操作原因
     */
    public String getOperReason() {
        return operReason;
    }

    /**
     * 设置操作原因
     *
     * @param operReason 操作原因
     */
    public void setOperReason(String operReason) {
        this.operReason = operReason == null ? null : operReason.trim();
    }

    /**
     * 获取会议室状态:不能为空1－永久关闭;2－正常开启;;3－临时关闭
     *
     * @return state - 会议室状态:不能为空1－永久关闭;2－正常开启;;3－临时关闭
     */
    public String getState() {
        return state;
    }

    /**
     * 设置会议室状态:不能为空1－永久关闭;2－正常开启;;3－临时关闭
     *
     * @param state 会议室状态:不能为空1－永久关闭;2－正常开启;;3－临时关闭
     */
    public void setState(String state) {
        this.state = state == null ? null : state.trim();
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
     * 获取全局排序用于全部区域排序
     *
     * @return global_sort - 全局排序用于全部区域排序
     */
    public Integer getGlobalSort() {
        return globalSort;
    }

    /**
     * 设置全局排序用于全部区域排序
     *
     * @param globalSort 全局排序用于全部区域排序
     */
    public void setGlobalSort(Integer globalSort) {
        this.globalSort = globalSort;
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

    public String getDeviceService() {
        return deviceService;
    }

    public void setDeviceService(String deviceService) {
        this.deviceService = deviceService;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public List<String> getDeviceServices() {
        return deviceServices;
    }

    public void setDeviceServices(List<String> deviceServices) {
        this.deviceServices = deviceServices;
    }

    public String getIsOccupy() {
        return isOccupy;
    }

    public void setIsOccupy(String isOccupy) {
        this.isOccupy = isOccupy;
    }

    public List<MeetingRoomVo> getRoomVos() {
        return roomVos;
    }

    public void setRoomVos(List<MeetingRoomVo> roomVos) {
        this.roomVos = roomVos;
    }
}