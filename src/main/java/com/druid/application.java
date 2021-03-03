package com.druid;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
public class application {	
	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(application.class);
		Properties properties = new Properties();
		properties.put("server.error.include-message", "always");
		springApplication.setDefaultProperties(properties);
		springApplication.run(args);
	}
}
