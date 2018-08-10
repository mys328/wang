package com.thinkwin.common.dto.publish;

import java.io.Serializable;

public class UpdateTerminalRequst implements Serializable {
	private static final long serialVersionUID = -64732932385021071L;

	private String id;
	private String name;
	private String roomId;
	private byte[] backgroundPicture;
	private String backgroundId;
	private String backgroundUrl;
	private String pictureExt;
	private String city;
	private String province;
	private String county;
	private String street;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public byte[] getBackgroundPicture() {
		return backgroundPicture;
	}

	public void setBackgroundPicture(byte[] backgroundPicture) {
		this.backgroundPicture = backgroundPicture;
	}

	public String getBackgroundId() {
		return backgroundId;
	}

	public void setBackgroundId(String backgroundId) {
		this.backgroundId = backgroundId;
	}

	public String getBackgroundUrl() {
		return backgroundUrl;
	}

	public void setBackgroundUrl(String backgroundUrl) {
		this.backgroundUrl = backgroundUrl;
	}

	public String getPictureExt() {
		return pictureExt;
	}

	public void setPictureExt(String pictureExt) {
		this.pictureExt = pictureExt;
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

}
