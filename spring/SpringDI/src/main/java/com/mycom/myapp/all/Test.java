package com.mycom.myapp.all;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("xml/all-calc-annotation.xml");
	    HasaCalculator hasaCalculator = (HasaCalculator) context.getBean("hasaCalculator"); // Spring 에게 객체 DI 반환
		System.out.println(hasaCalculator.add(3, 7));
		
		context.close();
	}
}
