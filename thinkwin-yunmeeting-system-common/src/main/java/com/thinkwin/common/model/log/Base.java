package com.thinkwin.common.model.log;

import javax.persistence.*;

@Table(name = "`base`")
public class Base {
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`name`")
    private String name;

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
}