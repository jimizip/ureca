# 실행 방법

## 환경 요구사항

| 항목 | 버전 |
|------|------|
| JDK | 21 (`java.version` = 21) |
| 빌드 도구 | Maven (프로젝트 내 `./mvnw` 래퍼 포함) |
| Spring Boot | parent (pom 기준) |
| 내장 WAS | Tomcat (spring-boot-starter-web) |

## DB 설정

> 해당 없음. DB 미사용. 로그인 상태는 HttpSession에 저장.

## 활성화 상태 주의

> - **Interceptor**: `WebMvcConfig`에 `@Configuration`이 살아 있어 **활성**. 로그인 인증이 동작한다.
> - **Filter**: `FilterConfig`의 `@Configuration`과 각 필터의 `@Component`가 모두 주석 처리되어 **비활성**.
>   필터 동작을 보려면 `FilterConfig`의 `//@Configuration` 주석을 해제하고 재시작.

## 빌드 및 실행

```bash
cd spring/SpringBootMVCFilterInterceptor
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

> 기본 포트 8080. 정적 페이지는 `src/main/resources/static/`에 위치.

## 동작 확인 시나리오 (Interceptor)

```bash
# 1. 로그인 없이 보호 페이지 접근 → 차단 ("Need Login")
curl http://localhost:8080/admin

# 2. 로그인 (세션에 login=success 저장)
curl -c cookie.txt http://localhost:8080/login

# 3. 같은 세션으로 보호 페이지 접근 → 통과
curl -b cookie.txt http://localhost:8080/admin

# 4. 로그아웃 (세션 무효화)
curl -b cookie.txt http://localhost:8080/logout
```

> 제외 경로(`/`, `/index.html`, `/no-login`, `/login`, `/logout.html`)는 로그인 없이 접근 가능.
> 각 단계에서 실행 콘솔에 `LoginInterceptor >> preHandle ...` 로그가 찍힌다.

## 테스트

```bash
./mvnw test
```

> `SpringBootMvcFilterInterceptorApplicationTests.contextLoads()` — 컨텍스트 로딩 확인 기본 테스트.
