package com.mycom.myapp.proxy;

// 비즈니스 로직을 구현한 클래스
// MyIF 에 정의된 메소드가 호출될 때 파라미터가 null 여부 사전 체크
// 개별 메소드 내 코드로 체크 X, proxy 이용
// 이 클래스외 다른 클래스에서도 동일한 null 여부 사전 체크 및 대응이 필요한...
public class MyIFImpl implements MyIF{

	@Override
	public void m(String param1, String param2) {
		// 코드 내에서 null 체크 X
//		if( param1 == null ) {}
		System.out.println("m()");
		System.out.println(param1 + ", " + param2);
	}

	@Override
	@CheckNotNull(parameterNames= {"param1", "param2"})
	public void m2(String param1, String param2) {
		System.out.println("m2()");
		System.out.println(param1 + ", " + param2);
	}
}