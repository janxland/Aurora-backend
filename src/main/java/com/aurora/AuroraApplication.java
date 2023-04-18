package com.aurora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@ServletComponentScan("com.aurora.api")
public class AuroraApplication {

	public static void main(String[] args) {
		new error("Class {ActionApplication} finished!");
		new mybatis();
		myJedis.configJedis();
		SpringApplication.run(AuroraApplication.class, args);
	}

}
