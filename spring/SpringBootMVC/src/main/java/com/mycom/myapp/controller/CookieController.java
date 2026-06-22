package com.mycom.myapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/cookie")
public class CookieController {
	// 쿠기 생성
	@GetMapping("/create")
	public void createCookie(HttpServletResponse response) {
		System.out.println("createCookie");
		Cookie cookie = new Cookie("domain", "board");
		// 경로, 유효기간 설정
		cookie.setPath("/");
//		cookie.setPath("/abc");
		cookie.setMaxAge(24 * 60 * 60); // 1일
		
		// 보안 관련 JavaScript 에서 보이지 않는
		cookie.setHttpOnly(true);
		
		// 실제 운영 배포
		// https 에서만 사용
//		cookie.setSecure(true);
		
		response.addCookie(cookie);
	}
	
	// 쿠기 읽기
	@GetMapping("/read")
	public void readCookie(@CookieValue(defaultValue="없음") String domain) {
		System.out.println("읽은 쿠기 값 : " + domain);
	}
	
	// 쿠기 삭제
	@GetMapping("/delete")
	public void deleteCookie(HttpServletResponse response) {
		System.out.println("쿠기 삭제 요청");
		Cookie cookie = new Cookie("domain", null); // 이름 동일
		cookie.setPath("/");
		cookie.setMaxAge(0); // 0 -> delete
		response.addCookie(cookie);
	}
}
