package com.mycom.myapp.hasa;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HasaCalculator {
	// Has A 관계의 DI 3가지 방법 
	// # Field DI -> 비추천
//	@Autowired
//	Calculator calculator;
	
	// 2.Setter DI
//	Calculator calculator;
//	
//	@Autowired
//	public void setCalculator(Calculator calculator) {
//		this.calculator = calculator;
//	}
	
	// 3. Constructor DI <- 추천(스프링이) 생성자 주입을 쓰자
	Calculator calculator;
	
	// @Autowired 필요 없음
	public HasaCalculator(Calculator calculator) {
		this.calculator = calculator;
	}
	
	public int add(int n1, int n2) {
		System.out.println("HasaCalculator add()");
		return calculator.add(n1, n2);
	}

}
