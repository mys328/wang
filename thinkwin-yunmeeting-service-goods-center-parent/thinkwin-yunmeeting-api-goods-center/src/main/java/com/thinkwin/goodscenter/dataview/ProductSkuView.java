package com.thinkwin.goodscenter.dataview;

import javax.persistence.Column;
import javax.persistence.Transient;

public class ProductSkuView implements java.io.Serializable {
	private static final long serialVersionUID = -3182453233514402724L;

	@Column(name = "sku")
	private String sku;

	@Column(name = "product_id")
	private String productId;

	@Column(name = "sku_desc")
	private String skuDesc;

	@Column(name = "sort_order")
	private Integer sortOrder;

	@Column(name = "list_price")
	private Double listPrice;

	@Column(name = "sale_price")
	private Double salePrice;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "product_desc")
	private String productDesc;

	@Column(name = "sku_uom")
	private String skuUom;

	@Transient
	private String uomCode;

	@Transient
	private String uomValue;

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Double getListPrice() {
		return listPrice;
	}

	public void setListPrice(Double listPrice) {
		this.listPrice = listPrice;
	}

	public Double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getSkuDesc() {
		return skuDesc;
	}

	public void setSkuDesc(String skuDesc) {
		this.skuDesc = skuDesc;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getSkuUom() {
		return skuUom;
	}

	public void setSkuUom(String skuUom) {
		this.skuUom = skuUom;
	}

	public String getUomCode() {
		return uomCode;
	}

	public void setUomCode(String uomCode) {
		this.uomCode = uomCode;
	}

	public String getUomValue() {
		return uomValue;
	}

	public void setUomValue(String uomValue) {
		this.uomValue = uomValue;
	}

}
