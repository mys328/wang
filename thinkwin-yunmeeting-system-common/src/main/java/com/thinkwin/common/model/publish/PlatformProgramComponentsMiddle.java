package com.thinkwin.common.model.publish;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`platform_program_components_middle`")
public class PlatformProgramComponentsMiddle implements Serializable {
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`program_id`")
    private String programId;

    @Column(name = "`components_id`")
    private String componentsId;

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

    /**
     * @return components_id
     */
    public String getComponentsId() {
        return componentsId;
    }

    /**
     * @param componentsId
     */
    public void setComponentsId(String componentsId) {
        this.componentsId = componentsId == null ? null : componentsId.trim();
    }
}