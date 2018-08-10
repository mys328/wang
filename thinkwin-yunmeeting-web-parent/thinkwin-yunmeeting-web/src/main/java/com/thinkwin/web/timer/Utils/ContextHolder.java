package com.thinkwin.web.timer.Utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ContextHolder implements ApplicationContextAware {
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
