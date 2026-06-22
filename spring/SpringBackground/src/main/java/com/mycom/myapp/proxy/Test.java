package com.mycom.myapp.proxy;

import java.lang.reflect.Proxy;

public class Test {

	public static void main(String[] args) {
		// Spring DI 이용하지만, 우리는 코드의 단순함을 위해 직접 객체 생성
		MyIF myIF = new MyIFImpl();
		
		String param1 = "abc";
		String param2 = "def";
//		String param2 = null;
		
		// proxy 없이 직접 호출 
		// InvocationHander 동작 X
//		myIF.m(param1, param2);
//		myIF.m2(param1, param2);
		
		// proxy 를 이용한 효출
		// Proxy 타입의 객체를 이용해서 MyIF 인터페이스를 동일하게 가지는 proxy 객체
		MyIF proxy = (MyIF) Proxy.newProxyInstance(
			myIF.getClass().getClassLoader(), 
			myIF.getClass().getInterfaces(), 
			new CheckNotNullInvocationHandler(myIF)
		);

		proxy.m(param1, param2);
		proxy.m2(param1, param2);
	}

}