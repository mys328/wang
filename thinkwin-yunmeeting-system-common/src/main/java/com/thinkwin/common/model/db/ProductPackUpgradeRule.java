package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`product_pack_upgrade_rule`")
public class ProductPackUpgradeRule implements Serializable{
    private static final long serialVersionUID = -7894595077769529061L;
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`product_pack`")
    private String productPack;

    /**
     * 如专业版一可从免费版升级
            专业版二可从专业版一升级
     */
    @Column(name = "`upgrade_to`")
    private String upgradeTo;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`update_time`")
    private Date updateTime;

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
     * @return product_pack
     */
    public String getProductPack() {
        return productPack;
    }

    /**
     * @param productPack
     */
    public void setProductPack(String productPack) {
        this.productPack = productPack == null ? null : productPack.trim();
    }

    /**
     * 获取如专业版一可从免费版升级
            专业版二可从专业版一升级
     *
     * @return upgrade_to - 如专业版一可从免费版升级
            专业版二可从专业版一升级
     */
    public String getUpgradeTo() {
        return upgradeTo;
    }

    /**
     * 设置如专业版一可从免费版升级
            专业版二可从专业版一升级
     *
     * @param upgradeTo 如专业版一可从免费版升级
            专业版二可从专业版一升级
     */
    public void setUpgradeTo(String upgradeTo) {
        this.upgradeTo = upgradeTo == null ? null : upgradeTo.trim();
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
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}