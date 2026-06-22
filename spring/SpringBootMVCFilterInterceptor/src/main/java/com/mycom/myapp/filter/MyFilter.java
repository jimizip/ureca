package com.mycom.myapp.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

// 입구를 지키는 경비원(들)  <= 첫번째 경비원 거치고 통과하는 요청을 두번째 경비원을 거치고.....
// 각각의 경비원은 어떤 요청에 대해, 어떤 검증하느냐. <= 대부분이 보안 이슈
// implements Filter
// 등록 : 1. @Component   2. Java Config   3. web.xml
//@Component
public class MyFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String requestURI = req.getRequestURI();
		
		System.out.println("MyFilter >> Before : " + requestURI);
		
		// 검증, 처리....
		
		// 통과
		chain.doFilter(request, response);
		
		// 거절
//		response.getWriter().write("Invalid Request");
		
		// response 과정
		System.out.println("MyFilter << After : " + requestURI);
		
	}

}
