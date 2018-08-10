package com.thinkwin.common.model.promotion;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`promotion_rule`")
public class PromotionRule implements Serializable{
    private static final long serialVersionUID = -24055628386460283L;
    @Id
    @Column(name = "`rule_code`")
    private String ruleCode;

    @Column(name = "`category`")
    private Integer category;

    @Column(name = "`accept_code`")
    private String acceptCode;

    @Column(name = "`except_code`")
    private String exceptCode;

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
     * @return category
     */
    public Integer getCategory() {
        return category;
    }

    /**
     * @param category
     */
    public void setCategory(Integer category) {
        this.category = category;
    }

    /**
     * @return accept_code
     */
    public String getAcceptCode() {
        return acceptCode;
    }

    /**
     * @param acceptCode
     */
    public void setAcceptCode(String acceptCode) {
        this.acceptCode = acceptCode == null ? null : acceptCode.trim();
    }

    /**
     * @return except_code
     */
    public String getExceptCode() {
        return exceptCode;
    }

    /**
     * @param exceptCode
     */
    public void setExceptCode(String exceptCode) {
        this.exceptCode = exceptCode == null ? null : exceptCode.trim();
    }
}