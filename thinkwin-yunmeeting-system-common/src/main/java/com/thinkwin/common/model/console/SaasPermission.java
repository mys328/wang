package com.thinkwin.common.model.console;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`saas_permission`")
public class SaasPermission implements Serializable{
    private static final long serialVersionUID = 2743930281287485860L;
    /**
     * 权限主键id
     */
    @Id
    @Column(name = "`permission_id`")
    private String permissionId;

    @Column(name = "`org_code`")
    private String orgCode;

    /**
     * 配置名称
     */
    @Column(name = "`org_name`")
    private String orgName;

    @Column(name = "`parent_id`")
    private String parentId;

    @Column(name = "`url`")
    private String url;

    /**
     * 选择权限时,是否显示该权限 2:不显示, 1显示
     */
    @Column(name = "`display`")
    private Integer display;

    /**
     * 显示顺序
     */
    @Column(name = "`sort_number`")
    private Integer sortNumber;

    /**
     * 权限描述
     */
    @Column(name = "`descritp`")
    private String descritp;

    /**
     * 有效：1；无效：0
     */
    @Column(name = "`status`")
    private Integer status;

    @Column(name = "`creater_id`")
    private String createrId;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`modifyer_id`")
    private String modifyerId;

    @Column(name = "`modify_time`")
    private Date modifyTime;

    /**
     * 预留字段1
     */
    @Column(name = "`reserve_1`")
    private String reserve1;

    /**
     * 预留字段2
     */
    @Column(name = "`reserve_2`")
    private String reserve2;

    /**
     * 预留字段3
     */
    @Column(name = "`reserve_3`")
    private String reserve3;

    /**
     * 获取权限主键id
     *
     * @return permission_id - 权限主键id
     */
    public String getPermissionId() {
        return permissionId;
    }

    /**
     * 设置权限主键id
     *
     * @param permissionId 权限主键id
     */
    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId == null ? null : permissionId.trim();
    }

    /**
     * @return org_code
     */
    public String getOrgCode() {
        return orgCode;
    }

    /**
     * @param orgCode
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode == null ? null : orgCode.trim();
    }

    /**
     * 获取配置名称
     *
     * @return org_name - 配置名称
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * 设置配置名称
     *
     * @param orgName 配置名称
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName == null ? null : orgName.trim();
    }

    /**
     * @return parent_id
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * @param parentId
     */
    public void setParentId(String parentId) {
        this.parentId = parentId == null ? null : parentId.trim();
    }

    /**
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     */
    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    /**
     * 获取选择权限时,是否显示该权限 2:不显示, 1显示
     *
     * @return display - 选择权限时,是否显示该权限 2:不显示, 1显示
     */
    public Integer getDisplay() {
        return display;
    }

    /**
     * 设置选择权限时,是否显示该权限 2:不显示, 1显示
     *
     * @param display 选择权限时,是否显示该权限 2:不显示, 1显示
     */
    public void setDisplay(Integer display) {
        this.display = display;
    }

    /**
     * 获取显示顺序
     *
     * @return sort_number - 显示顺序
     */
    public Integer getSortNumber() {
        return sortNumber;
    }

    /**
     * 设置显示顺序
     *
     * @param sortNumber 显示顺序
     */
    public void setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
    }

    /**
     * 获取权限描述
     *
     * @return descritp - 权限描述
     */
    public String getDescritp() {
        return descritp;
    }

    /**
     * 设置权限描述
     *
     * @param descritp 权限描述
     */
    public void setDescritp(String descritp) {
        this.descritp = descritp == null ? null : descritp.trim();
    }

    /**
     * 获取有效：1；无效：0
     *
     * @return status - 有效：1；无效：0
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置有效：1；无效：0
     *
     * @param status 有效：1；无效：0
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return creater_id
     */
    public String getCreaterId() {
        return createrId;
    }

    /**
     * @param createrId
     */
    public void setCreaterId(String createrId) {
        this.createrId = createrId == null ? null : createrId.trim();
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

    /**
     * @return modifyer_id
     */
    public String getModifyerId() {
        return modifyerId;
    }

    /**
     * @param modifyerId
     */
    public void setModifyerId(String modifyerId) {
        this.modifyerId = modifyerId == null ? null : modifyerId.trim();
    }

    /**
     * @return modify_time
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * @param modifyTime
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * 获取预留字段1
     *
     * @return reserve_1 - 预留字段1
     */
    public String getReserve1() {
        return reserve1;
    }

    /**
     * 设置预留字段1
     *
     * @param reserve1 预留字段1
     */
    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1 == null ? null : reserve1.trim();
    }

    /**
     * 获取预留字段2
     *
     * @return reserve_2 - 预留字段2
     */
    public String getReserve2() {
        return reserve2;
    }

    /**
     * 设置预留字段2
     *
     * @param reserve2 预留字段2
     */
    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2 == null ? null : reserve2.trim();
    }

    /**
     * 获取预留字段3
     *
     * @return reserve_3 - 预留字段3
     */
    public String getReserve3() {
        return reserve3;
    }

    /**
     * 设置预留字段3
     *
     * @param reserve3 预留字段3
     */
    public void setReserve3(String reserve3) {
        this.reserve3 = reserve3 == null ? null : reserve3.trim();
    }
}