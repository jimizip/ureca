# SpringAOP

## 실습 목표

> 횡단 관심사(cross-cutting concern)인 **로깅**을 비즈니스 로직에서 분리하는 방법을 배운다.
> Spring AOP의 핵심 요소(Aspect, Pointcut, Advice, JoinPoint)와 AspectJ `execution` 표현식 작성법을 익히고,
> 표현식의 범위(패키지 점 개수, 반환 타입, 파라미터)에 따라 Advice 적용 대상이 어떻게 달라지는지 확인한다.

## 핵심 개념

> **AOP (Aspect Oriented Programming)**
> 핵심 로직에 흩어져 반복되는 부가 기능(로깅·트랜잭션·보안 등)을 한 곳(Aspect)으로 모아, 원하는 지점에 자동 주입하는 기법. 핵심 로직과 부가 기능의 관심사를 분리한다.
> Spring AOP는 **런타임 Proxy** 기반으로 동작하며, AspectJ와는 별개지만 AspectJ의 **포인트컷 표현식 문법**을 차용한다. (`LogAspect.java` 주석: "Spring AOP 와 aspectj 는 별개 / Spring AOP 가 aspectj 의 표현식을 사용")

| 용어 | 정의 | 이 프로젝트의 구현 |
|------|------|--------------------|
| Aspect | 부가 기능 모듈 | `LogAspect` (`@Aspect @Component`) |
| Pointcut | Advice를 적용할 대상 선정 표현식 | `LogAspect.logPointcut()` (`@Pointcut`) |
| Advice | 끼워 넣을 동작 + 실행 시점 | `beforeLog()` (`@Before`) |
| JoinPoint | Advice가 개입하는 실제 호출 지점 | `beforeLog(JoinPoint)` 인자 |
| Target | 부가 기능이 적용될 원본 객체 | `BusinessProcess`, `AnotherBusinessProcess` 등 |
| Proxy | Target을 감싸 Advice를 실행하는 대리 객체 | 런타임 자동 생성 (`<aop:aspectj-autoproxy/>`) |

