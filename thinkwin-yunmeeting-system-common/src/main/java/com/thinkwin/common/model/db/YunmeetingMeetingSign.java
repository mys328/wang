package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`yunmeeting_meeting_sign`")
public class YunmeetingMeetingSign implements Serializable{
    private static final long serialVersionUID = 3355864693782842165L;

    @Id
    @Column(name = "`ID`")
    private String id;

    @Column(name = "`confreren_id`")
    private String confrerenId;

    @Column(name = "`participants_id`")
    private String participantsId;

    @Column(name = "`sign_time`")
    private Date signTime;

    @Column(name = "`sign_source`")
    private String signSource;

    @Column(name = "`reserve_1`")
    private String reserve1;

    @Column(name = "`reserve_2`")
    private String reserve2;

    @Column(name = "`reserve_3`")
    private String reserve3;

    /**
     * @return ID
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
     * @return confreren_id
     */
    public String getConfrerenId() {
        return confrerenId;
    }

    /**
     * @param confrerenId
     */
    public void setConfrerenId(String confrerenId) {
        this.confrerenId = confrerenId == null ? null : confrerenId.trim();
    }

    /**
     * @return participants_id
     */
    public String getParticipantsId() {
        return participantsId;
    }

    /**
     * @param participantsId
     */
    public void setParticipantsId(String participantsId) {
        this.participantsId = participantsId == null ? null : participantsId.trim();
    }

    /**
     * @return sign_time
     */
    public Date getSignTime() {
        return signTime;
    }

    /**
     * @param signTime
     */
    public void setSignTime(Date signTime) {
        this.signTime = signTime;
    }

    public String getSignSource() {
        return signSource;
    }

    public void setSignSource(String signSource) {
        this.signSource = signSource;
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