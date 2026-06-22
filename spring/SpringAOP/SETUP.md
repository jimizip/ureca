# 실행 방법

## 환경 요구사항

| 항목 | 버전 |
|------|------|
| JDK | 21 (`java.version` = 21) |
| 빌드 도구 | Maven (프로젝트 내 `./mvnw` 래퍼 포함) |
| Spring Boot | 4.1.0 (parent) |
| spring-boot-starter-aop | 4.0.0-M1 |

## DB 설정

> 해당 없음. DB·데이터소스 사용하지 않는다. `application.properties`엔 앱 이름만 설정되어 있다.

## 빌드 및 실행

### 1. 빌드

```bash
cd spring/SpringAOP
./mvnw clean package        # Windows: mvnw.cmd clean package
```

### 2. AOP 데모 실행 — `Test.main()`

이 프로젝트의 AOP 동작은 Spring Boot 웹 서버가 아니라 `Test` 클래스의 `main`으로 확인한다. `xml/aspect.xml`로 컨텍스트를 직접 띄워 빈을 가져와 호출한다.

```bash
./mvnw compile
./mvnw exec:java -Dexec.mainClass="com.mycom.myapp.aspect.Test"
```

> 또는 IDE에서 `com.mycom.myapp.aspect.Test`를 직접 Run.

**기대 출력** — 매칭되는 호출 직전에만 `[LogAspect : before]` 로그가 찍힌다:

```
BusinessProcess no_bp()
BusinessProcess int_bp()
[LogAspect : before] String_int_String_bp
BusinessProcess String_int_String_bp()
[LogAspect : before] String_int_String_bp
AnotherBusinessProcess String_int_String_bp()
SubBusinessProcess String_int_String_bp()
```

> `sub.String_int_String_bp()`는 하위 패키지라 로그가 붙지 않는다.

### 3. Spring Boot 앱 실행 (선택)

`SpringAopApplication`도 있으나 AOP 데모와는 별개의 진입점이다.

```bash
./mvnw spring-boot:run
```

## 테스트

```bash
./mvnw test
```

> `SpringAopApplicationTests.contextLoads()` — 스프링 컨텍스트가 정상 로딩되는지만 확인하는 기본 테스트.
