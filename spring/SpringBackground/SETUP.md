# 실행 방법

## 환경 요구사항

| 항목 | 버전 |
|------|------|
| JDK | 21 (`java.version` = 21) |
| 빌드 도구 | Maven (프로젝트 내 `./mvnw` 래퍼 포함) |
| Spring Boot | 4.1.0 (parent) |

> 실습 코드는 순수 JDK의 Reflection / Annotation / Dynamic Proxy만 사용한다. 스프링 런타임 없이 `main` 메소드로 직접 실행.

## DB 설정

> 해당 없음. DB·데이터소스 사용하지 않는다. `application.properties`엔 앱 이름만 설정됨.

## 빌드 및 실행

### 빌드

```bash
cd spring/SpringBackground
./mvnw clean compile        # Windows: mvnw.cmd clean compile
```

### 데모 3종 실행

각 패키지의 `Test.main()`을 직접 실행한다 (IDE에서 Run 또는 아래 명령).

**1) Reflection** — `com.mycom.myapp.reflection.Test`

```bash
./mvnw exec:java -Dexec.mainClass="com.mycom.myapp.reflection.Test"
```
기대 출력:
```
User [username=홍길동, password=1234]
```

**2) Annotation** — `com.mycom.myapp.annotation.Test`

```bash
./mvnw exec:java -Dexec.mainClass="com.mycom.myapp.annotation.Test"
```
기대 출력:
```
soccer
study
```

**3) Dynamic Proxy** — `com.mycom.myapp.proxy.Test`

```bash
./mvnw exec:java -Dexec.mainClass="com.mycom.myapp.proxy.Test"
```
기대 출력 (`m()`은 bypass, `m2()`는 @CheckNotNull 검사 통과 후 실행):
```
m()
abc, def
Proxy 의 handleCheckNotNull() 호출
m2()
abc, def
```
> `proxy/Test.java`에서 `param2=null`로 바꾸면 `m2()` 호출 시 `IllegalArgumentException: Parameter param2 is null!!!` 발생.

### Spring Boot 앱 실행 (선택)

`SpringBackgroundApplication`도 있으나 위 데모와 별개의 진입점이다.

```bash
./mvnw spring-boot:run
```

## 테스트

```bash
./mvnw test
```

> `SpringBackgroundApplicationTests.contextLoads()` — 스프링 컨텍스트 로딩만 확인하는 기본 테스트.
