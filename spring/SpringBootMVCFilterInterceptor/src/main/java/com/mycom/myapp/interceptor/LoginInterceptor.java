package com.mycom.myapp.interceptor;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor{
	// 통과 여부를 true, false 로 리턴
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		System.out.println("LoginInterceptor >> preHandle");
		
		// 요청 request 가 로그인 된 요청 <= session 에서 확인 <= request 의 session
		// 로그인 된 요청은 통과
		HttpSession session = request.getSession();
		String login = (String) session.getAttribute("login");
		System.out.println("LoginInterceptor >> preHandle : login " + login);
		
		// 통과
		if("success".equals(login)) return true;
		
		// 거절
		response.getWriter().write("Need Login");
		return false;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
		System.out.println("LoginInterceptor << postHandle");
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
		System.out.println("LoginInterceptor << afterCompletion");
	}
}
