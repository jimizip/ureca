package com.mycom.myapp.aspect.sub;

import org.springframework.stereotype.Component;

// 개발자가 작성하는 코드
// 비즈니스 로직을 수행
// AOP 가 없는 경우, 이 클래스의 각 메소드는 적절한 규칙에 따라 호출 앞, 뒤로 로그를 남겨야 하는 규칙
@Component
public class SubBusinessProcess {

	public void no_bp(){
		// 개발자 작성 로그 코드
		System.out.println("SubBusinessProcess no_bp()");
		// 개발자 작성 로그 코드
	}
	
	public int int_bp(){
		System.out.println("SubBusinessProcess int_bp()");
		return 0;
	}
	
	public int String_int_String_bp(String s1, int i, String s2) {
		System.out.println("SubBusinessProcess String_int_String_bp()");
		return 0;
	}
}
