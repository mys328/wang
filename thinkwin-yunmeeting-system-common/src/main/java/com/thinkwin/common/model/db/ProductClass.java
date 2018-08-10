package com.thinkwin.common.model.db;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`product_class`")
public class ProductClass implements Serializable {
    private static final long serialVersionUID = -1113571024280017141L;
    @Id
    @Column(name = "`class_code`")
    private String classCode;

    @Column(name = "`class_name`")
    private String className;

    @Column(name = "`status`")
    private Integer status;

    /**
     * @return class_code
     */
    public String getClassCode() {
        return classCode;
    }

    /**
     * @param classCode
     */
    public void setClassCode(String classCode) {
        this.classCode = classCode == null ? null : classCode.trim();
    }

    /**
     * @return class_name
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className
     */
    public void setClassName(String className) {
        this.className = className == null ? null : className.trim();
    }

    /**
     * @return status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}