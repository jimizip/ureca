# SpringBootMVC

## 실습 목표

> Spring Boot MVC의 **Controller**가 HTTP 요청을 받아 처리하고 View로 분기하는 전 과정을 익힌다.
> 1. URL 매핑 (`@GetMapping`/`@PostMapping`/`@PutMapping`/`@DeleteMapping`, PathVariable, 와일드카드)
> 2. 요청 파라미터 바인딩 (단일 값, DTO, Map, Header)
> 3. View 처리 (JSP ViewResolver, Model/ModelAndView, forward/redirect)
> 4. 상태 유지 (Session, Cookie)

## 핵심 개념

| 개념 | 정의 | 이 프로젝트의 구현 |
|------|------|--------------------|
| MVC 패턴 | 요청을 Controller가 받아 Model을 만들고 View로 분기하는 웹 아키텍처 | `controller/` + `WEB-INF/jsp/` |
| DispatcherServlet | 모든 요청을 받아 적절한 핸들러(Controller 메소드)로 분배하는 프론트 컨트롤러 | `spring-boot-starter-web`가 자동 구성 |
| 핸들러 매핑 | URL + HTTP 메소드를 Controller 메소드에 연결 | `@GetMapping` 등 |
| 파라미터 바인딩 | 요청 파라미터를 메소드 인자로 자동 변환, 주입 | `@RequestParam`, DTO, `@RequestHeader` |
| ViewResolver | 논리 뷰 이름을 실제 뷰 파일 경로로 변환 (prefix + 이름 + suffix) | `application.properties`의 prefix/suffix |

> ViewResolver 설정 (`application.properties`):
> `spring.mvc.view.prefix=/WEB-INF/jsp/`, `spring.mvc.view.suffix=.jsp`
> 따라서 `return "hello"` → `/WEB-INF/jsp/hello.jsp`

