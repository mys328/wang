package com.thinkwin.common.model.db;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`category_item`")
public class CategoryItem implements Serializable {
    private static final long serialVersionUID = 1679889140146494465L;
    /**
     * id
     */
    @Id
    @Column(name = "`item_id`")
    private String itemId;

    @Column(name = "`category_code`")
    private String categoryCode;

    /**
     * 名称
     */
    @Column(name = "`item_name`")
    private String itemName;

    /**
     * 分类描述
     */
    @Column(name = "`item_desc`")
    private String itemDesc;

    /**
     * 获取id
     *
     * @return item_id - id
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * 设置id
     *
     * @param itemId id
     */
    public void setItemId(String itemId) {
        this.itemId = itemId == null ? null : itemId.trim();
    }

    /**
     * @return category_code
     */
    public String getCategoryCode() {
        return categoryCode;
    }

    /**
     * @param categoryCode
     */
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode == null ? null : categoryCode.trim();
    }

    /**
     * 获取名称
     *
     * @return item_name - 名称
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * 设置名称
     *
     * @param itemName 名称
     */
    public void setItemName(String itemName) {
        this.itemName = itemName == null ? null : itemName.trim();
    }

    /**
     * 获取分类描述
     *
     * @return item_desc - 分类描述
     */
    public String getItemDesc() {
        return itemDesc;
    }

    /**
     * 设置分类描述
     *
     * @param itemDesc 分类描述
     */
    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc == null ? null : itemDesc.trim();
    }
}