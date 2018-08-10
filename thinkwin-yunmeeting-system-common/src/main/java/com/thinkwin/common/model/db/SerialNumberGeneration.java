package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`serial_number_generation`")
public class SerialNumberGeneration implements Serializable {

    private static final long serialVersionUID = -669944857347662310L;
    @Id
    @Column(name = "`Id`")
    private String id;

    /**
     * 类型
     */
    @Column(name = "`business_type`")
    private String businessType;

    @Column(name = "`create_time`")
    private Date createTime;

    /**
     * @return Id
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
     * 获取类型
     *
     * @return business_type - 类型
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     * 设置类型
     *
     * @param businessType 类型
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType == null ? null : businessType.trim();
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}