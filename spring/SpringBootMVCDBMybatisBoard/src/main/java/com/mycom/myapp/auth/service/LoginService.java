package com.mycom.myapp.auth.service;

import java.util.Optional;

import com.mycom.myapp.user.dto.UserDto;

// Login Process 는 항상 성공하는 것이 아니다. 로그인 실패도 매우 정상적인 결과
// 로그인 실패하면 dao 에서 null 이 return <= 정상적인 결과이고, 호출하는 쪽에서 대응 코드를 작성해라.

// 사용자가 로그인 요청할 때 전달한 username(userEmail), password 를 가지고, dao 에는 userEmail 만 전달해서 리턴값을 얻는다. 
// dao 응답이 null 이 아닌 경우, 추가로 password 검증을 한다.
public interface LoginService {
	Optional<UserDto> login(UserDto userDto);
}
