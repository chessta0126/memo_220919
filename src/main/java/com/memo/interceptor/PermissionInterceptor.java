package com.memo.interceptor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class PermissionInterceptor implements HandlerInterceptor {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
		// 요청 url을 가져온다.
		String uri = request.getRequestURI();
		logger.info("[###### preHandle: uri:{}", uri);
	
		// session이 있는지 확인 -> 있으면 로그인 된 것
		HttpSession session = request.getSession();
		Integer userId = (Integer)session.getAttribute("userId");
		
		// 비로그인인데 /post로 들어온 경우 -> 로그인 페이지로 redirect(권한 제한)
		// -> Controller에 못 가게 return false;
		if(userId == null && uri.startsWith("/post")) {
			response.sendRedirect("/user/sign_in_view");
			return false; // Controller 수행 x
		}
		
		// 로그인 중인데 /user로 들어온 경우 -> 글 목록 페이지로 redirect
		// -> Controller에 못 가게 return false;
		if(userId != null && uri.startsWith("/user")) {
			response.sendRedirect("/post/post_list_view");
			return false; // Controller 수행 x
		}
		// 로그아웃도 /user로 시작되기 때문에 작동이 안 됨
		// -> excludePathPatterns의 WebMvcConfig에서 예외처리 해주어야 함
		
		return true; // Controller 수행
	}
	
	@Override
	public void postHandle(
			HttpServletRequest request
			, HttpServletResponse response
			, Object handler, ModelAndView mav) {
		logger.info("[$$$$$$$$ postHandle]");
	}

	@Override
	public void afterCompletion(
			HttpServletRequest request
			, HttpServletResponse response
			, Object handler, Exception ex) {
		logger.info("[@@@@@@@ afterCompletion]");
	}
}
