package com.aurora;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class servletInitializer implements ServletContainerInitializer{
	@Override
	public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
		new error("Class {ServletContainerInitializer} finished!");
		new mybatis();
		myJedis.configJedis();
	}

}
