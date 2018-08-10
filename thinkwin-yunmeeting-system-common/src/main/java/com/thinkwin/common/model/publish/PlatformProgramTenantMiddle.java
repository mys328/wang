package com.thinkwin.common.model.publish;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`platform_program_tenant_middle`")
public class PlatformProgramTenantMiddle implements Serializable {
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`tenant_id`")
    private String tenantId;

    @Column(name = "`program_id`")
    private String programId;

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
     * @return program_id
     */
    public String getProgramId() {
        return programId;
    }

    /**
     * @param programId
     */
    public void setProgramId(String programId) {
        this.programId = programId == null ? null : programId.trim();
    }
}