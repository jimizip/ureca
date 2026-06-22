package com.mycom.myapp.reflection;

import java.lang.reflect.Constructor;

public class Test {

	public static void main(String[] args) throws Exception{
		// Java reflection api 를 이용해서 User 클래스를 들여다본다.
		Class<?> userClass = Class.forName("com.mycom.myapp.reflection.User");
		
		// class 이름
//		System.out.println(userClass.getName());
		
		// field
//		Field[] fields = userClass.getDeclaredFields();
//		for (Field field : fields) {
//			System.out.println(field.getName());
//			System.out.println(field.getType());
//		}
		
		// method
//		Method[] methods = userClass.getDeclaredMethods();
//		for (Method method : methods) {
//			System.out.println(
//				method.getName() + ", " + method.getParameterCount() + ", " + method.getReturnType()
//			);
//		}

		// constructor
//		Constructor<?>[] constructors = userClass.getDeclaredConstructors();
//		for (Constructor<?> constructor : constructors) {
//			System.out.println(
//				constructor.getName() + ", " + constructor.getParameterCount()
//			);
//		}
		
		// 자바 reflection api 를 통해서 특정 클래스의 컴파일된 .class 파일을 들여다 보고 
		// 그 클래스의 거의 모든 메타 정보를 알 수 있다.
		
		// constructor 를 이용한 객체 생성
		// 아래와 같은 방식 X
//		com.mycom.myapp.reflection.User user = new com.mycom.myapp.reflection.User();
		
		// 생성자 정보를 파악 후, 특정 생성자를 이용해서 객체를 생성
		Constructor<?> constructor = userClass.getDeclaredConstructor(String.class, String.class);
		Object obj = constructor.newInstance("홍길동", "1234");
		System.out.println(obj);
		
		// 다른 사람이 작성한 자바 파일의 .class 를 통해 거의 모든 것을 들여다 보고, 객체도 생성, 메소드 호출 등 이 가능하구나!
		// 내가 작성한 자바 파일의 .class 를 통해서 스프링이 거의 모든 것을 들여다 보고 객체도 생성, 메소드 호출 등이 가능하구나!
		
		
		
	}

}