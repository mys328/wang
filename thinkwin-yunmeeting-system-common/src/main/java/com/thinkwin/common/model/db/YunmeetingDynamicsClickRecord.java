package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`yunmeeting_dynamics_click_record`")
public class YunmeetingDynamicsClickRecord implements Serializable{
    @Column(name = "`id`")
    private String id;

    /**
     * 动态id
     */
    @Column(name = "`dynamics_id`")
    private String dynamicsId;

    /**
     * 动态点击人id
     */
    @Column(name = "`participants_id`")
    private String participantsId;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`reserve_1`")
    private String reserve1;

    @Column(name = "`reserve_2`")
    private String reserve2;

    @Column(name = "`reserve_3`")
    private String reserve3;

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
     * 获取动态id
     *
     * @return dynamics_id - 动态id
     */
    public String getDynamicsId() {
        return dynamicsId;
    }

    /**
     * 设置动态id
     *
     * @param dynamicsId 动态id
     */
    public void setDynamicsId(String dynamicsId) {
        this.dynamicsId = dynamicsId == null ? null : dynamicsId.trim();
    }

    /**
     * 获取动态点击人id
     *
     * @return participants_id - 动态点击人id
     */
    public String getParticipantsId() {
        return participantsId;
    }

    /**
     * 设置动态点击人id
     *
     * @param participantsId 动态点击人id
     */
    public void setParticipantsId(String participantsId) {
        this.participantsId = participantsId == null ? null : participantsId.trim();
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return reserve_1
     */
    public String getReserve1() {
        return reserve1;
    }

    /**
     * @param reserve1
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
}