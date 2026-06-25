package com.mycom.myapp.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;

// 개별 Controller 가 처리하지 않는 모든 예외 담당

// jsp 만 사용, 데이터 요청 X
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//	// error 페이지로 이동하는 예외 처리
//	@ExceptionHandler(Exception.class) // 이 컨트롤러에서 발생하는 모든 예외는 이곳에서 처리
//	public String pageExceptionHandler(Exception ex, Model model, HttpServletRequest request) {
//		// 필요한 처리
//		System.out.println("pageExceptionHandler : " + ex.getMessage());
//		model.addAttribute("exception", ex);
//		model.addAttribute("requestURI", request.getRequestURI());
//		// 더 많은 작업....
//		return "error"; // error.jsp forward
//	}
//}

// data 요청만
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

	// 데이터 요청에 대한 오류 예외 처리
	// 아래 예외처리가 다른 데이터 요청 Controller 모두에게 일괄적용한다면 한곳에서 처리하는 게 더 효율적
	@ExceptionHandler(Exception.class) // 이 컨트롤러에서 발생하는 모든 예외는 이곳에서 처리
	public Map<String, String> pageExceptionHandler(HttpServletResponse response) throws Exception{
		Map<String, String> map = new HashMap<>();
		map.put("result", "fail");
		// 더 많은 데이터 ( 프론트에게 제공할 내용... )
		return map;
	}	
}
