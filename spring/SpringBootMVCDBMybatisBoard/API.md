# API 명세

로그인 게시판 API. 컨트롤러가 역할별로 분리된다.
- `PageController` (`/pages`): JSP 뷰 이동 (HTML 응답)
- `UserController` (`/users`), `LoginController` (`/auth`), `BoardController` (`/boards`): 클래스 레벨 `@ResponseBody` 로 JSON 응답

## 인증 정책 (LoginInterceptor)

- 전체 URL(`/**`)에 인터셉터 적용. 세션에 `userDto` 가 없으면 차단.
- **인증 없이 허용**: `/`, `/index.html`, `/assets/**`, `/pages/register`, `/users/register`, `/pages/login`, `/auth/login`
- 그 외(`/boards/**`, `/pages/board`, `/pages/logout` 등)는 로그인 필수.
- 미인증 시: 데이터 요청(헤더 `ajax: true`)은 `{"result":"login"}` JSON, 페이지 요청은 `/pages/login` 으로 redirect.

## 엔드포인트 목록

### 뷰 이동 (PageController)

| Method | URL | 설명 | 응답 |
|--------|-----|------|------|
| GET | `/pages/register` | 회원가입 화면 | register.jsp |
| GET | `/pages/login` | 로그인 화면 | login.jsp |
| GET | `/pages/board` | 게시판 화면 (인증 필요) | board.jsp |
| GET | `/pages/logout` | 로그아웃(세션 무효화) | login.jsp |

### 회원 / 인증

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/users/register` | 회원 가입 | 불필요 |
| POST | `/auth/login` | 로그인(세션 생성) | 불필요 |

### 게시판 (BoardController, 인증 필요, 전체 JSON)

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/boards/list` | 목록 조회 (페이지네이션 + 검색) |
| GET | `/boards/detail/{boardId}` | 상세 조회 (+ 조회수 처리) |
| POST | `/boards/insert` | 게시글 등록 |
| POST | `/boards/update` | 게시글 수정 |
| GET | `/boards/delete/{boardId}` | 게시글 삭제 |

## 상세 명세

### POST /users/register
회원 가입. (매퍼 `registerUser`, `user_register_date` 는 `now()`)

**요청 파라미터** (form, `UserDto` 바인딩)

| 이름 | 타입 | 설명 |
|------|------|------|
| `userName` | String | 이름 |
| `userPassword` | String | 비밀번호 |
| `userEmail` | String | 이메일(로그인 ID) |

**응답 예시**
```json
{ "result": "success" }
```
insert 1건이면 `success`, 아니면 `fail`.

---

### POST /auth/login
로그인. 성공 시 세션에 `userDto` 저장. (매퍼 `login` — email 로 조회 후 서비스에서 비밀번호 비교)

**요청 파라미터** (form, `UserDto` 바인딩)

| 이름 | 타입 | 설명 |
|------|------|------|
| `userEmail` | String | 이메일 |
| `userPassword` | String | 비밀번호 |

**응답 예시**
```json
{ "result": "success" }
```
실패 시 `{"result":"fail"}`. 응답 전 비밀번호는 null 처리되어 세션에만 저장된다.

---

### GET /boards/list
게시글 목록. `searchWord` 가 있으면 제목 LIKE 검색, 없으면 전체 목록. (매퍼 `listBoard` / `listBoardSearchWord`)

**요청 파라미터** (query, `BoardParamDto` 바인딩)

| 이름 | 타입 | 설명 |
|------|------|------|
| `limit` | int | 페이지당 건수 |
| `offset` | int | 시작 위치 |
| `searchWord` | String | 제목 검색어 (선택) |

**응답 예시**
```json
{
  "result": "success",
  "count": 42,
  "list": [
    { "boardId": 42, "userSeq": 1, "userName": "홍길동", "userProfileImage": "noProfile.png",
      "title": "첫 글", "regDt": "2026-06-24T10:00:00", "readCount": 5, "sameUser": false }
  ],
  "dto": null
}
```

---

### GET /boards/detail/{boardId}
게시글 상세 + 조회수 처리. 조회자 `userSeq` 는 세션에서 가져온다. 최초 조회 시에만 `read_count` 증가(`@Transactional`). (매퍼 `detailBoard` 외)

**요청 파라미터**

| 위치 | 이름 | 타입 | 설명 |
|------|------|------|------|
| Path | `boardId` | Integer | 게시글 ID |

**응답 예시**
```json
{
  "result": "success",
  "count": 0,
  "list": null,
  "dto": {
    "boardId": 42, "userSeq": 1, "userName": "홍길동", "userProfileImage": "noProfile.png",
    "title": "첫 글", "content": "본문...", "regDt": "2026-06-24T10:00:00",
    "readCount": 6, "sameUser": true
  }
}
```

> `sameUser` 가 true 면 조회자가 작성자 본인(수정/삭제 가능). 현재 코드에 활성 NPE 가 있어 실제로는 예외 발생 — README 코드 점검 참고.

---

### POST /boards/insert
게시글 등록. `userSeq` 는 프론트 파라미터가 아닌 세션에서 주입. (매퍼 `insertBoard`)

**요청 파라미터** (form, `BoardDto` 바인딩)

| 이름 | 타입 | 설명 |
|------|------|------|
| `title` | String | 제목 |
| `content` | String | 본문 |

**응답 예시**
```json
{ "result": "success", "count": 0, "list": null, "dto": null }
```

---

### POST /boards/update
게시글 수정. (매퍼 `updateBoard`, `title`/`content` 갱신)

**요청 파라미터** (form, `BoardDto` 바인딩)

| 이름 | 타입 | 설명 |
|------|------|------|
| `boardId` | int | 수정할 게시글 ID |
| `title` | String | 제목 |
| `content` | String | 본문 |

**응답 예시**
```json
{ "result": "success" }
```

---

### GET /boards/delete/{boardId}
게시글 삭제. 연관 `board_user_read` 선삭제 후 `board` 삭제(`@Transactional`). (매퍼 `deleteBoardUserRead`, `deleteBoard`)

**요청 파라미터**

| 위치 | 이름 | 타입 | 설명 |
|------|------|------|------|
| Path | `boardId` | Integer | 삭제할 게시글 ID |

**응답 예시**
```json
{ "result": "success" }
```

## 공통 예외 응답

- 데이터 요청 처리 중 예외: `GlobalExceptionHandler`(`@ControllerAdvice @ResponseBody`)가 `{"result":"fail"}` 반환.
- 미인증 데이터 요청: 인터셉터가 `{"result":"login"}` 반환.
- 페이지 요청 예외: `PageController` 의 `@ExceptionHandler` 가 `error.jsp` 로 이동.
