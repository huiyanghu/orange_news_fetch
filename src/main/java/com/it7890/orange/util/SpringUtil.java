package com.it7890.orange.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringUtil implements ApplicationContextAware {
	private static ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		SpringUtil.applicationContext = arg0;
	}

	public static Object getBean(String name) {
		return applicationContext.getBean(name);
	}
}
