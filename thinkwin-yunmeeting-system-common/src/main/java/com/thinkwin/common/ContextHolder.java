package com.thinkwin.common;

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

	public static Object getBean(String name){
		return applicationContext.getBean(name);
	}

	public static Object getBean(Class<?> clazz){
		return applicationContext.getBean(clazz);
	}

	public static Object getBean(String name, Class<?> clazz){
		return applicationContext.getBean(name, clazz);
	}
}
