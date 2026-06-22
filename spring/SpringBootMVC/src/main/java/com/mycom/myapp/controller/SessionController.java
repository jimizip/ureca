package com.mycom.myapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class SessionController {

	// 로그인인 post 로! 지금은 테스트 용도
	@GetMapping("/login")
	public String login(String username, String password, HttpSession session) {
		// dskim / 1234 인증
		if( "dskim".equals(username) && "1234".equals(password)) {
			session.setAttribute("username", username);
		}
		return "sessionTest1";
	}
	
	
	@GetMapping("/doSomething")
	public String doSomething() {
		return "sessionTest2";
	}
	
	// 특정 항목을 삭제 X, 전체 session 삭제 (삭제 표현보다는 invalidate )
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "sessionTest3";
	}
}
