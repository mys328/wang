package com.thinkwin.common.model.db;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`commodity_item`")
public class CommodityItem implements Serializable {
    private static final long serialVersionUID = 3594463874847812366L;
    @Id
    @Column(name = "`Id`")
    private String id;

    /**
     * 版本id
     */
    @Column(name = "`category_code`")
    private String categoryCode;

    /**
     * 分类名称
     */
    @Column(name = "`item_name`")
    private String itemName;

    /**
     * 描述信息
     */
    @Column(name = "`item_desc`")
    private String itemDesc;

    /**
     * 排序
     */
    @Column(name = "`sort_order`")
    private Integer sortOrder;

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
     * 获取版本id
     *
     * @return category_code - 版本id
     */
    public String getCategoryCode() {
        return categoryCode;
    }

    /**
     * 设置版本id
     *
     * @param categoryCode 版本id
     */
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode == null ? null : categoryCode.trim();
    }

    /**
     * 获取分类名称
     *
     * @return item_name - 分类名称
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * 设置分类名称
     *
     * @param itemName 分类名称
     */
    public void setItemName(String itemName) {
        this.itemName = itemName == null ? null : itemName.trim();
    }

    /**
     * 获取描述信息
     *
     * @return item_desc - 描述信息
     */
    public String getItemDesc() {
        return itemDesc;
    }

    /**
     * 设置描述信息
     *
     * @param itemDesc 描述信息
     */
    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc == null ? null : itemDesc.trim();
    }

    /**
     * 获取排序
     *
     * @return sort_order - 排序
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * 设置排序
     *
     * @param sortOrder 排序
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}