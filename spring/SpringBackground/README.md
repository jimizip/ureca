# SpringBackground

## 실습 목표

> 스프링이 내부적으로 동작하는 **배경 기술 3종**을 순수 Java로 직접 구현해보며 원리를 이해한다.
> 1. **Reflection** — 컴파일된 `.class`를 런타임에 들여다보고 객체 생성, 메소드 호출
> 2. **Annotation** — 커스텀 어노테이션을 정의하고 Reflection으로 읽어 처리
> 3. **Dynamic Proxy** — 어노테이션 + InvocationHandler로 부가 기능(null 체크)을 가로채 주입
>
> 세 기술이 합쳐지면 `@Controller`, `@Transactional` 같은 어노테이션을 스프링이 읽어 Proxy로 부가 기능을 끼우는 메커니즘(= Spring AOP의 토대)이 된다. (`reflection/Test.java` 주석: "내가 작성한 자바 파일의 .class 를 통해서 스프링이 거의 모든 것을 들여다 보고 객체도 생성, 메소드 호출 등이 가능하구나!")

## 핵심 개념

| 기술 | 정의                                                                          | 이 프로젝트의 구현 |
|------|-----------------------------------------------------------------------------|--------------------|
| Reflection | 런타임에 클래스의 메타정보(필드, 메소드, 생성자)를 조회하고 동적으로 객체 생성, 호출                           | `reflection/Test.java` (`Class.forName`, `getDeclaredConstructor`, `newInstance`) |
| Annotation | 코드에 메타데이터를 부착하는 표식. `@interface`로 정의, `@Retention`/`@Target`으로 보존 범위, 대상 지정 | `annotation/AboutMe.java`, `proxy/CheckNotNull.java` |
| Dynamic Proxy | 인터페이스 구현체를 런타임에 감싸 메소드 호출을 `InvocationHandler`로 가로채는 JDK 표준 기법              | `proxy/` (`Proxy.newProxyInstance` + `InvocationHandler`) |

> `@Retention` 정책 (코드 주석 기반):
> - `SOURCE` — 소스에만 존재, `.class` 미포함. 예: `@Override`
> - `CLASS` — `.class` 포함, 런타임 Reflection 불가. 예: Lombok `@Getter`/`@Setter`
> - `RUNTIME` — `.class` 포함, 런타임 Reflection 가능. 예: `@Controller` 등 스프링 대부분 어노테이션

