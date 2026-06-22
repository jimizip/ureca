package com.mycom.myapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class UrlMappingController {

	// 지양, 과거에 사용하던 방식
//	@RequestMapping(value="/m1", method=RequestMethod.GET)
//	public void m1() {
//		
//	}
	
	@GetMapping("/m1")
	public void m1() {
		System.out.println("/m1");
	}
	
	@PostMapping("/post")
	public void m2() {
		System.out.println("/m2");
	}
	
	@PutMapping("/put")
	public void m3() {
		System.out.println("/m3");
	}
	
	@DeleteMapping("/delete")
	public void m4() {
		System.out.println("/m4");
	}
	
	// Path Variable
	// http://localhost/books?bookId=5  => http://localhost/books/5 REST API
	@GetMapping("/books/{bookId}")
	public void m5(@PathVariable Integer bookId) {
		System.out.println("/m5");
		System.out.println(bookId);
	}	
	
	@GetMapping("/books/{limit}/{offset}")
	public void m6(@PathVariable Integer limit, @PathVariable Integer offset) {
		System.out.println("/m6");
		System.out.println(limit);
		System.out.println(offset);
	}
	// url 요청 할 때, java.lang.IllegalStateException: Ambiguous handler methods mapped for '/books/5000': 오류 발생
	@GetMapping("/books/{price}")
	public void m7(@PathVariable Integer price) {
		System.out.println("/m7");
		System.out.println(price);
	}
	
	// 복수개의 url 을 하나의 메소드에서 처리
	@GetMapping({"/url1", "/url2"})
	public void m8() {
		System.out.println("/m8");
		System.out.println("url1, url2");
	}
	
	// sub url
	@GetMapping("/sub/*")
	public void m9() {
		System.out.println("/m9");
		System.out.println("/sub/*");
	}	
	
	@GetMapping("/sub2/**")
	public void m10() {
		System.out.println("/m10");
		System.out.println("/sub2/**");
	}
}
