package com.mycom.myapp.annotation;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("xml/calc-annotation.xml");
		Calculator calculator = (Calculator) context.getBean("calculator");
		System.out.println(calculator.add(3, 7));
		
		context.close();
	}
}
