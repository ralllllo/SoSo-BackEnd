package com.soso.global.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.soso.global.common.TokenValidator;


@Configuration
public class InterceptorConfig implements WebMvcConfigurer{
	
	@Autowired
	private TokenValidator Interceptor;
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		
		
		registry.addInterceptor(Interceptor)
		.addPathPatterns("/**") 
		.excludePathPatterns("/auth/**", "/api/member/check-*", "/api/member/signup", "/api/biz/**", "/find/**");
	}

}
