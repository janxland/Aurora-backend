package com.aurora;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


public class SpringBootStartApplication extends SpringBootServletInitializer{
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		new error("Class {SpringBootStartApplication} finished!");
		return application.sources(AuroraApplication.class);
	}
}
