package com.thinkwin.common.vo;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/29 0029.
 */
public class ProductSkuDto implements Serializable {


    private static final long serialVersionUID = 4085412245725626125L;
    private String  sku;	//String	是	商品sku
    private String  name;	//	String	是	商品名称，会议室、存储空间
    private String  displayName;	//	String	是	sku的UI展示名称，会议室数
    private double  price;	//	Double	是	单价
    private String  uom;	//	String	是	计价单位
    private Integer sortOrder;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

}