> 공식 문서:
> [Java Reflection (Oracle Trail)](https://docs.oracle.com/javase/tutorial/reflect/) /
> [Annotations (Oracle Trail)](https://docs.oracle.com/javase/tutorial/java/annotations/) /
> [java.lang.reflect.Proxy](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/Proxy.html)

## 코드 분석

### 1. Reflection — `reflection/`

`User`는 평범한 POJO(필드 2개 + 생성자 + getter/setter). `Test.main()`이 이 클래스를 **이름만으로** 들여다본다.

```java
Class<?> userClass = Class.forName("com.mycom.myapp.reflection.User");
// (주석으로) getDeclaredFields / getDeclaredMethods / getDeclaredConstructors 로 메타정보 조회 가능

// 특정 생성자 파악 후 동적 객체 생성 (new 키워드 없이)
Constructor<?> constructor = userClass.getDeclaredConstructor(String.class, String.class);
Object obj = constructor.newInstance("홍길동", "1234");
System.out.println(obj);   // User [username=홍길동, password=1234]
```

→ `new User(...)` 대신 런타임에 생성자 정보를 찾아 객체를 만든다. 스프링이 빈을 생성하는 방식의 핵심.

### 2. Annotation — `annotation/`

**커스텀 어노테이션 정의** (`AboutMe.java`):

```java
@Retention(RetentionPolicy.RUNTIME)   // 런타임에 Reflection으로 읽으려면 RUNTIME 필수
@Target(ElementType.TYPE)             // class/interface/enum 등에 부착 가능
public @interface AboutMe {
    String love();   // 속성 = 추상 메소드로 표현
    String hate();
}
```

**사용** (`MyClass.java`):

```java
@AboutMe(love="soccer", hate="study")
public class MyClass {}
```

**읽기** (`Test.java`) — 스프링이 어노테이션을 처리하는 과정의 축소판:

```java
Class<?> myClass = Class.forName("com.mycom.myapp.annotation.MyClass");
for (Annotation annotation : myClass.getAnnotations()) {
    if (annotation instanceof AboutMe aboutMe) {
        System.out.println(aboutMe.love());   // soccer
        System.out.println(aboutMe.hate());   // study
    }
}
```

→ 어노테이션 자체는 데이터일 뿐, **읽어서 처리하는 코드가 따로 필요**하다.

### 3. Dynamic Proxy — `proxy/`

**메소드용 어노테이션** (`CheckNotNull.java`): `@Target(METHOD)`, 속성 `String[] parameterNames()`.
주석 강조: "annotation 자체는 실행코드를 가지지 않는다. 대응하는 실행 코드가 별도로 필요 → InvocationHandler"

**대상 인터페이스/구현** (`MyIF` / `MyIFImpl`): `m()`엔 없고 `m2()`에만 `@CheckNotNull(parameterNames={"param1","param2"})` 부착. 구현 메소드 안에 null 체크 코드가 없다 — Proxy가 대신 한다.

**핸들러** (`CheckNotNullInvocationHandler.java`):

```java
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
    if (targetMethod.isAnnotationPresent(CheckNotNull.class)) {
        return handleCheckNotNull(targetMethod, args);   // 어노테이션 있으면 null 검사
    }
    return method.invoke(target, args);                  // 없으면 bypass
}
```

`handleCheckNotNull`은 `parameterNames`를 읽어 인자가 null이면 `IllegalArgumentException`을 던지고, 통과하면 원본 메소드를 호출.

**조립** (`Test.java`):

```java
MyIF myIF = new MyIFImpl();
MyIF proxy = (MyIF) Proxy.newProxyInstance(
    myIF.getClass().getClassLoader(),
    myIF.getClass().getInterfaces(),
    new CheckNotNullInvocationHandler(myIF));
proxy.m(param1, param2);    // @CheckNotNull 없음 → bypass
proxy.m2(param1, param2);   // @CheckNotNull 있음 → null 검사 후 호출
```

→ Reflection(어노테이션 읽기) + Proxy(호출 가로채기) = Spring AOP가 부가 기능을 끼우는 원리 그대로.

## 면접 Q&A

**Q: Reflection이란? 스프링은 왜 쓰나?**
A: 런타임에 클래스의 필드, 메소드, 생성자 등 메타정보를 조회하고 동적으로 객체를 생성, 호출하는 기능. 스프링은 개발자가 작성한 클래스를 컴파일된 `.class`로 들여다보고(`Class.forName`/`getDeclaredConstructor`) 빈을 생성, 주입하므로 Reflection이 DI 컨테이너의 토대다.

**Q: `@Retention` SOURCE / CLASS / RUNTIME 차이는?**
A: SOURCE는 소스에만 남고 컴파일 시 제거(`@Override`), CLASS는 `.class`에 남지만 런타임 조회 불가(Lombok), RUNTIME은 `.class`에 남고 런타임 Reflection으로 읽을 수 있다. 스프링 어노테이션 대부분은 런타임에 읽혀야 하므로 RUNTIME.

**Q: 커스텀 어노테이션만 붙이면 동작하나?**
A: 아니다. 어노테이션은 메타데이터(표식)일 뿐 실행 코드가 없다. `isAnnotationPresent`/`getAnnotation`으로 읽어 처리하는 코드(InvocationHandler, BeanPostProcessor 등)가 반드시 별도로 있어야 동작한다.

**Q: JDK Dynamic Proxy의 한계는?**
A: `Proxy.newProxyInstance`는 **인터페이스 기반**이라 대상이 인터페이스를 구현해야 한다. 인터페이스 없는 클래스는 CGLIB(상속 기반) Proxy가 필요하다. 스프링 AOP는 상황에 따라 둘을 선택한다.

**Q: 이 프로젝트가 Spring AOP와 어떻게 연결되나?**
A: AOP의 핵심은 "특정 지점(어노테이션 붙은 메소드)에 부가 기능을 자동 주입"하는 것. 여기선 `@CheckNotNull`(어노테이션) + Reflection(읽기) + Dynamic Proxy(가로채기)로 그 메커니즘을 직접 구현했다. 스프링은 이를 추상화해 `@Aspect`/`@Before` 등으로 제공한다.

## 참고 출처

> - [The Reflection API (Oracle Java Tutorials)](https://docs.oracle.com/javase/tutorial/reflect/)
> - [Annotations (Oracle Java Tutorials)](https://docs.oracle.com/javase/tutorial/java/annotations/)
> - [java.lang.reflect.Proxy (Java SE 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/Proxy.html)
> - [java.lang.reflect.InvocationHandler (Java SE 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/InvocationHandler.html)
> - pom.xml 의존성: spring-boot-starter-parent `4.1.0`, Java `21` (실습 코드는 순수 JDK Reflection/Proxy만 사용, 스프링 런타임 불필요)

## 더 나아가기 (선택)

> - `reflection/Test.java`의 주석 처리된 `getDeclaredFields`/`getDeclaredMethods` 해제해 메타정보 전체 출력
> - `proxy/Test.java`에서 `param2=null`로 바꿔 `IllegalArgumentException` 발생 확인
> - `m()`에도 `@CheckNotNull` 붙여 두 메소드 모두 검사되는지 확인
> - 인터페이스 없는 클래스에 CGLIB로 Proxy 적용해보기 (JDK Proxy 한계 체감)
> - 이 구조를 Spring AOP(`@Aspect`+`@Around`)로 재작성해 비교 — [SpringAOP 프로젝트](../SpringAOP/README.md) 참고
