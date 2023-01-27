package com.memo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.memo.common.FileManagerService;
import com.memo.interceptor.PermissionInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{

	@Autowired
	private PermissionInterceptor interceptor; 
	
	@Override
	public void addResourceHandlers(
			ResourceHandlerRegistry registry) {
		registry
		.addResourceHandler("/images/**") // 웹 이미지 주소 http://localhost:8080/images/aaaa_16205468768/sun.png
		// 실제 파일 위치 (window는 ///, mac은 //)
		.addResourceLocations("file:///" + FileManagerService.FILE_UPLOAD_PATH);
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(interceptor)
		.addPathPatterns("/**") // 모든 주소에 대해서(아래 디렉토리까지) 확인
		.excludePathPatterns("/favicon.ico","/error","/static/**","/user/sign_out"); // 이 주소로 들어오는 것들은 제외할 것(불필요)
	}
}
