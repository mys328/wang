package com.thinkwin.common.dto.publish;

import com.thinkwin.common.vo.publish.TerminalMeetingRoomVo;
import com.thinkwin.common.vo.publish.TerminalMessageVo;
import com.thinkwin.common.vo.publish.TerminalProgramVo;

import java.io.Serializable;

public class TerminalDto implements Serializable {
	private static final long serialVersionUID = 7628721869019086184L;

	private String id;
	private String tenantId;
	private String name;
	private String hardwareId;
	private String type;
	private String version;
	private String latestVersion;
	private boolean upgradable;
	private String resolution;
	private String backgroundUrl;  //租户自己的图片
	private String defaultBackgroundUrl;  //平台默认图片
	private Integer voice;
	private Integer light;
	private Integer status;

	/*
	 * 上级市名称
	 * */
	private String city;
	//省
	private String province;
	//县
	private String county;
	//街道
	private String street;

	private TerminalMessageVo message;
	private TerminalProgramVo program;
	private TerminalMeetingRoomVo meetingRoom;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getName() {
		if(name == null){
			return this.hardwareId;
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHardwareId() {
		return hardwareId;
	}

	public void setHardwareId(String hardwareId) {
		this.hardwareId = hardwareId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLatestVersion() {
		return latestVersion;
	}

	public void setLatestVersion(String latestVersion) {
		this.latestVersion = latestVersion;
	}

	public boolean isUpgradable() {
		return upgradable;
	}

	public void setUpgradable(boolean upgradable) {
		this.upgradable = upgradable;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getBackgroundUrl() {
		return backgroundUrl;
	}

	public void setBackgroundUrl(String backgroundUrl) {
		this.backgroundUrl = backgroundUrl;
	}

	public Integer getVoice() {
		return voice;
	}

	public void setVoice(Integer voice) {
		this.voice = voice;
	}

	public Integer getLight() {
		return light;
	}

	public void setLight(Integer light) {
		this.light = light;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public TerminalMessageVo getMessage() {
		return message;
	}

	public void setMessage(TerminalMessageVo message) {
		this.message = message;
	}

	public TerminalProgramVo getProgram() {
		return program;
	}

	public void setProgram(TerminalProgramVo program) {
		this.program = program;
	}

	public TerminalMeetingRoomVo getMeetingRoom() {
		return meetingRoom;
	}

	public void setMeetingRoom(TerminalMeetingRoomVo meetingRoom) {
		this.meetingRoom = meetingRoom;
	}

    public String getDefaultBackgroundUrl() {
        return defaultBackgroundUrl;
    }

    public void setDefaultBackgroundUrl(String defaultBackgroundUrl) {
        this.defaultBackgroundUrl = defaultBackgroundUrl;
    }
}
