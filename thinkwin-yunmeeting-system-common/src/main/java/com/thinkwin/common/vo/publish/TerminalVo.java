package com.thinkwin.common.vo.publish;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public class TerminalVo implements Serializable {
	private static final long serialVersionUID = 4600189860748467457L;

	private String terminalId;
	private String name;
	private String roomId;
	private String updateBackground;  //是否删除背景图片 0未删除 ，1删除
	private MultipartFile picture;

	private String city;
	//省
	private String province;
	//县
	private String county;
	//街道
	private String street;

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public MultipartFile getPicture() {
		return picture;
	}

	public void setPicture(MultipartFile picture) {
		this.picture = picture;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

    public String getUpdateBackground() {
        return updateBackground;
    }

    public void setUpdateBackground(String updateBackground) {
        this.updateBackground = updateBackground;
    }
}
