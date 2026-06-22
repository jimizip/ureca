package com.mycom.myapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 일반 자바 클래스
// url mapping 을 클래스 레벨 => 메소드 레벨
@Controller
public class HelloController {
	
	// static/hello.html
//	@GetMapping("/hello")
//	public String hello() {
//		System.out.println("/hello");
//		return "hello.html";
//	}
	
	// webapp/WEB-INF/jsp/hello.jsp
	// -------------------prefix
	//                         ----suffix
	@GetMapping("/hello")
	public String hello() {
		System.out.println("/hello");
		return "hello";
	}	
}
