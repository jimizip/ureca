package com.mycom.myapp.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CheckNotNull {
	// 속성
	// 속성을 이용해서 처리를 하려고 함
	String[] parameterNames();
}

// annotation 자체는 실행코드를 가지지 않는다.
// CheckNotNull 에 대응하는 실행 코드가 별도로 필요. InvocationHandler