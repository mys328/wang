package com.thinkwin.config.service;

public interface ConfigService {
	public String get(String key);

	public void set(String key, String value);
}
