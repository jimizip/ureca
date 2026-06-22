package com.mycom.myapp.configuration;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CalcConfiguration.class);
		Calculator calculator = (Calculator) context.getBean("calculator"); // Spring에게 객체 DI 의뢰
		System.out.println(calculator.add(3, 7));
		
		context.close();
	}
}
