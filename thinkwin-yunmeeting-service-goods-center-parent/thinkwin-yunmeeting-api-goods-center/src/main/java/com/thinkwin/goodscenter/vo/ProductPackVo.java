package com.thinkwin.goodscenter.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ProductPackVo implements Serializable {
	private static final long serialVersionUID = -4505783168112452522L;
	/**
	 * 版本id
	 */
	private String id;
	/**
	 * 价格
	 */
	private String price;

	/**
	 * 版本列表功能
	 */
	private List<String> functionList;

	/**
	 * 是否是当前版本
	 */
	private String isFront;

	/**
	 * 版本所带的功能
	 */
	private List<String> items;
	/**
	 * 会议室单价
	 */
	private String rommPrice;

	/**
	 * 空间单价
	 */
	private String spacePrice;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public List<String> getFunctionList() {
		return functionList;
	}

	public void setFunctionList(List<String> functionList) {
		this.functionList = functionList;
	}

	public String getIsFront() {
		return isFront;
	}

	public void setIsFront(String isFront) {
		this.isFront = isFront;
	}

	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}

	public String getRommPrice() {
		return rommPrice;
	}

	public void setRommPrice(String rommPrice) {
		this.rommPrice = rommPrice;
	}

	public String getSpacePrice() {
		return spacePrice;
	}

	public void setSpacePrice(String spacePrice) {
		this.spacePrice = spacePrice;
	}
}
