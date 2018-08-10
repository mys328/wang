package com.thinkwin.goodscenter.dataview;

import javax.persistence.Column;
import java.io.Serializable;

public class ProductPackSkuView implements Serializable {
	private static final long serialVersionUID = 848271066595915841L;

	@Column(name = "product_pack_id")
	private String productPackId;

	@Column(name = "category")
	private String category;

	@Column(name = "sku")
	private String sku;

	@Column(name = "sku_desc")
	private String skuDesc;

	@Column(name = "list_price")
	private Double listPrice;

	@Column(name = "sale_price")
	private Double salePrice;

	//基础价格，一年的价格
	@Column(name = "`unit_price`")
	private Double unitPrice;

	@Column(name = "discount")
	private Integer discount;

	@Column(name = "discount_tip")
	private String discountTip;

	@Column(name = "skuInfo")
	private String skuInfo;

	@Column(name = "spec_name")
	private String speName;

	@Column(name = "spec_value")
	private String specValue;

	@Column(name = "uom_name")
	private String uomName;

	@Column(name = "uom_class")
	private String uomClass;

	@Column(name = "uom_desc")
	private String uomDesc;


	public String getProductPackId() {
		return productPackId;
	}

	public void setProductPackId(String productPackId) {
		this.productPackId = productPackId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getSkuDesc() {
		return skuDesc;
	}

	public void setSkuDesc(String skuDesc) {
		this.skuDesc = skuDesc;
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

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}

	public String getDiscountTip() {
		return discountTip;
	}

	public void setDiscountTip(String discountTip) {
		this.discountTip = discountTip;
	}

	public String getSkuInfo() {
		return skuInfo;
	}

	public void setSkuInfo(String skuInfo) {
		this.skuInfo = skuInfo;
	}

	public String getSpeName() {
		return speName;
	}

	public void setSpeName(String speName) {
		this.speName = speName;
	}

	public String getSpecValue() {
		return specValue;
	}

	public void setSpecValue(String specValue) {
		this.specValue = specValue;
	}

	public String getUomName() {
		return uomName;
	}

	public void setUomName(String uomName) {
		this.uomName = uomName;
	}

	public String getUomClass() {
		return uomClass;
	}

	public void setUomClass(String uomClass) {
		this.uomClass = uomClass;
	}

	public String getUomDesc() {
		return uomDesc;
	}

	public void setUomDesc(String uomDesc) {
		this.uomDesc = uomDesc;
	}

}
