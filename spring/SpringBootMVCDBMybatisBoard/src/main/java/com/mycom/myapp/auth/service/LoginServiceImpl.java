package com.mycom.myapp.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mycom.myapp.auth.dao.LoginDao;
import com.mycom.myapp.user.dto.UserDto;

@Service
public class LoginServiceImpl implements LoginService{
	
	// 생성자 DI
	private final LoginDao loginDao;
	
	public LoginServiceImpl(LoginDao loginDao) {
		this.loginDao = loginDao;
	}
	
	@Override
	public Optional<UserDto> login(UserDto userDto) {
		UserDto dto = loginDao.login(userDto.getUserEmail());
		
		if( dto != null && userDto.getUserPassword().equals(dto.getUserPassword())) { // 로그인 성공
			dto.setUserPassword(null); // 비밀번호 null 처리
			return Optional.of(dto);
		}
		
		return Optional.empty(); // 로그인 실패
	}

}
