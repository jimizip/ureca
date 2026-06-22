package com.mycom.myapp.annotation;

import java.lang.annotation.Annotation;

// Spring Framework 이 약속된 annotation 을 사용한 클래스와 사용된 annotation 을 읽는 과정
public class Test {

	public static void main(String[] args) throws Exception{
		Class<?> myClass = Class.forName("com.mycom.myapp.annotation.MyClass");
		Annotation[] annotations = myClass.getAnnotations(); // AboutMe 만 가져올 수도 있다.
		for (Annotation annotation : annotations) {
			// 스프링이 관리하는 약속된 annotation 처리
			if( annotation instanceof AboutMe ) {
				AboutMe aboutMe = (AboutMe) annotation;
				System.out.println(aboutMe.love());
				System.out.println(aboutMe.hate());
			}
			// else if....
		}

	}

}