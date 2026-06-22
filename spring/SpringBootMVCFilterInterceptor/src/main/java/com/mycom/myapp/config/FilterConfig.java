package com.mycom.myapp.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mycom.myapp.filter.MyFilter;
import com.mycom.myapp.filter.MyFilter2;

//@Configuration
public class FilterConfig {

	@Bean
	FilterRegistrationBean<MyFilter> registrationMyFilter(){
		FilterRegistrationBean<MyFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new MyFilter());
//		registrationBean.addUrlPatterns("/*"); // 모든 요청
		registrationBean.addUrlPatterns("/admin"); // 관리자
		registrationBean.setOrder(1); // 낮은 숫자가 먼저 실행
		return registrationBean;
	}
	
	@Bean
	FilterRegistrationBean<MyFilter2> registrationMyFilter2(){
		FilterRegistrationBean<MyFilter2> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new MyFilter2());
		registrationBean.addUrlPatterns("/*"); // 모든 요청
		registrationBean.setOrder(2); // 낮은 숫자가 먼저 실행
		return registrationBean;
	}	
}
