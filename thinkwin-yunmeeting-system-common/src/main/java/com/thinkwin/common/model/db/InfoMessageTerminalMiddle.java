package com.thinkwin.common.model.db;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`info_message_terminal_middle`")
public class InfoMessageTerminalMiddle implements Serializable {
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`terminal_id`")
    private String terminalId;

    @Column(name = "`broadcast_message_id`")
    private String broadcastMessageId;

    /**
     * 消息发送状态, 0：正在发送，1：已送达，2：未送达
     */
    @Column(name = "`msg_status`")
    private Integer msgStatus;

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
     * @return broadcast_message_id
     */
    public String getBroadcastMessageId() {
        return broadcastMessageId;
    }

    /**
     * @param broadcastMessageId
     */
    public void setBroadcastMessageId(String broadcastMessageId) {
        this.broadcastMessageId = broadcastMessageId == null ? null : broadcastMessageId.trim();
    }

    public Integer getMsgStatus() {
        return msgStatus;
    }


    public void setMsgStatus(Integer msgStatus) {
        this.msgStatus = msgStatus;
    }
}