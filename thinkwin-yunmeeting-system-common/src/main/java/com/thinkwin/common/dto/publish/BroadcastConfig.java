package com.thinkwin.common.dto.publish;

import java.io.Serializable;
import java.util.List;

public class BroadcastConfig implements Serializable {
	private static final long serialVersionUID = -7756104388958296661L;

	private List<DictionaryEntity> length;
	private List<DictionaryEntity> speed;

	public List<DictionaryEntity> getLength() {
		return length;
	}

	public void setLength(List<DictionaryEntity> length) {
		this.length = length;
	}

	public List<DictionaryEntity> getSpeed() {
		return speed;
	}

	public void setSpeed(List<DictionaryEntity> speed) {
		this.speed = speed;
	}
}
