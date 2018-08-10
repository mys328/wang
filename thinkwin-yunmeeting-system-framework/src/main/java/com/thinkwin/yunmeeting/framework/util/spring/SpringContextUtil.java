package com.thinkwin.yunmeeting.framework.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 获取bean实例
 *
 */
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext context)
        throws BeansException {
        SpringContextUtil.applicationContext = context;
    }
    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }
}