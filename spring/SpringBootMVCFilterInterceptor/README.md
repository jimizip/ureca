# SpringBootMVCFilterInterceptor

## 실습 목표

> 웹 요청 처리 전후에 공통 작업(보안, 로그인 체크 등)을 끼워 넣는 두 가지 관문을 비교 학습한다.
> 1. **Filter** (Servlet 표준) — DispatcherServlet 앞단에서 모든 요청을 가로챔
> 2. **Interceptor** (Spring MVC) — DispatcherServlet 뒤, Controller 앞뒤에서 가로챔
>
> 로그인 여부를 세션으로 확인해 인증 안 된 요청을 차단하는 예제로 둘의 위치, 등록 방법, 실행 순서 차이를 익힌다.

## 핵심 개념

| 구분 | Filter | Interceptor |
|------|--------|-------------|
| 소속 | Servlet 스펙 (`jakarta.servlet.Filter`) | Spring MVC (`HandlerInterceptor`) |
| 위치 | DispatcherServlet **앞** (요청 최외곽) | DispatcherServlet **뒤**, Controller 앞뒤 |
| 핵심 메소드 | `doFilter(req, res, chain)` | `preHandle` / `postHandle` / `afterCompletion` |
| 통과/차단 | `chain.doFilter()` 호출 = 통과 | `preHandle` 반환 `true`=통과, `false`=차단 |
| 등록 | `FilterRegistrationBean` (Java Config) / `@Component` / web.xml | `WebMvcConfigurer.addInterceptors()` |
| 스프링 빈 접근 | 제한적 | 자유로움 (`@Autowired` 등) |

> 비유 (코드 주석): Filter는 "입구를 지키는 경비원". 첫 경비원을 통과한 요청이 다음 경비원으로. 각 경비원은 주로 보안 이슈를 검증.

