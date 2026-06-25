package com.mycom.myapp.common;

// 하나의 Controller 에서 페이지 요청과 데이터 요청을 함께 하면 구분짓는 응답 처리가 어렵다.
// 예외 처리도 그 중 한 요소.
// 페이지 요청을 담담하는 별도의 Controller 구성한 이유
// 이 Controller 에서 예외가 발생하면 페이지 요청에 대응되는 error 페이지로 이동
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
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
		
		// NullPointerException
//		String s = null;
//		s.length();

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
	
	// error 페이지로 이동하는 예외 처리
	@ExceptionHandler(Exception.class) // 이 컨트롤러에서 발생하는 모든 예외는 이곳에서 처리
	public String pageExceptionHandler(Exception ex, Model model, HttpServletRequest request) {
		// 필요한 처리
		System.out.println("pageExceptionHandler : " + ex.getMessage());
		model.addAttribute("exception", ex);
		model.addAttribute("requestURI", request.getRequestURI());
		// 더 많은 작업....
		return "error"; // error.jsp forward
	}
}













