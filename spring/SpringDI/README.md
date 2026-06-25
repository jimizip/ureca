# SpringDI

## 실습 목표

> Spring 의 핵심인 **IoC(제어의 역전)** 와 **DI(의존성 주입)** 를 다양한 설정 방식으로 직접 구현하며 익힌다.
>
> 동일한 `Calculator` 예제를 5가지 방식으로 빈 등록 및 주입한다.
> - XML 기반 빈 설정
> - 어노테이션(`@Component`) + 컴포넌트 스캔
> - Java 설정 클래스(`@Configuration` + `@Bean`)
> - Has-A 관계에서의 의존성 주입 (생성자 주입)
> - 인터페이스 타입 주입 + `@Qualifier` 로 구현체 선택

## 핵심 개념

### IoC (Inversion of Control, 제어의 역전)
객체의 생성과 생명주기 관리 제어권을 개발자가 아닌 **컨테이너(Spring)** 가 가진다. 개발자가 `new` 로 직접 객체를 만들지 않고, 컨테이너가 만든 객체(빈)를 받아서 사용한다.

### DI (Dependency Injection, 의존성 주입)
객체가 필요로 하는 다른 객체(의존성)를 컨테이너가 주입해준다. IoC 를 구현하는 구체적 방법 중 하나.

> "The Spring IoC container ... is responsible for instantiating, configuring, and assembling the beans. The container gets its instructions on what objects to instantiate, configure, and assemble by reading configuration metadata."
>
> 출처: [Spring Framework - The IoC Container](https://docs.spring.io/spring-framework/reference/core/beans/introduction.html)

### Bean (빈)
Spring IoC 컨테이너가 관리하는 객체. 설정 메타데이터(XML, 어노테이션, Java Config)를 통해 등록된다.

### ApplicationContext
Spring IoC 컨테이너의 인터페이스. 빈을 등록하고 `getBean()` 으로 꺼내 쓴다.
- `ClassPathXmlApplicationContext` : XML 설정 기반
- `AnnotationConfigApplicationContext` : Java Config 기반

## 코드 분석

### 1. XML 기반 빈 설정 (`xml` 패키지)

`calc-xml.xml` 에서 `<bean>` 태그로 직접 객체를 등록한다.

```xml
<!-- Configuration By XML -->
<bean id="calculator" class="com.mycom.myapp.xml.Calculator"/><!-- id 로 di -->
```

```java
ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("xml/calc-xml.xml");
Calculator calculator = (Calculator) context.getBean("calculator");
```

`Calculator` 클래스 자체에는 어노테이션이 없다. 빈 등록 정보 전부를 XML 이 담당한다. `id` 값으로 `getBean()` 한다.

### 2. 어노테이션 + 컴포넌트 스캔 (`annotation` 패키지)

클래스에 `@Component` 를 붙이고, XML 에서는 스캔할 패키지만 지정한다.

```java
@Component
public class Calculator {
    public int add(int n1, int n2) { return n1+n2; }
}
```

```xml
<!-- Configuration By Annotation 이거 중요함 -->
<context:component-scan base-package="com.mycom.myapp.annotation"></context:component-scan>
```

`<context:component-scan>` 이 해당 패키지를 훑어 `@Component` 가 붙은 클래스를 자동으로 빈 등록한다. 빈 이름 기본값은 클래스명 첫 글자를 소문자로 바꾼 `calculator`.

### 3. Java 설정 클래스 (`configuration` 패키지)

XML 없이 Java 코드로 빈을 등록한다.

```java
@Configuration
public class CalcConfiguration {
    @Bean
    Calculator calculator() { // method 이름이 빈 이름
        return new Calculator();
    }
}
```

```java
AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CalcConfiguration.class);
Calculator calculator = (Calculator) context.getBean("calculator"); // Spring에게 객체 DI 의뢰
```

`@Bean` 메서드의 **이름** 이 빈 이름이 된다. 메서드 반환 객체가 컨테이너에 등록된다.

### 4. Has-A 관계 + 생성자 주입 (`hasa` 패키지)

`HasaCalculator` 가 `Calculator` 를 **가지고(Has-A)** 동작을 위임한다. 주석에 DI 3가지 방법이 모두 정리됨.

```java
@Component
public class HasaCalculator {
    // 1. Field DI -> 비추천
    // 2. Setter DI
    // 3. Constructor DI <- 추천(스프링이) 생성자 주입을 쓰자
    Calculator calculator;

    // @Autowired 필요 없음 (생성자 1개면 자동 주입)
    public HasaCalculator(Calculator calculator) {
        this.calculator = calculator;
    }

    public int add(int n1, int n2) {
        return calculator.add(n1, n2);
    }
}
```

- **필드 주입**: `@Autowired` 필드 직접. 테스트/불변성 측면에서 비추천.
- **세터 주입**: 선택적 의존성에 사용.
- **생성자 주입**: Spring 이 권장. 생성자가 하나면 `@Autowired` 생략 가능.

> "Constructor-based or setter-based DI? ... the Spring team generally advocates constructor injection, as it lets you implement application components as immutable objects and ensures that required dependencies are not null."
>
> 출처: [Spring Framework - Constructor-based or setter-based DI](https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html)

### 5. 인터페이스 주입 + `@Qualifier` (`all` 패키지)

`Calculator` 가 인터페이스이고 구현체가 2개(`CalculatorImpl`, `CalculatorImpl2`)일 때, 어떤 구현체를 주입할지 `@Qualifier` 로 지정한다.

```java
@Component("aaa")
public class CalculatorImpl implements Calculator { ... }

@Component("bbb")
public class CalculatorImpl2 implements Calculator { ... }
```

```java
@Component
public class HasaCalculator {
    Calculator calculator; // Interface 타입으로 필드 선언

    // 객체 여러 개일 때 @Qualifier로 특정 빈 선택
    public HasaCalculator(@Qualifier("aaa") Calculator calculator) {
        this.calculator = calculator;
    }
}
```

같은 타입 빈이 여러 개면 Spring 이 어떤 걸 주입할지 몰라 `NoUniqueBeanDefinitionException` 이 난다. `@Qualifier("빈이름")` 으로 명시해 해결한다.

## 면접 Q&A

**Q. IoC 와 DI 의 차이는?**
A. IoC 는 객체 생성/생명주기 제어권을 컨테이너에 넘기는 **원칙**. DI 는 그 원칙을 구현하는 **방법** 으로, 의존 객체를 외부(컨테이너)에서 주입하는 것. ([IoC Container 문서](https://docs.spring.io/spring-framework/reference/core/beans/introduction.html))

**Q. 빈을 등록하는 방법 3가지는?**
A. XML `<bean>`, 어노테이션 `@Component` + 컴포넌트 스캔, Java Config `@Configuration` + `@Bean`. 본 실습에서 셋 다 구현함.

**Q. 의존성 주입 방법 중 왜 생성자 주입을 권장하나?**
A. 객체를 불변(immutable)으로 만들 수 있고, 필수 의존성이 null 이 아님을 보장하며, 순환 참조를 컴파일/기동 시점에 발견할 수 있기 때문. Spring 공식 문서도 생성자 주입을 권장. ([Collaborators 문서](https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html))

**Q. 같은 타입 빈이 여러 개일 때 주입 충돌은 어떻게 해결하나?**
A. `@Qualifier("빈이름")` 으로 주입할 빈을 명시하거나, `@Primary` 로 기본 빈을 지정한다. 본 실습은 `@Qualifier("aaa")` 사용. ([Qualifier 문서](https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/autowired-qualifiers.html))

**Q. 생성자가 하나일 때 `@Autowired` 를 생략할 수 있는 이유는?**
A. Spring 4.3 부터 빈에 생성자가 하나뿐이면 `@Autowired` 없이도 자동으로 해당 생성자로 주입한다.

## 참고 출처

- [Spring Framework - The IoC Container](https://docs.spring.io/spring-framework/reference/core/beans/introduction.html)
- [Spring Framework - Dependency Injection](https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html)
- [Spring Framework - Using @Qualifier](https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/autowired-qualifiers.html)
- [Spring Framework - Java-based Container Configuration](https://docs.spring.io/spring-framework/reference/core/beans/java.html)
- pom.xml 의존성: `spring-boot-starter-parent` 4.1.0, Java 21

## 더 나아가기 (선택)

- `@Autowired` vs `@Resource` vs `@Inject` 차이
- `@Primary` 로 기본 빈 지정해 `@Qualifier` 없이 충돌 해결하기
- 빈 스코프(singleton, prototype)와 생명주기 콜백(`@PostConstruct`, `@PreDestroy`)
- 순환 참조(circular dependency) 문제와 생성자 주입이 이를 어떻게 조기에 잡아내는지
