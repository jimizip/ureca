# SpringBootMVCDBMybatisBoard

## 실습 목표

> 앞선 단일 도메인 CRUD 실습들을 종합해, **로그인/인증이 있는 실전 게시판 애플리케이션** 을 구현한다.
>
> - 도메인(기능)별 패키지 구조: `user`(회원), `auth`(로그인), `board`(게시판)
> - 세션 기반 로그인 + `HandlerInterceptor` 로 인증 가드
> - 게시판 CRUD + 페이지네이션 + 제목 검색 + 조회수 처리
> - 다중 DML 메서드의 `@Transactional` 트랜잭션 관리
> - 페이지 요청 vs 데이터(Ajax) 요청 분리 처리
> - 전역 예외 처리(`@ControllerAdvice`)와 페이지 예외 처리(`@ExceptionHandler`)
> - 요청/응답 DTO 표준화(`BoardParamDto`, `BoardResultDto`, `UserResultDto`)

## 핵심 개념

### HandlerInterceptor 기반 인증
컨트롤러 실행 전후에 공통 로직을 끼워넣는 Spring MVC 인터셉터. 로그인 세션 유무를 `preHandle` 에서 검사해 미인증 요청을 차단한다.

> "HandlerInterceptor ... allows for custom pre-processing with the option of prohibiting the execution of the handler itself."
>
> 출처: [Spring Framework - Interception](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/interceptors.html)

### `WebMvcConfigurer` 로 인터셉터 등록
`WebMvcConfigurer.addInterceptors()` 를 구현해 인터셉터와 적용/제외 URL 패턴을 등록한다.

> 출처: [Spring Framework - MVC Config / Interceptors](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/interceptors.html)

### `@Transactional` 선언적 트랜잭션
여러 DML 이 한 메서드에서 실행될 때 원자성을 보장한다. Spring 이 AOP 프록시로 메서드 호출 전 트랜잭션을 열고, 정상 종료 시 commit, 예외 시 rollback 한다.

> "The most important concept ... is the notion of declarative transaction management ... via @Transactional."
>
> 출처: [Spring Framework - Declarative Transaction Management](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html)

### `@ControllerAdvice` 전역 예외 처리
여러 컨트롤러에 공통 적용되는 예외 처리를 한곳에 모은다. `@ResponseBody` 와 함께 쓰면 데이터(JSON) 요청의 예외 응답을 일괄 처리한다.

> 출처: [Spring Framework - Controller Advice](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-advice.html)

### `Optional` 로 로그인 결과 표현
조회 결과 유무를 `Optional<UserDto>` 로 표현하고 `ifPresentOrElse` 로 성공/실패 분기한다.

## 코드 분석

### 패키지 구조 (기능별)
```
user/   회원: 가입
auth/   인증: 로그인 (UserDto 재사용)
board/  게시판: 목록/검색/상세/등록/수정/삭제 + 조회수
common/ LoginInterceptor, PageController(뷰 이동)
config/ WebMvcConfig(인터셉터 등록), GlobalExceptionHandler
```

### 인증 - `LoginInterceptor`
세션의 `userDto` 유무로 통과/차단. 핵심은 **데이터 요청과 페이지 요청 구분**.

```java
@Component
public class LoginInterceptor implements HandlerInterceptor {
    private final String jsonStr = "{\"result\":\"login\"}";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession();
        UserDto userDto = (UserDto) session.getAttribute("userDto");

        if(userDto == null) { // 미인증
            if("true".equals(request.getHeader("ajax"))) { // 데이터 요청
                response.getWriter().write(jsonStr);        // {"result":"login"} 응답
            } else {                                        // 페이지 요청
                response.sendRedirect("/pages/login");      // 로그인 페이지로 redirect
            }
            return false; // 차단
        }
        return true; // 통과
    }
}
```

주석 설명: 데이터 요청에 redirect 를 주면 프론트 JS 의 JSON 파싱이 실패한다. 그래서 프론트가 `ajax: true` 헤더를 보내기로 합의하고, 백엔드는 그 경우 `{"result":"login"}` JSON 으로 응답한다.

