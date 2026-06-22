package com.mycom.myapp.auth.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mycom.myapp.auth.service.LoginService;
import com.mycom.myapp.user.dto.UserDto;

import jakarta.servlet.http.HttpSession;

@Controller
@ResponseBody
@RequestMapping("/auth")
public class LoginController {

	// 생성자 DI
	private final LoginService loginService;
	
	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}

	// Login 처리
	// 1. 성공, 실패 판단
	// 2. Login 성공 -> Authentication (인증) 성공 ( Authorization (권한) )
	@PostMapping("/login")
	public Map<String, String> login(UserDto dto, HttpSession session){
		Map<String, String> map = new HashMap<>();
		Optional<UserDto> optional = loginService.login(dto); // 사용자가 입력한 userEmail, userPassword 포함.
		
		// #1 : isPresent
//		// 성공
//		if( optional.isPresent() ) {
//			UserDto userDto = optional.get();
//			session.setAttribute("userDto", userDto); // 세션에 추가
//			map.put("result", "success");
//			return map;
//		}
//		
//		// 실패
//		map.put("result", "fail");
//		return map;
		
		// #2 : IfPresentOrElse
		optional.ifPresentOrElse(
			userDto -> {
				session.setAttribute("userDto", userDto); // 세션에 추가
				map.put("result", "success");				
			}, 
			() -> {
				map.put("result", "fail");
			}
		);
		
		return map;
	}
}












