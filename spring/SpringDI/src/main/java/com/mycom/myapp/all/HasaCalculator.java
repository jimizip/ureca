package com.mycom.myapp.all;

import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HasaCalculator {
	
	// Interface 타입으로 필드 선언
	Calculator calculator;
	
	// 객체 여러 개일 때 @Qualifier로
	public HasaCalculator(@Qualifier("aaa") Calculator calculator) {
		this.calculator = calculator;
	}
	
	public int add(int n1, int n2) {
		System.out.println("HasaCalculator add()");
		return calculator.add(n1, n2);
	}

}
