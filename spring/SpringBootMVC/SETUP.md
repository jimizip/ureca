# 실행 방법

## 환경 요구사항

| 항목 | 버전 |
|------|------|
| JDK | 21 (`java.version` = 21) |
| 빌드 도구 | Maven (프로젝트 내 `./mvnw` 래퍼 포함) |
| Spring Boot | 3.4.1 (parent) |
| 내장 WAS | Tomcat (JSP 렌더링 위해 tomcat-embed-jasper 포함) |

## DB 설정

> 해당 없음. DB 미사용.

## 주요 설정 (`application.properties`)

```properties
spring.mvc.view.prefix=/WEB-INF/jsp/      # JSP ViewResolver prefix
spring.mvc.view.suffix=.jsp               # JSP ViewResolver suffix
server.servlet.session.persistent=false   # 재시작 시 세션 복원 안 함
```

> JSP는 `src/main/webapp/WEB-INF/jsp/`에 위치. ViewResolver가 `return "이름"` → `/WEB-INF/jsp/이름.jsp`로 변환.

## 빌드 및 실행

```bash
cd spring/SpringBootMVC
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

> 기본 포트 8080. 브라우저 또는 curl로 아래 엔드포인트 호출 (전체 명세는 [API.md](API.md) 참고).

빠른 확인:

```bash
curl http://localhost:8080/hello          # hello.jsp 렌더링
curl http://localhost:8080/viewTest3      # Model 담아 JSP 렌더링
curl "http://localhost:8080/param4?bookName=spring"   # @RequestParam 필수 파라미터
```

> 대부분의 컨트롤러 메소드는 `System.out.println`으로 콘솔에 값을 찍으므로, 실행 콘솔에서 동작을 확인한다.

## 패키징 (선택)

```bash
./mvnw clean package
java -jar target/SpringBootMVC-0.0.1-SNAPSHOT.jar
```

## 테스트

```bash
./mvnw test
```

> `SpringBootMvcApplicationTests.contextLoads()` — 스프링 컨텍스트 로딩만 확인하는 기본 테스트.
