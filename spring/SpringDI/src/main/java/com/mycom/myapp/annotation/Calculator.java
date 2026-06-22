package com.mycom.myapp.annotation;

import org.springframework.stereotype.Component;

@Component
public class Calculator {
	public int add(int n1, int n2) {
		return n1+n2;
	}

}
