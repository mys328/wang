package com.thinkwin.common.model.promotion;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "`promotion`")
public class Promotion implements Serializable{
    private static final long serialVersionUID = 4086562100802378576L;
    @Id
    @Column(name = "`promotion_code`")
    private String promotionCode;

    @Column(name = "`name`")
    private String name;

    @Column(name = "`start_time`")
    private Date startTime;

    @Column(name = "`end_time`")
    private Date endTime;

    @Column(name = "`enabled`")
    private Integer enabled;

    /**
     * @return promotion_code
     */
    public String getPromotionCode() {
        return promotionCode;
    }

    /**
     * @param promotionCode
     */
    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode == null ? null : promotionCode.trim();
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * @return start_time
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return end_time
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return enabled
     */
    public Integer getEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     */
    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }
}