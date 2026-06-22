package com.mycom.myapp.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mycom.myapp.user.dto.UserDto;
import com.mycom.myapp.user.dto.UserResultDto;
import com.mycom.myapp.user.service.UserService;

@Controller
@ResponseBody
@RequestMapping("/users")
public class UserController {

	// 생성자 DI
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	// 회원 가입
	@PostMapping("/register")
	public UserResultDto register(UserDto userDto) {
		return userService.registerUser(userDto);
	}
	
}
