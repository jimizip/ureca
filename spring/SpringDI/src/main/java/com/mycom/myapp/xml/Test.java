package com.mycom.myapp.xml;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {
	public static void main(String[] args) {
		// DI 이전 -> 생성자 호출
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("xml/calc-xml.xml");
		Calculator calculator = (Calculator) context.getBean("calculator");
		System.out.println(calculator.add(3, 7));
		
		context.close();
	}
}
