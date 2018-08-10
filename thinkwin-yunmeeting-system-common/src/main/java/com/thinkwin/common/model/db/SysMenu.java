package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`sys_menu`")
public class SysMenu  implements Serializable {
    /**
     * 菜单主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 菜单码
     */
    @Column(name = "`menu_code`")
    private String menuCode;

    /**
     * 菜单名称
     */
    @Column(name = "`menu_name`")
    private String menuName;

    @Column(name = "`url`")
    private String url;

    @Column(name = "`parent_id`")
    private String parentId;

    /**
     * 图标url
     */
    @Column(name = "`icon`")
    private String icon;

    @Column(name = "`descript`")
    private String descript;

    /**
     * 排序号
     */
    @Column(name = "`sort_num`")
    private Integer sortNum;

    @Column(name = "`creater`")
    private String creater;

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
     * 版本需求类型（0：免费 1：收费）
     */
    @Column(name = "`version_type`")
    private String versionType;

    /**
     * 获取菜单主键
     *
     * @return id - 菜单主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置菜单主键
     *
     * @param id 菜单主键
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取菜单码
     *
     * @return menu_code - 菜单码
     */
    public String getMenuCode() {
        return menuCode;
    }

    /**
     * 设置菜单码
     *
     * @param menuCode 菜单码
     */
    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode == null ? null : menuCode.trim();
    }

    /**
     * 获取菜单名称
     *
     * @return menu_name - 菜单名称
     */
    public String getMenuName() {
        return menuName;
    }

    /**
     * 设置菜单名称
     *
     * @param menuName 菜单名称
     */
    public void setMenuName(String menuName) {
        this.menuName = menuName == null ? null : menuName.trim();
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
     * 获取图标url
     *
     * @return icon - 图标url
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置图标url
     *
     * @param icon 图标url
     */
    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }

    /**
     * @return descript
     */
    public String getDescript() {
        return descript;
    }

    /**
     * @param descript
     */
    public void setDescript(String descript) {
        this.descript = descript == null ? null : descript.trim();
    }

    /**
     * 获取排序号
     *
     * @return sort_num - 排序号
     */
    public Integer getSortNum() {
        return sortNum;
    }

    /**
     * 设置排序号
     *
     * @param sortNum 排序号
     */
    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    /**
     * @return creater
     */
    public String getCreater() {
        return creater;
    }

    /**
     * @param creater
     */
    public void setCreater(String creater) {
        this.creater = creater == null ? null : creater.trim();
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

    /**
     * 获取版本需求类型（0：免费 1：收费）
     *
     * @return version_type - 版本需求类型（0：免费 1：收费）
     */
    public String getVersionType() {
        return versionType;
    }

    /**
     * 设置版本需求类型（0：免费 1：收费）
     *
     * @param versionType 版本需求类型（0：免费 1：收费）
     */
    public void setVersionType(String versionType) {
        this.versionType = versionType == null ? null : versionType.trim();
    }
}