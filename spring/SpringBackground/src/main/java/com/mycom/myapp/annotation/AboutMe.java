package com.mycom.myapp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 사용 정책 - 어디까지 사용할 거다.
//   RetentionPolicy 의 SOURCE : 컴파일러에게 소통하는 목적, java source 에만 존재. .class 에 포함 X  => @Override
//   RetentionPolicy 의 CLASS : .class 에 포함된다. 실행 중에 Reflection 으로 읽을 수는 없다. => Lombok @Setter, @Getter 등.
//   RetentionPolicy 의 RUNTIME : .class 에 포함된다. 실행 중에 Reflection 으로 읽을 수 있다. => @Controller 등 스프링에서 사용하는 대부분의 annotation
// 사용 대상 - 누구에 쓸 거다.
//   TYPE : class, interface, enum, annotation, record
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AboutMe {
	// 속성 - 추상 메소드로 표현
	String love();
	String hate();
}