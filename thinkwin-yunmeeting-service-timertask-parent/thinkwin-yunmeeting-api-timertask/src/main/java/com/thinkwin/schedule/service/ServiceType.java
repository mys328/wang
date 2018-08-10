package com.thinkwin.schedule.service;

import java.util.EnumSet;

public enum ServiceType {
	NODE,EXECUTOR;

	public static final EnumSet<ServiceType> BOTH = EnumSet.allOf(ServiceType.class);
}
