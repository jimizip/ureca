package com.mycom.myapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {

	@GetMapping("/admin")
	public String admin() {
		return "/admin/admin.html";
	}
	
	@GetMapping("/no-login")
	public String noLogin() {
		return "/no-login.html";
	}
	
	@GetMapping("/login")
	public String login(HttpSession session) {
		session.setAttribute("login", "success");
		return "/login.html";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "/logout.html";
	}
}
