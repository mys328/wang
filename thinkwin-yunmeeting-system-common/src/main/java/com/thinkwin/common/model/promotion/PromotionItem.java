package com.thinkwin.common.model.promotion;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`promotion_item`")
public class PromotionItem implements Serializable{
    private static final long serialVersionUID = 5367988729891115198L;
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`rule_code`")
    private String ruleCode;

    @Column(name = "`promotion_code`")
    private String promotionCode;

    @Column(name = "`enabled`")
    private Integer enabled;

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
     * @return rule_code
     */
    public String getRuleCode() {
        return ruleCode;
    }

    /**
     * @param ruleCode
     */
    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode == null ? null : ruleCode.trim();
    }

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