package com.mycom.myapp.auth.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mycom.myapp.user.dto.UserDto;

// login : username(userEmail), password 일치 여부 확인
//    #1. dao 에서 username(userEmail), password 모두 확인
//    #2. dao 에서 username(userEmail) 만 확인, password 는 service 에서 확인 <= 2번 사용
@Mapper
public interface LoginDao {
	// userEmail 로 검색해서 그런 사용자가 있으면 UserDto 만들어서 return
	// 그런 사용자가 없으면 null return
	UserDto login(String userEmail);
}
