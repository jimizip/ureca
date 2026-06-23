# API 명세

> 학습용 컨트롤러 모음. 대부분 메소드는 별도 응답 본문 없이 `System.out.println`으로 콘솔에 값을 출력하거나(`void` 반환) JSP로 forward한다. "출력 위치"는 결과를 확인하는 곳을 뜻한다.

## 엔드포인트 목록

| Method | URL | 설명 | 컨트롤러 |
|--------|-----|------|----------|
| GET | /hello | hello.jsp 렌더링 | HelloController |
| GET | /m1 | GET 매핑 예시 | UrlMappingController |
| POST | /post | POST 매핑 예시 | UrlMappingController |
| PUT | /put | PUT 매핑 예시 | UrlMappingController |
| DELETE | /delete | DELETE 매핑 예시 | UrlMappingController |
| GET | /books/{bookId} | PathVariable 단일 | UrlMappingController |
| GET | /books/{limit}/{offset} | PathVariable 복수 | UrlMappingController |
| GET | /books/{price} | ⚠️ /books/{bookId}와 충돌(Ambiguous) | UrlMappingController |
| GET | /url1, /url2 | 복수 URL 한 메소드 | UrlMappingController |
| GET | /sub/* | 한 단계 와일드카드 | UrlMappingController |
| GET | /sub2/** | 다단계 와일드카드 | UrlMappingController |
| GET | /param1 | HttpServletRequest 직접 사용 | ParamController |
| GET | /param2 | 파라미터 자동 주입 | ParamController |
| GET | /param3 | String→Integer 자동 변환 | ParamController |
| GET | /param4 | @RequestParam 필수 파라미터 | ParamController |
| POST | /car | DTO 바인딩 | ParamController |
| POST | /map | @RequestParam Map | ParamController |
| GET | /header | @RequestHeader | ParamController |
| GET | /viewTest1 | forward → viewTest1.jsp | ViewController |
| GET | /viewTest2 | forward → sub/viewTest2.jsp | ViewController |
| GET | /viewTest3 | Model 전달 후 렌더링 | ViewController |
| GET | /viewTest4 | ModelAndView 전달 | ViewController |
| GET | /redirect | redirect → /viewTest1 | ViewController |
| GET | /void | void 반환 → void.jsp | ViewController |
| GET | /login | 인증 후 세션 저장 | SessionController |
| GET | /doSomething | sessionTest2.jsp | SessionController |
| GET | /logout | 세션 무효화 | SessionController |
| GET | /cookie/create | 쿠키 생성 | CookieController |
| GET | /cookie/read | 쿠키 읽기 | CookieController |
| GET | /cookie/delete | 쿠키 삭제 | CookieController |

## 상세 명세

### GET /hello
- 응답: `/WEB-INF/jsp/hello.jsp` 렌더링.

### GET /books/{bookId}
- Path 변수: `bookId` (Integer)
- 예: `/books/5` → 콘솔에 `5` 출력.
- 참고: `/books/{price}`와 같은 `GET /books/{정수}` 패턴이라 둘이 공존하면 `IllegalStateException: Ambiguous handler methods` 발생.

### GET /param3
- 요청 파라미터: `bookId` (Integer, 선택), `bookName` (String, 선택)
- 예: `/param3?bookId=10&bookName=spring`
- 참고: `int`로 받으면 누락 시 오류 → 래퍼 타입 `Integer` 사용.

### GET /param4
- 요청 파라미터: `bookName` (String, **필수** — `@RequestParam`)
- 누락 시 HTTP 400.
- 예: `/param4?bookName=spring`

### POST /car
- 요청 본문(form): `name` (String), `price` (int), `owner` (String) → `CarDto`로 바인딩.
- 예: `curl -d "name=myCar&price=20000&owner=hong" http://localhost:8080/car`
- 콘솔: `CarDto [name=myCar, price=20000, owner=hong]`

### POST /map
- 요청 파라미터: 임의의 key/value들 → `Map<String,String>`. 코드에서 `abc`, `def`, `xyz` 키 조회.

### GET /header
- 요청 헤더: `User-Agent`, `Accept`, `API-KEY`(사용자 정의) → 콘솔 출력.
- 예: `curl -H "API-KEY: abc123" http://localhost:8080/header`

### GET /viewTest3
- 동작: Model에 `seq="12345"`, `carDto=CarDto("myCar",20000,"홍길동")` 담아 `viewTest3.jsp`로 전달.

### GET /redirect
- 동작: `redirect:viewTest1` → 클라이언트가 `/viewTest1`로 재요청(302).

### GET /login
- 요청 파라미터: `username`, `password`
- 동작: `dskim`/`1234`면 세션에 `username` 저장 후 `sessionTest1.jsp`. (실제 로그인은 POST 권장)

### GET /logout
- 동작: `session.invalidate()`로 전체 세션 제거 후 `sessionTest3.jsp`.

### GET /cookie/create
- 동작: 쿠키 `domain=board` 생성. Path `/`, MaxAge 1일, HttpOnly.

### GET /cookie/read
- 요청 쿠키: `domain` (없으면 기본값 `"없음"` — `@CookieValue`).

### GET /cookie/delete
- 동작: 같은 이름 쿠키를 MaxAge 0으로 덮어써 삭제.
