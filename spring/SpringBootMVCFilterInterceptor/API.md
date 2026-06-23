# API 명세

> 페이지 제공용 컨트롤러(`PageController`). 모든 엔드포인트는 GET이며 정적 HTML 페이지를 반환한다.
> `/admin` 등 보호 페이지는 `LoginInterceptor`가 세션 로그인 여부(`login=success`)를 확인해 통과/차단한다.

## 엔드포인트 목록

| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|:---:|
| GET | /login | 세션에 `login=success` 저장(로그인) 후 login.html | ❌ (제외 경로) |
| GET | /logout | 세션 무효화(로그아웃) 후 logout.html | ✅ |
| GET | /admin | 관리자 페이지 admin/admin.html | ✅ |
| GET | /no-login | 비로그인 안내 페이지 no-login.html | ❌ (제외 경로) |

> 정적 페이지: `/`, `/index.html`, `/no-login.html`, `/login.html`, `/logout.html`도 인터셉터 제외 경로라 직접 접근 가능.

## 상세 명세

### GET /login
- 동작: `session.setAttribute("login", "success")` 후 `login.html` 반환.
- 인증: 불필요 (인터셉터 `excludePathPatterns`에 포함).
- 효과: 이후 같은 세션으로 보호 페이지 접근 가능.

### GET /admin
- 동작: `admin/admin.html` 반환.
- 인증: **필요**. 세션 `login != "success"`면 `LoginInterceptor.preHandle`이 `Need Login` 출력 후 차단(false).

### GET /logout
- 동작: `session.invalidate()`로 전체 세션 제거 후 `logout.html` 반환.
- 인증: 필요(제외 경로 아님).

### GET /no-login
- 동작: `no-login.html` 반환.
- 인증: 불필요 (제외 경로).

## 인증 정책 (LoginInterceptor)

- 적용 대상: `addPathPatterns("/**")` — 전체 URL.
- 제외 대상: `/`, `/index.html`, `/no-login`, `/no-login.html`, `/login`, `/login.html`, `/logout.html`.
- 통과 조건: 세션 속성 `login` 값이 `"success"`.
- 차단 응답: 본문 `Need Login` (HTTP 200, `preHandle` 반환 false).

## 참고: Filter (현재 비활성)

> `FilterConfig`가 주석 처리되어 동작하지 않음. 활성화 시:
> - `MyFilter` → `/admin` 요청에만, order 1
> - `MyFilter2` → 모든 요청(`/*`), order 2
> - 콘솔에 `MyFilter >> Before/After`, `MyFilter2 >> Before/After` 로그 출력.
