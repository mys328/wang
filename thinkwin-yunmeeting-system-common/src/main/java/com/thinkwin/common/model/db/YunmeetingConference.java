package com.thinkwin.common.model.db;

import com.thinkwin.common.vo.meetingVo.PersonsVo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Table(name = "`yunmeeting_conference`")
public class YunmeetingConference implements Serializable{

    private static final long serialVersionUID = -4816497216877149646L;
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**

     * 会议名称
     */
    @Column(name = "`conference_name`")
    private String conferenceName;

    /**
     * 会议来源  0－WEB端；1－微信端；2－APP端；3－信息发布屏
     */
    @Column(name = "`client_type`")
    private String clientType;

    /**
     * 预订人Id
     */
    @Column(name = "`reservation_person_id`")
    private String reservationPersonId;

    /**
     * 组织者id
     */
    @Column(name = "`organizer_id`")
    private String organizerId;

    /**
     * 预计开始时间
     */
    @Column(name = "`take_start_date`")
    private Date takeStartDate;

    /**
     * 预计结束时间
     */
    @Column(name = "`take_end_date`")
    private Date takeEndDate;

    /**
     * 会议创建时间
     */
    @Column(name = "`confrerence_create_time`")
    private Date confrerenceCreateTime;

    /**
     * 会议取消时间
     */
    @Column(name = "`confrerence_cancel_time`")
    private Date confrerenceCancelTime;

    /**
     * 实际开始时间
     */
    @Column(name = "`act_start_time`")
    private Date actStartTime;

    /**
     * 实际结束时间
     */
    @Column(name = "`act_end_time`")
    private Date actEndTime;

    /**
     * 预约成功时间
     */
    @Column(name = "`reservation_success_time`")
    private Date reservationSuccessTime;

    /**
     * 是否公开  0:不公开；1:公开
     */
    @Column(name = "`is_public`")
    private String isPublic;

    /**
     * 是否需要审核  0:不需要审核；1:需要审核
     */
    @Column(name = "`is_audit`")
    private String isAudit;

    /**
     * 主办单位
     */
    @Column(name = "`host_unit`")
    private String hostUnit;

    /**
     * 排序
     */
    @Column(name = "`sort`")
    private Integer sort;

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
     * 修改时间
     */
    @Column(name = "`modify_reason`")
    private String modifyReason;

    /**
     * 参会回复状态:1审核通过；0:审核未通过；2：审核中
     */
    @Column(name = "`state`")
    private String state;

    /**
     * 记录状态:1:已删除；0:未删除
     */
    @Column(name = "`delete_state`")
    private String deleteState;

    /**
     * 取消状态：1:已取消；0:未取消
     */
    @Column(name = "`cancel_state`")
    private String cancelState;

    /**
     * 取消原因
     */
    @Column(name = "`cancel_reason`")
    private String cancelReason;

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
     * 会议内容
     */
    @Column(name = "`conterence_content`")
    private String conterenceContent;

    /**
     * 扩展数据
     */
    @Column(name = "`ext_data`")
    private String extData;

    /**
     * 会议创建者姓名
     */
    @Transient
    private String userName;
    /**
     * 会议室地址
     */
    @Transient
    private String address;
    /**
     * 参会人员
     */
    @Transient
    private List<PersonsVo> personsVos;

    /**
     * 参会人员数量
     */
    @Transient
    private String count;

    /**
     * 是否是我组织的 1是；0不是：
     */
    @Transient
    private String isOrg;

    /**
     * 审核未通过原因
     */
    @Transient
    private String auditWhy;

    /**
     * 创建者头像
     */
    @Transient
    private String userNameUrl;

    /**
     * 创建者头像大图
     */
    @Transient
    private String userNameBigPicture;
    /**
     * 创建者头像中图
     */
    @Transient
    private String userNameInPicture;
    /**
     * 创建者头像小图
     */
    @Transient
    private String userNameSmallPicture;
    /**
     * 审核人姓名
     */
    @Transient
    private String examineName;

    /**
     * 审核人头像
     */
    @Transient
    private String examineNameUrl;

    /**
     * 审核人头像大图
     */
    @Transient
    private String examineNameBigPicture;
    /**
     * 审核人头像中图
     */
    @Transient
    private String examineNameInPicture;
    /**
     * 审核人头像小图
     */
    @Transient
    private String examineNameSmallPicture;

    /**
     * 审核时间
     */
    @Transient
    private Date examineTime;
    /**
     * 会议室id
     */
    @Transient
    private String roomId;
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
     * 获取会议名称
     *
     * @return conference_name - 会议名称
     */
    public String getConferenceName() {
        return conferenceName;
    }

