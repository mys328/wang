package com.thinkwin.common.model.db;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`info_release_terminal_middle`")
public class InfoReleaseTerminalMiddle implements Serializable {
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`terminal_id`")
    private String terminalId;

    @Column(name = "`restart_log_id`")
    private String restartLogId;

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
     * @return terminal_id
     */
    public String getTerminalId() {
        return terminalId;
    }

    /**
     * @param terminalId
     */
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId == null ? null : terminalId.trim();
    }

    /**
     * @return restart_log_id
     */
    public String getRestartLogId() {
        return restartLogId;
    }

    /**
     * @param restartLogId
     */
    public void setRestartLogId(String restartLogId) {
        this.restartLogId = restartLogId == null ? null : restartLogId.trim();
    }
}