package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`yunmeeting_participants_info`")
public class YunmeetingParticipantsInfo implements Serializable {
    private static final long serialVersionUID = 5939349791707134741L;
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 会议表Id
     */
    @Column(name = "`conference_id`")
    private String conferenceId;

    /**
     * 当参会记录为人时，记录人的组织机构ID;当参会为机构时，同参会ID;当参会为会议分组时，空
     */
    @Column(name = "`org_id`")
    private String orgId;

    /**
     * 如果是个人，则为user ID;如果是组织机构，则是组织机构ID;如果是会议分组，则是会议分组ID
     */
    @Column(name = "`participants_id`")
    private String participantsId;

    /**
     * 如果是人，保存姓名信息；如果是组织机构，保存组织机构名称；如果会议分组，则保存会议分组名称
     */
    @Column(name = "`participants_name`")
    private String participantsName;

    /**
     * 用户名拼音
     */
    @Column(name = "`participants_name_pinyin`")
    private String participantsNamePinyin;

    /**
     * 是否内部参会人 1:内部参会人；0:非内部参会人
     */
    @Column(name = "`is_inner`")
    private String isInner;

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
     * 参会类型j:0:普通参会人；1：组织机构;2:会议分组参会[本状态待设计]
     */
    @Column(name = "`type`")
    private String type;

    /**
     * 排序字段
     */
    @Column(name = "`sort`")
    private Long sort;

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
     * 获取会议表Id
     *
     * @return conference_id - 会议表Id
     */
    public String getConferenceId() {
        return conferenceId;
    }

    /**
     * 设置会议表Id
     *
     * @param conferenceId 会议表Id
     */
    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId == null ? null : conferenceId.trim();
    }

    /**
     * 获取当参会记录为人时，记录人的组织机构ID;当参会为机构时，同参会ID;当参会为会议分组时，空
     *
     * @return org_id - 当参会记录为人时，记录人的组织机构ID;当参会为机构时，同参会ID;当参会为会议分组时，空
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * 设置当参会记录为人时，记录人的组织机构ID;当参会为机构时，同参会ID;当参会为会议分组时，空
     *
     * @param orgId 当参会记录为人时，记录人的组织机构ID;当参会为机构时，同参会ID;当参会为会议分组时，空
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId == null ? null : orgId.trim();
    }

    /**
     * 获取如果是个人，则为user ID;如果是组织机构，则是组织机构ID;如果是会议分组，则是会议分组ID
     *
     * @return participants_id - 如果是个人，则为user ID;如果是组织机构，则是组织机构ID;如果是会议分组，则是会议分组ID
     */
    public String getParticipantsId() {
        return participantsId;
    }

    /**
     * 设置如果是个人，则为user ID;如果是组织机构，则是组织机构ID;如果是会议分组，则是会议分组ID
     *
     * @param participantsId 如果是个人，则为user ID;如果是组织机构，则是组织机构ID;如果是会议分组，则是会议分组ID
     */
    public void setParticipantsId(String participantsId) {
        this.participantsId = participantsId == null ? null : participantsId.trim();
    }

    /**
     * 获取如果是人，保存姓名信息；如果是组织机构，保存组织机构名称；如果会议分组，则保存会议分组名称
     *
     * @return participants_name - 如果是人，保存姓名信息；如果是组织机构，保存组织机构名称；如果会议分组，则保存会议分组名称
     */
    public String getParticipantsName() {
        return participantsName;
    }

    /**
     * 设置如果是人，保存姓名信息；如果是组织机构，保存组织机构名称；如果会议分组，则保存会议分组名称
     *
     * @param participantsName 如果是人，保存姓名信息；如果是组织机构，保存组织机构名称；如果会议分组，则保存会议分组名称
     */
    public void setParticipantsName(String participantsName) {
        this.participantsName = participantsName == null ? null : participantsName.trim();
    }

    /**
     * 获取用户名拼音
     *
     * @return participantsNamePinyin - 用户名拼音
     */
    public String getParticipantsNamePinyin() {
        return participantsNamePinyin;
    }

    /**
     * 设置用户名拼音
     *
     * @param participantsNamePinyin 用户名拼音
     */
    public void setParticipantsNamePinyin(String participantsNamePinyin) {
        this.participantsNamePinyin = participantsNamePinyin == null ? null : participantsNamePinyin.trim();
    }

    /**
     * 获取是否内部参会人 1:内部参会人；0:非内部参会人
     *
     * @return is_inner - 是否内部参会人 1:内部参会人；0:非内部参会人
     */
    public String getIsInner() {
        return isInner;
    }

    /**
     * 设置是否内部参会人 1:内部参会人；0:非内部参会人
     *
     * @param isInner 是否内部参会人 1:内部参会人；0:非内部参会人
     */
    public void setIsInner(String isInner) {
        this.isInner = isInner == null ? null : isInner.trim();
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
     * 获取参会类型j:0:普通参会人；1：组织机构;2:会议分组参会[本状态待设计]
     *
     * @return type - 参会类型j:0:普通参会人；1：组织机构;2:会议分组参会[本状态待设计]
     */
    public String getType() {
        return type;
    }

    /**
     * 设置参会类型j:0:普通参会人；1：组织机构;2:会议分组参会[本状态待设计]
     *
     * @param type 参会类型j:0:普通参会人；1：组织机构;2:会议分组参会[本状态待设计]
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }


    /**
     *获取排序字段
     * @return sort 排序字段
     */
    public Long getSort() {
        return sort;
    }

    /**
     * 设置排序字段
     *
     * @param sort 排序字段
     */
    public void setSort(Long sort) {
        this.sort = sort;
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