package com.mycom.myapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mycom.myapp.dto.CarDto;

// client 의 요청 -> Controller 에서 Business Logic 처리 (service - dao - db) 후 view 처리를 위해 jsp 분기(forward)
@Controller
public class ViewController {

	@GetMapping("/viewTest1")
	public String viewTest1() {
		System.out.println("viewTest1");
		return "viewTest1";
	}
	
	@GetMapping("/viewTest2")
	public String viewTest2() {
		System.out.println("viewTest2");
		return "sub/viewTest2";
		// /WEB-INF/jsp/ + sub/viewTest2 + .jsp
	}
	
	// MVC 의 M 을 V 에게 전달
	// C <-> S <-> D : Model 을 jsp 에게 전달
	@GetMapping("/viewTest3")
	public String viewTest3(Model model) {
		System.out.println("viewTest3");
		
		model.addAttribute("seq", "12345");
		model.addAttribute("carDto", new CarDto("myCar", 20000, "홍길동"));
		return "viewTest3";
	}
	
	@GetMapping("/viewTest4")
	public ModelAndView viewTest4() {
		System.out.println("viewTest4");
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("seq", "12345");
		mav.addObject("carDto", new CarDto("myCar", 20000, "홍길동"));
		mav.setViewName("viewTest4");
		return mav;
	}
	
	// 위 스프링 코드는 모두 forward
	// 아래 redirect 처리
	@GetMapping("/redirect")
	public String redirect() {
		System.out.println("redirect");
		return "redirect:viewTest1";
	}
	
	// void return <= response 객체를 직접 제어하지 않으면 void 메소드에 대해서 요청 url 의 jsp 를 찾는다.
	@GetMapping("/void")
	public void voidMethod() {
		System.out.println("void");
	}
	
	
	
	
}