### 인터셉터 등록 - `WebMvcConfig`

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")               // 전체 URL
                .excludePathPatterns(                 // 인증 없이 허용
                    "/", "/index.html", "/assets/**",
                    "/pages/register", "/users/register",
                    "/pages/login", "/auth/login");
    }
}
```

회원가입/로그인 관련 경로와 정적 리소스만 열어두고, 나머지(게시판 등)는 로그인 필수.

### 로그인 - `LoginController` / `LoginServiceImpl`
DAO 는 email 로 사용자만 조회하고, **비밀번호 일치 검증은 서비스에서** 처리한다.

```java
// LoginServiceImpl
public Optional<UserDto> login(UserDto userDto) {
    UserDto dto = loginDao.login(userDto.getUserEmail());
    if(dto != null && userDto.getUserPassword().equals(dto.getUserPassword())) {
        dto.setUserPassword(null); // 비밀번호 제거 후 반환
        return Optional.of(dto);
    }
    return Optional.empty();
}
```

```java
// LoginController - 성공 시 세션에 userDto 저장
optional.ifPresentOrElse(
    userDto -> { session.setAttribute("userDto", userDto); map.put("result", "success"); },
    () ->       { map.put("result", "fail"); }
);
```

### 게시판 - 요청/응답 DTO 표준화
- `BoardParamDto`: 목록(`limit`, `offset`, `searchWord`)과 상세(`boardId`, `userSeq`) 파라미터 표준화
- `BoardResultDto`: 결과(`result`) + 목록(`list`) + 상세(`dto`) + 전체건수(`count`) 통합 응답
- `BoardDto`: board + users 조인 결과(작성자 이름/프로필 포함) + `sameUser`(작성자 본인 여부) + `readCount`

```java
// BoardDto - board 와 users 조인으로 작성자 정보 포함
private String userName;          // users join
private String userProfileImage;  // users join
private boolean sameUser;         // 로그인 사용자 == 작성자 (수정/삭제 가능 판단)
```

### 게시판 - 보안: userSeq 는 세션에서
등록/상세 시 `userSeq` 를 프론트 파라미터가 아닌 **세션** 에서 꺼낸다. 위변조 방지.

```java
// BoardController.insertBoard
int userSeq = ((UserDto) session.getAttribute("userDto")).getUserSeq();
boardDto.setUserSeq(userSeq); // 프론트 파라미터 신뢰 X, 세션 기준
```

### 게시판 - 조회수 + 트랜잭션
상세 조회 시 `board_user_read`(사용자-게시글 조회 기록) 테이블을 확인해 **처음 보는 경우에만** 조회수를 증가시킨다. insert + update 두 DML 이 함께 일어나므로 `@Transactional`.

```java
@Transactional
public BoardResultDto detailBoard(BoardParamDto boardParamDto) {
    try {
        int userReadCount = boardDao.countBoardUserRead(boardParamDto);
        if(userReadCount == 0) {                       // 처음 조회
            boardDao.insertBoardUserRead(boardParamDto); // 조회 기록
            boardDao.updateBoardUserRead(boardParamDto.getBoardId()); // read_count + 1
        }
        BoardDto boardDto = boardDao.detailBoard(boardParamDto);
        boardDto.setSameUser(boardDto.getUserSeq() == boardParamDto.getUserSeq());
        boardResultDto.setDto(boardDto);
        boardResultDto.setResult("success");
    } catch(Exception e) {
        boardResultDto.setResult("fail");
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // AOP 에 rollback 지시
    }
    return boardResultDto;
}
```

`setRollbackOnly()` 로 예외를 직접 던지지 않고도 트랜잭션 AOP 에 롤백을 지시한다. 삭제도 `board_user_read` 선삭제 후 `board` 삭제라 `@Transactional`.

### 게시판 - 페이지네이션 / 검색
```xml
<!-- 목록: board + users 조인, 최신순, limit/offset 페이지네이션 -->
<select id="listBoard" resultType="...BoardDto">
    select b.board_id, u.user_seq, u.user_name, u.user_profile_image, b.title, b.reg_dt, b.read_count
      from board b, users u
     where b.user_seq = u.user_seq
     order by b.board_id desc
     limit #{limit} offset #{offset};
</select>

