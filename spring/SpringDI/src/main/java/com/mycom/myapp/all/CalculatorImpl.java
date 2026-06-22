package com.mycom.myapp.all;

import org.springframework.stereotype.Component;

@Component("aaa")
public class CalculatorImpl implements Calculator {
	
	@Override
	public int add(int n1, int n2) {
		System.out.println("CalculatorImpl add");
		return n1 + n2;
	}

}
