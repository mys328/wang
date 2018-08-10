package com.thinkwin.common.model.db;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`yummeeting_conference_room_middle`")
public class YummeetingConferenceRoomMiddle implements Serializable{
    private static final long serialVersionUID = 5812323238181649204L;
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`confreren_id`")
    private String confrerenId;

    @Column(name = "`room_id`")
    private String roomId;

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
     * @return room_id
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * @param roomId
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId == null ? null : roomId.trim();
    }
}