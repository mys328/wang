package com.thinkwin.common.model.db;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`info_label_program_middle`")
public class InfoLabelProgramMiddle implements Serializable {
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`program_label_id`")
    private String programLabelId;

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
     * @return program_label_id
     */
    public String getProgramLabelId() {
        return programLabelId;
    }

    /**
     * @param programLabelId
     */
    public void setProgramLabelId(String programLabelId) {
        this.programLabelId = programLabelId == null ? null : programLabelId.trim();
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