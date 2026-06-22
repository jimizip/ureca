package com.mycom.myapp.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

// Filter 의 순서? <= @Order 방법도 있으나, 일반적으로는 Java Config 로 적용
// Java Config 로 필터를 등록할 경우, @Component 삭제
//@Component
public class MyFilter2 implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String requestURI = req.getRequestURI();
		
		System.out.println("MyFilter2 >> Before : " + requestURI);
		
		// 검증, 처리....
		
		// 통과
		chain.doFilter(request, response);
		
		// 거절
//		response.getWriter().write("Security Violation");
		
		// response 과정
		System.out.println("MyFilter2 << After : " + requestURI);
		
	}

}