> 공식 문서: [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html), [Spring Boot Web](https://docs.spring.io/spring-boot/reference/web/servlet.html)

## 코드 분석

### 1. URL 매핑 — `UrlMappingController`

```java
@GetMapping("/m1")          // RequestMapping(method=GET)의 축약형. 과거 @RequestMapping 방식은 지양
@PostMapping("/post")       // POST, PUT, DELETE 각각 전용 어노테이션
@GetMapping("/books/{bookId}")               // PathVariable, REST 스타일 (/books?bookId=5 → /books/5)
public void m5(@PathVariable Integer bookId)

@GetMapping({"/url1", "/url2"})   // 복수 URL을 한 메소드로
@GetMapping("/sub/*")             // 한 단계 와일드카드
@GetMapping("/sub2/**")           // 모든 하위 경로 와일드카드
```

> 주의 (코드 주석): `/books/{bookId}`와 `/books/{price}`는 둘 다 `GET /books/{정수}`로 매핑돼 충돌 →
> `java.lang.IllegalStateException: Ambiguous handler methods mapped for '/books/5000'`. 같은 패턴 중복 매핑 금지.

### 2. 파라미터 바인딩 — `ParamController`

```java
public void m1(HttpServletRequest request)   // 서블릿 방식 직접 사용
public void m2(String bookId)                 // 스프링이 동명 파라미터 자동 주입
public void m3(Integer bookId, String name)   // String → Integer 자동 변환
public void m4(@RequestParam String bookName) // @RequestParam = 필수 파라미터 (없으면 400)
public void m5(CarDto carDto)                 // 파라미터를 DTO에 자동 바인딩 (setter 기반)
public void m6(@RequestParam Map<String,String> params)  // 가변 파라미터를 Map으로
public void m7(@RequestHeader("User-Agent") String ua)   // HTTP 헤더 바인딩
```

> 타입 주의: `int bookId`로 받으면 파라미터 누락 시 `Optional int parameter ... cannot be translated into a null value` 오류 → 래퍼 타입 `Integer` 사용. DTO는 기본 생성자 필수(`CarDto()`).

### 3. View 처리 — `ViewController`

```java
return "viewTest1";       // forward → /WEB-INF/jsp/viewTest1.jsp
return "sub/viewTest2";   // 하위 폴더 → /WEB-INF/jsp/sub/viewTest2.jsp
return "redirect:viewTest1";   // redirect (forward와 구분)

// Model 전달 두 방식
model.addAttribute("carDto", new CarDto(...));   // Model 파라미터
ModelAndView mav = new ModelAndView(); mav.addObject(...); mav.setViewName("viewTest4");

public void voidMethod() {}   // void 반환 → 요청 URL과 같은 이름의 jsp를 찾음
```

> 흐름 (주석): client 요청 → Controller에서 Business Logic 처리(service-dao-db) → Model을 담아 View(JSP)로 forward.

### 4. Session / Cookie — `SessionController`, `CookieController`

```java
// Session: 서버 측 상태 저장
session.setAttribute("username", username);   // 로그인 (실제론 POST 권장)
session.invalidate();                          // 로그아웃 = 전체 세션 무효화

// Cookie: 클라이언트 측 상태 저장 (@RequestMapping("/cookie")로 클래스 레벨 매핑)
Cookie c = new Cookie("domain", "board");
c.setMaxAge(24*60*60); c.setHttpOnly(true);   // 유효기간 1일, JS 접근 차단(보안)
response.addCookie(c);
@CookieValue(defaultValue="없음") String domain // 쿠키 읽기
// 삭제 = 같은 이름 쿠키를 setMaxAge(0)으로 덮어쓰기
```

## 면접 Q&A

**Q: `@Controller`와 `@RestController` 차이는?**
A: `@Controller`는 반환 문자열을 뷰 이름으로 해석해 JSP 등으로 forward한다(이 프로젝트). `@RestController`는 `@Controller`+`@ResponseBody`로, 반환값을 HTTP 응답 본문(JSON 등)에 직접 쓴다.

**Q: `@RequestParam`을 생략해도 파라미터가 바인딩되는데 차이는?**
A: 생략 시 해당 파라미터는 선택(없으면 null) 취급. `@RequestParam`을 붙이면 기본적으로 필수가 되어 누락 시 400 에러. 필수 여부, 기본값, 다른 이름 매핑을 명시할 때 사용.

**Q: 파라미터를 `int`로 받으면 왜 오류가 나나?**
A: 파라미터 누락 시 스프링이 null을 넘기려 하는데 원시 타입 `int`는 null을 가질 수 없어 `IllegalStateException`. 래퍼 타입 `Integer`로 받아야 null 허용. DTO도 기본 생성자가 없으면 바인딩 실패.

**Q: forward와 redirect 차이는?**
A: forward는 서버 내부에서 요청을 JSP로 넘겨 URL이 그대로(요청 1회). redirect(`redirect:`)는 클라이언트에 새 URL로 재요청을 지시(요청 2회, URL 변경). PRG 패턴 등 새로고침 중복 제출 방지에 redirect 사용.

**Q: Session과 Cookie 차이는?**
A: Session은 서버에 데이터를 저장하고 클라이언트엔 세션 ID만 쿠키로 전달. Cookie는 데이터 자체를 클라이언트에 저장. 민감 정보는 Session, 가벼운 식별 정보는 Cookie. `setHttpOnly(true)`로 JS 접근을 막아 XSS 쿠키 탈취를 완화한다.

## 참고 출처

> - [Spring Web MVC (Spring Framework Reference)](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
> - [Spring Boot Servlet Web Applications](https://docs.spring.io/spring-boot/reference/web/servlet.html)
> - [Handler Methods, Method Arguments](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/arguments.html)
> - pom.xml 의존성: spring-boot-starter-parent `3.4.1`, spring-boot-starter-web, tomcat-embed-jasper(JSP), Java `21`

## 더 나아가기 (선택)

> - `/books/{price}` 메소드를 주석 처리해 Ambiguous 매핑 오류 해소
> - `@RestController` + `@ResponseBody`로 JSON 응답 비교
> - `@ModelAttribute`, `@RequestBody`(JSON 바인딩) 추가 실습
> - 인터셉터로 로그인 세션 체크 공통화 — [SpringBootMVCFilterInterceptor 프로젝트](../SpringBootMVCFilterInterceptor) 참고
