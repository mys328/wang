package com.thinkwin.orders.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class OrderLine {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "order_id")
	private String orderId;

	@Column(name = "product_sku")
	private String productSku;

	@Column(name = "product_pack_sku")
	private String productPackSku;

	@Column(name = "item_desc")
	private String itemDesc;

	@Column(name = "uom")
	private String uom;

	@Column(name = "qty")
	private Integer qty;

	@Column(name = "free")
	private Integer free;

	@Column(name = "giveaway")
	private Integer giveaway;

	@Column(name = "create_time")
	private Date createTime;

	@Column(name = "update_time")
	private Date updateTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getProductSku() {
		return productSku;
	}

	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}

	public String getProductPackSku() {
		return productPackSku;
	}

	public void setProductPackSku(String productPackSku) {
		this.productPackSku = productPackSku;
	}

	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Integer getFree() {
		return free;
	}

	public void setFree(Integer free) {
		this.free = free;
	}

	public Integer getGiveaway() {
		return giveaway;
	}

	public void setGiveaway(Integer giveaway) {
		this.giveaway = giveaway;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
