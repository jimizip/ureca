package com.mycom.myapp.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
// Spring AOP 와 aspectj 는 별개
// Spring AOP 가 aspectj 의 표현식을 사용
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LogAspect {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// pointcut
	// PointCut 설정
	// value 속성에 매칭 설정 - aspectj 표현식을 따른다.
	// execution(
	//    * 						return type
	//    com.mycom.myapp.aspect.. 	패키지(.) 및 하위 패키지(..)
	//	  * MyClass					클래스 (모든 클래스 또는 특정 클래스)  *Abc : ~ Abc 클래스
	//	  .*						메소드 (모든 메소드 또는 특정 메소드)  *Abc : ~ Abc 메소드
	//    (..)						.. 모든 타입의 파라미터,  (String, int) String, int 타입 2개 파라미터
	//								(String, int, ..) 앞의 2개가 String, int 타입 2개 파라미터이고 나머지는 상관없음.
	// )
//	@Pointcut(value="execution(* com.mycom.myapp.aspect..*.*(..))")
//	@Pointcut(value="execution(void com.mycom.myapp.aspect..*.*(..))")
//	@Pointcut(value="execution(int com.mycom.myapp.aspect..*.*(..))")
//	@Pointcut(value="execution(int com.mycom.myapp.aspect..*.*(String, int, String))") // AnotherBusinessProcess 까지 확인
//	@Pointcut(value="execution(int com.mycom.myapp.aspect..AnotherBusinessProcess.*(String, int, String))")
//	@Pointcut(value="execution(int com.mycom.myapp.aspect..*BusinessProcess.*(String, int, String))")
	@Pointcut(value="execution(int com.mycom.myapp.aspect.*BusinessProcess.*(String, int, String))")
	private void logPointcut() {}
	
	@Before("logPointcut()")
	public void beforeLog(JoinPoint joinPoint) {  // 개입하는 호출된 메소드
		logger.info("[LogAspect : before] " + joinPoint.getSignature().getName());
	}
	
//	@After("logPointcut()")
//	public void afterLog(JoinPoint joinPoint) {  // 개입하는 호출된 메소드
//		logger.info("[LogAspect : after] " + joinPoint.getSignature().getName());
//	}
}
