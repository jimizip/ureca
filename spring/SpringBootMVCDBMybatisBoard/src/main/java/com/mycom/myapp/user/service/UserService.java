package com.mycom.myapp.user.service;

import com.mycom.myapp.user.dto.UserDto;
import com.mycom.myapp.user.dto.UserResultDto;

public interface UserService {
	// 회원 가입
	UserResultDto registerUser(UserDto userDto);
}
