package com.thinkwin.common.model.promotion;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`coupon_batch_limit`")
public class CouponBatchLimit implements Serializable{
    private static final long serialVersionUID = -7225537084016925363L;
    @Id
    @Column(name = "`batch_id`")
    private String batchId;

    @Column(name = "`tenant_id`")
    private String tenantId;

    @Column(name = "`max`")
    private Integer max;

    @Column(name = "`used_qty`")
    private Integer usedQty;

    /**
     * @return batch_id
     */
    public String getBatchId() {
        return batchId;
    }

    /**
     * @param batchId
     */
    public void setBatchId(String batchId) {
        this.batchId = batchId == null ? null : batchId.trim();
    }

    /**
     * @return tenant_id
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    /**
     * @return max
     */
    public Integer getMax() {
        return max;
    }

    /**
     * @param max
     */
    public void setMax(Integer max) {
        this.max = max;
    }

    /**
     * @return used_qty
     */
    public Integer getUsedQty() {
        return usedQty;
    }

    /**
     * @param usedQty
     */
    public void setUsedQty(Integer usedQty) {
        this.usedQty = usedQty;
    }
}