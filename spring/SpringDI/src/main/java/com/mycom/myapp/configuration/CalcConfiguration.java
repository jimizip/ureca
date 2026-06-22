package com.mycom.myapp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CalcConfiguration {
	
	@Bean
	Calculator calculator() { // method 이
		return new Calculator();
	}

}
