package com.mycom.myapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// jsp 페이지 이동만 처리
@Controller
public class PageController {

	@GetMapping("/emps")
	public String emps() {
		return "emps";
	}
	
	@GetMapping("/salaries")
	public String salaries() {
		return "salaries";
	}
	
	@GetMapping("/stores")
	public String stores() {
		return "stores";
	}
}