<!-- 검색: 제목 LIKE -->
<select id="listBoardSearchWord" resultType="...BoardDto">
    ... and b.title like concat('%', #{searchWord}, '%') ...
</select>
```

컨트롤러에서 `searchWord` 유무로 일반 목록/검색 목록을 분기한다.

### 예외 처리 이원화
- **페이지 요청** (`PageController`): 클래스 내 `@ExceptionHandler` 가 `error.jsp` 로 forward
- **데이터 요청** (`GlobalExceptionHandler`): `@ControllerAdvice @ResponseBody` 가 `{"result":"fail"}` JSON 반환

```java
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Map<String, String> pageExceptionHandler(HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();
        map.put("result", "fail");
        return map;
    }
}
```

### 민감정보 보호
- `UserDto.toString()` 에서 `userPassword` 출력 제거 (로그 유출 방지)
- 로그인 성공 응답 전 `dto.setUserPassword(null)` 로 비밀번호 제거

## 면접 Q&A

**Q. 인증을 인터셉터로 처리한 이유는?**
A. 모든 컨트롤러마다 로그인 체크 코드를 중복 작성하지 않고, 진입 지점(`preHandle`)에서 공통 처리하기 위해서다. 적용/제외 URL 을 설정으로 관리할 수 있다. ([Interceptors](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/interceptors.html))

**Q. 데이터 요청과 페이지 요청을 왜 구분하나?**
A. 미인증 시 페이지 요청은 로그인 페이지로 redirect 하면 되지만, Ajax 데이터 요청에 redirect(HTML) 를 주면 JS 의 JSON 파싱이 깨진다. 그래서 `ajax: true` 헤더가 있으면 `{"result":"login"}` JSON 으로 응답하기로 프론트와 합의했다.

**Q. `@Transactional` 은 언제 붙였나?**
A. 한 메서드에서 여러 DML 이 함께 실행되는 `detailBoard`(조회기록 insert + 조회수 update)와 `deleteBoard`(연관 테이블 선삭제 + 게시글 삭제)에만 붙였다. AOP 프록시 비용이 있어 꼭 필요한 메서드에만 적용한다. ([Declarative TX](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html))

**Q. `setRollbackOnly()` 와 예외 throw 의 차이는?**
A. 예외를 throw 하면 롤백되지만 호출 흐름이 끊기고 사용자 정의 응답을 만들기 어렵다. `setRollbackOnly()` 는 코드 흐름을 유지하면서 트랜잭션 AOP 에 "롤백하라"는 정책만 전달해, `BoardResultDto` 같은 표준 응답을 그대로 반환할 수 있다.

**Q. `userSeq` 를 프론트가 아니라 세션에서 꺼내는 이유는?**
A. 작성자/조회자 식별 값을 프론트 파라미터로 받으면 위변조가 가능하다. 신뢰할 수 있는 서버 세션에서 꺼내야 안전하다.

**Q. 비밀번호 검증을 DAO 가 아니라 서비스에서 한 이유는?**
A. DAO 는 email 로 사용자 존재만 조회하고, 비밀번호 일치 비교는 서비스 책임으로 둬서 DB 쿼리에 평문 비밀번호를 노출하지 않고 비교 로직을 분리했다. (코드 주석의 #2 방식)

## 참고 출처

- [Spring Framework - Interceptors](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/interceptors.html)
- [Spring Framework - Declarative Transaction Management](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html)
- [Spring Framework - Controller Advice](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-advice.html)
- [MyBatis 3 - Mapper XML Files](https://mybatis.org/mybatis-3/sqlmap-xml.html)
- pom.xml 의존성: `spring-boot-starter-parent` 4.1.0, `mybatis-spring-boot-starter` 4.0.1, `spring-boot-starter-webmvc`, `mysql-connector-j`, `tomcat-embed-jasper`, Java 21

## 더 나아가기 (선택) / 코드 점검

발견된 개선점:

1. **`BoardController.detailBoard` 의 활성 NPE** — 메서드 본문 앞에 `String s = null; s.length();` 가 주석 처리되지 않고 남아 있어 상세 조회가 항상 `NullPointerException` 을 던진다(예외 처리 데모 흔적으로 추정). 실제 동작시키려면 제거해야 한다.

2. **비밀번호 평문 저장/비교** — `users.user_password` 가 평문이다. 학습 단계 이후엔 BCrypt 등 해시 + Spring Security 도입 권장.

3. **SQL 조인 방식** — `from board b, users u where b.user_seq = u.user_seq` 식의 암묵적 조인 대신 명시적 `JOIN ... ON` 이 가독성에 유리.

추가 학습:
- Spring Security 로 인증/인가 표준화 (인터셉터 직접 구현 대체)
- soft delete(`del_yn`) 정책 적용 (DAO 주석에 4가지 삭제 전략 정리됨)
- 페이지네이션 응답에 총 페이지/현재 페이지 메타 추가
