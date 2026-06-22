package com.mycom.myapp.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycom.myapp.dto.CarDto;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ParamController {
	// request 객체를 직접 사용
	@GetMapping("/param1")
	public void m1(HttpServletRequest request) {
		System.out.println(request.getParameter("bookId"));
		System.out.println(request.getParameter("bookName"));
	}
	
	// spring framework 가 자동으로 처리
	@GetMapping("/param2")
	public void m2(String bookId) {
		System.out.println(bookId);
	}
	
	// bookId String -> int
	// 서블릿 경우 Integer.parseInt(request.getParameter(bookId))
	// int 파라미터가 누락되었을 때	
	//	public void m3(int bookId, String bookName) { // <= java.lang.IllegalStateException: Optional int parameter 'bookId' is present but cannot be translated into a null value
	@GetMapping("/param3")
	public void m3(Integer bookId, String bookName) {
		System.out.println(bookId);
		System.out.println(bookName);
	}
	
	// 필수 파라미터
	@GetMapping("/param4")
	public void m4(@RequestParam String bookName) {
		System.out.println(bookName);
	}	
	
	// 파라미터로 Dto 객체 활용
	// 서블릿은 request.getParameter() 로 개별 파라미터값을 각각 얻어서, ~Dto 객체 생성 후, Service <-> Dao 전달....
	@PostMapping("/car")
	public void m5(CarDto carDto) {
		System.out.println(carDto);
	}
	
	// Map 활용
	// 가변적인 파라미터들
	@PostMapping("/map")
	public void m6(@RequestParam Map<String, String> params) {
		System.out.println(params.get("abc"));
		System.out.println(params.get("def"));
		System.out.println(params.get("xyz"));
		
		
	}
	
	// Header
	@GetMapping("/header")
	public void m7(
			@RequestHeader("User-Agent") String userAgent,
			@RequestHeader("Accept") String accept,
			@RequestHeader("API-KEY") String apiKey  // 사용자 정의 Header
	) {
		System.out.println(userAgent);
		System.out.println(accept);
		System.out.println(apiKey);
	}
}








