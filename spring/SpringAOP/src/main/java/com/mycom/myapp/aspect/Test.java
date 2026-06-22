package com.mycom.myapp.aspect;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mycom.myapp.aspect.sub.SubBusinessProcess;

public class Test {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("xml/aspect.xml");
		BusinessProcess bp = (BusinessProcess) context.getBean("businessProcess");
		
		bp.no_bp();
		
		bp.int_bp();
		
		bp.String_int_String_bp("a", 0, "b");
		
		
		AnotherBusinessProcess abp = (AnotherBusinessProcess) context.getBean("anotherBusinessProcess");
		
		abp.String_int_String_bp("a", 0, "b");
		
		SubBusinessProcess sub = (SubBusinessProcess) context.getBean("subBusinessProcess");
		
		sub.String_int_String_bp("a", 0, "b");
		
		context.close();
	}

}
