package com.thinkwin.goodscenter.vo;

import java.util.List;

public class ProductSkuVo {
	private Integer sku;

	private Double price;

	private ProductVo product;

	public Integer getSku() {
		return sku;
	}

	public void setSku(Integer sku) {
		this.sku = sku;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public ProductVo getProduct() {
		return product;
	}

	public void setProduct(ProductVo product) {
		this.product = product;
	}

}
