# Spring 실습 모음

Spring 핵심 개념부터 Spring Boot + MyBatis 게시판까지, 단계별 실습 프로젝트 모음이다.
아래는 학습 순서대로 정리한 목차다. 각 프로젝트의 상세 문서(README / SETUP / API)는 폴더별로 들어 있다.

> 공통 스택: Java 21, Spring Boot 4.1.0 (일부 순수 Java/MyBatis 단독), Maven, MySQL, MyBatis, JSP.
> DB 접속 정보는 학습용 로컬 기본 계정(`root`/`1234`)을 사용한다.

## 학습 순서

### 1. [SpringBackground](./SpringBackground/README.md) — 스프링의 배경 기술
> 스프링이 내부적으로 쓰는 배경 기술 3종(Reflection, Annotation, Dynamic Proxy)을 순수 Java 로 직접 구현한다.

세 기술이 합쳐져 `@Controller`, `@Transactional` 같은 어노테이션을 읽어 Proxy 로 부가 기능을 끼우는 메커니즘(AOP 의 토대)을 이해한다. 이후 모든 실습의 원리적 기반.

### 2. [SpringDI](./SpringDI/README.md) — IoC / DI 기초
> Spring 핵심인 IoC(제어의 역전)와 DI(의존성 주입)를 5가지 설정 방식으로 직접 구현한다.

XML / 어노테이션(`@Component`) / Java Config(`@Bean`) / 생성자 주입(Has-A) / 인터페이스 주입 + `@Qualifier`. 빈 등록과 주입의 모든 방식을 한 예제(`Calculator`)로 비교한다.

### 3. [SpringAOP](./SpringAOP/README.md) — 관점 지향 프로그래밍
> 횡단 관심사(로깅)를 비즈니스 로직에서 분리하는 Spring AOP 를 배운다.

Aspect / Pointcut / Advice / JoinPoint 핵심 요소와 AspectJ `execution` 표현식을 익히고, 표현식 범위(패키지, 반환 타입, 파라미터)에 따라 Advice 적용 대상이 어떻게 달라지는지 확인한다.

### 4. [SpringBootMVC](./SpringBootMVC/README.md) — MVC Controller
> Spring Boot MVC 의 Controller 가 HTTP 요청을 받아 처리하고 View 로 분기하는 전 과정을 익힌다.

URL 매핑(`@GetMapping` 등, PathVariable), 요청 파라미터 바인딩(단일/DTO/Map/Header), View 처리(JSP ViewResolver, Model, forward/redirect), 상태 유지(Session, Cookie)를 다룬다.

### 5. [SpringBootMVCFilterInterceptor](./SpringBootMVCFilterInterceptor/README.md) — Filter vs Interceptor
> 웹 요청 전후에 공통 작업(보안, 로그인 체크)을 끼워 넣는 두 관문을 비교 학습한다.

Filter(Servlet 표준, DispatcherServlet 앞단)와 Interceptor(Spring MVC, Controller 앞뒤)의 위치/등록 방법/실행 순서 차이를 로그인 인증 예제로 익힌다.

### 6. [SpringBootMVCDB](./SpringBootMVCDB/README.md) — MVC + DB (순수 JDBC)
> Spring Boot MVC 에서 DB 연동 CRUD 를 계층형 구조(Controller-Service-DAO-DTO)로 구현한다.

`DataSource`(HikariCP 커넥션 풀)에서 커넥션을 받아 순수 JDBC 로 처리하고, JSP 뷰와 `@ResponseBody` JSON 응답을 혼용한다. 다음 단계 MyBatis 전환의 출발점.

### 7. [MybatisBasic](./MybatisBasic/README.md) — 순수 MyBatis 기초
> Spring 없이 순수 MyBatis 만으로 DB 조회를 수행하며 동작 원리를 익힌다.

`SqlSessionFactoryBuilder` → `SqlSessionFactory` → `SqlSession` → `getMapper` 부트스트랩 흐름과 매퍼 인터페이스/XML 매핑을 직접 다룬다. JDBC 보일러플레이트가 어떻게 사라지는지 확인.

### 8. [SpringBootMVCDBMybatis](./SpringBootMVCDBMybatis/README.md) — Spring Boot + MyBatis CRUD
> 앞선 순수 JDBC CRUD 를 MyBatis 로 전환한다.

`mybatis-spring-boot-starter` 자동 구성, `@Mapper` 인터페이스로 DAO 구현체 제거, SQL 을 매퍼 XML 로 외부화. 6번 JDBC 버전과 DAO 계층만 다르고 나머지는 동일해 비교에 좋다.

### 9. [SpringBootMVCDBMybatis2](./SpringBootMVCDBMybatis2/README.md) — MyBatis 실전 기능
> 기본 CRUD 를 넘어 MyBatis 실전 기능을 사원(Emp) 도메인으로 익힌다.

`mapUnderscoreToCamelCase`, 매퍼 파일 분리, LIKE 검색, `<resultMap>` 수동 매핑, 동적 SQL(`<where>`/`<if>`), 생성자 주입, 클래스 레벨 `@ResponseBody`. API.md 포함.

### 10. [SpringBootMVCDBMybatisBoard](./SpringBootMVCDBMybatisBoard/README.md) — 종합 게시판
> 위 실습들을 종합해 로그인/인증이 있는 실전 게시판을 구현한다.

기능별 패키지(user/auth/board), 세션 로그인 + `HandlerInterceptor` 인증, 게시판 CRUD + 페이지네이션 + 검색 + 조회수(조인 테이블), `@Transactional` 트랜잭션, 전역/페이지 예외 처리 이원화. API.md 포함.

## 한눈에 보기

| 순서 | 프로젝트 | 핵심 주제 | DB | API 문서 |
|------|----------|-----------|----|----------|
| 1 | [SpringBackground](./SpringBackground/README.md) | Reflection / Annotation / Proxy | - | - |
| 2 | [SpringDI](./SpringDI/README.md) | IoC / DI | - | - |
| 3 | [SpringAOP](./SpringAOP/README.md) | AOP | - | - |
| 4 | [SpringBootMVC](./SpringBootMVC/README.md) | MVC Controller | - | - |
| 5 | [SpringBootMVCFilterInterceptor](./SpringBootMVCFilterInterceptor/README.md) | Filter / Interceptor | - | - |
| 6 | [SpringBootMVCDB](./SpringBootMVCDB/README.md) | MVC + JDBC CRUD | madang | O |
| 7 | [MybatisBasic](./MybatisBasic/README.md) | 순수 MyBatis | madang | - |
| 8 | [SpringBootMVCDBMybatis](./SpringBootMVCDBMybatis/README.md) | MyBatis CRUD | madang | O |
| 9 | [SpringBootMVCDBMybatis2](./SpringBootMVCDBMybatis2/README.md) | MyBatis 실전 기능 | madang | O |
| 10 | [SpringBootMVCDBMybatisBoard](./SpringBootMVCDBMybatisBoard/README.md) | 종합 게시판 | board | O |
