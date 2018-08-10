package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`yunmeeting_conference_user_info`")
public class YunmeetingConferenceUserInfo implements Serializable {
    private static final long serialVersionUID = 3110796945908757764L;
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 参会信息表id
     */
    @Column(name = "`participants_info_id`")
    private String participantsInfoId;

    /**
     * 参会人Id
     */
    @Column(name = "`participants_id`")
    private String participantsId;

    /**
     * 参会人姓名
     */
    @Column(name = "`participants_name`")
    private String participantsName;

    /**
     * 用户名拼音
     */
    @Column(name = "`participants_name_pinyin`")
    private String participantsNamePinyin;

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
     * 获取参会信息表id
     *
     * @return participants_info_id - 参会信息表id
     */
    public String getParticipantsInfoId() {
        return participantsInfoId;
    }

    /**
     * 设置参会信息表id
     *
     * @param participantsInfoId 参会信息表id
     */
    public void setParticipantsInfoId(String participantsInfoId) {
        this.participantsInfoId = participantsInfoId == null ? null : participantsInfoId.trim();
    }

    /**
     * 获取参会人Id
     *
     * @return participants_id - 参会人Id
     */
    public String getParticipantsId() {
        return participantsId;
    }

    /**
     * 设置参会人Id
     *
     * @param participantsId 参会人Id
     */
    public void setParticipantsId(String participantsId) {
        this.participantsId = participantsId == null ? null : participantsId.trim();
    }

    /**
     * 获取参会人姓名
     *
     * @return participants_name - 参会人姓名
     */
    public String getParticipantsName() {
        return participantsName;
    }

    /**
     * 设置参会人姓名
     *
     * @param participantsName 参会人姓名
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