> 공식 정의: [Spring Framework — Aspect Oriented Programming with Spring](https://docs.spring.io/spring-framework/reference/core/aop.html)

## 코드 분석

### 1. Aspect와 Pointcut — `LogAspect.java`

```java
@Component
@Aspect
public class LogAspect {

    @Pointcut(value="execution(int com.mycom.myapp.aspect.*BusinessProcess.*(String, int, String))")
    private void logPointcut() {}

    @Before("logPointcut()")
    public void beforeLog(JoinPoint joinPoint) {  // 개입하는 호출된 메소드
        logger.info("[LogAspect : before] " + joinPoint.getSignature().getName());
    }
}
```

`@Aspect`로 Aspect 선언, `@Component`로 빈 등록. `@Pointcut`이 대상을 고르고, `@Before`가 대상 메소드 실행 **직전**에 로그를 남긴다. `JoinPoint`로 가로챈 호출 정보(`getSignature().getName()` → 메소드 이름)에 접근한다.

**`execution` 표현식 해부** (코드 주석 기반):

| 부분 | 값 | 의미 |
|------|----|------|
| 반환 타입 | `int` | `int` 반환 메소드만 (`void`·기타 제외) |
| 패키지 | `com.mycom.myapp.aspect` | `.` 단일 점 = **해당 패키지만** (하위 패키지 미포함) |
| 클래스 | `*BusinessProcess` | 이름이 `BusinessProcess`로 끝나는 클래스 |
| 메소드 | `.*` | 모든 메소드 |
| 파라미터 | `(String, int, String)` | 정확히 이 3개 타입 |

> 점 개수: `.` 한 개 = 해당 패키지만 / `..` 두 개 = 해당 패키지 + 모든 하위 패키지.
> LogAspect 상단엔 학습용으로 넓은 표현식 → 좁은 표현식 순으로 좁혀가는 단계가 주석 처리되어 남아 있다.

### 2. Target — `BusinessProcess` / `AnotherBusinessProcess` / `SubBusinessProcess`

세 클래스 모두 동일한 구조. 주석: "AOP 가 없는 경우, 이 클래스의 각 메소드는 호출 앞/뒤로 로그를 남겨야 하는 규칙" → AOP가 이 반복 로그 코드를 제거한다.

```java
public void no_bp(){ ... }                                       // void, 파라미터 0
public int int_bp(){ ... }                                       // int, 파라미터 0
public int String_int_String_bp(String s1, int i, String s2){...}// int, (String,int,String)
```

`SubBusinessProcess`만 `com.mycom.myapp.aspect.sub` **하위 패키지**에 위치 → 단일 점 표현식에서 제외된다.

### 3. 매칭 결과 — `Test.main()`의 호출별 Advice 적용

| 호출 | 반환 | 클래스 | 패키지 | 파라미터 | 적용? |
|------|------|--------|--------|----------|:---:|
| `bp.no_bp()` | void | BusinessProcess | aspect | () | ❌ |
| `bp.int_bp()` | int | BusinessProcess | aspect | () | ❌ |
| `bp.String_int_String_bp("a",0,"b")` | int | BusinessProcess | aspect | (String,int,String) | ✅ |
| `abp.String_int_String_bp(...)` | int | AnotherBusinessProcess | aspect | (String,int,String) | ✅ |
| `sub.String_int_String_bp(...)` | int | SubBusinessProcess | aspect.**sub** | (String,int,String) | ❌ |

→ 반환 타입·파라미터·패키지 세 조건을 모두 만족하는 `bp`·`abp`의 `String_int_String_bp`에만 `[LogAspect : before]` 로그가 붙는다.

### 4. 자동 프록시 설정 — `aspect.xml`

```xml
<context:component-scan base-package="com.mycom.myapp.aspect"/>  <!-- @Component/@Aspect 빈 스캔 -->
<aop:aspectj-autoproxy/>                                          <!-- @Aspect 발견 시 Proxy 자동 구성 -->
```

`Test.main()`은 이 XML로 `ClassPathXmlApplicationContext`를 띄워 데모를 실행한다(Spring Boot 웹 구동 아님).

## 면접 Q&A

**Q: AOP를 왜 쓰나?**
A: 로깅·트랜잭션처럼 여러 메소드에 반복되는 부가 기능(횡단 관심사)을 핵심 비즈니스 로직에서 분리하기 위해. 코드 중복이 사라지고 핵심 로직 가독성이 올라가며, 부가 기능 변경 시 Aspect 한 곳만 고치면 된다. (`BusinessProcess` 주석의 "각 메소드가 앞/뒤로 로그를 남겨야 하는 규칙" 문제를 AOP가 해결)

**Q: Spring AOP와 AspectJ 차이는?**
A: 별개 기술. Spring AOP는 **런타임 Proxy** 기반이라 스프링 빈의 메소드 호출에만, public 메소드 중심으로 동작한다. AspectJ는 컴파일/로드 타임 위빙으로 더 강력하다. Spring AOP는 AspectJ의 **포인트컷 표현식 문법만** 빌려 쓴다. (`LogAspect.java` 주석 근거)

**Q: `execution` 표현식에서 점 한 개(`.`)와 두 개(`..`) 차이는?**
A: `.aspect.`는 해당 패키지만, `.aspect..`는 해당 패키지와 모든 하위 패키지. 그래서 `aspect.sub`의 `SubBusinessProcess`는 현재 단일 점 표현식에서 빠진다.

**Q: `@Before`/`@After`/`@Around` 차이는?**
A: `@Before`는 대상 메소드 실행 직전, `@After`는 실행 후(예외 포함), `@Around`는 실행 전후를 모두 감싸 `ProceedingJoinPoint.proceed()`로 직접 호출을 제어한다(실행 시간 측정 등). 이 프로젝트는 `@Before`만 활성, `@After`는 주석 처리.

**Q: Proxy는 어떻게 생성되나?**
A: `<aop:aspectj-autoproxy/>`(또는 Boot의 `spring-boot-starter-aop` 자동 설정)가 `@Aspect` 빈을 찾아 대상 빈을 감싸는 Proxy를 자동 생성한다. 클라이언트는 Proxy를 호출하고, Proxy가 Advice → 실제 메소드 순으로 위임한다.

## 참고 출처

> - [Spring Framework Reference — Aspect Oriented Programming with Spring](https://docs.spring.io/spring-framework/reference/core/aop.html)
> - [Spring Framework Reference — Declaring a Pointcut](https://docs.spring.io/spring-framework/reference/core/aop/ataspectj/pointcuts.html)
> - [Spring Boot — spring-boot-starter-aop](https://docs.spring.io/spring-boot/reference/using/build-systems.html#using.build-systems.starters)
> - pom.xml 의존성: spring-boot-starter-parent `4.1.0`, spring-boot-starter-aop `4.0.0-M1`, Java `21`

## 더 나아가기 (선택)

> - Pointcut `.` → `..`로 바꿔 `SubBusinessProcess`까지 잡히는지 확인
> - 반환 타입 `int` → `*`로 넓혀 `void no_bp()`·`int_bp()`도 매칭되는지 확인
> - `@After` 주석 해제 후 before/after 실행 순서 관찰
> - `@Around`로 바꿔 메소드 실행 시간 측정 추가
> - `@AfterReturning` / `@AfterThrowing`으로 반환값·예외 처리 다뤄보기
