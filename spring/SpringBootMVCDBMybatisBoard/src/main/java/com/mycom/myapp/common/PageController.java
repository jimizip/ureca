package com.mycom.myapp.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/pages")
public class PageController {

	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/board")
	public String board() {
		return "board";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		// session 정리
		session.invalidate();
		// 추가 백엔드 작업이 필요한 경우, 프론트는 javascript 로, 백엔드는 LoginController 같은 data 요청, 응답 처리하는 구조가 더 낫다.
		return "login";
	}
}