> 공식 문서: [Filters (Spring Boot)](https://docs.spring.io/spring-boot/reference/web/servlet.html#web.servlet.embedded-container.servlets-filters-listeners), [HandlerInterceptor](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/interceptors.html)

## 코드 분석

### 1. Filter — `MyFilter`, `MyFilter2`

```java
public class MyFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        System.out.println("MyFilter >> Before : " + req.getRequestURI());
        chain.doFilter(request, response);   // 통과 (다음 필터/서블릿으로)
        // response.getWriter().write("Invalid Request");  // 거절 시 chain 호출 안 함
        System.out.println("MyFilter << After : " + req.getRequestURI());
    }
}
```

`chain.doFilter()` **이전** = 요청 처리 전, **이후** = 응답 처리 후. 호출을 생략하면 요청이 차단된다.

### 2. Filter 등록과 순서 — `FilterConfig`

```java
//@Configuration   // ← 현재 주석 처리: 필터 미동작 상태
public class FilterConfig {
    @Bean
    FilterRegistrationBean<MyFilter> registrationMyFilter() {
        var bean = new FilterRegistrationBean<>();
        bean.setFilter(new MyFilter());
        bean.addUrlPatterns("/admin");   // /admin 요청에만 적용
        bean.setOrder(1);                // 낮은 숫자가 먼저
        return bean;
    }
    // MyFilter2: addUrlPatterns("/*") 모든 요청, setOrder(2)
}
```

> 현재 `FilterConfig`의 `@Configuration`과 각 필터의 `@Component`가 모두 주석 처리되어 **필터는 비활성**. 활성화하려면 `@Configuration` 주석 해제(Java Config 방식). 등록 방법 3가지: ① `@Component` ② Java Config(`FilterRegistrationBean`) ③ web.xml. 순서 제어가 필요하면 Java Config 권장(`setOrder`).

### 3. Interceptor — `LoginInterceptor`

```java
@Component
public class LoginInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("login");
        if ("success".equals(login)) return true;   // 통과
        response.getWriter().write("Need Login");    // 거절
        return false;
    }
    public void postHandle(...)        // Controller 실행 후, View 렌더링 전
    public void afterCompletion(...)   // View 렌더링 후 (정리 작업)
}
```

세션의 `login` 속성이 `"success"`면 통과, 아니면 `Need Login` 출력 후 차단(`false`).

### 4. Interceptor 등록 — `WebMvcConfig`

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired private LoginInterceptor loginInterceptor;
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")          // 전체 URL 대상
                .excludePathPatterns(            // 인증 없이 허용
                    "/", "/index.html",
                    "/no-login", "/no-login.html",
                    "/login", "/login.html", "/logout.html");
    }
}
```

`addPathPatterns`로 적용 대상, `excludePathPatterns`로 제외 대상(로그인 페이지 등)을 지정. `/admin`은 제외 목록에 없으므로 로그인해야 접근 가능.

### 5. 로그인 흐름 — `PageController`

```java
@GetMapping("/login")  session.setAttribute("login", "success");  // 로그인 = 세션 저장
@GetMapping("/logout") session.invalidate();                       // 로그아웃 = 세션 무효화
@GetMapping("/admin")  // 인터셉터가 막음 → /login 먼저 호출해야 통과
```

## 면접 Q&A

**Q: Filter와 Interceptor 차이는?**
A: Filter는 서블릿 스펙으로 DispatcherServlet **앞**에서 동작해 모든 요청(정적 리소스 포함)을 가로챈다. Interceptor는 스프링 MVC 기능으로 DispatcherServlet **뒤**, Controller 앞뒤에서 동작하며 핸들러 정보 접근, 스프링 빈 활용이 자유롭다. 인코딩, CORS, 보안 같은 저수준은 Filter, 로그인 체크, 권한 같은 도메인 로직은 Interceptor가 적합.

**Q: Interceptor의 세 메소드 호출 시점은?**
A: `preHandle`은 Controller 실행 전(여기서 차단 가능), `postHandle`은 Controller 실행 후 View 렌더링 전(ModelAndView 조작 가능), `afterCompletion`은 View 렌더링 완료 후(예외 포함, 리소스 정리). `preHandle`이 false면 이후 단계는 실행 안 됨.

**Q: Filter 실행 순서는 어떻게 정하나?**
A: `FilterRegistrationBean.setOrder()`로 지정하며 낮은 숫자가 먼저 실행. `@Order`도 있으나 순서, URL 패턴을 함께 제어하려면 Java Config(`FilterRegistrationBean`)가 일반적. 요청은 order 오름차순, 응답은 역순으로 빠져나간다.

**Q: 요청 차단 방법이 Filter와 Interceptor에서 어떻게 다른가?**
A: Filter는 `chain.doFilter()`를 호출하지 않으면(또는 직접 응답을 써버리면) 차단. Interceptor는 `preHandle`에서 `false`를 반환하면 차단. 둘 다 차단 시 응답을 직접 작성(`response.getWriter().write(...)`)할 수 있다.

**Q: 정적 리소스(html)도 가로채려면?**
A: Filter는 기본적으로 정적 리소스까지 거치지만, Interceptor의 `addPathPatterns("/**")`도 정적 리소스에 걸릴 수 있어 `excludePathPatterns`로 로그인 페이지, 공개 페이지를 제외해야 무한 차단을 막는다(이 프로젝트가 그 예).

## 참고 출처

> - [Spring Boot — Servlet Filters](https://docs.spring.io/spring-boot/reference/web/servlet.html)
> - [Spring MVC — Interceptors](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/interceptors.html)
> - [HandlerInterceptor API](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/handlermapping-interceptor.html)
> - pom.xml 의존성: spring-boot-starter-web, Java `21`

## 더 나아가기 (선택)

> - `FilterConfig`의 `@Configuration` 주석 해제 후 콘솔에서 Filter → Interceptor 실행 순서 관찰
> - `MyFilter`/`MyFilter2`의 `setOrder` 값을 바꿔 실행 순서 변화 확인
> - `preHandle`에서 차단 시 에러 페이지로 redirect 처리
> - Spring Security로 동일한 로그인 인증을 선언적으로 구현해 비교
> - 기본 MVC 흐름은 [SpringBootMVC 프로젝트](../SpringBootMVC/README.md) 참고