    /**
     * 设置会议名称
     *
     * @param conferenceName 会议名称
     */
    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName == null ? null : conferenceName.trim();
    }

    /**
     * 获取会议来源  0－WEB端；1－微信端；2－APP端；3－信息发布屏
     *
     * @return client_type - 会议来源  0－WEB端；1－微信端；2－APP端；3－信息发布屏
     */
    public String getClientType() {
        return clientType;
    }

    /**
     * 设置会议来源  0－WEB端；1－微信端；2－APP端；3－信息发布屏
     *
     * @param clientType 会议来源  0－WEB端；1－微信端；2－APP端；3－信息发布屏
     */
    public void setClientType(String clientType) {
        this.clientType = clientType == null ? null : clientType.trim();
    }

    /**
     * 获取预订人Id
     *
     * @return reservation_person_id - 预订人Id
     */
    public String getReservationPersonId() {
        return reservationPersonId;
    }

    /**
     * 设置预订人Id
     *
     * @param reservationPersonId 预订人Id
     */
    public void setReservationPersonId(String reservationPersonId) {
        this.reservationPersonId = reservationPersonId == null ? null : reservationPersonId.trim();
    }

    /**
     * 获取组织者id
     *
     * @return organizer_id - 组织者id
     */
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * 设置组织者id
     *
     * @param organizerId 组织者id
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId == null ? null : organizerId.trim();
    }

    /**
     * 获取预计开始时间
     *
     * @return take_start_date - 预计开始时间
     */
    public Date getTakeStartDate() {
        return takeStartDate;
    }

    /**
     * 设置预计开始时间
     *
     * @param takeStartDate 预计开始时间
     */
    public void setTakeStartDate(Date takeStartDate) {
        this.takeStartDate = takeStartDate;
    }

    /**
     * 获取预计结束时间
     *
     * @return take_end_date - 预计结束时间
     */
    public Date getTakeEndDate() {
        return takeEndDate;
    }

    /**
     * 设置预计结束时间
     *
     * @param takeEndDate 预计结束时间
     */
    public void setTakeEndDate(Date takeEndDate) {
        this.takeEndDate = takeEndDate;
    }

    /**
     * 获取会议创建时间
     *
     * @return confrerence_create_time - 会议创建时间
     */
    public Date getConfrerenceCreateTime() {
        return confrerenceCreateTime;
    }

    /**
     * 设置会议创建时间
     *
     * @param confrerenceCreateTime 会议创建时间
     */
    public void setConfrerenceCreateTime(Date confrerenceCreateTime) {
        this.confrerenceCreateTime = confrerenceCreateTime;
    }

    /**
     * 获取会议取消时间
     *
     * @return confrerence_cancel_time - 会议取消时间
     */
    public Date getConfrerenceCancelTime() {
        return confrerenceCancelTime;
    }

    /**
     * 设置会议取消时间
     *
     * @param confrerenceCancelTime 会议取消时间
     */
    public void setConfrerenceCancelTime(Date confrerenceCancelTime) {
        this.confrerenceCancelTime = confrerenceCancelTime;
    }

    /**
     * 获取实际开始时间
     *
     * @return act_start_time - 实际开始时间
     */
    public Date getActStartTime() {
        return actStartTime;
    }

    /**
     * 设置实际开始时间
     *
     * @param actStartTime 实际开始时间
     */
    public void setActStartTime(Date actStartTime) {
        this.actStartTime = actStartTime;
    }

    /**
     * 获取实际结束时间
     *
     * @return act_end_time - 实际结束时间
     */
    public Date getActEndTime() {
        return actEndTime;
    }

    /**
     * 设置实际结束时间
     *
     * @param actEndTime 实际结束时间
     */
    public void setActEndTime(Date actEndTime) {
        this.actEndTime = actEndTime;
    }

    /**
     * 获取预约成功时间
     *
     * @return reservation_success_time - 预约成功时间
     */
    public Date getReservationSuccessTime() {
        return reservationSuccessTime;
    }

    /**
     * 设置预约成功时间
     *
     * @param reservationSuccessTime 预约成功时间
     */
    public void setReservationSuccessTime(Date reservationSuccessTime) {
        this.reservationSuccessTime = reservationSuccessTime;
    }

    /**
     * 获取是否公开  0:不公开；1:公开
     *
     * @return is_public - 是否公开  0:不公开；1:公开
     */
    public String getIsPublic() {
        return isPublic;
    }

    /**
     * 设置是否公开  0:不公开；1:公开
     *
     * @param isPublic 是否公开  0:不公开；1:公开
     */
    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic == null ? null : isPublic.trim();
    }

    /**
     * 获取是否需要审核  0:不需要审核；1:需要审核
     *
     * @return is_audit - 是否需要审核  0:不需要审核；1:需要审核
     */
    public String getIsAudit() {
        return isAudit;
    }

    /**
     * 设置是否需要审核  0:不需要审核；1:需要审核
     *
     * @param isAudit 是否需要审核  0:不需要审核；1:需要审核
     */
    public void setIsAudit(String isAudit) {
        this.isAudit = isAudit == null ? null : isAudit.trim();
    }

    /**
     * 获取主办单位
     *
     * @return host_unit - 主办单位
     */
    public String getHostUnit() {
        return hostUnit;
    }

    /**
     * 设置主办单位
     *
     * @param hostUnit 主办单位
     */
    public void setHostUnit(String hostUnit) {
        this.hostUnit = hostUnit == null ? null : hostUnit.trim();
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
     * @return modifyReason - 修改时间
     */
    public String getModifyReason() {
        return modifyReason;
    }

    /**
     * 设置修改时间
     *
     * @param modifyReason 修改时间
     */
    public void setModifyReason(String modifyReason) {
        this.modifyReason = modifyReason;
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
     * 获取参会回复状态:1审核通过；0:审核未通过
     *
     * @return state - 参会回复状态:1审核通过；0:审核未通过
     */
    public String getState() {
        return state;
    }

    /**
     * 设置参会回复状态:1审核通过；0:审核未通过
     *
     * @param state 参会回复状态:1审核通过；0:审核未通过
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
     * 获取取消状态：1:已取消；0:未取消
     *
     * @return cancel_state - 取消状态：1:已取消；0:未取消
     */
    public String getCancelState() {
        return cancelState;
    }

    /**
     * 设置取消状态：1:已取消；0:未取消
     *
     * @param cancelState 取消状态：1:已取消；0:未取消
     */
    public void setCancelState(String cancelState) {
        this.cancelState = cancelState == null ? null : cancelState.trim();
    }

    /**
     * 获取取消原因
     *
     * @return cancel_reason - 取消原因
     */
    public String getCancelReason() {
        return cancelReason;
    }

    /**
     * 设置取消原因
     *
     * @param cancelReason 取消原因
     */
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason == null ? null : cancelReason.trim();
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

    /**
     * 获取会议内容
     *
     * @return conterence_content - 会议内容
     */
    public String getConterenceContent() {
        return conterenceContent;
    }

    /**
     * 设置会议内容
     *
     * @param conterenceContent 会议内容
     */
    public void setConterenceContent(String conterenceContent) {
        this.conterenceContent = conterenceContent == null ? null : conterenceContent.trim();
    }

    /**
     * 获取扩展数据
     *
     * @return ext_data - 扩展数据
     */
    public String getExtData() {
        return extData;
    }

    /**
     * 设置扩展数据
     *
     * @param extData 扩展数据
     */
    public void setExtData(String extData) {
        this.extData = extData == null ? null : extData.trim();
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<PersonsVo> getPersonsVos() {
        return personsVos;
    }

    public void setPersonsVos(List<PersonsVo> personsVos) {
        this.personsVos = personsVos;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getIsOrg() {
        return isOrg;
    }

    public void setIsOrg(String isOrg) {
        this.isOrg = isOrg;
    }

    public String getAuditWhy() {
        return auditWhy;
    }

    public void setAuditWhy(String auditWhy) {
        this.auditWhy = auditWhy;
    }

    public String getUserNameUrl() {
        return userNameUrl;
    }

    public void setUserNameUrl(String userNameUrl) {
        this.userNameUrl = userNameUrl;
    }

    public String getExamineName() {
        return examineName;
    }

    public void setExamineName(String examineName) {
        this.examineName = examineName;
    }

    public String getExamineNameUrl() {
        return examineNameUrl;
    }

    public void setExamineNameUrl(String examineNameUrl) {
        this.examineNameUrl = examineNameUrl;
    }

    public Date getExamineTime() {
        return examineTime;
    }

    public void setExamineTime(Date examineTime) {
        this.examineTime = examineTime;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserNameBigPicture() {
        return userNameBigPicture;
    }

    public void setUserNameBigPicture(String userNameBigPicture) {
        this.userNameBigPicture = userNameBigPicture;
    }

    public String getUserNameInPicture() {
        return userNameInPicture;
    }

    public void setUserNameInPicture(String userNameInPicture) {
        this.userNameInPicture = userNameInPicture;
    }

    public String getUserNameSmallPicture() {
        return userNameSmallPicture;
    }

    public void setUserNameSmallPicture(String userNameSmallPicture) {
        this.userNameSmallPicture = userNameSmallPicture;
    }

    public String getExamineNameBigPicture() {
        return examineNameBigPicture;
    }

    public void setExamineNameBigPicture(String examineNameBigPicture) {
        this.examineNameBigPicture = examineNameBigPicture;
    }

    public String getExamineNameInPicture() {
        return examineNameInPicture;
    }

    public void setExamineNameInPicture(String examineNameInPicture) {
        this.examineNameInPicture = examineNameInPicture;
    }

    public String getExamineNameSmallPicture() {
        return examineNameSmallPicture;
    }

    public void setExamineNameSmallPicture(String examineNameSmallPicture) {
        this.examineNameSmallPicture = examineNameSmallPicture;
    }
}