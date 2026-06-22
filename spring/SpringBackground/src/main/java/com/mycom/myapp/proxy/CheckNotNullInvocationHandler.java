package com.mycom.myapp.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class CheckNotNullInvocationHandler implements InvocationHandler{
	
	// proxy 뒤의 원 기본 객체 - MyIFImpl 객체
	private Object target;
	
	public CheckNotNullInvocationHandler(Object target) {
		this.target = target;
	}
	
	// @CheckNotNull 이 붙은 메소드가 호출될 때 아래 코드 호출
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// proxy 가 할 일을 먼저 처리하고 필요에 따라 원 기본 객체(target) 의 메소드를 호출
		// proxy 가 힐 일이 따로 없으면 bypass
		Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
		
		// 호출된 메소드가 @CheckNotNull 을 가진 메소드인지 확인
		if( targetMethod.isAnnotationPresent(CheckNotNull.class)) {
			// 처리
			return handleCheckNotNull(targetMethod, args);
		}
		return method.invoke(target, args); // bypass
	}

	// CheckNotNull 어노테이션이 있을 때 호출
	private Object handleCheckNotNull(Method method, Object[] args) throws Exception{
		CheckNotNull annotation = method.getAnnotation(CheckNotNull.class);
		String[] parameterNames = annotation.parameterNames();
		
		// 파라미터의 not null 체크
		for (int i = 0; i < args.length; i++) {
			if( args[i] == null ) {
				// null 일 때 해야 할 처리 코드
				throw new IllegalArgumentException("Parameter " + parameterNames[i] + " is null!!!");
			}
		}
		
		System.out.println("Proxy 의 handleCheckNotNull() 호출");
		
		return method.invoke(target, args);
		
		
		
		
	}
}