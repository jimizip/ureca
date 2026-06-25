# 실행 방법

## 환경 요구사항

- JDK 21 (pom.xml `java.version=21`)
- Maven (프로젝트 내 `mvnw` 래퍼 포함, 별도 설치 불필요)
- Spring Boot 4.1.0 (`spring-boot-starter-parent`)

## DB 설정

이 프로젝트는 DB 를 사용하지 않는다. DI 개념 학습용 예제로, `application.properties` 에는 애플리케이션 이름만 설정되어 있다.

```properties
spring.application.name=SpringDI
```

## 빌드 및 실행

각 DI 방식은 **패키지별 `Test` 클래스의 `main()`** 으로 독립 실행한다. Spring Boot 웹 애플리케이션(`SpringDiApplication`)이 아니라, 각 `Test` 의 `main` 을 직접 실행해 결과를 확인한다.

### IDE 에서 실행 (권장)

각 패키지의 `Test.java` 를 열고 `main()` 실행.

| 패키지 | 실행 클래스 | DI 방식 | 출력 |
|--------|------------|---------|------|
| `xml` | `com.mycom.myapp.xml.Test` | XML 빈 등록 | `10` |
| `annotation` | `com.mycom.myapp.annotation.Test` | `@Component` + 스캔 | `10` |
| `configuration` | `com.mycom.myapp.configuration.Test` | Java Config `@Bean` | `10` |
| `hasa` | `com.mycom.myapp.hasa.Test` | 생성자 주입 (Has-A) | `HasaCalculator add()` / `10` |
| `all` | `com.mycom.myapp.all.Test` | 인터페이스 + `@Qualifier` | `CalculatorImpl add` / `HasaCalculator add()` / `10` |

### 명령행 빌드

```bash
# 의존성 다운로드 및 컴파일
./mvnw clean compile

# 특정 Test main 실행 (예: configuration)
./mvnw exec:java -Dexec.mainClass="com.mycom.myapp.configuration.Test"
```

> `exec:java` 사용 시 `exec-maven-plugin` 이 필요할 수 있다. IDE 에서 `main()` 직접 실행이 가장 간단하다.

### 패키징

```bash
./mvnw clean package
```

## 테스트

```bash
./mvnw test
```

`SpringDiApplicationTests` 의 컨텍스트 로딩 테스트가 실행된다.
