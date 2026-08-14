package com.soso.global.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer{
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		WebMvcConfigurer.super.addCorsMappings(registry);
		registry.addMapping("/**") 
        .allowedOrigins("http://localhost:5173","https://soso-test-1dd3d.web.app","https://emsemsdl.shop")
        .allowedMethods("GET", "POST", "PUT", "DELETE","PATCH", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true);
	}
}
